package me.nentify.titles.player;

import me.nentify.titles.Titles;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TitlesPlayerFactory {

    public static TitlesPlayer createTitlesPlayer(UUID uuid) {
        TitlesPlayer titlesPlayer = new TitlesPlayer(uuid);

        Map<Title.Type, Title.Tier> titleTiers = Titles.instance.storage.getTitlesForPlayer(uuid);

        Map<Stat, Integer> stats = new HashMap<>();

        titleTiers.forEach(titlesPlayer::setTitleTier);
        stats.forEach(titlesPlayer::setStat);

        return titlesPlayer;
    }
}
