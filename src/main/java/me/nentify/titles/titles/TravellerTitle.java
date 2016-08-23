package me.nentify.titles.titles;

import me.nentify.titles.Maths;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;

import java.util.List;
import java.util.Optional;

public class TravellerTitle extends Title {

    public TravellerTitle(Tier tier) {
        super(Type.TRAVELLER, "Traveller", tier);
    }

    @Override
    boolean canRankUp(TitlesPlayer titlesPlayer) {
        Optional<Integer> distanceTravelled = titlesPlayer.getStat(Stat.DISTANCE_TRAVELLED);

        if (distanceTravelled.isPresent()) {
            error
            Maths.exponential(getTier().getTierRank(), distanceTravelled.get(), 100);
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return null;
    }
}
