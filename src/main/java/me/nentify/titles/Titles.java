package me.nentify.titles;

import com.google.inject.Inject;
import me.nentify.titles.commands.ChooseCommand;
import me.nentify.titles.events.BlockEventHandler;
import me.nentify.titles.events.PlayerEventHandler;
import me.nentify.titles.titles.Title;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

/*
 * Titles:
 * - Block Breaker - Done
 * - Online Time - In Progress
 * - ???
 */

@Plugin(id = Titles.PLUGIN_ID, name = Titles.PLUGIN_NAME, version = Titles.PLUGIN_VERSION)
public class Titles {

    public static final String PLUGIN_ID = "titles";
    public static final String PLUGIN_NAME = "Titles";
    public static final String PLUGIN_VERSION = "0.3.0";

    @Inject
    private Logger logger;

    private static final Map<UUID, TitlesPlayer> titlesPlayers = new WeakHashMap<>();

    @Listener
    public void onPreIinit(GamePreInitializationEvent event) {
        logger.info("Starting " + PLUGIN_NAME + " v" + PLUGIN_VERSION);

        CommandSpec titlesCommandSpec = CommandSpec.builder()
                .description(Text.of("Choose your title"))
                .permission("titles.choose")
                .arguments(
                        GenericArguments.optionalWeak(
                                GenericArguments.enumValue(Text.of("titleType"), Title.Type.class)
                        )
                )
                .executor(new ChooseCommand())
                .build();

        Sponge.getCommandManager().register(this, titlesCommandSpec, "titles", "title");

        Sponge.getGame().getEventManager().registerListeners(this, new BlockEventHandler());
        Sponge.getGame().getEventManager().registerListeners(this, new PlayerEventHandler());
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        UUID uuid = player.getUniqueId();

        if (!titlesPlayers.containsKey(uuid)) {
            titlesPlayers.put(uuid, new TitlesPlayer(uuid, player.getName(), this));
        }
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
}
