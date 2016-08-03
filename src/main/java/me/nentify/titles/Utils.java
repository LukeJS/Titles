package me.nentify.titles;

import java.util.UUID;

public class Utils {

    public static String uuidToString(UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}
