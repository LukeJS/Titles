package me.nentify.titles.events;

import me.nentify.titles.Titles;
import me.nentify.titles.TitlesPlayer;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class PlayerEventHandler {

    @Listener(order = Order.LAST)
    public void onPlayerChat(MessageChannelEvent.Chat event, @First Player player) {
        Optional<TitlesPlayer> titlesPlayerOptional = Titles.getTitlesPlayer(player.getUniqueId());

        if (titlesPlayerOptional.isPresent()) {
            TitlesPlayer titlesPlayer = titlesPlayerOptional.get();

            // Chat stat
            titlesPlayer.incrementStat(Stat.CHAT_MESSAGES);
            titlesPlayer.checkTitle(Title.Type.CHATTY, player);

            // Format chat with custom title
            Text message = event.getMessage();

            Text newMessage = Text.builder().append(titlesPlayer.getCurrentTitle().getPrefixText(false)).append(Text.of(" ")).append(message).build();

            event.setMessage(newMessage);
        }
    }
}
