package tfar.quickstack.message;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import tfar.quickstack.config.DropOffConfig;
import tfar.quickstack.util.InventoryData;
import tfar.quickstack.client.RendererCubeTarget;
import tfar.quickstack.util.PacketBufferExt;
import tfar.quickstack.util.Utils;

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

  public C2SPacketRequestDropoff(FriendlyByteBuf buf) {
    buf = new PacketBufferExt(buf);
    ignoreHotbar = buf.readBoolean();
    dump = buf.readBoolean();
    teTypes = ((PacketBufferExt) buf).readRegistryIdArray();
    minSlotCount = buf.readInt();
  }

  public C2SPacketRequestDropoff(boolean ignoreHotbar, boolean dump, List<BlockEntityType<?>> teTypes, int minSlotCount) {
    this.ignoreHotbar = ignoreHotbar;
    this.dump = dump;
    this.teTypes = teTypes;
    this.minSlotCount = minSlotCount;
  }

  private boolean ignoreHotbar;
  private boolean dump;
  private List<BlockEntityType<?>> teTypes;
  private int minSlotCount;

  private int itemsCounter;

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ServerPlayer player = ctx.get().getSender();
    Set<InventoryData> nearbyInventories = getNearbyInventories(player);

    nearbyInventories.forEach(data -> {
      BlockEntity blockEntity = player.level.getBlockEntity(data.pos);
      blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
              target -> {
                if (dump)
                  dropOff(player, target, data);
                else dropOffExisting(player, target, data);
              });
    });

    List<RendererCubeTarget> rendererCubeTargets = new ArrayList<>();
    int affectedContainers = 0;
    player.containerMenu.broadcastChanges();

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
            rendererCubeTargets), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    ctx.get().setPacketHandled(true);
  }

  public void dropOff(Player player, IItemHandler target, InventoryData data) {
    IItemHandler playerstacks = new InvWrapper(player.getInventory());
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

  public void dropOffExisting(Player player, IItemHandler target, InventoryData data) {

    IItemHandler playerstacks = new InvWrapper(player.getInventory());
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

  public Set<InventoryData> getNearbyInventories(ServerPlayer player) {
    int minX = (int) (player.position().x - DropOffConfig.scanRadius.get());
    int maxX = (int) (player.position().x + DropOffConfig.scanRadius.get());

    int minY = (int) (player.position().y - DropOffConfig.scanRadius.get());
    int maxY = (int) (player.position().y + DropOffConfig.scanRadius.get());

    int minZ = (int) (player.position().z - DropOffConfig.scanRadius.get());
    int maxZ = (int) (player.position().z + DropOffConfig.scanRadius.get());

    Level world = player.level;

    return BlockPos
            .betweenClosedStream(minX, minY, minZ, maxX, maxY, maxZ)
            .map(world::getBlockEntity)
            .filter(Objects::nonNull)
            .filter(tileEntity ->
                    tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                            .filter(iItemHandler -> iItemHandler.getSlots() >= minSlotCount)
                            .isPresent())
            .filter(tileEntity -> !teTypes.contains(tileEntity.getType()))
            .map(BlockEntity::getBlockPos)
            .map(InventoryData::new)
            .collect(Collectors.toSet());
  }

  public void encode(FriendlyByteBuf buf) {
    buf = new PacketBufferExt(buf);
    buf.writeBoolean(ignoreHotbar);
    buf.writeBoolean(dump);
    ((PacketBufferExt) buf).writeRegistryIdArray(teTypes);
    buf.writeInt(minSlotCount);
  }
}

