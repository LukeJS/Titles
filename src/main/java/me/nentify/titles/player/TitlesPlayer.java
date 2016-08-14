package me.nentify.titles.player;

import me.nentify.titles.Titles;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.tasks.OnlineTrackerTask;
import me.nentify.titles.titles.BlockBreakerTitle;
import me.nentify.titles.titles.ChattyTitle;
import me.nentify.titles.titles.OnlineTimeTitle;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TitlesPlayer {

    private final UUID uuid;

    private Map<Title.Type, Title> titles = new HashMap<>();
    private Map<Stat, Integer> stats = new HashMap<>();

    private Title.Type currentTitleType;

    private Task task;

    public TitlesPlayer(UUID uuid, Title.Type currentTitleType) {
        this.uuid = uuid;

        // Default title
        addTitle(new OnlineTimeTitle(Title.Tier.NOOB));

        addTitle(new BlockBreakerTitle(Title.Tier.UNRANKED));
        addTitle(new ChattyTitle(Title.Tier.UNRANKED));

        OnlineTrackerTask onlineTrackerTask = new OnlineTrackerTask(this);
        task = Sponge.getScheduler().createTaskBuilder()
                .execute(onlineTrackerTask)
                .interval(1, TimeUnit.MINUTES)
                .name("Online Time Tracker: " + uuid)
                .submit(Titles.instance);

        this.currentTitleType = currentTitleType;
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

    public Optional<Integer> getStat(Stat stat) {
        if (stats.containsKey(stat))
            return Optional.of(stats.get(stat));

        return Optional.empty();
    }

    public void addStat(Stat stat, int count) {
        stats.put(stat, count);
    }

    public void incrementStat(Stat stat) {
        int count;

        if (stats.containsKey(stat))
            count = stats.get(stat) + 1;
        else
            count = 1;

        stats.put(stat, count);

        // Update MySQL data asynchronously
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> Titles.instance.storage.updateStat(getUUID(), stat, count));
    }

    public void checkTitle(Title.Type type) {
        Optional<Title> title = getTitle(type);

        if (title.isPresent())
            title.get().check(this, true);
    }

    public void checkTitles() {
        titles.forEach((type, title) -> title.check(this, false));
    }

    public boolean hasTitle(Title.Type type) {
        return titles.values().stream().filter(x -> x.getTier() != Title.Tier.UNRANKED).map(Title::getType).collect(Collectors.toSet()).contains(type);
    }

    public Title.Type getCurrentTitleType() {
        return currentTitleType;
    }

    public void setCurrentTitleType(Title.Type type) {
        currentTitleType = type;

        // Update MySQL data asynchronously
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Titles.instance.storage.updateCurrentTitle(getUUID(), type);
        });
    }

    public Title getCurrentTitle() {
        return getTitle(getCurrentTitleType()).get();
    }

    public void cancelTask() {
        task.cancel();
    }
}
