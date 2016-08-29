package me.nentify.titles.events;

import com.google.common.collect.Sets;
import me.nentify.titles.Titles;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.All;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
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
import java.util.Set;

public class PlayerEventHandler {

    private TitlesMessageChannel titlesChannel = new TitlesMessageChannel();

    @Listener(order = Order.LATE)
    public void onPlayerChat(MessageChannelEvent.Chat event, @Root Player player) {
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

            if (recipient instanceof Player && !((Player) sender).hasPermission("titles.no-prefix")) {
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

    @Listener
    public void onEntityAttack(DamageEntityEvent event, @All(ignoreEmpty = true) EntityDamageSource[] damageSources) {
        Entity target = event.getTargetEntity();

        for (EntityDamageSource damageSource : damageSources) {
            if (damageSource instanceof Player) {
                Player player = (Player) damageSource;
                Optional<TitlesPlayer> titlesPlayerOptional = Titles.getTitlesPlayer(player.getUniqueId());

                if (titlesPlayerOptional.isPresent()) {
                    TitlesPlayer titlesPlayer = titlesPlayerOptional.get();

                    if (event.willCauseDeath()) {
                        if (isHostileMob(target)) {
                            titlesPlayer.incrementStat(Stat.AGGRESSIVE_MOBS_KILLED);
                            titlesPlayer.checkTitle(Title.Type.AGGRESSIVE_MOB_KILLER);
                        }
                    }
                }
            }
        }
    }

    private static Set<EntityType> hostileMobs = Sets.newHashSet(EntityTypes.BLAZE,
            EntityTypes.CAVE_SPIDER,
            EntityTypes.CREEPER,
            EntityTypes.ENDERMAN,
            EntityTypes.ENDERMITE,
            EntityTypes.GHAST,
            EntityTypes.GIANT,
            EntityTypes.GUARDIAN,
            EntityTypes.MAGMA_CUBE,
            EntityTypes.PIG_ZOMBIE,
            EntityTypes.SHULKER,
            EntityTypes.SKELETON,
            EntityTypes.SPIDER,
            EntityTypes.WITCH,
            EntityTypes.WITHER,
            EntityTypes.ZOMBIE);

    public static boolean isHostileMob(Entity entity) {
        return hostileMobs.contains(entity.getType());
    }

    @Listener
    public void onPlayerInteract(InteractBlockEvent.Secondary.MainHand event, @Root Player player) {
        Optional<TitlesPlayer> titlesPlayerOptional = Titles.getTitlesPlayer(player.getUniqueId());

        if (titlesPlayerOptional.isPresent()) {
            TitlesPlayer titlesPlayer = titlesPlayerOptional.get();
            String blockId = event.getTargetBlock().getState().getId();

            if (blockId.startsWith("opencomputers")) {
                titlesPlayer.incrementStat(Stat.OC_INTERACTIONS);
                titlesPlayer.checkTitle(Title.Type.CODER);
            }
        }
    }

    @Listener
    public void onFish(FishingEvent.HookEntity event, @Root Player player) {
        Optional<TitlesPlayer> titlesPlayerOptional = Titles.getTitlesPlayer(player.getUniqueId());

        if (titlesPlayerOptional.isPresent()) {
            TitlesPlayer titlesPlayer = titlesPlayerOptional.get();

            titlesPlayer.incrementStat(Stat.OC_INTERACTIONS);
            titlesPlayer.checkTitle(Title.Type.CODER);
        }
    }
}
