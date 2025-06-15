package com.ytdd9527.networksexpansion.utils;

import io.github.sefiraat.networks.Networks;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

/**
 * @author Final_ROOT
 * @since 2.0
 */
@SuppressWarnings("deprecation")
@UtilityClass
public class TextUtil {
    public static final String PLACEHOLDER = "†";
    public static final String COLOR_NORMAL = "§x§8§8§f§f§f§f";
    public static final String COLOR_STRESS = "§x§f§f§f§f§8§8";
    public static final String COLOR_ACTION = "§x§f§f§8§8§0§0";
    public static final String COLOR_INITIATIVE = "§x§0§0§8§8§f§f";
    public static final String COLOR_PASSIVE = "§x§0§0§f§f§8§8";
    public static final String COLOR_NUMBER = "§x§f§f§8§8§f§f";
    public static final String COLOR_POSITIVE = "§x§8§8§f§f§8§8";
    public static final String COLOR_NEGATIVE = "§x§f§f§8§8§8§8";
    public static final String COLOR_CONCEAL = "§x§8§8§8§8§8§8";
    public static final String COLOR_INPUT = "§9";
    public static final String COLOR_OUTPUT = "§6";
    public static final String BLACK = color("&0");
    public static final String DARK_BLUE = color("&1");
    public static final String DARK_GREEN = color("&2");
    public static final String DARK_AQUA = color("&3");
    public static final String DARK_RED = color("&4");
    public static final String DARK_PURPLE = color("&5");
    public static final String GOLD = color("&6");
    public static final String GRAY = color("&7");
    public static final String DARK_GRAY = color("&8");
    public static final String BLUE = color("&9");
    public static final String GREEN = color("&a");
    public static final String AQUA = color("&b");
    public static final String RED = color("&c");
    public static final String LIGHT_PURPLE = color("&d");
    public static final String YELLOW = color("&e");
    public static final String WHITE = color("&f");
    public static final String OBFUSCATED = color("&k");
    public static final String BOLD = color("&l");
    public static final String STRIKETHROUGH = color("&m");
    public static final String UNDERLINE = color("&n");
    public static final String ITALIC = color("&o");
    public static final String RESET = color("&r");
    public static final String MAGIC = OBFUSCATED;

    public static final Color WHITE_COLOR = Color.fromRGB(255, 255, 255);

    private static long COUNT = 0;

    @NotNull public static String colorString(@NotNull String string0, @NotNull List<Color> colorList) {
        StringBuilder stringBuilder = new StringBuilder();
        if (string0.isEmpty()) {
            string0 += " ";
        }
        if (string0.length() == 1) {
            string0 += " ";
        }
        String string = string0.replaceAll("%s", PLACEHOLDER);
        for (int i = 0, length = string.length() - 1; i <= length; i++) {
            double p = ((double) i) / length * (colorList.size() - 1);
            Color color1 = colorList.get((int) Math.floor(p));
            Color color2 = colorList.get((int) Math.ceil(p));
            int blue = (int) (color1.getBlue() * (1 - p + Math.floor(p)) + color2.getBlue() * (p - Math.floor(p)));
            int green = (int) (color1.getGreen() * (1 - p + Math.floor(p)) + color2.getGreen() * (p - Math.floor(p)));
            int red = (int) (color1.getRed() * (1 - p + Math.floor(p)) + color2.getRed() * (p - Math.floor(p)));
            stringBuilder
                    .append("§x")
                    .append("§")
                    .append(TextUtil.codeColor(red / 16))
                    .append("§")
                    .append(TextUtil.codeColor(red % 16))
                    .append("§")
                    .append(TextUtil.codeColor(green / 16))
                    .append("§")
                    .append(TextUtil.codeColor(green % 16))
                    .append("§")
                    .append(TextUtil.codeColor(blue / 16))
                    .append("§")
                    .append(TextUtil.codeColor(blue % 16));
            stringBuilder.append(string.charAt(i));
        }

        String re = stringBuilder.toString();
        re = re.replaceAll(PLACEHOLDER, "%s");
        return re;
    }

    @NotNull public static String colorRandomString(@NotNull String string) {
        List<Color> colorList = new ArrayList<>();
        double r = 1;
        while (1 / r >= Math.random() && r * r <= string.length()) {
            int red = (int) ((Math.random() * 8 + 8) * 15 + Math.random() * 12 + 4);
            int green = (int) ((Math.random() * 8 + 8) * 15 + Math.random() * 12 + 4);
            int blue = (int) ((Math.random() * 8 + 8) * 15 + Math.random() * 12 + 4);
            colorList.add(Color.fromRGB(red, green, blue));
            r++;
        }
        return TextUtil.colorString(string, colorList);
    }

    @NotNull public static String colorPseudorandomString(@NotNull String string) {
        List<Color> colorList = new ArrayList<>();
        double r = 1;
        Random random = new Random(string.hashCode() / 2
                + Networks.getInstance().getServer().getName().hashCode() / 2);
        while (1 / r >= random.nextDouble() && r * r <= string.length()) {
            int red = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int green = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int blue = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            colorList.add(Color.fromRGB(red, green, blue));
            r++;
        }

        return TextUtil.colorString(string, colorList);
    }

    @NotNull public static String colorPseudorandomString(@NotNull String string0, long seed) {
        List<Color> colorList = new ArrayList<>();
        double r = 1;
        String string = string0.replaceAll("%s", PLACEHOLDER);
        Random random = new Random(string.hashCode() / 2 + seed / 2);
        while (1 / r >= random.nextDouble() && r * r <= string.length()) {
            int red = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int green = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int blue = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            colorList.add(Color.fromRGB(red, green, blue));
            r++;
        }
        return TextUtil.colorString(string, colorList);
    }

    public static @NotNull String getRandomColor() {
        return "§x" + "§"
                + (TextUtil.codeColor((int) (Math.random() * 8) + 8)) + "§"
                + (TextUtil.codeColor((int) (Math.random() * 8) + 8)) + "§"
                + (TextUtil.codeColor((int) (Math.random() * 8) + 8)) + "§"
                + (TextUtil.codeColor((int) (Math.random() * 8) + 8)) + "§"
                + (TextUtil.codeColor((int) (Math.random() * 8) + 8)) + "§"
                + (TextUtil.codeColor((int) (Math.random() * 8) + 8));
    }

    public static @NotNull String getPseudorandomColor(long seed) {
        COUNT += seed;
        Random random = new Random(COUNT);
        return "§x" + "§"
                + (TextUtil.codeColor(random.nextInt(8) + 8)) + "§"
                + (TextUtil.codeColor(random.nextInt(8) + 8)) + "§"
                + (TextUtil.codeColor(random.nextInt(8) + 8)) + "§"
                + (TextUtil.codeColor(random.nextInt(8) + 8)) + "§"
                + (TextUtil.codeColor(random.nextInt(8) + 8)) + "§"
                + (TextUtil.codeColor(random.nextInt(8) + 8));
    }

    @NotNull public static String toTextCode(@NotNull Color color) {
        return "§x" + "§" + TextUtil.codeColor(color.getRed() / 16) + "§" + TextUtil.codeColor(color.getRed() % 16)
                + "§" + TextUtil.codeColor(color.getGreen() / 16) + "§" + TextUtil.codeColor(color.getGreen() % 16)
                + "§" + TextUtil.codeColor(color.getBlue() / 16) + "§" + TextUtil.codeColor(color.getBlue() % 16);
    }

    @NotNull public static String codeColor(int c) {
        if (c < 10 && c >= 0) {
            return String.valueOf(c);
        }
        return switch (c) {
            case 10 -> "a";
            case 11 -> "b";
            case 12 -> "c";
            case 13 -> "d";
            case 14 -> "e";
            case 15 -> "f";
            default -> "0";
        };
    }

    @NotNull public static Color cloneColor(@NotNull Color color) {
        return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color @NotNull [] disperse(int size, Color @NotNull ... colors) {
        if (size == 1 && colors.length > 0) {
            return new Color[] {TextUtil.cloneColor(colors[0])};
        } else if (size == 0 || colors.length == 0) {
            return new Color[0];
        }
        Color[] result = new Color[size--];
        for (int i = 0; i <= size; i++) {
            double p = ((double) i) / size * (colors.length - 1);

            int r = (int) (colors[(int) Math.floor(p)].getRed() * (1 - p + Math.floor(p))
                    + colors[(int) Math.ceil(p)].getRed() * (p - Math.floor(p)));
            int g = (int) (colors[(int) Math.floor(p)].getGreen() * (1 - p + Math.floor(p))
                    + colors[(int) Math.ceil(p)].getGreen() * (p - Math.floor(p)));
            int b = (int) (colors[(int) Math.floor(p)].getBlue() * (1 - p + Math.floor(p))
                    + colors[(int) Math.ceil(p)].getBlue() * (p - Math.floor(p)));

            result[i] = Color.fromRGB(r, g, b);
        }
        return result;
    }

    public static Color @NotNull [] disperse(int size, @NotNull List<Color> colorList) {
        if (size == 1 && !colorList.isEmpty()) {
            return new Color[] {TextUtil.cloneColor(colorList.get(0))};
        } else if (size == 0 || colorList.isEmpty()) {
            return new Color[0];
        }
        Color[] result = new Color[size--];
        for (int i = 0; i <= size; i++) {
            double p = ((double) i) / size * (colorList.size() - 1);

            int r = (int) (colorList.get((int) Math.floor(p)).getRed() * (1 - p + Math.floor(p))
                    + colorList.get((int) Math.ceil(p)).getRed() * (p - Math.floor(p)));
            int g = (int) (colorList.get((int) Math.floor(p)).getGreen() * (1 - p + Math.floor(p))
                    + colorList.get((int) Math.ceil(p)).getGreen() * (p - Math.floor(p)));
            int b = (int) (colorList.get((int) Math.floor(p)).getBlue() * (1 - p + Math.floor(p))
                    + colorList.get((int) Math.ceil(p)).getBlue() * (p - Math.floor(p)));

            result[i] = Color.fromRGB(r, g, b);
        }
        return result;
    }

    public static @NotNull String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static @NotNull String stripColor(String s) {
        return ChatColor.stripColor(s);
    }
}
