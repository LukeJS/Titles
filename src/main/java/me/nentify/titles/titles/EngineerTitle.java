package me.nentify.titles.titles;

import me.nentify.titles.Maths;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EngineerTitle extends Title {

    public EngineerTitle(Tier tier) {
        super(Type.ENGINEER, "Engineer", tier);
    }

    @Override
    boolean canRankUp(TitlesPlayer titlesPlayer) {
        Optional<Integer> engineeredStat = titlesPlayer.getStat(Stat.ENGINEER);

        if (engineeredStat.isPresent()) {
            return Maths.exponential(engineeredStat.get(), getTier().getTierRank(), Maths.BIG_MULTI);
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return Collections.singletonList(Stat.ENGINEER);
    }
}
