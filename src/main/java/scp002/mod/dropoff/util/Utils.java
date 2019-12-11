package scp002.mod.dropoff.util;

import net.minecraft.item.ItemStack;

public class Utils {
  public static boolean isFavorited(ItemStack stack){
    return (stack.hasTag() && stack.getTag().getBoolean("favorite"));
  }
}
