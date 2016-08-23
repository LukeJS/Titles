package me.nentify.titles;

public class Maths {

    public static final int SMALL_MULTI = 5;
    public static final int BIG_MULTI = 50;

    public static boolean exponential(int tier, int stat, int multi) {
        int required = (int) (multi * Math.pow(4, tier));
        return stat >= required;
    }
}
