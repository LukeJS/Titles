package me.nentify.titles.titles;

import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PermissionTitle extends Title {

    private String permission;
    private TextColor color;

    public PermissionTitle(Type type, String name, String permission, TextColor color) {
        super(type, name, Tier.UNRANKED);

        this.permission = permission;
        this.color = color;
    }

    public String getPermission() {
        return permission;
    }

    public TextColor getColor() {
        return color;
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
            return user.hasPermission(getPermission());
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return Collections.emptyList();
    }
}
