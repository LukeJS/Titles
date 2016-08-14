package me.nentify.titles.player;

import me.nentify.titles.Titles;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TitlesPlayerFactory {

    public static TitlesPlayer createTitlesPlayer(UUID uuid) {
        // Get player's current title from database
        Optional<Title.Type> currentTitleType = Titles.instance.storage.getCurrentTitleType(uuid);

        // Get player's stats from database
        Map<Stat, Integer> stats = Titles.instance.storage.getStatsForPlayer(uuid);

        // Create new TitlesPlayer
        TitlesPlayer titlesPlayer = new TitlesPlayer(uuid, currentTitleType.isPresent() ? currentTitleType.get() : Title.Type.ONLINE_TIME);

        // Add their stats to their TitlesPlayer
        stats.forEach(titlesPlayer::addStat);

        // Check their titles to set their title ranks based on their stats
        titlesPlayer.checkTitles();

        // Return the complete TitlesPlayer object
        return titlesPlayer;
    }
}
