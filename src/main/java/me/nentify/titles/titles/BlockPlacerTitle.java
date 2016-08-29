package me.nentify.titles.titles;

import me.nentify.titles.Maths;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;

import java.util.List;
import java.util.Optional;

public class BlockPlacerTitle extends Title {

    public BlockPlacerTitle(Tier tier) {
        super(Type.BUILDER, "Builder", tier);
    }

    @Override
    boolean canRankUp(TitlesPlayer titlesPlayer) {
        Optional<Integer> stat = titlesPlayer.getStat(Stat.BLOCKS_PLACED);

        if (stat.isPresent()) {
            return Maths.exponential(getTier().getTierRank(), stat.get(), Maths.BIGGER_MULTI);
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return null;
    }
}
