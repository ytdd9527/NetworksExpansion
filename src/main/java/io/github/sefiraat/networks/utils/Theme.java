package io.github.sefiraat.networks.utils;

import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
@Getter
public enum Theme {
    GOLD(ChatColor.GOLD, Lang.getString("theme.gold")),
    WHITE(ChatColor.WHITE, Lang.getString("theme.white")),
    AQUA(ChatColor.AQUA, Lang.getString("theme.aqua")),
    WARNING(ChatColor.YELLOW, Lang.getString("theme.warning")),
    ERROR(ChatColor.RED, Lang.getString("theme.error")),
    NOTICE(ChatColor.WHITE, Lang.getString("theme.notice")),
    PASSIVE(ChatColor.GRAY, Lang.getString("theme.passive")),
    SUCCESS(ChatColor.GREEN, Lang.getString("theme.success")),
    MAIN(ChatColor.of("#21588f"), Lang.getString("theme.main")),
    CLICK_INFO(ChatColor.of("#e4ed32"), Lang.getString("theme.click_info")),
    RESEARCH(ChatColor.of("#a60e03"), Lang.getString("theme.research")),
    CRAFTING(ChatColor.of("#dbcea9"), Lang.getString("theme.crafting")),
    MACHINE(ChatColor.of("#3295a8"), Lang.getString("theme.machine")),

    TOOL(ChatColor.of("#6b32a8"), Lang.getString("theme.tool")),
    MECHANISM(ChatColor.of("#3295a8"), Lang.getString("theme.mechanism")),
    FUEL(ChatColor.of("#112211"), Lang.getString("theme.fuel")),
    MATERIAL_CLASS(ChatColor.of("#a4c2ba"), Lang.getString("theme.material_class")),
    RECIPE_TYPE(ChatColor.of("#ffe89c"), Lang.getString("theme.recipe_type")),

    GUIDE(ChatColor.of("#444444"), Lang.getString("theme.guide"));

    private static final Theme[] cachedValues = values();
    private final @NotNull ChatColor color;
    private final @NotNull String loreLine;

    @ParametersAreNonnullByDefault
    Theme(ChatColor color, String loreLine) {
        this.color = color;
        this.loreLine = loreLine;
    }

    /**
     * Gets a SlimefunItemStack with a pre-populated lore and name with themed colors.
     *
     * @param id        The ID for the new {@link SlimefunItemStack}
     * @param itemStack The vanilla {@link ItemStack} used to base the {@link SlimefunItemStack} on
     * @param themeType The {@link Theme} {@link ChatColor} to apply to the {@link SlimefunItemStack} name
     * @param name      The name to apply to the {@link SlimefunItemStack}
     * @param lore      The lore lines for the {@link SlimefunItemStack}. Lore is book-ended with empty strings.
     * @return Returns the new {@link SlimefunItemStack}
     */
    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack themedSlimefunItemStack(
            String id, ItemStack itemStack, Theme themeType, String name, String... lore) {
        ChatColor passiveColor = Theme.PASSIVE.getColor();
        List<String> finalLore = new ArrayList<>();
        finalLore.add("");
        for (String s : lore) {
            finalLore.add(passiveColor + s);
        }
        finalLore.add("");
        finalLore.add(applyThemeToString(Theme.CLICK_INFO, themeType.getLoreLine()));
        return new SlimefunItemStack(
                id,
                itemStack,
                Theme.applyThemeToString(themeType, name),
                finalLore.toArray(new String[finalLore.size() - 1]));
    }

    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack themedSlimefunItemStack(
            String id, String texture, Theme themeType, String name, String... lore) {
        ChatColor passiveColor = Theme.PASSIVE.getColor();
        List<String> finalLore = new ArrayList<>();
        finalLore.add("");
        for (String s : lore) {
            finalLore.add(passiveColor + s);
        }
        finalLore.add("");
        finalLore.add(applyThemeToString(Theme.CLICK_INFO, themeType.getLoreLine()));
        return new SlimefunItemStack(
                id,
                texture,
                Theme.applyThemeToString(themeType, name),
                finalLore.toArray(new String[finalLore.size() - 1]));
    }

    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack themedSlimefunItemStack(SlimefunItemStack sfis, Theme themeType) {
        String id = sfis.getItemId();
        ItemStack itemStack = ItemStackUtil.getCleanItem(sfis);
        String name = sfis.getDisplayName();
        ItemMeta meta = sfis.getItemMeta();
        List<String> lore = meta == null ? new ArrayList<>() : meta.getLore();
        return themedSlimefunItemStack(
                id,
                itemStack,
                themeType,
                name == null ? Lang.getString("theme.name_not_found") : name,
                lore == null ? new String[] {Lang.getString("theme.lore_not_found")} : lore.toArray(new String[0]));
    }

    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack tsItem(
            String id, ItemStack itemStack, Theme themeType, String name, String... lore) {
        List<String> finalLore = new ArrayList<>(Arrays.stream(lore).toList());
        finalLore.add("");
        finalLore.add(applyThemeToString(Theme.SUCCESS, themeType.getLoreLine()));
        return new SlimefunItemStack(
                id,
                itemStack,
                Theme.applyThemeToString(themeType, name),
                finalLore.toArray(new String[finalLore.size() - 1]));
    }

    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack Random(
            String id, ItemStack itemStack, Theme themeType, String name, String... lore) {
        String coloredName = TextUtil.colorPseudorandomString(name);
        ChatColor passiveColor = Theme.PASSIVE.getColor();
        List<String> finalLore = new ArrayList<>();
        finalLore.add("");
        for (String s : lore) {
            finalLore.add(passiveColor + s);
        }
        finalLore.add(applyThemeToString(Theme.SUCCESS, themeType.getLoreLine()));
        return new SlimefunItemStack(id, itemStack, coloredName, finalLore.toArray(new String[0]));
    }

    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack Random(SlimefunItemStack sfis, Theme themeType) {
        String id = sfis.getItemId();
        ItemStack itemStack = ItemStackUtil.getCleanItem(sfis);
        String name = sfis.getDisplayName();
        ItemMeta meta = sfis.getItemMeta();
        List<String> lore = meta == null ? new ArrayList<>() : meta.getLore();
        return Random(
                id,
                itemStack,
                themeType,
                name == null ? Lang.getString("theme.name_not_found") : name,
                lore == null ? new String[] {Lang.getString("theme.lore_not_found")} : lore.toArray(new String[0]));
    }

    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack Random(String id, String texture, Theme themeType, String name, String... lore) {
        String coloredName = TextUtil.colorPseudorandomString(name);
        ChatColor passiveColor = Theme.PASSIVE.getColor();
        List<String> finalLore = new ArrayList<>();
        finalLore.add("");
        for (String s : lore) {
            finalLore.add(passiveColor + s);
        }
        finalLore.add(applyThemeToString(Theme.SUCCESS, themeType.getLoreLine()));
        return new SlimefunItemStack(id, texture, coloredName, finalLore.toArray(new String[0]));
    }

    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack model(
            String id, ItemStack itemStack, Theme themeType, String name, String... lore) {
        String coloredName = TextUtil.colorPseudorandomString(name);
        ChatColor passiveColor = Theme.PASSIVE.getColor();
        List<String> finalLore = new ArrayList<>();
        finalLore.add("");
        for (String s : lore) {
            finalLore.add(passiveColor + s);
        }
        finalLore.add(applyThemeToString(Theme.SUCCESS, themeType.getLoreLine()));
        return new SlimefunItemStack(
                id, itemStack, coloredName + Lang.getString("theme.model"), finalLore.toArray(new String[0]));
    }

    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack model(String id, String texture, Theme themeType, String name, String... lore) {
        String coloredName = TextUtil.colorPseudorandomString(name);
        ChatColor passiveColor = Theme.PASSIVE.getColor();
        List<String> finalLore = new ArrayList<>();
        finalLore.add("");
        for (String s : lore) {
            finalLore.add(passiveColor + s);
        }
        finalLore.add(applyThemeToString(Theme.SUCCESS, themeType.getLoreLine()));
        return new SlimefunItemStack(
                id,
                texture,
                coloredName + Lang.getString("theme.model"),
                finalLore.toArray(new String[finalLore.size() - 1]));
    }

    @NotNull @ParametersAreNonnullByDefault
    public static SlimefunItemStack model(SlimefunItemStack sfis, Theme themeType) {
        String id = sfis.getItemId();
        AtomicReference<String> texture = new AtomicReference<>("");
        sfis.getSkullTexture().ifPresentOrElse(texture::set, () -> {});
        String name = sfis.getDisplayName();
        ItemMeta meta = sfis.getItemMeta();
        List<String> lore = meta == null ? new ArrayList<>() : meta.getLore();
        return model(
                id,
                texture.get(),
                themeType,
                name == null ? Lang.getString("theme.name_not_found") : name,
                lore == null ? new String[] {Lang.getString("theme.lore_not_found")} : lore.toArray(new String[0]));
    }

    /**
     * Applies the theme color to a given string
     *
     * @param themeType The {@link Theme} to apply the color from
     * @param string    The string to apply the color to
     * @return Returns the string provides preceded by the color
     */
    @NotNull @ParametersAreNonnullByDefault
    public static String applyThemeToString(Theme themeType, String string) {
        return themeType.getColor() + string;
    }

    /**
     * Gets an ItemStack with a pre-populated lore and name with themed colors.
     *
     * @param material  The {@link Material} used to base the {@link ItemStack} on
     * @param themeType The {@link Theme} {@link ChatColor} to apply to the {@link ItemStack} name
     * @param name      The name to apply to the {@link ItemStack}
     * @param lore      The lore lines for the {@link ItemStack}. Lore is book-ended with empty strings.
     * @return Returns the new {@link ItemStack}
     */
    @NotNull @ParametersAreNonnullByDefault
    public static ItemStack themedItemStack(Material material, Theme themeType, String name, String... lore) {
        ChatColor passiveColor = Theme.PASSIVE.getColor();
        List<String> finalLore = new ArrayList<>();
        finalLore.add("");
        for (String s : lore) {
            finalLore.add(passiveColor + s);
        }
        finalLore.add("");
        finalLore.add(applyThemeToString(Theme.CLICK_INFO, themeType.getLoreLine()));
        return ItemStackUtil.getCleanItem(new CustomItemStack(
                material,
                Theme.applyThemeToString(themeType, name),
                finalLore.toArray(new String[finalLore.size() - 1])));
    }

    @NotNull @ParametersAreNonnullByDefault
    public static ItemStack themedItemStack(ItemStack itemStack, Theme themeType) {
        String name = ItemStackHelper.getDisplayName(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        ChatColor passiveColor = Theme.PASSIVE.getColor();
        List<String> finalLore = new ArrayList<>();
        finalLore.add("");
        for (String s : lore) {
            finalLore.add(passiveColor + s);
        }
        finalLore.add("");
        finalLore.add(applyThemeToString(Theme.CLICK_INFO, themeType.getLoreLine()));
        return ItemStackUtil.getCleanItem(
                new CustomItemStack(itemStack.getType(), Theme.applyThemeToString(themeType, name), finalLore));
    }

    @NotNull public Particle.DustOptions getDustOptions(float size) {
        return new Particle.DustOptions(
                Color.fromRGB(
                        color.getColor().getRed(),
                        color.getColor().getGreen(),
                        color.getColor().getBlue()),
                size);
    }

    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum class should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return this.color.toString();
    }
}
