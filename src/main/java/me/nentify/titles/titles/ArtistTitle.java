package me.nentify.titles.titles;

import me.nentify.titles.Maths;
import me.nentify.titles.player.TitlesPlayer;
import me.nentify.titles.stats.Stat;

import java.util.List;
import java.util.Optional;

public class ArtistTitle extends Title {

    public ArtistTitle(Tier tier) {
        super(Type.ARTIST, "Artist", tier);
    }

    @Override
    boolean canRankUp(TitlesPlayer titlesPlayer) {
        Optional<Integer> stat = titlesPlayer.getStat(Stat.BLOCKS_CHISELLED);

        if (stat.isPresent()) {
            return Maths.exponential(getTier().getTierRank(), stat.get(), Maths.BIG_MULTI);
        }

        return false;
    }

    @Override
    public List<Stat> getRequiredStats() {
        return null;
    }
}
