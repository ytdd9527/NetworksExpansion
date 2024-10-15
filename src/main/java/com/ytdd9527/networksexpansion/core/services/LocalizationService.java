package com.ytdd9527.networksexpansion.core.services;

import com.balugaq.netex.api.data.Language;
import com.google.common.base.Preconditions;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class LocalizationService {
    private static final String KEY_NAME = ".name";
    private static final String KEY_LORE = ".lore";
    private static final String MSG_KEY_NULL = "key cannot be null";
    private static final String MSG_ID_NULL = "id cannot be null";
    private static final String MSG_MATERIAL_NULL = "Material cannot be null";
    private static final String MSG_ITEMSTACK_NULL = "ItemStack cannot be null";
    private static final String MSG_TEXTURE_NULL = "Texture cannot be null";
    private final JavaPlugin plugin;
    private final String langFolderName;
    private final File langFolder;
    private final List<String> languages;
    private final Map<String, Language> langMap;
    @Getter
    private String idPrefix = "";
    private String itemGroupKey = "categories";
    private String itemsKey = "items";
    private String recipesKey = "recipes";
    private String colorTagRegex = "<[a-zA-Z0-9_]+>";
    private Pattern pattern = Pattern.compile(this.colorTagRegex);

    public LocalizationService(Networks plugin) {
        this(plugin.getJavaPlugin());
    }

    @ParametersAreNonnullByDefault
    public LocalizationService(JavaPlugin plugin) {
        this(plugin, "lang");
    }

    @ParametersAreNonnullByDefault
    public LocalizationService(JavaPlugin plugin, String folderName) {
        this.languages = new LinkedList<>();
        this.langMap = new LinkedHashMap<>();
        Preconditions.checkArgument(plugin != null, "The plugin instance should not be null");
        Preconditions.checkArgument(folderName != null, "The folder name should not be null");
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        this.langFolderName = folderName;
        this.langFolder = new File(plugin.getDataFolder(), folderName);
        if (!this.langFolder.exists()) {
            this.langFolder.mkdir();
        }

    }
    @ParametersAreNonnullByDefault
    public LocalizationService(JavaPlugin plugin, String folderName, String langFile) {
        this(plugin, folderName);
        this.addLanguage(langFile);
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public String getString(String key, Object... args) {
        return MessageFormat.format(getString(key), args);
    }

    @ParametersAreNonnullByDefault
    public void sendMessage(CommandSender sender, String messageKey, Object... args) {
        Preconditions.checkArgument(sender != null, "CommandSender cannot be null");
        Preconditions.checkArgument(messageKey != null, "Message key cannot be null");

        send(sender, MessageFormat.format(getString("messages." + messageKey), args));
    }

    @ParametersAreNonnullByDefault
    public void sendActionbarMessage(Player p, String messageKey, Object... args) {
        Preconditions.checkArgument(p != null, "Player cannot be null");
        Preconditions.checkArgument(messageKey != null, "Message key cannot be null");

        String message = MessageFormat.format(getString("messages." + messageKey), args);

        BaseComponent[] components = TextComponent.fromLegacyText(color(message));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
    }

    public final void addLanguage(@Nonnull String langFilename) {
        Preconditions.checkArgument(langFilename != null, "The language file name should not be null");
        File langFile = new File(this.langFolder, langFilename + ".yml");
        String resourcePath = this.langFolderName + "/" + langFilename + ".yml";
        if (!langFile.exists()) {
            try {
                this.plugin.saveResource(resourcePath, false);
            } catch (IllegalArgumentException var6) {
                this.plugin.getLogger().log(Level.SEVERE, "The default language file {0} does not exist in jar file!", resourcePath);
                return;
            }
        }

        this.languages.add(langFilename);
        InputStreamReader defaultReader = new InputStreamReader(this.plugin.getResource(resourcePath), StandardCharsets.UTF_8);
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultReader);
        this.langMap.put(langFilename, new Language(langFilename, langFile, defaultConfig));
    }
    @Nonnull
    public String getString0(@Nonnull String path) {
        Preconditions.checkArgument(path != null, "path cannot be null");
        Iterator<String> var2 = this.languages.iterator();

        String localization;
        do {
            if (!var2.hasNext()) {
                Networks.getInstance().getLogger().severe("No localization found for path: " + path);
                return path;
            }

            String lang = (String) var2.next();
            localization = ((Language) this.langMap.get(lang)).getLang().getString(path);
        } while (localization == null);

        return localization;
    }

    @Nonnull
    public List<String> getStringList(@Nonnull String path) {
        Preconditions.checkArgument(path != null, "path cannot be null");
        Iterator<String> var2 = this.languages.iterator();

        List<String> localization;
        do {
            if (!var2.hasNext()) {
                return new ArrayList<>();
            }

            String lang = (String) var2.next();
            localization = ((Language) this.langMap.get(lang)).getLang().getStringList(path);
        } while (localization.isEmpty());

        return localization;
    }

    @Nonnull
    public String[] getStringArray(@Nonnull String path) {
        return (String[]) this.getStringList(path).stream().map(this::color).toList().toArray(new String[0]);
    }

    protected JavaPlugin getPlugin() {
        return this.plugin;
    }

    @Nonnull
    public String getString(@Nonnull String path) {
        return color(this.getString0(path));
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemBy(String key, String id, Material material, String... extraLore) {
        Preconditions.checkArgument(key != null, MSG_KEY_NULL);
        Preconditions.checkArgument(id != null, MSG_ID_NULL);
        Preconditions.checkArgument(material != null, MSG_MATERIAL_NULL);
        SlimefunItemStack item = new SlimefunItemStack((this.idPrefix + id).toUpperCase(Locale.ROOT), material, this.getString(key + "." + id + KEY_NAME), this.getStringArray(key + "." + id + KEY_LORE));
        if (extraLore != null && extraLore.length != 0) {
            appendLore(item, extraLore);
        }
        return item;
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemBy(String key, String id, String texture, String... extraLore) {
        Preconditions.checkArgument(key != null, MSG_KEY_NULL);
        Preconditions.checkArgument(id != null, MSG_ID_NULL);
        Preconditions.checkArgument(texture != null, MSG_TEXTURE_NULL);
        return (SlimefunItemStack) appendLore(new SlimefunItemStack((this.idPrefix + id).toUpperCase(Locale.ROOT), texture, this.getString(key + "." + id + ".name"), this.getStringArray(key + "." + id + ".lore")), extraLore);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemBy(String key, String id, ItemStack itemStack, String... extraLore) {
        Preconditions.checkArgument(key != null, MSG_KEY_NULL);
        Preconditions.checkArgument(id != null, MSG_ID_NULL);
        Preconditions.checkArgument(itemStack != null, MSG_ITEMSTACK_NULL);
        return (SlimefunItemStack) appendLore(new SlimefunItemStack((this.idPrefix + id).toUpperCase(Locale.ROOT), itemStack, this.getString(key + "." + id + ".name"), this.getStringArray(key + "." + id + ".lore")), extraLore);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemGroupItem(String id, Material material) {
        return this.getItemBy(this.itemGroupKey, id, material);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemGroupItem(String id, String texture) {
        return this.getItemBy(this.itemGroupKey, id, texture);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItemGroupItem(String id, ItemStack itemStack) {
        return this.getItemBy(this.itemGroupKey, id, itemStack);
    }

    public SlimefunItemStack getItem(String id, Material material, String... extraLore) {
        return this.getItemBy(this.itemsKey, id, material, extraLore);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItem(String id, String texture, String... extraLore) {
        return this.getItemBy(this.itemsKey, id, texture, extraLore);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public SlimefunItemStack getItem(String id, ItemStack itemStack, String... extraLore) {
        return this.getItemBy(this.itemsKey, id, itemStack, extraLore);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public RecipeType getRecipeType(String id, Material material, String... extraLore) {
        return new RecipeType(new NamespacedKey(this.getPlugin(), id), this.getItemBy(this.recipesKey, id, material, extraLore));
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public RecipeType getRecipeType(String id, String texture, String... extraLore) {
        return new RecipeType(new NamespacedKey(this.getPlugin(), id), this.getItemBy(this.recipesKey, id, texture, extraLore));
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public RecipeType getRecipeType(String id, ItemStack itemStack, String... extraLore) {
        return new RecipeType(new NamespacedKey(this.getPlugin(), id), this.getItemBy(this.recipesKey, id, itemStack, extraLore));
    }

    public void setIdPrefix(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    public void setItemGroupKey(String itemGroupKey) {
        this.itemGroupKey = itemGroupKey;
    }

    public void setItemsKey(String itemsKey) {
        this.itemsKey = itemsKey;
    }

    public void setRecipesKey(String recipesKey) {
        this.recipesKey = recipesKey;
    }
    private <T extends ItemStack> T appendLore(@Nonnull T itemStack, @Nullable String... extraLore) {
        Preconditions.checkArgument(itemStack != null, MSG_ITEMSTACK_NULL);
        if (extraLore != null && extraLore.length != 0) {
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList();
            ((List)lore).addAll(color(Arrays.asList(extraLore)));
            meta.setLore((List)lore);
            itemStack.setItemMeta(meta);
            return itemStack;
        } else {
            return itemStack;
        }
    }

    @Nonnull
    public String color(@Nonnull String str) {
        str = ChatColor.translateAlternateColorCodes('&', str);
        if (str.startsWith("<random_color>")) {
            str = str.replaceAll("<random_color>", "");
            str = TextUtil.colorPseudorandomString(str);
        }

        if (str.startsWith("<color_random_string>")) {
            str = str.replaceAll("<color_random_string>", "");
            str = TextUtil.colorRandomString(str);
        }

        Matcher matcher = this.pattern.matcher(str);
        while (matcher.find()) {
            boolean found = false;
            String colorCode = matcher.group();
            for (Theme testTheme : Theme.values()) {
                String name = testTheme.name();
                if (name.equalsIgnoreCase(colorCode.replace("<", "").replace(">", ""))) {
                    str = str.replaceAll(colorCode, testTheme + "");
                    found = true;
                    break;
                }
            }
            if (!found) {
                Networks.getInstance().getLogger().warning("Unknown color code: " + colorCode);
                str = str.replaceAll(colorCode, "");
            }
            matcher = this.pattern.matcher(str);
        }

        return str;
    }

    @Nonnull
    public List<String> color(@Nonnull List<String> strList) {
        Preconditions.checkArgument(strList != null, "String list cannot be null");
        return (List)strList.stream().map(this::color).collect(Collectors.toList());
    }

    @ParametersAreNonnullByDefault
    public void send(CommandSender sender, String message, Object... args) {
        sender.sendMessage(color(MessageFormat.format(message, args)));
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public ItemStack getItemStack(String key, Material material) {
        return ItemStackUtil.getCleanItem(new CustomItemStack(material, this.getString(key + KEY_NAME), this.getStringArray(key + KEY_LORE)));
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public ItemStack getIcon(String key, Material material) {
        return getItemStack("icons." + key, material);
    }
}