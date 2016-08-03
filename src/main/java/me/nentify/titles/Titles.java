package me.nentify.titles;

import com.google.inject.Inject;
import me.nentify.titles.commands.TitlesCommand;
import me.nentify.titles.config.Config;
import me.nentify.titles.events.BlockEventHandler;
import me.nentify.titles.events.PlayerEventHandler;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.player.TitlesPlayerFactory;
import me.nentify.titles.storage.MySQLStorage;
import me.nentify.titles.titles.Title;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

@Plugin(id = Titles.PLUGIN_ID, name = Titles.PLUGIN_NAME, version = Titles.PLUGIN_VERSION)
public class Titles {

    public static final String PLUGIN_ID = "titles";
    public static final String PLUGIN_NAME = "Titles";
    public static final String PLUGIN_VERSION = "0.3.0";

    public static Titles instance;

    @Inject
    public Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path configPath;
    private Config config;

    private static final Map<UUID, TitlesPlayer> titlesPlayers = new WeakHashMap<>();

    public MySQLStorage storage;

    @Listener
    public void onPreIinit(GamePreInitializationEvent event) {
        logger.info("Starting " + PLUGIN_NAME + " v" + PLUGIN_VERSION);

        instance = this;

        try {
            config = new Config(configPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CommandSpec titlesCommandSpec = CommandSpec.builder()
                .description(Text.of("Choose your title"))
                .permission("titles.choose")
                .arguments(
                        GenericArguments.optionalWeak(
                                GenericArguments.enumValue(Text.of("titleType"), Title.Type.class)
                        )
                )
                .executor(new TitlesCommand())
                .build();

        Sponge.getCommandManager().register(this, titlesCommandSpec, "titles", "title");

        Sponge.getGame().getEventManager().registerListeners(this, new BlockEventHandler());
        Sponge.getGame().getEventManager().registerListeners(this, new PlayerEventHandler());

        storage = new MySQLStorage(config.hostname, config.port, config.database, config.username, config.password);
    }

    // this event is async
    @Listener
    public void onPlayerAuth(ClientConnectionEvent.Auth event) {
        GameProfile profile = event.getProfile();
        UUID uuid = profile.getUniqueId();

        if (!storage.userExists(uuid)) {
            storage.insertUser(uuid);
        }

        if (!titlesPlayers.containsKey(uuid)) {
            titlesPlayers.put(uuid, TitlesPlayerFactory.createTitlesPlayer(uuid));
        }
    }

    // this one isnt async
    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event) {

    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        UUID uuid = player.getUniqueId();

        Optional<TitlesPlayer> titlesPlayerOptional = getTitlesPlayer(uuid);

        if (titlesPlayerOptional.isPresent()) {
            TitlesPlayer titlesPlayer = titlesPlayerOptional.get();

            titlesPlayer.cancelTask();
        }
    }

    public static Optional<TitlesPlayer> getTitlesPlayer(UUID uuid) {
        if (titlesPlayers.containsKey(uuid))
            return Optional.of(titlesPlayers.get(uuid));

        return Optional.empty();
    }

    public static void sendDelayedMessage(Player player, Text message) {
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        taskBuilder
                .execute(() -> player.sendMessage(message))
                .delayTicks(1)
                .submit(instance);
    }
}
