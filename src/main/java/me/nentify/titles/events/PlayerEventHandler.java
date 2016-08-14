package me.nentify.titles.events;

import me.nentify.titles.Titles;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class PlayerEventHandler {

    private TitlesMessageChannel titlesChannel = new TitlesMessageChannel();

    @Listener(order = Order.LATE)
    public void onPlayerChat(MessageChannelEvent.Chat event, @First Player player) {
        Optional<TitlesPlayer> titlesPlayerOptional = Titles.getTitlesPlayer(player.getUniqueId());

        // Chat stat
        if (titlesPlayerOptional.isPresent()) {
            TitlesPlayer titlesPlayer = titlesPlayerOptional.get();
            titlesPlayer.incrementStat(Stat.CHAT_MESSAGES);
            titlesPlayer.checkTitle(Title.Type.CHATTY);
        }

        // Prefix chat with title
        MessageChannel originalChannel = event.getOriginalChannel();
        MessageChannel newChannel = MessageChannel.combined(originalChannel, titlesChannel);
        event.setChannel(newChannel);
    }

    public class TitlesMessageChannel implements MessageChannel {

        @Override
        public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
            Player senderPlayer = (Player) sender;

            if (recipient instanceof Player) {
                Player receiverPlayer = (Player) recipient;

                Optional<TitlesPlayer> titlesPlayer = Titles.getTitlesPlayer(senderPlayer.getUniqueId());

                if (titlesPlayer.isPresent()) {
                    return Optional.of(
                            getTitledMessage(
                                    titlesPlayer.get(),
                                    original,
                                    senderPlayer.getUniqueId().equals(
                                            receiverPlayer.getUniqueId()
                                    )
                            )
                    );
                }
            }

            return Optional.of(original);
        }

        @Override
        public Collection<MessageReceiver> getMembers() {
            return Collections.emptyList();
        }

        private Text getTitledMessage(TitlesPlayer titlesPlayer, Text message, boolean edit) {
            return Text.builder()
                    .append(Text.of(TextColors.GRAY, "["))
                    .append(titlesPlayer.getCurrentTitle().getPrefixText(edit))
                    .append(Text.of(TextColors.GRAY, "] "))
                    .append(message)
                    .build();
        }
    }
}
