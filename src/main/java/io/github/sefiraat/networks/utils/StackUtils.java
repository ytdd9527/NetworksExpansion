package io.github.sefiraat.networks.utils;

import com.balugaq.netex.api.enums.MinecraftVersion;
import com.ytdd9527.networksexpansion.utils.itemstacks.ItemStackUtil;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.stackcaches.ItemStackCache;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.inventory.meta.OminousBottleMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.ShieldMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.bukkit.inventory.meta.WritableBookMeta;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("deprecation")
@UtilityClass
@NullMarked
public class StackUtils {
    private static final boolean FORCE_CHECK_LORE = Networks.getConfigManager().isForceCheckLore();
    private static final MinecraftVersion MC_VERSION = Networks.getInstance().getMCVersion();
    public static final boolean IS_1_20_5 = MC_VERSION.isAtLeast(MinecraftVersion.V1_20_5);
    public static final boolean IS_1_21 = MC_VERSION.isAtLeast(MinecraftVersion.V1_21);
    public static final boolean IS_1_21_3 = MC_VERSION.isAtLeast(MinecraftVersion.V1_21_3);
    public static final boolean IS_1_21_4 = MC_VERSION.isAtLeast(MinecraftVersion.V1_21_4);

    /**
     * 把历史上 {@code itemsMatch} 的「check」参数翻译成 {@link MatchOption} 的「ignore」位。
     * <p>
     * check 与 ignore 互为反义：{@code checkLore = false}（不比较 lore）即 {@code ignoreLore = true}。
     * 因此这里对 {@code false} 的调用方保持逐位等价，{@code true} 的调用方则额外打开强制比较 lore 的开关，
     * 使 {@code shouldCompareLore} 的短路语义不丢失。
     */
    private static MatchOption toOption(
        boolean checkLore, boolean checkAmount, boolean checkCustomModelId) {
        long flags                      = MatchOption.NO_IGNORE;
        if (!checkLore)          flags |= MatchOption.IGNORE_LORE;
        if (!checkCustomModelId) flags |= MatchOption.IGNORE_CUSTOM_MODEL_DATA;
        if (!checkAmount)        flags |= MatchOption.IGNORE_AMOUNT;
        return MatchOption.of(flags);
    }

    public static ItemStack getAsQuantity(@Nullable ItemStack itemStack, int amount) {
        if (itemStack == null) return ItemStackUtil.AIR;

        ItemStack clone = itemStack.clone();
        clone.setAmount(amount);
        return clone;
    }

    public static boolean itemsMatch(
        @Nullable ItemStack itemStack1,
        @Nullable ItemStack itemStack2,
        boolean checkLore,
        boolean checkAmount,
        boolean checkCustomModelId) {
        return itemsMatch(
            new ItemStackCache(itemStack1), itemStack2, toOption(checkLore, checkAmount, checkCustomModelId));
    }

    public static boolean itemsMatch(
        @Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, boolean checkLore, boolean checkAmount) {
        return itemsMatch(new ItemStackCache(itemStack1), itemStack2, toOption(checkLore, checkAmount, true));
    }

    public static boolean itemsMatch(
        @Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, boolean checkLore) {
        return itemsMatch(new ItemStackCache(itemStack1), itemStack2, toOption(checkLore, false, true));
    }

    public static boolean itemsMatch(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
        return itemsMatch(new ItemStackCache(itemStack1), itemStack2, MatchOption.DEFAULT);
    }

    public static boolean itemsMatch(
        ItemStackCache cache, @Nullable ItemStack itemStack, boolean checkLore, boolean checkAmount) {
        return itemsMatch(cache, itemStack, toOption(checkLore, checkAmount, true));
    }

    public static boolean itemsMatch(ItemStackCache cache, @Nullable ItemStack itemStack, boolean checkLore) {
        return itemsMatch(cache, itemStack, toOption(checkLore, false, true));
    }

    public static boolean itemsMatch(ItemStackCache cache, @Nullable ItemStack itemStack) {
        return itemsMatch(cache, itemStack, MatchOption.DEFAULT);
    }

    public static boolean itemsMatch(
        @Nullable ItemStack itemStack,
        ItemStackCache cache,
        boolean checkLore,
        boolean checkAmount,
        boolean checkCustomModelId) {
        return itemsMatch(cache, itemStack, toOption(checkLore, checkAmount, checkCustomModelId));
    }

    public static boolean itemsMatch(
        @Nullable ItemStack itemStack, ItemStackCache cache, boolean checkLore, boolean checkAmount) {
        return itemsMatch(cache, itemStack, toOption(checkLore, checkAmount, true));
    }

    public static boolean itemsMatch(@Nullable ItemStack itemStack, ItemStackCache cache, boolean checkLore) {
        return itemsMatch(cache, itemStack, toOption(checkLore, false, true));
    }

    public static boolean itemsMatch(@Nullable ItemStack itemStack, ItemStackCache cache) {
        return itemsMatch(cache, itemStack, MatchOption.DEFAULT);
    }

    /**
     * Checks if items match each other, checks go in order from lightest to heaviest
     *
     * @param cache     The cached {@link ItemStack} to compare against
     * @param itemStack The {@link ItemStack} being evaluated
     * @param option    The {@link MatchOption} describing what to ignore, null means {@link MatchOption#DEFAULT}
     *                  (which does not ignore anything)
     * @return True if items match
     */
    public static boolean itemsMatch(
        ItemStackCache cache, @Nullable ItemStack itemStack, @Nullable MatchOption option) {
        final MatchOption matchOption = option == null ? MatchOption.DEFAULT : option;
        // Ignore options
        final boolean ignoreAmount = matchOption.isIgnoreAmount();

        // Null check
        if (cache.getItemStack() == null || itemStack == null) {
            return itemStack == null && cache.getItemStack() == null;
        }

        // If types do not match, then the items cannot possibly match
        if (itemStack.getType() != cache.getItemType()) {
            return false;
        }

        // If amounts do not match, then the items cannot possibly match
        if (!matchOption.isIgnoreAmount() && itemStack.getAmount() > cache.getItemStack().getAmount()) {
            return false;
        }

        if (isBlacklisted(itemStack) || isBlacklisted(cache.getItemStack())) {
            return false;
        }

        if (Networks.getConfigManager().useBukkitItemComparison()) {
            return itemStack.isSimilar(cache.getItemStack());
        }

        // Use DataComponent API
        if (IS_1_21_4) {
            return itemsMatchModern(ItemStackUtil.asCraftItemStack(cache.getItemStack()), ItemStackUtil.asCraftItemStack(itemStack), matchOption);
        }

        return legacyItemsMatch(cache, itemStack, matchOption);
    }

    private static boolean legacyItemsMatch(ItemStackCache cache, ItemStack itemStack, MatchOption matchOption) {
        final boolean ignoreDurability = matchOption.isIgnoreDurability();
        final boolean ignoreEnchantment = matchOption.isIgnoreEnchantment();
        final boolean ignoreAttribute = matchOption.isIgnoreAttribute();
        final boolean ignoreLore = matchOption.isIgnoreLore();
        final boolean ignoreCustomModelData = matchOption.isIgnoreCustomModelData();
        // below 1.21.4
        // If either item has no custom data components then either a mismatch or both without components = vanilla
        final boolean itemHasComponents = ItemStackUtil.hasCustomComponents(itemStack);
        final boolean cachedHasComponents = ItemStackUtil.hasCustomComponents(cache.getItemStack());
        if (!itemHasComponents || !cachedHasComponents) {
            return itemHasComponents == cachedHasComponents;
        }

        // Now we need to compare meta's directly - cache is already out, but let's fetch the 2nd meta also
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final ItemMeta cachedMeta = cache.getItemMeta();

        if (itemMeta == null || cachedMeta == null) {
            return itemMeta == cachedMeta;
        }

        // ItemMetas are different types and cannot match
        if (!itemMeta.getClass().equals(cachedMeta.getClass())) {
            return false;
        }

        // Quick meta-extension escapes
        if (canQuickEscapeMetaVariant(itemMeta, cachedMeta, ignoreDurability)) {
            return false;
        }

        // Has a display name (checking the name occurs later)
        if (itemMeta.hasDisplayName() != cachedMeta.hasDisplayName()) {
            return false;
        }

        // PDCs don't match
        if (!itemMeta.getPersistentDataContainer().equals(cachedMeta.getPersistentDataContainer())) {
            return false;
        }

        // Make sure enchantments match
        if (!ignoreEnchantment && !itemMeta.getEnchants().equals(cachedMeta.getEnchants())) {
            return false;
        }

        // Check item flags
        if (!itemMeta.getItemFlags().equals(cachedMeta.getItemFlags())) {
            return false;
        }

        // Check the attribute modifiers
        if (!ignoreAttribute) {
            final boolean hasAttributeOne = itemMeta.hasAttributeModifiers();
            final boolean hasAttributeTwo = cachedMeta.hasAttributeModifiers();
            if (hasAttributeOne) {
                if (!hasAttributeTwo
                    || !Objects.equals(itemMeta.getAttributeModifiers(), cachedMeta.getAttributeModifiers())) {
                    return false;
                }
            } else if (hasAttributeTwo) {
                return false;
            }
        }

        if (IS_1_20_5) {
            // Check if fire-resistant
            if (itemMeta.isFireResistant() != cachedMeta.isFireResistant()) {
                return false;
            }

            // Check if unbreakable
            if (itemMeta.isUnbreakable() != cachedMeta.isUnbreakable()) {
                return false;
            }

            // Check if hide tooltip
            if (itemMeta.isHideTooltip() != cachedMeta.isHideTooltip()) {
                return false;
            }

            // Check rarity
            final boolean hasRarityOne = itemMeta.hasRarity();
            final boolean hasRarityTwo = cachedMeta.hasRarity();
            if (hasRarityOne) {
                if (!hasRarityTwo || itemMeta.getRarity() != cachedMeta.getRarity()) {
                    return false;
                }
            } else if (hasRarityTwo) {
                return false;
            }

            // Check food components
            if (itemMeta.hasFood() && cachedMeta.hasFood()) {
                if (!Objects.equals(itemMeta.getFood(), cachedMeta.getFood())) {
                    return false;
                }
            } else if (itemMeta.hasFood() != cachedMeta.hasFood()) {
                return false;
            }

            // Check tool components
            if (itemMeta.hasTool() && cachedMeta.hasTool()) {
                if (!Objects.equals(itemMeta.getTool(), cachedMeta.getTool())) {
                    return false;
                }
            } else if (itemMeta.hasTool() != cachedMeta.hasTool()) {
                return false;
            }

            if (IS_1_21) {
                // Check jukebox playable
                if (itemMeta.hasJukeboxPlayable() && cachedMeta.hasJukeboxPlayable()) {
                    if (!Objects.equals(itemMeta.getJukeboxPlayable(), cachedMeta.getJukeboxPlayable())) {
                        return false;
                    }
                } else if (itemMeta.hasJukeboxPlayable() != cachedMeta.hasJukeboxPlayable()) {
                    return false;
                }
            }
        }

        // Check the lore
        if (!ignoreLore || shouldCompareLore(itemStack) || shouldCompareLore(cache.getItemStack())) {
            if (itemMeta.hasLore() && cachedMeta.hasLore()) {
                if (!Objects.equals(itemMeta.getLore(), cachedMeta.getLore())) {
                    return false;
                }
            } else if (itemMeta.hasLore() != cachedMeta.hasLore()) {
                return false;
            }
        }

        // Slimefun ID check no need to worry about distinction, covered in PDC + lore
        final Optional<String> optionalStackId1 = Slimefun.getItemDataService().getItemData(itemMeta);
        final Optional<String> optionalStackId2 = Slimefun.getItemDataService().getItemData(cachedMeta);
        if (optionalStackId1.isPresent() != optionalStackId2.isPresent()) {
            return false;
        }
        if (optionalStackId1.isPresent()) {
            if (optionalStackId1.get().equals(optionalStackId2.get())) {
                if (!ignoreCustomModelData) {
                    // Custom model data is different, no match
                    final boolean hasCustomOne = itemMeta.hasCustomModelData();
                    final boolean hasCustomTwo = cachedMeta.hasCustomModelData();
                    if (hasCustomOne) {
                        return hasCustomTwo && itemMeta.getCustomModelData() == cachedMeta.getCustomModelData();
                    } else return !hasCustomTwo;
                }
                return true;
            }
            return false;
        }

        // Check the display name
        return !itemMeta.hasDisplayName() || Objects.equals(itemMeta.getDisplayName(), cachedMeta.getDisplayName());

        // Everything should match if we've managed to get here
    }

    @SuppressWarnings("UnstableApiUsage")
    private static boolean itemsMatchModern(
        ItemStack cacheItem,
        ItemStack itemStack,
        MatchOption option) {
        // most case pdc and others are enough
        // always ignore lore
        if (!cacheItem.matchesWithoutData(itemStack, option.toDataComponentSet(), true)) {
            return false;
        }

        if (!option.isIgnoreLore() || shouldCompareLore(itemStack) || shouldCompareLore(cacheItem)) {
            // we have to check lore manually, otherwise `matchesWithoutData` cannot identify non-style-preset text.
            // of course, we can use CraftBukkit utils like `ItemMeta.getLore()`, but it needs reflection.
            return loreMatchesLoose(
                cacheItem.getData(DataComponentTypes.LORE).styledLines(),
                itemStack.getData(DataComponentTypes.LORE).styledLines());
        }

        return true;
    }

    /**
     * Compare plain text (no style) only,
     * Fix #436
     */
    private static boolean loreMatchesLoose(List<Component> a1, List<Component> a2) {
        if (a1.size() != a2.size()) return false;
        var serializer = PlainTextComponentSerializer.plainText();
        for (int i = 0; i < a1.size(); i++) {
            if (!serializer.serialize(a1.get(i)).equals(serializer.serialize(a2.get(i)))) return false;
        }
        return true;
    }

    private static boolean shouldCompareLore(ItemStack itemStack) {
        return FORCE_CHECK_LORE
            || itemStack.getMaxStackSize() == 1 // Fix RPG weapons
            || itemStack.getType()
            == Material.PLAYER_HEAD // Fix Soul jars in SoulJars & Number Components in MomoTech
            // & Backpacks-like items in Slimefun & DynaTech & MerakTech & TsingshanTechnology
            || itemStack.getType() == Material.SPAWNER // Fix Reinforced Spawner in Slimefun4
            || itemStack.getType() == Material.SUGAR // Fix Symbols in MomoTech
            || itemStack.getType() == Material.MINECART // Fix Dolly(possible) in FluffyMachines
            || itemStack.getType() == Material.CHEST_MINECART; // Fix Packed Dolly(possible) in FluffyMachines
    }

    public static boolean canQuickEscapeMetaVariant(ItemMeta metaOne, ItemMeta metaTwo) {
        return canQuickEscapeMetaVariant(metaOne, metaTwo, false);
    }

    @SuppressWarnings("removal")
    public static boolean canQuickEscapeMetaVariant(ItemMeta metaOne, ItemMeta metaTwo, boolean ignoreDurability) {
        // Damageable (first as everything can be damageable apparently)
        if (!ignoreDurability) {
            if (metaOne instanceof Damageable instanceOne && metaTwo instanceof Damageable instanceTwo) {
                if (instanceOne.hasDamage() != instanceTwo.hasDamage()) {
                    return true;
                }

                if (instanceOne.hasDamage()) {
                    if (instanceOne.getDamage() != instanceTwo.getDamage()) {
                        return true;
                    }
                }
            }

            if (metaOne instanceof Repairable instanceOne && metaTwo instanceof Repairable instanceTwo) {
                if (instanceOne.hasRepairCost() != instanceTwo.hasRepairCost()) {
                    return true;
                }

                if (instanceOne.hasRepairCost()) {
                    if (instanceOne.getRepairCost() != instanceTwo.getRepairCost()) {
                        return true;
                    }
                }
            }
        }

        // Axolotl
        if (metaOne instanceof AxolotlBucketMeta instanceOne && metaTwo instanceof AxolotlBucketMeta instanceTwo) {
            if (instanceOne.hasVariant() != instanceTwo.hasVariant()) {
                return true;
            }

            if (instanceOne.hasVariant()) {
                if (instanceOne.getVariant() != instanceTwo.getVariant()) {
                    return true;
                }
            }
        }

        // Banner
        if (metaOne instanceof BannerMeta instanceOne && metaTwo instanceof BannerMeta instanceTwo) {
            if (instanceOne.numberOfPatterns() != instanceTwo.numberOfPatterns()) {
                return true;
            }

            if (!instanceOne.getPatterns().equals(instanceTwo.getPatterns())) {
                return true;
            }
        }

        // BlockData
        if (metaOne instanceof BlockDataMeta instanceOne && metaTwo instanceof BlockDataMeta instanceTwo) {
            if (instanceOne.hasBlockData() != instanceTwo.hasBlockData()) {
                return true;
            }
        }

        // BlockState
        if (metaOne instanceof BlockStateMeta instanceOne && metaTwo instanceof BlockStateMeta instanceTwo) {
            if (instanceOne.hasBlockState() != instanceTwo.hasBlockState()) {
                return true;
            }

            if (instanceOne.hasBlockState()) {
                if (!instanceOne.getBlockState().equals(instanceTwo.getBlockState())) {
                    return true;
                }
            }
        }

        // Books
        if (metaOne instanceof BookMeta instanceOne && metaTwo instanceof BookMeta instanceTwo) {
            if (instanceOne.getPageCount() != instanceTwo.getPageCount()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getAuthor(), instanceTwo.getAuthor())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getTitle(), instanceTwo.getTitle())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getGeneration(), instanceTwo.getGeneration())) {
                return true;
            }
        }

        // Bundle
        if (metaOne instanceof BundleMeta instanceOne && metaTwo instanceof BundleMeta instanceTwo) {
            // Patch start - No bundle allowed
            if (true) return false;
            // Patch end - No bundle allowed
            if (instanceOne.hasItems() != instanceTwo.hasItems()) {
                return true;
            }
            if (instanceOne.hasItems()) {
                if (!instanceOne.getItems().equals(instanceTwo.getItems())) {
                    return true;
                }
            }
        }

        // Compass
        if (metaOne instanceof CompassMeta instanceOne && metaTwo instanceof CompassMeta instanceTwo) {
            if (instanceOne.isLodestoneTracked() != instanceTwo.isLodestoneTracked()) {
                return true;
            }
            if (instanceOne.isLodestoneTracked()) {
                if (!Objects.equals(instanceOne.getLodestone(), instanceTwo.getLodestone())) {
                    return true;
                }
            }
        }

        // Crossbow
        if (metaOne instanceof CrossbowMeta instanceOne && metaTwo instanceof CrossbowMeta instanceTwo) {
            if (instanceOne.hasChargedProjectiles() != instanceTwo.hasChargedProjectiles()) {
                return true;
            }
            if (instanceOne.hasChargedProjectiles()) {
                if (!instanceOne.getChargedProjectiles().equals(instanceTwo.getChargedProjectiles())) {
                    return true;
                }
            }
        }

        // Enchantment Storage
        if (metaOne instanceof EnchantmentStorageMeta instanceOne
            && metaTwo instanceof EnchantmentStorageMeta instanceTwo) {
            if (instanceOne.hasStoredEnchants() != instanceTwo.hasStoredEnchants()) {
                return true;
            }
            if (instanceOne.hasStoredEnchants()) {
                if (!instanceOne.getStoredEnchants().equals(instanceTwo.getStoredEnchants())) {
                    return true;
                }
            }
        }

        // Firework Star
        if (metaOne instanceof FireworkEffectMeta instanceOne && metaTwo instanceof FireworkEffectMeta instanceTwo) {
            if (!Objects.equals(instanceOne.getEffect(), instanceTwo.getEffect())) {
                return true;
            }
        }

        // Firework
        if (metaOne instanceof FireworkMeta instanceOne && metaTwo instanceof FireworkMeta instanceTwo) {
            if (instanceOne.getPower() != instanceTwo.getPower()) {
                return true;
            }
            if (!instanceOne.getEffects().equals(instanceTwo.getEffects())) {
                return true;
            }
        }

        // Leather Armor
        if (metaOne instanceof LeatherArmorMeta instanceOne && metaTwo instanceof LeatherArmorMeta instanceTwo) {
            if (!instanceOne.getColor().equals(instanceTwo.getColor())) {
                return true;
            }
        }

        // Maps
        if (metaOne instanceof MapMeta instanceOne && metaTwo instanceof MapMeta instanceTwo) {
            if (instanceOne.hasMapView() != instanceTwo.hasMapView()) {
                return true;
            }
            if (instanceOne.hasLocationName() != instanceTwo.hasLocationName()) {
                return true;
            }
            if (instanceOne.hasColor() != instanceTwo.hasColor()) {
                return true;
            }
            if (instanceOne.hasMapView()) {
                if (!Objects.equals(instanceOne.getMapView(), instanceTwo.getMapView())) {
                    return true;
                }
            }
            if (instanceOne.hasLocationName()) {
                if (!Objects.equals(instanceOne.getLocationName(), instanceTwo.getLocationName())) {
                    return true;
                }
            }
            if (instanceOne.hasColor()) {
                if (!Objects.equals(instanceOne.getColor(), instanceTwo.getColor())) {
                    return true;
                }
            }
        }

        // Potion
        if (metaOne instanceof PotionMeta instanceOne && metaTwo instanceof PotionMeta instanceTwo) {
            if (IS_1_20_5) {
                if (instanceOne.getBasePotionType() != instanceTwo.getBasePotionType()) {
                    return true;
                }
            } else {
                if (!Objects.equals(instanceOne.getBasePotionData(), instanceTwo.getBasePotionData())) {
                    return true;
                }
            }
            if (instanceOne.hasCustomEffects() != instanceTwo.hasCustomEffects()) {
                return true;
            }
            if (instanceOne.hasColor() != instanceTwo.hasColor()) {
                return true;
            }
            if (instanceOne.hasColor()) {
                if (!Objects.equals(instanceOne.getColor(), instanceTwo.getColor())) {
                    return true;
                }
            }
            if (instanceOne.hasCustomEffects()) {
                if (!instanceOne.getCustomEffects().equals(instanceTwo.getCustomEffects())) {
                    return true;
                }
            }
        }

        // Skull
        if (metaOne instanceof SkullMeta instanceOne && metaTwo instanceof SkullMeta instanceTwo) {
            if (instanceOne.hasOwner() != instanceTwo.hasOwner()) {
                return true;
            }
            if (!Networks.getConfigManager().isDisableProfileCheck()) {
                if (!Objects.equals(instanceOne.getOwningPlayer(), instanceTwo.getOwningPlayer())) {
                    return true;
                }
            }
        }

        // Stew
        if (metaOne instanceof SuspiciousStewMeta instanceOne && metaTwo instanceof SuspiciousStewMeta instanceTwo) {
            if (instanceOne.hasCustomEffects() != instanceTwo.hasCustomEffects()) {
                return true;
            }

            if (instanceOne.hasCustomEffects()) {
                if (!Objects.equals(instanceOne.getCustomEffects(), instanceTwo.getCustomEffects())) {
                    return true;
                }
            }
        }

        // Fish Bucket
        if (metaOne instanceof TropicalFishBucketMeta instanceOne
            && metaTwo instanceof TropicalFishBucketMeta instanceTwo) {
            if (instanceOne.hasVariant() != instanceTwo.hasVariant()) {
                return true;
            }
            if (instanceOne.hasVariant()) {
                if (!instanceOne.getPattern().equals(instanceTwo.getPattern())) {
                    return true;
                }
                if (!instanceOne.getBodyColor().equals(instanceTwo.getBodyColor())) {
                    return true;
                }
                if (!instanceOne.getPatternColor().equals(instanceTwo.getPatternColor())) {
                    return true;
                }
            }
        }

        // Knowledge Book
        if (metaOne instanceof KnowledgeBookMeta instanceOne && metaTwo instanceof KnowledgeBookMeta instanceTwo) {
            if (instanceOne.hasRecipes() != instanceTwo.hasRecipes()) {
                return true;
            }

            if (instanceOne.hasRecipes()) {
                if (!Objects.equals(instanceOne.getRecipes(), instanceTwo.getRecipes())) {
                    return true;
                }
            }
        }

        // Music Instrument
        if (metaOne instanceof MusicInstrumentMeta instanceOne && metaTwo instanceof MusicInstrumentMeta instanceTwo) {
            if (!Objects.equals(instanceOne.getInstrument(), instanceTwo.getInstrument())) {
                return true;
            }
        }

        // Armor
        if (metaOne instanceof ArmorMeta instanceOne && metaTwo instanceof ArmorMeta instanceTwo) {
            if (!Objects.equals(instanceOne.getTrim(), instanceTwo.getTrim())) {
                return true;
            }
        }

        if (IS_1_20_5) {
            // Writable Book
            if (metaOne instanceof WritableBookMeta instanceOne && metaTwo instanceof WritableBookMeta instanceTwo) {
                if (instanceOne.getPageCount() != instanceTwo.getPageCount()) {
                    return true;
                }
                if (!Objects.equals(instanceOne.getPages(), instanceTwo.getPages())) {
                    return true;
                }
            }
            if (IS_1_21) {
                // Ominous Bottle
                if (metaOne instanceof OminousBottleMeta instanceOne
                    && metaTwo instanceof OminousBottleMeta instanceTwo) {
                    if (instanceOne.hasAmplifier() != instanceTwo.hasAmplifier()) {
                        return true;
                    }

                    if (instanceOne.hasAmplifier()) {
                        if (instanceOne.getAmplifier() != instanceTwo.getAmplifier()) {
                            return true;
                        }
                    }
                }
                // Shield
                if (metaOne instanceof ShieldMeta instanceOne && metaTwo instanceof ShieldMeta instanceTwo) {
                    return Objects.equals(instanceOne.getBaseColor(), instanceTwo.getBaseColor());
                }
            }
        }

        // Cannot escape via any meta extension check
        return false;
    }

    /**
     * Heal the entity by the provided amount
     *
     * @param itemStack         The {@link LivingEntity} to heal
     * @param durationInSeconds The amount to heal by
     */
    @ParametersAreNonnullByDefault
    public static void putOnCooldown(ItemStack itemStack, int durationInSeconds) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataAPI.setLong(
                itemMeta, Keys.ON_COOLDOWN, System.currentTimeMillis() + (durationInSeconds * 1000L));
            itemStack.setItemMeta(itemMeta);
        }
    }

    /**
     * Heal the entity by the provided amount
     *
     * @param itemStack The {@link LivingEntity} to heal
     */
    @ParametersAreNonnullByDefault
    public static boolean isOnCooldown(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            return System.currentTimeMillis() < Keys.getCooldown(itemMeta);
        }
        return false;
    }

    public static boolean isBlacklisted(@Nullable ItemStack itemStack) {
        return itemStack == null
            || itemStack.getType() == Material.AIR
            || itemStack.getType().getMaxDurability() < 0
            || Tag.SHULKER_BOXES.isTagged(itemStack.getType())
            || isBundle(itemStack.getType());
    }

    private static boolean isBundle(Material material) {
        return material == Material.BUNDLE
            || IS_1_21_3 && (
                material == Material.BLACK_BUNDLE
            || material == Material.BLUE_BUNDLE
            || material == Material.BROWN_BUNDLE
            || material == Material.CYAN_BUNDLE
            || material == Material.GRAY_BUNDLE
            || material == Material.GREEN_BUNDLE
            || material == Material.LIGHT_BLUE_BUNDLE
            || material == Material.LIGHT_GRAY_BUNDLE
            || material == Material.LIME_BUNDLE
            || material == Material.MAGENTA_BUNDLE
            || material == Material.ORANGE_BUNDLE
            || material == Material.PINK_BUNDLE
            || material == Material.PURPLE_BUNDLE
            || material == Material.RED_BUNDLE
            || material == Material.WHITE_BUNDLE
            || material == Material.YELLOW_BUNDLE);
    }
}
