package com.balugaq.netex.api.algorithm;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.Function;

/**
 * @author balugaq
 */
@Deprecated
public class LevenshteinSorter {
    public static int distance(@NotNull String a, @NotNull String b) {
        if (a.isEmpty()) return b.length();
        if (b.isEmpty()) return a.length();

        int[] prev = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;

        for (int i = 1; i <= a.length(); i++) {
            int[] curr = new int[b.length() + 1];
            curr[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(Math.min(prev[j] + 1, curr[j - 1] + 1), prev[j - 1] + cost);
            }
            prev = curr;
        }
        return prev[b.length()];
    }

    public static <T> @NotNull Comparator<T> sortByDistance(@NotNull Function<T, String> extractor) {
        return (a, b) -> {
            String aName = extractor.apply(a);
            String bName = extractor.apply(b);
            return distance(aName, bName) - distance(bName, aName);
        };
    }
}
