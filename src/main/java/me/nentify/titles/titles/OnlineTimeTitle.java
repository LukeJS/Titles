package me.nentify.titles.titles;

import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OnlineTimeTitle extends Title {

    public OnlineTimeTitle(Tier tier) {
        super(Type.ONLINE_TIME, "Member", tier);
    }

    @Override
    public boolean canRankUp(TitlesPlayer titlesPlayer) {
        Optional<Integer> onlineTime = titlesPlayer.getStat(Stat.ONLINE_TIME);

        if (onlineTime.isPresent())
            return onlineTime.get() > getTier().getTierRank() * 3;

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return Collections.singletonList(Stat.ONLINE_TIME);
    }
}