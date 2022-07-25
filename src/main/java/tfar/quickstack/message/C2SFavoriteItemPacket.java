package tfar.quickstack.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SFavoriteItemPacket {

  /**
   * Leave public default constructor for Netty.
   */
  public C2SFavoriteItemPacket() {}

  public C2SFavoriteItemPacket(FriendlyByteBuf buf){
    slotId = buf.readInt();
  }

  public C2SFavoriteItemPacket(int slotId) {this.slotId = slotId;}

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(slotId);
  }


  int slotId;

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ServerPlayer player = ctx.get().getSender();
    Slot slot = player.containerMenu.getSlot(slotId);
    ItemStack stack = slot.getItem();
    boolean alreadyFavorited = stack.hasTag() && stack.getTag().contains("favorite");
    if (alreadyFavorited) {
      //kill empty tags
      stack.getTag().remove("favorite");
      if (stack.getTag().isEmpty()) {
        stack.setTag(null);
      }
    } else {
      stack.getOrCreateTag().putBoolean("favorite",true);
    }
    ctx.get().setPacketHandled(true);
  }
}

