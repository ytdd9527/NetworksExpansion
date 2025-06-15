package io.github.sefiraat.networks.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class StringUtils {

    /**
     * List of names to be given to ArmourStands, invisible but mods and Minimaps can see them :)
     */
    @NotNull private static final List<String> EGG_NAMES = Arrays.asList(
            "TheBusyBiscuit",
            "Alessio",
            "Walshy",
            "Jeff",
            "Seggan",
            "BOOMER_1",
            "svr333",
            "variananora",
            "ProfElements",
            "Riley",
            "FluffyBear",
            "GallowsDove",
            "Apeiros",
            "Martin",
            "Bunnky",
            "ReasonFoundDecoy",
            "Oah",
            "Azak",
            "andrewandy",
            "EpicPlayer10",
            "GentlemanCheesy",
            "ybw0014",
            "Ashian",
            "R.I.P",
            "OOOOMAGAAA",
            "TerslenK",
            "FN_FAL",
            "supertechxter");

    @NotNull public static String toTitleCase(@NotNull String string) {
        return toTitleCase(string, true);
    }

    @NotNull public static String toTitleCase(@NotNull String string, boolean delimiterToSpace) {
        return toTitleCase(string, delimiterToSpace, " _'-/");
    }

    @NotNull public static String toTitleCase(@NotNull String string, boolean delimiterToSpace, @NotNull String delimiters) {
        final StringBuilder builder = new StringBuilder();
        boolean capNext = true;

        for (char character : string.toCharArray()) {
            character = (capNext) ? Character.toUpperCase(character) : Character.toLowerCase(character);
            builder.append(character);
            capNext = (delimiters.indexOf(character) >= 0);
        }

        String built = builder.toString();

        if (delimiterToSpace) {
            final char space = ' ';
            for (char c : delimiters.toCharArray()) {
                built = built.replace(c, space);
            }
        }
        return built;
    }

    @NotNull public static String getRandomEggName() {
        int rnd = ThreadLocalRandom.current().nextInt(0, EGG_NAMES.size());
        return EGG_NAMES.get(rnd);
    }

    @NotNull public static List<String> getEggNames() {
        return EGG_NAMES;
    }
}
