package me.nentify.titles.player;

import me.nentify.titles.Titles;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.tasks.OnlineTrackerTask;
import me.nentify.titles.titles.BlockBreakerTitle;
import me.nentify.titles.titles.ChattyTitle;
import me.nentify.titles.titles.OnlineTimeTitle;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TitlesPlayer {

    private UUID uuid;

    private Map<Title.Type, Title> titles = new HashMap<>();
    private Map<Stat, Integer> stats = new HashMap<>();

    private Title.Type currentTitleType;

    private Task task;

    public TitlesPlayer(UUID uuid) {
        this.uuid = uuid;

        addTitle(new BlockBreakerTitle(Title.Tier.UNRANKED));
        addTitle(new OnlineTimeTitle(Title.Tier.NOOB));
        addTitle(new ChattyTitle(Title.Tier.UNRANKED));

        for (Title title : titles.values()) {
            for (Stat stat : title.getRequiredStats()) {
                addStat(stat, 0);
            }
        }

        OnlineTrackerTask onlineTrackerTask = new OnlineTrackerTask(this);
        task = Sponge.getScheduler().createTaskBuilder()
                .execute(onlineTrackerTask)
                .interval(1, TimeUnit.MINUTES)
                .name("Online Time Tracker: " + uuid)
                .submit(Titles.instance);

        // testing
        currentTitleType = Title.Type.ONLINE_TIME; // default
    }

    public UUID getUUID() {
        return uuid;
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

    public void setTitleTier(Title.Type type, Title.Tier tier) {
        if (titles.containsKey(type)) {
            Title title = titles.get(type);
            title.setTier(tier);
        }
    }

    public Optional<Integer> getStat(Stat stat) {
        if (stats.containsKey(Stat.BLOCKS_BROKEN))
            return Optional.of(stats.get(stat));

        return Optional.empty();
    }

    public void addStat(Stat stat, int count) {
        stats.put(stat, count);
    }

    public void incrementStat(Stat stat) {
        if (stats.containsKey(stat))
            stats.put(stat, stats.get(stat) + 1);
    }

    public void setStat(Stat stat, int count) {
        if (stats.containsKey(stat))
            stats.put(stat, count);
    }

    public void checkTitle(Title.Type type, Player player) {
        Optional<Title> title = getTitle(type);

        if (title.isPresent())
            title.get().check(this, player);
    }

    // Want to return something for this
//    public void checkTitles() {
//        for (Title title : titles.values()) {
//            title.check(this);
//        }
//    }

    public boolean hasTitle(Title.Type type) {
        return titles.values().stream().filter(x -> x.getTier() != Title.Tier.UNRANKED).map(Title::getType).collect(Collectors.toSet()).contains(type);
    }

    public Title.Type getCurrentTitleType() {
        return currentTitleType;
    }

    public void setCurrentTitleType(Title.Type type) {
        currentTitleType = type;
    }

    public Title getCurrentTitle() {
        return getTitle(getCurrentTitleType()).get();
    }

    public void cancelTask() {
        task.cancel();
    }
}
