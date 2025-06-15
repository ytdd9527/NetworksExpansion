package com.ytdd9527.networksexpansion.utils.registry;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Final_ROOT
 * @since 2.0
 */
public class RecipeTypeRegistry {
    private static volatile RecipeTypeRegistry instance;
    boolean init = false;

    @Getter
    private Set<RecipeType> recipeTypeSet;

    @Getter
    private Map<RecipeType, List<SlimefunItem>> recipeSlimefunItemMap;

    private RecipeTypeRegistry() {
        this.init();
    }

    @NotNull public static RecipeTypeRegistry getInstance() {
        if (instance == null) {
            synchronized (RecipeTypeRegistry.class) {
                if (instance == null) {
                    instance = new RecipeTypeRegistry();
                }
            }
        }
        return instance;
    }

    public synchronized void init() {
        if (this.init) {
            return;
        }

        this.reload();

        this.init = true;
    }

    public void reload() {
        Set<RecipeType> recipeTypeSet = new HashSet<>();
        Map<RecipeType, List<SlimefunItem>> recipeSlimefunItemMap = new HashMap<>();

        RecipeType recipeType;
        List<SlimefunItem> slimefunItemList;
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            recipeType = slimefunItem.getRecipeType();
            if (recipeSlimefunItemMap.containsKey(recipeType)) {
                slimefunItemList = recipeSlimefunItemMap.get(recipeType);
                slimefunItemList.add(slimefunItem);
            } else {
                slimefunItemList = new ArrayList<>();
                slimefunItemList.add(slimefunItem);
                recipeSlimefunItemMap.put(recipeType, slimefunItemList);
                recipeTypeSet.add(recipeType);
            }
        }

        this.recipeTypeSet = recipeTypeSet;
        this.recipeSlimefunItemMap = recipeSlimefunItemMap;
    }

    public void update() {
        if (this.init) {
            this.reload();
        } else {
            this.init();
        }
    }

    @Nullable public RecipeType getRecipeTypeById(String id) {
        for (RecipeType recipeType : this.getRecipeTypeSet()) {
            if (recipeType.getKey().getKey().equals(id)) {
                return recipeType;
            }
        }
        return null;
    }

    @NotNull public List<SlimefunItem> getByRecipeType(@NotNull RecipeType recipeType) {
        if (this.recipeSlimefunItemMap.containsKey(recipeType)) {
            return this.recipeSlimefunItemMap.get(recipeType);
        } else {
            return new ArrayList<>();
        }
    }
}
