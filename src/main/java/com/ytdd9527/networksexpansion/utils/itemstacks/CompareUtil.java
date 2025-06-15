package com.ytdd9527.networksexpansion.utils.itemstacks;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * @author Final_ROOT
 */
@UtilityClass
public class CompareUtil {
    public static @NotNull SimpleComparator<Integer> INTEGER_SIMPLE_COMPARATOR =
            (comparison, compared) -> comparison - compared;

    /**
     * @param sources          should be ordered
     * @param simpleComparator {@link SimpleComparator#compare(Object, Object)}
     * @return The index that the targetValue match. Or -1 if not matched.
     */
    public static <T> int getIndex(
            T @NotNull [] sources, T targetValue, @NotNull SimpleComparator<T> simpleComparator) {
        int left = 0;
        int right = sources.length;
        int mid = (left + right) >> 1;
        int r;
        while (mid != left && mid != right) {
            r = simpleComparator.compare(targetValue, sources[mid]);
            if (r > 0) {
                left = mid;
            } else if (r < 0) {
                right = mid;
            } else {
                return mid;
            }
            mid = (left + right) >> 1;
        }
        return -1;
    }

    /**
     * @param sources          should be ordered
     * @param simpleComparator {@link SimpleComparator#compare(Object, Object)}
     * @return The smallest index that the targetValue match (or be close to).
     */
    public static <T> int getSmallFuzzyIndex(
            T @NotNull [] sources, T targetValue, @NotNull SimpleComparator<T> simpleComparator) {
        int left = 0;
        int right = sources.length;
        int mid = (left + right) >> 1;
        int r;
        while (mid != left && mid != right) {
            r = simpleComparator.compare(targetValue, sources[mid]);
            if (r > 0) {
                left = mid;
            } else if (r < 0) {
                right = mid;
            } else {
                return mid;
            }
            mid = (left + right) >> 1;
        }
        return left;
    }

    /**
     * @param sources          should be ordered
     * @param simpleComparator {@link SimpleComparator#compare(Object, Object)}
     * @return The smallest index that the targetValue match (or be close to).
     */
    public static <T> int getBigFuzzyIndex(
            T @NotNull [] sources, T targetValue, @NotNull SimpleComparator<T> simpleComparator) {
        int left = 0;
        int right = sources.length;
        int mid = (left + right) >> 1;
        int r;
        while (mid != left && mid != right) {
            r = simpleComparator.compare(targetValue, sources[mid]);
            if (r > 0) {
                left = mid;
            } else if (r < 0) {
                right = mid;
            } else {
                return mid;
            }
            mid = (left + right) >> 1;
        }
        return right;
    }

    /**
     * @param sources should be ordered
     * @return The index that the targetValue match. Or -1 if not matched.
     */
    public static int getIntIndex(int @NotNull [] sources, int targetValue) {
        int left = 0;
        int right = sources.length;
        int mid = (left + right) >> 1;
        int r;
        while (mid != left && mid != right) {
            r = targetValue - sources[mid];
            if (r > 0) {
                left = mid;
            } else if (r < 0) {
                right = mid;
            } else {
                return mid;
            }
            mid = (left + right) >> 1;
        }
        return -1;
    }

    /**
     * @param sources should be ordered
     * @return The smallest index that the targetValue match (or be close to).
     */
    public static int getIntSmallFuzzyIndex(int @NotNull [] sources, int targetValue) {
        int left = 0;
        int right = sources.length;
        int mid = (left + right) >> 1;
        int r;
        while (mid != left && mid != right) {
            r = targetValue - sources[mid];
            if (r > 0) {
                left = mid;
            } else if (r < 0) {
                right = mid;
            } else {
                return mid;
            }
            mid = (left + right) >> 1;
        }
        return left;
    }

    /**
     * @param sources should be ordered
     * @return The smallest index that the targetValue match (or be close to).
     */
    public static int getIntBigFuzzyIndex(int @NotNull [] sources, int targetValue) {
        int left = 0;
        int right = sources.length;
        int mid = (left + right) >> 1;
        int r;
        while (mid != left && mid != right) {
            r = targetValue - sources[mid];
            if (r > 0) {
                left = mid;
            } else if (r < 0) {
                right = mid;
            } else {
                return mid;
            }
            mid = (left + right) >> 1;
        }
        return left;
    }

    public interface SimpleComparator<T> {

        /**
         * @return 0: comparison = compared
         * >0: comparison > compared
         * <0: comparison < compared
         */
        int compare(T comparison, T compared);
    }
}
