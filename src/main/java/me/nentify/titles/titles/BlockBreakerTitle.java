package me.nentify.titles.titles;

import me.nentify.titles.Maths;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BlockBreakerTitle extends Title {

    public BlockBreakerTitle(Tier tier) {
        super(Type.BLOCK_BREAKER, "Destroyer", tier);
    }

    @Override
    boolean canRankUp(TitlesPlayer titlesPlayer) {
        Optional<Integer> stat = titlesPlayer.getStat(Stat.BLOCKS_BROKEN);

        if (stat.isPresent()) {
            return Maths.exponential(getTier().getTierRank(), stat.get(), Maths.BIGGER_MULTI);
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return Arrays.asList(Stat.BLOCKS_BROKEN);
    }
}
