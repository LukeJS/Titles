package me.nentify.titles;

import com.google.inject.Inject;
import me.nentify.titles.commands.ChooseCommand;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

import static java.lang.Thread.sleep;

@Plugin(id = Titles.PLUGIN_ID, name = Titles.PLUGIN_NAME, version = Titles.PLUGIN_VERSION)
public class Titles {
    public static final String PLUGIN_ID = "titles";
    public static final String PLUGIN_NAME = "Titles";
    public static final String PLUGIN_VERSION = "0.2.0";

    @Inject
    private Logger logger;

    private static final Map<UUID, TitlesPlayer> titlesPlayers = new WeakHashMap<>();

    @Listener
    public void onPreIinit(GamePreInitializationEvent event) {
        logger.info("Starting " + PLUGIN_NAME + " v" + PLUGIN_VERSION);

        CommandSpec commandSpec = CommandSpec.builder()
                .description(Text.of("Choose your title"))
                .permission("titles.choose")
                .executor(new ChooseCommand())
                .build();

        Sponge.getCommandManager().register(this, commandSpec, "title");
    }

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        UUID uuid = player.getUniqueId();

        TitlesPlayer titlesPlayer;

        if (titlesPlayers.containsKey(uuid)) {
            titlesPlayer = titlesPlayers.get(uuid);
            player.sendMessage(Text.of(TextColors.RED, "YOU WERE IN TEH MAP"));
        } else {
            titlesPlayer = new TitlesPlayer();
            titlesPlayers.put(uuid, titlesPlayer);
        }

        // testing
        Optional<Title> blockBreakerTitle = titlesPlayer.getTitle(Title.Type.BLOCK_BREAKER);
        blockBreakerTitle.ifPresent(title -> player.sendMessage(title.getPrefixText(true)));

        // Create thread to check player's stats every 5 minutes and update ranks

        Runnable timeChecker = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    titlesPlayer.incrementStat(Stat.ONLINE_TIME);

                    if (!titlesPlayer.getTitle(Title.Type.ONLINE_TIME).get().isMaxTier())
                        player.sendMessage(Text.of(titlesPlayer.getStat(Stat.ONLINE_TIME).get()));

                    if (titlesPlayer.checkTitle(Title.Type.ONLINE_TIME)) {
                        player.sendMessage(Text.of(TextColors.GOLD, "GRAKTSAKTA!!!!!!!!!!!!!!!!!!!!!!"));
                        player.sendMessage(titlesPlayer.getTitle(Title.Type.ONLINE_TIME).get().getPrefixText(false));
                    }
                }
            }
        };

        Thread thread = new Thread(timeChecker);
        thread.start();
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @Root Player player) {
        UUID uuid = player.getUniqueId();

        if (titlesPlayers.containsKey(uuid)) {
            TitlesPlayer titlesPlayer = titlesPlayers.get(uuid);
            titlesPlayer.incrementStat(Stat.BLOCKS_BROKEN);

            // DEBUG
            player.sendMessage(titlesPlayer.getTitle(Title.Type.BLOCK_BREAKER).get().getPrefixText(true));
            player.sendMessage(Text.of(titlesPlayer.getStat(Stat.BLOCKS_BROKEN).get()));

            // For testing for now, maybe keep? Will have to see
            if (titlesPlayer.checkTitle(Title.Type.BLOCK_BREAKER)) {
                player.sendMessage(Text.of(TextColors.GOLD, "GRATS!!"));
            }
        } else {
            logger.error("Player " + player.getName() + " not found in database");
        }
    }

    public static Optional<TitlesPlayer> getTitlesPlayer(UUID uuid) {
        if (titlesPlayers.containsKey(uuid))
            return Optional.of(titlesPlayers.get(uuid));

        return Optional.empty();
    }
}
