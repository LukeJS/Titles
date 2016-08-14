package me.nentify.titles.titles;

import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MobKillerTitle extends Title {

    public MobKillerTitle(Tier tier) {
        super(Type.MOB_KILLER, "Butcher", tier);
    }

    @Override
    boolean canRankUp(TitlesPlayer titlesPlayer) {
        Optional<Integer> mobsKilled = titlesPlayer.getStat(Stat.MOBS_KILLED);

        if (mobsKilled.isPresent()) {
            int mobsKilledForRankup = (int) (5 * Math.pow(4, getTier().getTierRank()));

            return mobsKilled.get() >= mobsKilledForRankup;
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return Collections.singletonList(Stat.MOBS_KILLED);
    }
}
