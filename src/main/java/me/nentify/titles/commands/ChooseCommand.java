package me.nentify.titles.commands;

import me.nentify.titles.Titles;
import me.nentify.titles.TitlesPlayer;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ChooseCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (source instanceof Player) {
            Player player = (Player) source;
            UUID uuid = player.getUniqueId();

            Optional<TitlesPlayer> titlesPlayer = Titles.getTitlesPlayer(uuid);

            if (titlesPlayer.isPresent()) {
                Map<Title.Type, Title> titles = titlesPlayer.get().getTitles();

                Text.Builder titlesList = Text.builder("Choose your title:\n");

                Iterator<Title> iterator = titles.values().stream().filter(x -> x.getTier() != Title.Tier.UNRANKED).iterator();

                while (iterator.hasNext()) {
                    Title title = iterator.next();
                    titlesList = titlesList.append(title.getChooseText());

                    if (iterator.hasNext())
                        titlesList = titlesList.append(Text.of(", "));
                }

                player.sendMessage(titlesList.build());

                return CommandResult.success();
            }
        }

        return CommandResult.empty();
    }
}
