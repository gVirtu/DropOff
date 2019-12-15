package scp002.quickstack.message;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import scp002.quickstack.config.DropOffConfig;
import scp002.quickstack.inventory.InventoryData;
import scp002.quickstack.render.RendererCubeTarget;
import scp002.quickstack.util.PacketBufferExt;
import scp002.quickstack.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class C2SPacketRequestDropoff {

  /**
   * Leave public default constructor for Netty.
   */
  public C2SPacketRequestDropoff() {
  }

  public C2SPacketRequestDropoff(PacketBuffer buf) {
    buf = new PacketBufferExt(buf);
    ignoreHotbar = buf.readBoolean();
    dump = buf.readBoolean();
    teTypes = ((PacketBufferExt) buf).readRegistryIdArray();
    minSlotCount = buf.readInt();
  }

  public C2SPacketRequestDropoff(boolean ignoreHotbar,boolean dump, List<TileEntityType<?>> teTypes, int minSlotCount) {
    this.ignoreHotbar = ignoreHotbar;
    this.dump = dump;
    this.teTypes = teTypes;
    this.minSlotCount = minSlotCount;
  }

  private boolean ignoreHotbar;
  private boolean dump;
  private List<TileEntityType<?>> teTypes;
  private int minSlotCount;

  private int itemsCounter;

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ServerPlayerEntity player = ctx.get().getSender();
    Set<InventoryData> nearbyInventories = getNearbyInventories(player);

    nearbyInventories.forEach(data -> {
      TileEntity blockEntity = player.world.getTileEntity(data.pos);
      blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
              target -> {
                if (dump)
                  dropOff(player, target, data);
                else dropOffExisting(player, target, data);
              });
    });

    List<RendererCubeTarget> rendererCubeTargets = new ArrayList<>();
    int affectedContainers = 0;
    player.container.detectAndSendChanges();

    for (InventoryData inventoryData : nearbyInventories) {
      int color;

      if (inventoryData.success) {
        ++affectedContainers;
        color = 0x00FF00;
      } else {
        color = 0xFF0000;
      }

      RendererCubeTarget rendererCubeTarget = new RendererCubeTarget(inventoryData.pos, color);
      rendererCubeTargets.add(rendererCubeTarget);
    }

    PacketHandler.INSTANCE.sendTo(new S2CReportPacket(itemsCounter, affectedContainers, nearbyInventories.size(),
            rendererCubeTargets), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    ctx.get().setPacketHandled(true);
  }

  public void dropOff(PlayerEntity player, IItemHandler target, InventoryData data) {

    IItemHandler playerstacks = new InvWrapper(player.inventory);
    for (int i = 0; i < 36; ++i) {
      if (ignoreHotbar && i < 9)continue;
      ItemStack playerstack = playerstacks.getStackInSlot(i);

      if (playerstack.isEmpty() || Utils.isFavorited(playerstack)) continue;
      data.setSuccessful();
      itemsCounter += playerstack.getCount();
      ItemStack rem = playerstacks.extractItem(i, Integer.MAX_VALUE, false);
      for (int j = 0; j < target.getSlots(); ++j) {
        rem = target.insertItem(j, rem, false);
        if (rem.isEmpty()) break;
      }
      if (!rem.isEmpty()) {
        itemsCounter -= rem.getCount();
        playerstacks.insertItem(i, rem, false);
      }
    }
  }

  public void dropOffExisting(PlayerEntity player, IItemHandler target, InventoryData data) {

    IItemHandler playerstacks = new InvWrapper(player.inventory);
    for (int i = 0; i < 36; ++i) {
      if (ignoreHotbar && i < 9)continue;
      ItemStack playerstack = playerstacks.getStackInSlot(i);
      if (playerstack.isEmpty() || Utils.isFavorited(playerstack)) continue;
      boolean hasExistingStack = IntStream.range(0, target.getSlots()).mapToObj(target::getStackInSlot).filter(existing -> !existing.isEmpty()).anyMatch(existing -> existing.getItem() == playerstack.getItem());
      if (!hasExistingStack) continue;
      data.setSuccessful();
      itemsCounter += playerstack.getCount();
      ItemStack rem = playerstacks.extractItem(i, Integer.MAX_VALUE, false);
      for (int j = 0; j < target.getSlots(); ++j) {
        rem = target.insertItem(j, rem, false);
        if (rem.isEmpty()) break;
      }
      if (!rem.isEmpty()) {
        itemsCounter -= rem.getCount();
        playerstacks.insertItem(i, rem, false);
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

    World world = player.world;

    return BlockPos
            .getAllInBox(minX, minY, minZ, maxX, maxY, maxZ)
            .map(world::getTileEntity)
            .filter(Objects::nonNull)
            .filter(tileEntity ->
                    tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                            .filter(iItemHandler -> iItemHandler.getSlots() >= minSlotCount)
                            .isPresent())
            .filter(tileEntity -> !teTypes.contains(tileEntity.getType()))
            .map(TileEntity::getPos)
            .map(InventoryData::new)
            .collect(Collectors.toSet());
  }

  public void encode( PacketBuffer buf) {
    buf = new PacketBufferExt(buf);
    buf.writeBoolean(ignoreHotbar);
    buf.writeBoolean(dump);
    ((PacketBufferExt) buf).writeRegistryIdArray(teTypes);
    buf.writeInt(minSlotCount);
  }
}

