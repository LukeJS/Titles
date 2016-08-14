package me.nentify.titles.titles;

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
        Optional<Integer> blocksBroken = titlesPlayer.getStat(Stat.BLOCKS_BROKEN);

        if (blocksBroken.isPresent()) {
            int blocksForRankup = (int) (50 * Math.pow(4, getTier().getTierRank()));

            return blocksBroken.get() >= blocksForRankup;
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return Arrays.asList(Stat.BLOCKS_BROKEN);
    }
}
