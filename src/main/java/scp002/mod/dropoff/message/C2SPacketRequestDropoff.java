package scp002.mod.dropoff.message;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import scp002.mod.dropoff.config.DropOffConfig;
import scp002.mod.dropoff.inventory.InventoryData;
import scp002.mod.dropoff.render.RendererCubeTarget;
import scp002.mod.dropoff.util.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class C2SPacketRequestDropoff {

  /**
   * Leave public default constructor for Netty.
   */
  public C2SPacketRequestDropoff() {}

  public C2SPacketRequestDropoff(PacketBuffer buf){
    dump = buf.readBoolean();
  }

  public C2SPacketRequestDropoff(boolean dump) {this.dump = dump;}

  public void encode(PacketBuffer buf) {
    buf.writeBoolean(dump);
  }


  boolean dump;
  public int itemsCounter;

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ServerPlayerEntity player = ctx.get().getSender();

    Set<InventoryData> nearbyInventories = getNearbyInventories(player);

    for (InventoryData data : nearbyInventories) {
      TileEntity blockEntity = player.world.getTileEntity(data.pos);
      blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
              target -> {
                if (dump)
                dropOff(player,target,data);
                else dropOffExisting(player,target,data);
              });
    }

    List<RendererCubeTarget> rendererCubeTargets = new ArrayList<>();
    int affectedContainers = 0;
    player.container.detectAndSendChanges();

    for (InventoryData inventoryData : nearbyInventories) {
      Color color;

      if (inventoryData.success) {
        ++affectedContainers;
        color = new Color(0, 255, 0);
      } else {
        color = new Color(255, 0, 0);
      }

      RendererCubeTarget rendererCubeTarget = new RendererCubeTarget(inventoryData.pos, color);
      rendererCubeTargets.add(rendererCubeTarget);
    }

    PacketHandler.INSTANCE.sendTo(new S2CPacketReportPacket(itemsCounter, affectedContainers, nearbyInventories.size(),
            rendererCubeTargets), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    ctx.get().setPacketHandled(true);
  }

  public void dropOff(PlayerEntity player, IItemHandler target,InventoryData data) {

    IItemHandler playerstacks = new InvWrapper(player.inventory);
    for (int i = 0; i < playerstacks.getSlots(); ++i) {
      ItemStack playerstack = playerstacks.getStackInSlot(i);

      if (playerstack.isEmpty() || Utils.isFavorited(playerstack))continue;
      data.setSuccessful();
      itemsCounter += playerstack.getCount();
      ItemStack rem = playerstacks.extractItem(i,Integer.MAX_VALUE,false);
      for (int j = 0; j < target.getSlots(); ++j) {
        rem = target.insertItem(j,rem,false);
        if (rem.isEmpty()) break;
      }
      if (!rem.isEmpty()){
        itemsCounter -= rem.getCount();
        playerstacks.insertItem(i,rem,false);
      }
    }
  }

  public void dropOffExisting(PlayerEntity player, IItemHandler target, InventoryData data) {

    IItemHandler playerstacks = new InvWrapper(player.inventory);
    for (int i = 0; i < playerstacks.getSlots(); ++i) {
      ItemStack playerstack = playerstacks.getStackInSlot(i);
      if (playerstack.isEmpty() || Utils.isFavorited(playerstack))continue;
      boolean hasExistingStack = IntStream.range(0, target.getSlots()).mapToObj(target::getStackInSlot).filter(existing -> !existing.isEmpty()).anyMatch(existing -> existing.getItem() == playerstack.getItem());
      if (!hasExistingStack)continue;
      data.setSuccessful();
      itemsCounter += playerstack.getCount();
      ItemStack rem = playerstacks.extractItem(i,Integer.MAX_VALUE,false);
      for (int j = 0; j < target.getSlots(); ++j) {
        rem = target.insertItem(j,rem,false);
        if (rem.isEmpty()) break;
      }
      if (!rem.isEmpty()){
        itemsCounter -= rem.getCount();
        playerstacks.insertItem(i,rem,false);
      }
    }
  }

  public Set<InventoryData> getNearbyInventories(ServerPlayerEntity player) {
    int minX = (int) (player.posX - DropOffConfig.scanRadius.get());
    int maxX = (int) (player.posX + DropOffConfig.scanRadius.get());

    int minY = (int) (player.posY - DropOffConfig.scanRadius.get());
    int maxY = (int) (player.posY + DropOffConfig.scanRadius.get());

    int minZ = (int) (player.posZ - DropOffConfig.scanRadius.get());
    int maxZ = (int) (player.posZ + DropOffConfig.scanRadius.get());

    final Set<InventoryData> locations = new HashSet<>();
    BlockPos.getAllInBox(minX,minY,minZ,maxX,maxY,maxZ).forEach(pos ->
    {
      TileEntity currentEntity = player.world.getTileEntity(pos);
      if (currentEntity != null && currentEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent())locations.add(new InventoryData(pos.toImmutable()));
    });
    return locations;
  }
}

