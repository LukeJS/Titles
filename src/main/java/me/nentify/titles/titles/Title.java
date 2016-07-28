package me.nentify.titles.titles;

import com.flowpowered.math.vector.Vector3d;
import me.nentify.titles.Titles;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.FireworkEffectData;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Firework;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.extent.Extent;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class Title {

    private static int titleId = 0;

    public static final int BLOCK_BREAKER = titleId++;

    private Type type;
    private String name;
    private Tier tier;

    public Title(Type type, String name, Tier tier) {
        this.type = type;
        this.name = name;
        this.tier = tier;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Tier getTier() {
        return tier;
    }

    public void promote() {
        Optional<Tier> nextTier = tier.getNextTier();

        if (nextTier.isPresent()) {
            tier = nextTier.get();
        }
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    public boolean isMaxTier() {
        return tier.ordinal() >= Tier.values().length - 1;
    }

    public Text.Builder getTitleTextBuilder() {
        return Text.builder(getName())
                .color(getTier().getColor());
    }

    public Text.Builder getHoverTextBuilder() {
        return Text.builder(getName() + " ")
                .append(Text.of(getTier().getColor(), toRomanNumerals(tier.getTierRank()) + "\n"))
                .append(getTier().getText());
    }

    public Text getPrefixText(boolean edit) {
        Text.Builder prefixTest = getTitleTextBuilder();
        Text.Builder hoverText = getHoverTextBuilder();

        if (edit) {
            Text hoverEditText = Text.builder("\n")
                    .append(Text.of(TextColors.GRAY, TextStyles.ITALIC, "Click to change your title")).build();

            hoverText = hoverText.append(hoverEditText);
            prefixTest = prefixTest.onClick(TextActions.runCommand("/titles"));
        }

        return prefixTest.onHover(TextActions.showText(hoverText.build())).build();
    }

    public Text getChooseText() {
        return getTitleTextBuilder()
                .onHover(TextActions.showText(
                        getHoverTextBuilder()
                                .append(Text.of("\n"))
                                .append(Text.of(TextColors.GREEN, TextStyles.ITALIC, "Click to choose this title"))
                                .build()))
                .onClick(TextActions.runCommand("/titles " + getType()))
                .build();
    }

    private void sendMessage(Player player) {
        if (getTier() == Tier.NOOB)
            Titles.sendDelayedMessage(player, getUnlockMessage());
        else
            Titles.sendDelayedMessage(player, getRankUpMessage());
    }

    private Text getUnlockMessage() {
        return Text.builder("Congratulations! You have unlcoked a new title: ").color(TextColors.GOLD)
                .append(getChooseText())
                .build();
    }

    private Text getRankUpMessage() {
        return Text.builder("Congratulations! ").color(TextColors.GOLD)
                .append(getChooseText())
                .append(Text.of(TextColors.GOLD, " has ranked up to "))
                .append(getTier().getText())
                .build();
    }

    public static String toRomanNumerals(int number) {
        switch (number) {
            case 0:
                return "";
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return "You have more than 10 tiers...";
        }
    }

    private FireworkEffect getFireworkEffect() {
        return FireworkEffect.builder()
                .colors(getTier().getColor().getColor(), Color.WHITE)
                .trail(true)
                .build();
    }

    private void spawnFireworks(Player player) {
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();

        taskBuilder
                .execute(new Consumer<Task>() {
                    private int count = getTier().getTierRank() - 1;

                    @Override
                    public void accept(Task task) {
                        count--;

                        player.playSound(SoundTypes.ENTITY_FIREWORK_LAUNCH, player.getLocation().getPosition(), 1);

                        if (count <= 0)
                            task.cancel();
                    }
                })
                .intervalTicks(5)
                .delayTicks(2)
                .submit(Titles.instance);

        taskBuilder
                .execute(new Consumer<Task>() {
                    private int count = getTier().getTierRank();

                    @Override
                    public void accept(Task task) {
                        count--;

                        Extent extent = player.getLocation().getExtent();
                        Optional<Entity> entityOptional = extent.createEntity(EntityTypes.FIREWORK, player.getLocation().getPosition());

                        if (entityOptional.isPresent()) {
                            Entity firework = entityOptional.get();

                            FireworkEffectData fireworkEffectData = firework.getOrCreate(FireworkEffectData.class).get();
                            fireworkEffectData.addElement(getFireworkEffect());

                            firework.offer(fireworkEffectData);
                            firework.offer(Keys.FIREWORK_FLIGHT_MODIFIER, 2);

                            extent.spawnEntity(firework, Cause.source(EntitySpawnCause.builder().entity(firework).type(SpawnTypes.PLUGIN).build()).build());
                        }

                        if (count <= 0)
                            task.cancel();
                    }
                })
                .intervalTicks(5)
                .delayTicks(1)
                .submit(Titles.instance);

        // TODO: Put this into another method
        Extent extent = player.getLocation().getExtent();
        Optional<Entity> entityOptional = extent.createEntity(EntityTypes.FIREWORK, player.getLocation().getPosition());

        if (entityOptional.isPresent()) {
            Entity firework = entityOptional.get();

            FireworkEffectData fireworkEffectData = firework.getOrCreate(FireworkEffectData.class).get();
            fireworkEffectData.addElement(getFireworkEffect());

            firework.offer(fireworkEffectData);

            extent.spawnEntity(firework, Cause.source(EntitySpawnCause.builder().entity(firework).type(SpawnTypes.PLUGIN).build()).build());
        }
    }

    public void check(TitlesPlayer titlesPlayer, Player player) {
        if (!isMaxTier() && canRankUp(titlesPlayer)) {
            promote();
            sendMessage(player);
            spawnFireworks(player);
        }
    }

    abstract boolean canRankUp(TitlesPlayer titlesPlayer);

    public abstract List<Stat> getRequiredStats();

    public enum Tier {
        UNRANKED("Unranked", TextColors.DARK_GRAY),
        NOOB("Noob", TextColors.GRAY),
        NOVICE("Novice", TextColors.YELLOW),
        EXPERIENCED("Experienced", TextColors.GREEN),
        MASTER("Master", TextColors.DARK_PURPLE);

        private final String name;
        private final TextColor color;

        Tier(String name, TextColor color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public TextColor getColor() {
            return color;
        }

        public Text getText() {
            return Text.of(getColor(), getName());
        }

        public int getTierRank() {
            return ordinal();
        }

        public Optional<Tier> getNextTier() {
            if (ordinal() < values().length - 1)
                return Optional.of(values()[ordinal() + 1]);

            return Optional.empty();
        }
    }

    public enum Type {
        BLOCK_BREAKER,
        ONLINE_TIME,
        CHATTY;
    }
}
