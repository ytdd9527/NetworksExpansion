package com.ytdd9527.networksexpansion.utils.registry;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Final_ROOT
 * @since 2.2
 */
public class SlimefunCraftRegistry {
    private static volatile SlimefunCraftRegistry instance;
    private boolean init = false;
    private @NotNull Map<String, List<String>> craftMap = new HashMap<>();

    @NotNull public static SlimefunCraftRegistry getInstance() {
        if (instance == null) {
            synchronized (SlimefunCraftRegistry.class) {
                if (instance == null) {
                    instance = new SlimefunCraftRegistry();
                }
            }
        }
        return instance;
    }

    public void init() {
        if (this.init) {
            return;
        }

        this.reload();

        this.init = true;
    }

    public void reload() {
        Map<String, List<String>> craftMap = new HashMap<>();

        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            ItemStack[] itemStacks = slimefunItem.getRecipe();
            for (ItemStack itemStack : itemStacks) {
                SlimefunItem craftItem = SlimefunItem.getByItem(itemStack);
                if (craftItem != null) {
                    List<String> idList;
                    if (craftMap.containsKey(craftItem.getId())) {
                        idList = craftMap.get(craftItem.getId());
                    } else {
                        idList = new ArrayList<>();
                        craftMap.put(craftItem.getId(), idList);
                    }
                    if (!idList.contains(slimefunItem.getId())) {
                        idList.add(slimefunItem.getId());
                    }
                }
            }
        }

        this.craftMap = craftMap;
    }

    @NotNull public List<String> getCraftSlimefunItemIdList(@NotNull String id) {
        if (!this.init) {
            this.init();
        }

        return this.craftMap.containsKey(id) ? this.craftMap.get(id) : new ArrayList<>();
    }

    @NotNull public List<String> getCraftSlimefunItemIdList(@NotNull SlimefunItem slimefunItem) {
        return this.getCraftSlimefunItemIdList(slimefunItem.getId());
    }

    @NotNull public List<SlimefunItem> getCraftSlimefunItemList(@NotNull String id) {
        if (!this.init) {
            this.init();
        }

        List<String> idList = this.craftMap.get(id);
        if (idList == null) {
            idList = new ArrayList<>();
        }
        List<SlimefunItem> slimefunItemList = new ArrayList<>(idList.size());
        for (String targetId : idList) {
            SlimefunItem slimefunItem = SlimefunItem.getById(targetId);
            if (slimefunItem != null) {
                slimefunItemList.add(slimefunItem);
            }
        }

        return slimefunItemList;
    }

    @NotNull public List<SlimefunItem> getCraftSlimefunItemList(@NotNull SlimefunItem slimefunItem) {
        return this.getCraftSlimefunItemList(slimefunItem.getId());
    }
}
