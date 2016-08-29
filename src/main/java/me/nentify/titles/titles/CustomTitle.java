package me.nentify.titles.titles;

import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CustomTitle extends Title {

    private String name;
    private TextColor color;

    public CustomTitle() {
        super(Type.CUSTOM_PREFIX, "", Tier.UNRANKED);
    }

    public TextColor getColor() {
        return color;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

    @Override
    public void promote() {
        setTier(Tier.NOOB);
    }

    @Override
    public boolean isMaxTier() {
        return getTier() != Tier.UNRANKED;
    }

    @Override
    public Text.Builder getTitleTextBuilder() {
        return Text.builder(getName())
                .color(getColor());
    }

    @Override
    public Text.Builder getHoverTextBuilder() {
        return Text.builder(getName() + "\n")
                .color(getColor());
    }

    public boolean canRankUp(TitlesPlayer titlesPlayer) {
        UserStorageService userStorage = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        Optional<User> userOptional = userStorage.get(titlesPlayer.getUUID());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<String> customTitleName = user.getOption("custom-title-name");
            Optional<String> customTitleColor = user.getOption("custom-title-color");

            if (customTitleName.isPresent() && customTitleColor.isPresent()) {
                setName(customTitleName.get());
                setColor(Sponge.getRegistry().getType(TextColor.class, customTitleColor.get().toUpperCase()).orElse(TextColors.NONE));

                return true;
            }
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return Collections.emptyList();
    }
}
