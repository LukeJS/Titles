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

        if (onlineTime.isPresent()) {
            int minutesRequired;

            switch (getTier()) {
                case NOOB: // Default rank, cannot actually unlock
                    minutesRequired = getMinutesFromHours(1);
                    break;
                case NOVICE:
                    minutesRequired = getMinutesFromHours(10);
                    break;
                case EXPERIENCED:
                    minutesRequired = getMinutesFromHours(50);
                    break;
                case MASTER:
                    minutesRequired = getMinutesFromHours(300);
                    break;
                default:
                    minutesRequired = -1;
                    break;
            }

            return onlineTime.get() >= minutesRequired;
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return Collections.singletonList(Stat.ONLINE_TIME);
    }

    private int getMinutesFromHours(int hours) {
        return hours * 60;
    }
}