package com.ytdd9527.networksexpansion.core.items.unusable;

import com.balugaq.netex.utils.Lang;
import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.network.stackcaches.BlueprintInstance;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.Theme;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.sefiraat.networks.utils.datatypes.PersistentCraftingBlueprintType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBlueprint extends UnusableSlimefunItem implements DistinctiveItem {

    public AbstractBlueprint(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public static void setBlueprint(ItemStack blueprint, ItemStack[] recipe, ItemStack output) {
        final ItemMeta itemMeta = blueprint.getItemMeta();
        DataTypeMethods.setCustom(
                itemMeta,
                Keys.BLUEPRINT_INSTANCE,
                PersistentCraftingBlueprintType.TYPE,
                new BlueprintInstance(recipe, output));
        List<String> lore = new ArrayList<>();

        lore.add(Lang.getString("messages.blueprint.title"));

        for (ItemStack item : recipe) {
            if (item == null) {
                lore.add(Theme.PASSIVE + "- " + Lang.getString("messages.blueprint.empty"));
                continue;
            }
            lore.add(Theme.PASSIVE + "- " + TextUtil.stripColor(ItemStackHelper.getDisplayName(item)));
        }

        lore.add("");
        lore.add(Lang.getString("messages.blueprint.output"));

        lore.add(Theme.PASSIVE + "- " + TextUtil.stripColor(ItemStackHelper.getDisplayName(output)));

        itemMeta.setLore(lore);

        blueprint.setItemMeta(itemMeta);
    }

    /*
     * Fix https://github.com/Sefiraat/Networks/issues/201
     */
    @Override
    public boolean canStack(@NotNull ItemMeta meta1, @NotNull ItemMeta meta2) {
        return meta1.getPersistentDataContainer().equals(meta2.getPersistentDataContainer());
    }
}
