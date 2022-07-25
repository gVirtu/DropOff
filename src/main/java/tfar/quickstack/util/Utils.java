package tfar.quickstack.util;

import net.minecraft.world.item.ItemStack;

public class Utils {
  public static boolean isFavorited(ItemStack stack){
    return stack.hasTag() && stack.getTag().getBoolean("favorite");
  }
}
