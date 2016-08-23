package me.nentify.titles.titles;

import me.nentify.titles.Maths;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AggressiveMobKillerTitle extends Title {

    public AggressiveMobKillerTitle(Tier tier) {
        super(Type.AGGRESSIVE_MOB_KILLER, "Slayer", tier);
    }

    @Override
    boolean canRankUp(TitlesPlayer titlesPlayer) {
        Optional<Integer> mobsKilled = titlesPlayer.getStat(Stat.AGGRESSIVE_MOBS_KILLED);

        if (mobsKilled.isPresent()) {
            return Maths.exponential(mobsKilled.get(), getTier().getTierRank(), Maths.SMALL_MULTI);
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return Collections.singletonList(Stat.AGGRESSIVE_MOBS_KILLED);
    }
}
