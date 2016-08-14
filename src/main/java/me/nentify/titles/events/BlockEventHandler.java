package me.nentify.titles.events;

import me.nentify.titles.Titles;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.Optional;

public class BlockEventHandler {

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @Root Player player) {
        Optional<TitlesPlayer> titlesPlayerOptional = Titles.getTitlesPlayer(player.getUniqueId());

        if (titlesPlayerOptional.isPresent()) {
            TitlesPlayer titlesPlayer = titlesPlayerOptional.get();
            titlesPlayer.incrementStat(Stat.BLOCKS_BROKEN);
            titlesPlayer.checkTitle(Title.Type.BLOCK_BREAKER);
        }
    }
}
