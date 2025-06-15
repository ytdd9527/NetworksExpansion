package com.ytdd9527.networksexpansion.utils;

import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class WorldUtils {
    protected static @Nullable Class<?> craftBlockStateClass;
    protected static @Nullable Field interfaceBlockDataField;
    protected static @Nullable Field blockPositionField;
    protected static @Nullable Field worldField;
    protected static @Nullable Field weakWorldField;
    protected static boolean success = false;

    static {
        try {
            World sampleWorld = Bukkit.getWorlds().get(0);
            BlockState blockstate = sampleWorld.getBlockAt(0, 0, 0).getState();
            boolean fail = false;
            Pair<Field, Class<?>> result = ReflectionUtil.getDeclaredFieldsRecursively(blockstate.getClass(), "data");
            if (result != null) {
                interfaceBlockDataField = result.getFirstValue();
                if (interfaceBlockDataField != null) {
                    interfaceBlockDataField.setAccessible(true);
                } else {
                    fail = true;
                }
                craftBlockStateClass = result.getSecondValue();
                if (craftBlockStateClass != null) {
                    Pair<Field, Class<?>> r2 =
                            ReflectionUtil.getDeclaredFieldsRecursively(craftBlockStateClass, "position");
                    if (r2 != null) {
                        blockPositionField = r2.getFirstValue();
                        if (blockPositionField != null) {
                            blockPositionField.setAccessible(true);
                        } else {
                            fail = true;
                        }
                    } else {
                        fail = true;
                    }

                    Pair<Field, Class<?>> r3 =
                            ReflectionUtil.getDeclaredFieldsRecursively(craftBlockStateClass, "world");
                    if (r3 != null) {
                        worldField = r3.getFirstValue();
                        if (worldField != null) {
                            worldField.setAccessible(true);
                        } else {
                            fail = true;
                        }
                    } else {
                        fail = true;
                    }

                    Pair<Field, Class<?>> r4 =
                            ReflectionUtil.getDeclaredFieldsRecursively(craftBlockStateClass, "weakWorld");
                    if (r4 != null) {
                        weakWorldField = r4.getFirstValue();
                        if (weakWorldField != null) {
                            weakWorldField.setAccessible(true);
                        } else {
                            fail = true;
                        }
                    } else {
                        fail = true;
                    }
                }
            } else {
                fail = true;
            }
            success = !fail;
        } catch (Throwable ignored) {

        }
    }

    public static boolean copyBlockState(@NotNull BlockState fromBlockState, @NotNull Block toBlock) {
        if (!success) {
            return false;
        }

        BlockState toState = toBlock.getState();
        if (craftBlockStateClass != null
                && (!craftBlockStateClass.isInstance(toState) || !craftBlockStateClass.isInstance(fromBlockState))) {
            return false;
        }

        try {
            if (blockPositionField != null) {
                blockPositionField.set(fromBlockState, blockPositionField.get(toState));
            }

            if (worldField != null) {
                worldField.set(fromBlockState, worldField.get(toState));
            }
            if (weakWorldField != null) {
                weakWorldField.set(fromBlockState, weakWorldField.get(toState));
            }
            fromBlockState.update(true, false);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
