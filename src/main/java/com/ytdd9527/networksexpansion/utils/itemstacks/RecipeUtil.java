package com.ytdd9527.networksexpansion.utils.itemstacks;

import com.balugaq.netex.api.data.RandomMachineRecipe;
import com.balugaq.netex.api.interfaces.RecipeItem;
import com.balugaq.netex.utils.Debug;
import com.ytdd9527.networksexpansion.core.items.unusable.ReplaceableCard;
import com.ytdd9527.networksexpansion.utils.ReflectionUtil;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.Composter;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.Crucible;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.GoldPan;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.NetherGoldPan;
import io.github.thebusybiscuit.slimefun4.implementation.settings.GoldPanDrop;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Final_ROOT
 * @since 2.0
 */
@SuppressWarnings({"deprecation", "unused", "unchecked"})
@UtilityClass
public class RecipeUtil {
    public static @Nullable Map<ItemStack[], ItemStack> getRecipesBySlimefunId(@NotNull String slimefunId) {
        final SlimefunItem slimefunItem = SlimefunItem.getById(slimefunId);
        try {
            if (slimefunItem == null) {
                return new HashMap<>();
            }

            Method method = ReflectionUtil.getMethod(slimefunItem.getClass(), "getMachineRecipes");
            if (method != null && method.getReturnType().equals(List.class)) {
                method.setAccessible(true);
                List<MachineRecipe> recipes = (List<MachineRecipe>) method.invoke(slimefunItem);
                if (recipes != null) {
                    Map<ItemStack[], ItemStack> recipeMap = new HashMap<>();
                    for (MachineRecipe recipe : recipes) {
                        boolean disabled = false;
                        for (ItemStack itemStack : recipe.getOutput()) {
                            SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
                            if (sfItem != null && sfItem.isDisabled()) {
                                disabled = true;
                                break;
                            }
                        }
                        if (!disabled) {
                            ItemStack[] inputs = recipe.getInput();
                            ItemStack[] outputs = recipe.getOutput();
                            recipeMap.put(inputs, outputs[0]);
                        }
                    }
                    return recipeMap;
                }
                return null;
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            Debug.trace(e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static void registerRecipeBySlimefunId(@NotNull RecipeItem recipeItem, @NotNull String slimefunId) {
        final SlimefunItem slimefunItem = SlimefunItem.getById(slimefunId);
        try {
            if (slimefunItem == null) {
                return;
            }

            if (slimefunItem instanceof Composter || slimefunItem instanceof Crucible) {
                List<ItemStack> displayRecipes = ((RecipeDisplayItem) slimefunItem).getDisplayRecipes();
                RecipeUtil.registerRecipeBySimpleDisplayRecipe(recipeItem, displayRecipes);
                return;
            }

            Method method = ReflectionUtil.getMethod(slimefunItem.getClass(), "getMachineRecipes");
            if (method != null && method.getReturnType().equals(List.class)) {
                method.setAccessible(true);
                List<MachineRecipe> recipes = (List<MachineRecipe>) method.invoke(slimefunItem);
                if (recipes != null) {
                    for (MachineRecipe recipe : recipes) {
                        boolean disabled = false;
                        for (ItemStack itemStack : recipe.getOutput()) {
                            SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
                            if (sfItem != null && sfItem.isDisabled()) {
                                disabled = true;
                                break;
                            }
                        }
                        if (!disabled) {
                            recipeItem.registerRecipeInCard(0, recipe.getInput(), recipe.getOutput());
                        }
                    }
                }
                return;
            }

            method = ReflectionUtil.getMethod(slimefunItem.getClass(), "getRecipes");
            if (method != null) {
                method.setAccessible(true);
                List<ItemStack[]> recipes = (List<ItemStack[]>) method.invoke(slimefunItem);
                if (recipes != null) {
                    for (int i = 0; i * 2 + 1 < recipes.size(); i++) {
                        ItemStack[] inputs = recipes.get(i * 2);
                        ItemStack[] outputs = recipes.get(i * 2 + 1);

                        boolean disabled = false;
                        for (ItemStack itemStack : outputs) {
                            SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
                            if (sfItem != null && sfItem.isDisabled()) {
                                disabled = true;
                                break;
                            }
                        }

                        if (!disabled) {
                            recipeItem.registerRecipeInCard(0, inputs, outputs);
                        }
                    }
                }
                return;
            }

            if (slimefunItem instanceof RecipeDisplayItem) {
                List<ItemStack> displayRecipes = ((RecipeDisplayItem) slimefunItem).getDisplayRecipes();
                RecipeUtil.registerRecipeBySimpleDisplayRecipe(recipeItem, displayRecipes);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            Debug.trace(e);
            recipeItem.clearRecipe();
            if (slimefunItem instanceof RecipeDisplayItem) {
                List<ItemStack> displayRecipes = ((RecipeDisplayItem) slimefunItem).getDisplayRecipes();
                RecipeUtil.registerRecipeBySimpleDisplayRecipe(recipeItem, displayRecipes);
            } else {
                Debug.trace(e);
            }
        }
    }

    public static void registerRecipeByRecipeType(@NotNull RecipeItem recipeItem, @NotNull RecipeType recipeType) {
        List<SlimefunItem> list = Slimefun.getRegistry().getEnabledSlimefunItems();
        for (SlimefunItem slimefunItem : list) {
            if (!slimefunItem.isDisabled() && recipeType.equals(slimefunItem.getRecipeType())) {
                recipeItem.registerRecipeInCard(0, slimefunItem);
            }
        }
    }

    public static void registerRecipeBySimpleDisplayRecipe(
            @NotNull RecipeItem recipeItem, @NotNull List<ItemStack> displayRecipes) {
        for (int i = 0; i < displayRecipes.size(); i += 2) {
            boolean disabled = false;
            ItemStack itemStack = displayRecipes.get(i + 1);
            SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
            if (sfItem != null) {
                disabled = sfItem.isDisabled();
            }
            if (!disabled) {
                recipeItem.registerRecipeInCard(
                        0, new ItemStack[] {displayRecipes.get(i)}, new ItemStack[] {displayRecipes.get(i + 1)});
            }
        }
    }

    public static void registerGoldPan(@NotNull RecipeItem recipeItem) {
        GoldPan goldPan = SlimefunItems.GOLD_PAN.getItem(GoldPan.class);
        if (goldPan == null) {
            return;
        }
        try {
            Field field = ReflectionUtil.getField(goldPan.getClass(), "drops");
            if (field == null) {
                return;
            }
            field.setAccessible(true);
            Set<GoldPanDrop> goldPanDrops = (Set<GoldPanDrop>) field.get(goldPan);
            List<RandomMachineRecipe.RandomOutput> randomOutputList = new ArrayList<>(goldPanDrops.size());
            for (GoldPanDrop goldPanDrop : goldPanDrops) {
                randomOutputList.add(new RandomMachineRecipe.RandomOutput(
                        new ItemStack[] {goldPanDrop.getOutput()}, goldPanDrop.getValue()));
            }
            recipeItem.registerRecipe(new RandomMachineRecipe(
                    new ItemStack[] {new ItemStack(goldPan.getInputMaterial())}, randomOutputList));
        } catch (Exception e) {
            Debug.trace(e);
            List<RandomMachineRecipe.RandomOutput> randomOutputList = new ArrayList<>();
            randomOutputList.add(
                    new RandomMachineRecipe.RandomOutput(new ItemStack[] {new ItemStack(Material.FLINT)}, 40));
            randomOutputList.add(
                    new RandomMachineRecipe.RandomOutput(new ItemStack[] {new ItemStack(Material.CLAY_BALL)}, 20));
            randomOutputList.add(new RandomMachineRecipe.RandomOutput(new ItemStack[] {SlimefunItems.SIFTED_ORE}, 35));
            randomOutputList.add(
                    new RandomMachineRecipe.RandomOutput(new ItemStack[] {new ItemStack(Material.IRON_NUGGET)}, 5));
            recipeItem.registerRecipe(new RandomMachineRecipe(
                    new ItemStack[] {new ItemStack(goldPan.getInputMaterial())}, randomOutputList));
        }
    }

    public static void registerNetherGoldPan(@NotNull RecipeItem recipeItem) {
        NetherGoldPan netherGoldPan = SlimefunItems.NETHER_GOLD_PAN.getItem(NetherGoldPan.class);
        try {
            if (netherGoldPan == null) {
                return;
            }
            Field field = ReflectionUtil.getField(netherGoldPan.getClass(), "drops");
            if (field == null) {
                return;
            }
            field.setAccessible(true);
            Set<GoldPanDrop> goldPanDrops = (Set<GoldPanDrop>) field.get(netherGoldPan);
            List<RandomMachineRecipe.RandomOutput> randomOutputList = new ArrayList<>(goldPanDrops.size());
            for (GoldPanDrop goldPanDrop : goldPanDrops) {
                randomOutputList.add(new RandomMachineRecipe.RandomOutput(
                        new ItemStack[] {goldPanDrop.getOutput()}, goldPanDrop.getValue()));
            }
            recipeItem.registerRecipe(new RandomMachineRecipe(
                    new ItemStack[] {new ItemStack(netherGoldPan.getInputMaterial())}, randomOutputList));
        } catch (Exception e) {
            Debug.trace(e);
            List<RandomMachineRecipe.RandomOutput> randomOutputList = new ArrayList<>();
            randomOutputList.add(
                    new RandomMachineRecipe.RandomOutput(new ItemStack[] {new ItemStack(Material.FLINT)}, 40));
            randomOutputList.add(
                    new RandomMachineRecipe.RandomOutput(new ItemStack[] {new ItemStack(Material.CLAY_BALL)}, 20));
            randomOutputList.add(new RandomMachineRecipe.RandomOutput(new ItemStack[] {SlimefunItems.SIFTED_ORE}, 35));
            randomOutputList.add(
                    new RandomMachineRecipe.RandomOutput(new ItemStack[] {new ItemStack(Material.IRON_NUGGET)}, 5));
            recipeItem.registerRecipe(new RandomMachineRecipe(
                    new ItemStack[] {new ItemStack(netherGoldPan.getInputMaterial())}, randomOutputList));
        }
    }

    /**
     * @return The #{@link ReplaceableCard} in #{@link ItemStack}
     */
    @Nullable public static ReplaceableCard getReplaceableCard(@Nullable ItemStack itemStack) {
        if (ItemStackUtil.isItemNull(itemStack) || ReplaceableCard.getByMaterial(itemStack.getType()) == null) {
            return null;
        }
        if (!StackUtils.itemsMatch(itemStack, new ItemStack(itemStack.getType()))) {
            return null;
        }
        return ReplaceableCard.getByMaterial(itemStack.getType());
    }
}
