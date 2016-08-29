package me.nentify.titles.titles;

import me.nentify.titles.Maths;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ChattyTitle extends Title {

    public ChattyTitle(Tier tier) {
        super(Type.CHATTY, "Chatty", tier);
    }

    @Override
    boolean canRankUp(TitlesPlayer titlesPlayer) {
        Optional<Integer> chatMessages = titlesPlayer.getStat(Stat.CHAT_MESSAGES);

        if (chatMessages.isPresent()) {
            return Maths.exponential(getTier().getTierRank(), chatMessages.get(), 20);
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return Collections.singletonList(Stat.CHAT_MESSAGES);
    }
}
