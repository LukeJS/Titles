package me.nentify.titles.titles;

import me.nentify.titles.Maths;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;

import java.util.List;
import java.util.Optional;

public class FisherTitle extends Title {

    public FisherTitle(Tier tier) {
        super(Type.FISHER, "Fisher", tier);
    }

    @Override
    boolean canRankUp(TitlesPlayer titlesPlayer) {
        Optional<Integer> stat = titlesPlayer.getStat(Stat.FISH_CAUGHT);

        if (stat.isPresent()) {
            return Maths.exponential(getTier().getTierRank(), stat.get(), Maths.SMALL_MULTI);
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return null;
    }
}
