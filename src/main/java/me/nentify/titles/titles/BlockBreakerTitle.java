package me.nentify.titles.titles;

import me.nentify.titles.TitlesPlayer;
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
            int blocksForRankup = (int) Math.pow(5, getTier().getTierRank()); // 5, 25, 125, 625 : Noob, Novice, Experienced, Master

            return blocksBroken.get() >= blocksForRankup;
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return Arrays.asList(Stat.BLOCKS_BROKEN);
    }
}
