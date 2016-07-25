package me.nentify.titles;

import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.BlockBreakerTitle;
import me.nentify.titles.titles.OnlineTimeTitle;
import me.nentify.titles.titles.Title;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TitlesPlayer {

    private Map<Title.Type, Title> titles = new HashMap<>();
    private Map<Stat, Integer> stats = new HashMap<>();

    private Title.Type chosenTitle;

    public TitlesPlayer() {
        // Will need to get rank + stat from DB
        addTitle(new BlockBreakerTitle(Title.Tier.UNRANKED));
        addTitle(new OnlineTimeTitle(Title.Tier.NOOB));

        for (Title title : titles.values()) {
            for (Stat stat : title.getRequiredStats()) {
                addStat(stat, 0);
            }
        }

        // testing
        chosenTitle = Title.Type.ONLINE_TIME; // default
    }

    public void addTitle(Title title) {
        titles.put(title.getType(), title);
    }

    public Optional<Title> getTitle(Title.Type type) {
        if (titles.containsKey(type))
            return Optional.of(titles.get(type));

        return Optional.empty();
    }

    public Map<Title.Type, Title> getTitles() {
        return titles;
    }

    public Optional<Integer> getStat(Stat stat) {
        if (stats.containsKey(Stat.BLOCKS_BROKEN))
            return Optional.of(stats.get(stat));

        return Optional.empty();
    }

    public void addStat(Stat stat, int count) {
        stats.put(stat, count);
    }

    /**
     * Increments the stat and returns true if it causes an increase in the tier.
     * @return true if the stat increase caused an increase in the tier
     */
    public void incrementStat(Stat stat) {
        if (stats.containsKey(stat))
            stats.put(stat, stats.get(stat) + 1);
    }

    public boolean checkTitle(Title.Type type) {
        Optional<Title> title = getTitle(type);

        if (title.isPresent())
            return title.get().check(this);

        return false;
    }

    // Want to return something for this
//    public void checkTitles() {
//        for (Title title : titles.values()) {
//            title.check(this);
//        }
//    }

    public Title.Type getChosenTitle() {
        return chosenTitle;
    }

    public void setChosenTitle(Title.Type type) {
        chosenTitle = type;
    }
}
