package me.nentify.titles.events;

import me.nentify.titles.Titles;
import me.nentify.titles.TitlesPlayer;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;

import java.util.Optional;

public class PlayerEventHandler {

    @Listener
    public void onPlayerChat(MessageChannelEvent.Chat event, @First Player player) {
        Optional<TitlesPlayer> titlesPlayerOptional = Titles.getTitlesPlayer(player.getUniqueId());

        if (titlesPlayerOptional.isPresent()) {
            TitlesPlayer titlesPlayer = titlesPlayerOptional.get();
            titlesPlayer.incrementStat(Stat.CHAT_MESSAGES);
            titlesPlayer.checkTitle(Title.Type.CHATTY, player);
        }
    }
}
