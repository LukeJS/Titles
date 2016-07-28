package me.nentify.titles.player;

import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TitlesPlayerFactory {

    public static TitlesPlayer createTitlesPlayer(UUID uuid, String name) {
        TitlesPlayer titlesPlayer = new TitlesPlayer(uuid, name);

        // some mysql thing

        Map<Title.Type, Title.Tier> titleTiers = new HashMap<>();
        titleTiers.put(Title.Type.CHATTY, Title.Tier.MASTER);
        titleTiers.put(Title.Type.BLOCK_BREAKER, Title.Tier.EXPERIENCED);

        Map<Stat, Integer> stats = new HashMap<>();
        stats.put(Stat.BLOCKS_BROKEN, 150);
        stats.put(Stat.CHAT_MESSAGES, 52502);

        titleTiers.forEach(titlesPlayer::setTitleTier);
        stats.forEach(titlesPlayer::setStat);

        return titlesPlayer;
    }
}
