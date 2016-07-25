package me.nentify.titles.titles;

import me.nentify.titles.TitlesPlayer;
import me.nentify.titles.stats.Stat;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.Optional;

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

    public void incrementTier() {
        Optional<Tier> nextTier = tier.getNextTier();

        if (nextTier.isPresent()) {
            tier = nextTier.get();
        }
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
                .append(Text.of(getTier().getColor(), getTier().getName()));
    }

    public Text getPrefixText(boolean edit) {
        Text.Builder prefixTest = getTitleTextBuilder();
        Text.Builder hoverText = getHoverTextBuilder();

        if (edit) {
            Text hoverEditText = Text.builder("\n")
                    .append(Text.of(TextColors.GRAY, TextStyles.ITALIC, "Click to edit")).build();

            hoverText = hoverText.append(hoverEditText);
            prefixTest = prefixTest.onClick(TextActions.runCommand("/broadcast edit"));
        }

        return prefixTest.onHover(TextActions.showText(hoverText.build())).build();
    }

    public Text getChooseText() {
        return getTitleTextBuilder()
                .onHover(TextActions.showText(
                        getHoverTextBuilder()
                                .append(Text.of(TextColors.GREEN, TextStyles.ITALIC, "Choose"))
                                .build()))
                .onClick(TextActions.runCommand("/broadcast choose " + getName()))
                .build();
    }

    public static void test() {

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

    public boolean check(TitlesPlayer titlesPlayer) {
        if (!isMaxTier() && canRankUp(titlesPlayer)) {
            incrementTier();
            return true;
        }

        return false;
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
        ONLINE_TIME;
    }
}
