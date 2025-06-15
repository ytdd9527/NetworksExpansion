package io.github.sefiraat.networks.utils;

import com.balugaq.netex.api.enums.MinecraftVersion;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.stackcaches.ItemStackCache;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.experimental.UtilityClass;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
@UtilityClass
public class StackUtils {
    private static final boolean FORCE_CHECK_LORE = Networks.getConfigManager().isForceCheckLore();
    private static final MinecraftVersion MC_VERSION = Networks.getInstance().getMCVersion();
    private static final boolean IS_1_20_5 = MC_VERSION.isAtLeast(MinecraftVersion.MC1_20_5);
    private static final boolean IS_1_21 = MC_VERSION.isAtLeast(MinecraftVersion.MC1_21);

    @NotNull public static ItemStack getAsQuantity(@Nullable ItemStack itemStack, int amount) {
        if (itemStack == null) {
            return new ItemStack(Material.AIR);
        }
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
        return itemsMatch(new ItemStackCache(itemStack1), itemStack2, checkLore, checkAmount, checkCustomModelId);
    }

    public static boolean itemsMatch(
            @Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, boolean checkLore, boolean checkAmount) {
        return itemsMatch(new ItemStackCache(itemStack1), itemStack2, checkLore, checkAmount, true);
    }

    public static boolean itemsMatch(
            @Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, boolean checkLore) {
        return itemsMatch(new ItemStackCache(itemStack1), itemStack2, checkLore, false, true);
    }

    public static boolean itemsMatch(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
        return itemsMatch(new ItemStackCache(itemStack1), itemStack2, false, false, true);
    }

    public static boolean itemsMatch(
            @NotNull ItemStackCache cache, @Nullable ItemStack itemStack, boolean checkLore, boolean checkAmount) {
        return itemsMatch(cache, itemStack, checkLore, checkAmount, true);
    }

    public static boolean itemsMatch(@NotNull ItemStackCache cache, @Nullable ItemStack itemStack, boolean checkLore) {
        return itemsMatch(cache, itemStack, checkLore, false, true);
    }

    public static boolean itemsMatch(@NotNull ItemStackCache cache, @Nullable ItemStack itemStack) {
        return itemsMatch(cache, itemStack, false, false, true);
    }

    public static boolean itemsMatch(
            @Nullable ItemStack itemStack,
            @NotNull ItemStackCache cache,
            boolean checkLore,
            boolean checkAmount,
            boolean checkCustomModelId) {
        return itemsMatch(cache, itemStack, checkLore, checkAmount, checkCustomModelId);
    }

    public static boolean itemsMatch(
            @Nullable ItemStack itemStack, @NotNull ItemStackCache cache, boolean checkLore, boolean checkAmount) {
        return itemsMatch(cache, itemStack, checkLore, checkAmount, true);
    }

    public static boolean itemsMatch(@Nullable ItemStack itemStack, @NotNull ItemStackCache cache, boolean checkLore) {
        return itemsMatch(cache, itemStack, checkLore, false, true);
    }

    public static boolean itemsMatch(@Nullable ItemStack itemStack, @NotNull ItemStackCache cache) {
        return itemsMatch(cache, itemStack, false, false, true);
    }

    /**
     * Checks if items match each other, checks go in order from lightest to heaviest
     *
     * @param cache     The cached {@link ItemStack} to compare against
     * @param itemStack The {@link ItemStack} being evaluated
     * @return True if items match
     */
    @SuppressWarnings("UnstableApiUsage")
    public static boolean itemsMatch(
            @NotNull ItemStackCache cache,
            @Nullable ItemStack itemStack,
            boolean checkLore,
            boolean checkAmount,
            boolean checkCustomModelId) {
        // Null check
        if (cache.getItemStack() == null || itemStack == null) {
            return itemStack == null && cache.getItemStack() == null;
        }

        // If types do not match, then the items cannot possibly match
        if (itemStack.getType() != cache.getItemType()) {
            return false;
        }

        if (Tag.SHULKER_BOXES.isTagged(itemStack.getType())) {
            return false;
        }

        if (itemStack.getType() == Material.BUNDLE) {
            return false;
        }

        // If amounts do not match, then the items cannot possibly match
        if (checkAmount && itemStack.getAmount() > cache.getItemStack().getAmount()) {
            return false;
        }

        // If either item does not have a meta then either a mismatch or both without meta = vanilla
        if (!itemStack.hasItemMeta() || !cache.getItemStack().hasItemMeta()) {
            return itemStack.hasItemMeta() == cache.getItemStack().hasItemMeta();
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
        if (canQuickEscapeMetaVariant(itemMeta, cachedMeta)) {
            return false;
        }

        // Has a display name (checking the name occurs later)
        if (itemMeta.hasDisplayName() != cachedMeta.hasDisplayName()) {
            return false;
        }

        if (checkCustomModelId) {
            // Custom model data is different, no match
            final boolean hasCustomOne = itemMeta.hasCustomModelData();
            final boolean hasCustomTwo = cachedMeta.hasCustomModelData();
            if (hasCustomOne) {
                if (!hasCustomTwo || itemMeta.getCustomModelData() != cachedMeta.getCustomModelData()) {
                    return false;
                }
            } else if (hasCustomTwo) {
                return false;
            }
        }

        // PDCs don't match
        if (!itemMeta.getPersistentDataContainer().equals(cachedMeta.getPersistentDataContainer())) {
            return false;
        }

        // Make sure enchantments match
        if (!itemMeta.getEnchants().equals(cachedMeta.getEnchants())) {
            return false;
        }

        // Check item flags
        if (!itemMeta.getItemFlags().equals(cachedMeta.getItemFlags())) {
            return false;
        }

        // Check the attribute modifiers
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
        if (checkLore
                || FORCE_CHECK_LORE
                || itemStack.getMaxStackSize() == 1 // Fix RPG weapons
                || itemStack.getType()
                        == Material.PLAYER_HEAD // Fix Soul jars in SoulJars & Number Components in MomoTech
                || itemStack.getType() == Material.SPAWNER // Fix Reinforced Spawner in Slimefun4
                || itemStack.getType() == Material.SUGAR // Fix Symbols in MomoTech
        ) {
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
            return optionalStackId1.get().equals(optionalStackId2.get());
        }

        // Check the display name
        return !itemMeta.hasDisplayName() || Objects.equals(itemMeta.getDisplayName(), cachedMeta.getDisplayName());

        // Everything should match if we've managed to get here
    }

    @SuppressWarnings("removal")
    public static boolean canQuickEscapeMetaVariant(@NotNull ItemMeta metaOne, @NotNull ItemMeta metaTwo) {

        // Damageable (first as everything can be damageable apparently)
        if (metaOne instanceof Damageable instanceOne && metaTwo instanceof Damageable instanceTwo) {
            if (instanceOne.hasDamage() != instanceTwo.hasDamage()) {
                return true;
            }

            if (instanceOne.getDamage() != instanceTwo.getDamage()) {
                return true;
            }
        }

        if (metaOne instanceof Repairable instanceOne && metaTwo instanceof Repairable instanceTwo) {
            if (instanceOne.hasRepairCost() != instanceTwo.hasRepairCost()) {
                return true;
            }

            if (instanceOne.getRepairCost() != instanceTwo.getRepairCost()) {
                return true;
            }
        }

        // Axolotl
        if (metaOne instanceof AxolotlBucketMeta instanceOne && metaTwo instanceof AxolotlBucketMeta instanceTwo) {
            if (instanceOne.hasVariant() != instanceTwo.hasVariant()) {
                return true;
            }

            if (!instanceOne.hasVariant() || !instanceTwo.hasVariant()) {
                return true;
            }

            if (instanceOne.getVariant() != instanceTwo.getVariant()) {
                return true;
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

            if (!instanceOne.getBlockState().equals(instanceTwo.getBlockState())) {
                return true;
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
            if (instanceOne.hasItems() != instanceTwo.hasItems()) {
                return true;
            }
            if (!instanceOne.getItems().equals(instanceTwo.getItems())) {
                return true;
            }
        }

        // Compass
        if (metaOne instanceof CompassMeta instanceOne && metaTwo instanceof CompassMeta instanceTwo) {
            if (instanceOne.isLodestoneTracked() != instanceTwo.isLodestoneTracked()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getLodestone(), instanceTwo.getLodestone())) {
                return true;
            }
        }

        // Crossbow
        if (metaOne instanceof CrossbowMeta instanceOne && metaTwo instanceof CrossbowMeta instanceTwo) {
            if (instanceOne.hasChargedProjectiles() != instanceTwo.hasChargedProjectiles()) {
                return true;
            }
            if (!instanceOne.getChargedProjectiles().equals(instanceTwo.getChargedProjectiles())) {
                return true;
            }
        }

        // Enchantment Storage
        if (metaOne instanceof EnchantmentStorageMeta instanceOne
                && metaTwo instanceof EnchantmentStorageMeta instanceTwo) {
            if (instanceOne.hasStoredEnchants() != instanceTwo.hasStoredEnchants()) {
                return true;
            }
            if (!instanceOne.getStoredEnchants().equals(instanceTwo.getStoredEnchants())) {
                return true;
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
            if (!Objects.equals(instanceOne.getMapView(), instanceTwo.getMapView())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getLocationName(), instanceTwo.getLocationName())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getColor(), instanceTwo.getColor())) {
                return true;
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
            if (!Objects.equals(instanceOne.getColor(), instanceTwo.getColor())) {
                return true;
            }
            if (!instanceOne.getCustomEffects().equals(instanceTwo.getCustomEffects())) {
                return true;
            }
        }

        // Skull
        if (metaOne instanceof SkullMeta instanceOne && metaTwo instanceof SkullMeta instanceTwo) {
            if (instanceOne.hasOwner() != instanceTwo.hasOwner()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getOwningPlayer(), instanceTwo.getOwningPlayer())) {
                return true;
            }
        }

        // Stew
        if (metaOne instanceof SuspiciousStewMeta instanceOne && metaTwo instanceof SuspiciousStewMeta instanceTwo) {
            if (instanceOne.hasCustomEffects() != instanceTwo.hasCustomEffects()) {
                return true;
            }

            if (!Objects.equals(instanceOne.getCustomEffects(), instanceTwo.getCustomEffects())) {
                return true;
            }
        }

        // Fish Bucket
        if (metaOne instanceof TropicalFishBucketMeta instanceOne
                && metaTwo instanceof TropicalFishBucketMeta instanceTwo) {
            if (instanceOne.hasVariant() != instanceTwo.hasVariant()) {
                return true;
            }
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

        // Knowledge Book
        if (metaOne instanceof KnowledgeBookMeta instanceOne && metaTwo instanceof KnowledgeBookMeta instanceTwo) {
            if (instanceOne.hasRecipes() != instanceTwo.hasRecipes()) {
                return true;
            }

            if (!Objects.equals(instanceOne.getRecipes(), instanceTwo.getRecipes())) {
                return true;
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

                    if (instanceOne.getAmplifier() != instanceTwo.getAmplifier()) {
                        return true;
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
            long cooldownUntil = PersistentDataAPI.getLong(itemMeta, Keys.ON_COOLDOWN, -1);
            if (cooldownUntil == -1) {
                cooldownUntil = PersistentDataAPI.getLong(itemMeta, Keys.ON_COOLDOWN2, -1);
            }
            if (cooldownUntil == -1) {
                cooldownUntil = PersistentDataAPI.getLong(itemMeta, Keys.ON_COOLDOWN3, 0);
            }
            return System.currentTimeMillis() < cooldownUntil;
        }
        return false;
    }
}
