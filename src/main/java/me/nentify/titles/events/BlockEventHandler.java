package me.nentify.titles.events;

import javafx.scene.control.TextFormatter;
import me.nentify.titles.Titles;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

public class BlockEventHandler {

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @Root Player player) {
        Optional<TitlesPlayer> titlesPlayerOptional = Titles.getTitlesPlayer(player.getUniqueId());

        if (titlesPlayerOptional.isPresent()) {
            TitlesPlayer titlesPlayer = titlesPlayerOptional.get();

            // Blocks Broken
            titlesPlayer.incrementStat(Stat.BLOCKS_BROKEN);
            titlesPlayer.checkTitle(Title.Type.BLOCK_BREAKER);

            event.getTransactions().forEach(x -> {
                String blockId = x.getFinal().getState().getId();

                if (blockId.contains("wheat")
                        || blockId.contains("carrot")
                        || blockId.contains("potato")
                        || blockId.contains("melon")
                        || blockId.contains("pumpkin")
                        || blockId.startsWith("harvestcraft")) {
                    titlesPlayer.incrementStat(Stat.FARM_INTERACTIONS);
                    titlesPlayer.checkTitle(Title.Type.FARMER);
                }
            });
        }
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {
        Optional<TitlesPlayer> titlesPlayerOptional = Titles.getTitlesPlayer(player.getUniqueId());

        if (titlesPlayerOptional.isPresent()) {
            TitlesPlayer titlesPlayer = titlesPlayerOptional.get();

            // Blocks Placed
            titlesPlayer.incrementStat(Stat.BLOCKS_PLACED);
            titlesPlayer.checkTitle(Title.Type.BUILDER);

            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                String blockId = transaction.getFinal().getState().getId();

                if (blockId.startsWith("enderio") || blockId.startsWith("forestry") || blockId.startsWith("refinedstorage")) {
                    titlesPlayer.incrementStat(Stat.ENGINEER);
                    titlesPlayer.checkTitle(Title.Type.ENGINEER);
                }

                // Chisel and Chisels & Bits
                if (blockId.startsWith("chisel")) {
                    titlesPlayer.incrementStat(Stat.BLOCKS_CHISELLED);
                    titlesPlayer.checkTitle(Title.Type.ARTIST);
                }
            }
        }
    }
}
