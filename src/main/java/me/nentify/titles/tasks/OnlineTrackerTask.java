package me.nentify.titles.tasks;

import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;
import java.util.function.Consumer;

public class OnlineTrackerTask implements Consumer<Task> {

    private TitlesPlayer titlesPlayer;

    public OnlineTrackerTask(TitlesPlayer titlesPlayer) {
        this.titlesPlayer = titlesPlayer;
    }

    @Override
    public void accept(Task task) {
        titlesPlayer.incrementStat(Stat.ONLINE_TIME);

        Optional<Player> player = Sponge.getServer().getPlayer(titlesPlayer.getUUID());

        if (player.isPresent()) {
            titlesPlayer.checkTitle(Title.Type.ONLINE_TIME, player.get());
        } else {
            task.cancel();
        }
    }
}
