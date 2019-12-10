package scp002.mod.dropoff.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.StringUtils;
import scp002.mod.dropoff.config.DropOffConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryManager {

    private final ServerPlayerEntity player;
    private final World world;

    public InventoryManager(ServerPlayerEntity player) {
        this.player = player;
        world = player.getEntityWorld();
    }

    ServerPlayerEntity getPlayer() {
        return player;
    }

    public <T extends TileEntity> List<InventoryData> getNearbyInventories() {
        int minX = (int) (player.posX - DropOffConfig.scanRadius.get());
        int maxX = (int) (player.posX + DropOffConfig.scanRadius.get());

        int minY = (int) (player.posY - DropOffConfig.scanRadius.get());
        int maxY = (int) (player.posY + DropOffConfig.scanRadius.get());

        int minZ = (int) (player.posZ - DropOffConfig.scanRadius.get());
        int maxZ = (int) (player.posZ + DropOffConfig.scanRadius.get());

        List<InventoryData> inventoryDataList = new ArrayList<>();

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    BlockPos currentBlockPos = new BlockPos(x, y, z);
                    TileEntity currentEntity = world.getTileEntity(currentBlockPos);

                    InventoryData currentInvData;

                    if (currentEntity != null && currentEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
                        currentInvData = getInventoryData(currentEntity);
                    } else if (currentEntity instanceof EnderChestTileEntity) {
                        currentInvData = getInventoryData((EnderChestTileEntity) currentEntity);
                    } else {
                        continue;
                    }

                    int listSize = inventoryDataList.size();

                    if (listSize > 0) {
                        InventoryData previousInvData = inventoryDataList.get(listSize - 1);

                        // Check for duplicates generated from double chests.
                        if (previousInvData.getEntities().contains(currentEntity)) {
                            continue;
                        }
                    }

                    if (currentInvData.getInventory().isUsableByPlayer(player) && isInventoryValid(currentInvData)) {
                        inventoryDataList.add(currentInvData);
                    }
                }
            }
        }

        return inventoryDataList;
    }

    boolean isStacksEqual(ItemStack left, ItemStack right) {
        CompoundNBT leftTag = left.getTag();
        CompoundNBT rightTag = right.getTag();

        return left.getItem() == right.getItem() &&
                ((leftTag == null && rightTag == null) || (leftTag != null && leftTag.equals(rightTag)));
    }

    /**
     * This method returns the name of the block that appears in the tooltip when you move the mouse over the item that
     * corresponds to it.
     */
    String getItemStackName(IInventory inventory) {
        if (inventory instanceof DoubleSidedInventory) {
            return "chest";//Block.getBlockById(54).getLocalizedName();
        }

        if (inventory instanceof BrewingStandTileEntity) {
            return "brewingstand";//Block.getBlockById(117).getLocalizedName();
        }

        if (inventory instanceof TileEntity) {
            TileEntity entity = (TileEntity) inventory;
            ItemStack itemStack = new ItemStack(entity.getBlockState().getBlock());

            return itemStack.getDisplayName().getUnformattedComponentText();
        }

        return I18n.format(inventory.toString());
    }

    int getMaxAllowedStackSize(IInventory inventory, ItemStack stack) {
        return Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
    }

    /**
     * This method checks the config to determine whether to process the inventory of the specified type or not.
     */
    private boolean isInventoryValid(InventoryData inventoryData) {
        TileEntity entity = inventoryData.getEntities().get(0);

        if (entity instanceof BeaconTileEntity) {
            return DropOffConfig.checkBeacons.get();
        }

        if (entity instanceof BrewingStandTileEntity) {
            return DropOffConfig.checkBrewingStands.get();
        }

        if (entity instanceof ChestTileEntity) {
            return DropOffConfig.checkChests.get();
        }

        if (entity instanceof DispenserTileEntity) {
            if (entity instanceof DropperTileEntity) {
                return DropOffConfig.checkDroppers.get();
            }

            return DropOffConfig.checkDispensers.get();
        }

        if (entity instanceof EnderChestTileEntity) {
            return DropOffConfig.checkEnderChests.get();
        }

        if (entity instanceof FurnaceTileEntity) {
            return DropOffConfig.checkFurnaces.get();
        }

        if (entity instanceof HopperTileEntity) {
            return DropOffConfig.checkHoppers.get();
        }

        if (entity instanceof ShulkerBoxTileEntity) {
            return DropOffConfig.checkShulkerBoxes.get();
        }

        String inventoryName = getItemStackName(inventoryData.getInventory());

        return isInventoryNameValid(inventoryName) || DropOffConfig.dropOffEveryPlace.get();
    }

    /**
     * This method checks the config text field to determine whether to process the inventory with the specified name
     * or not.
     */
    private boolean isInventoryNameValid(String name) {
        String[] containerNames =
                StringUtils.split(DropOffConfig.processContainersWithNames.get(), DropOffConfig.delimiter);

        for (String containerName : containerNames) {
            String regex = containerName.replace("*", ".*").trim();

            if (name.matches(regex)) {
                return true;
            }
        }

        return false;
    }

    // Implemented without a loop, because the order of the arguments in the "new InventoryLargeChest()" is important.
    private InventoryData getInventoryData(TileEntity leftEntity) {
        List<TileEntity> entities = new ArrayList<>();

        if (leftEntity instanceof ChestTileEntity) {

            BlockState leftBlockState = world.getBlockState(leftEntity.getPos());

            BlockPos rightBlockPos = new BlockPos(leftEntity.getPos().getX() - 1, leftEntity.getPos().getY(),
                    leftEntity.getPos().getZ());
            TileEntity rightEntity = world.getTileEntity(rightBlockPos);
            BlockState rightBlockState = world.getBlockState(rightBlockPos);

            // ----------------------------------------Check for trapped chests-----------------------------------------
            if (leftBlockState.canProvidePower()) {
                if (rightEntity instanceof ChestTileEntity && rightBlockState.canProvidePower()) {
                    DoubleSidedInventory largeChest = new DoubleSidedInventory((ChestTileEntity)rightEntity, leftEntity);

                    entities.add(leftEntity);
                    entities.add(rightEntity);

                    return new InventoryData(entities, largeChest, InteractionResult.DROPOFF_FAIL);
                }

                rightBlockPos = new BlockPos(leftEntity.getPos().getX() + 1, leftEntity.getPos().getY(),
                        leftEntity.getPos().getZ());
                rightEntity = world.getTileEntity(rightBlockPos);
                rightBlockState = world.getBlockState(rightBlockPos);

                if (rightEntity instanceof ChestTileEntity && rightBlockState.canProvidePower()) {
                    DoubleSidedInventory largeChest = new DoubleSidedInventory(
                            leftEntity, (ChestTileEntity) rightEntity);

                    entities.add(leftEntity);
                    entities.add(rightEntity);

                    return new InventoryData(entities, largeChest, InteractionResult.DROPOFF_FAIL);
                }

                rightBlockPos = new BlockPos(leftEntity.getPos().getX(), leftEntity.getPos().getY(),
                        leftEntity.getPos().getZ() - 1);
                rightEntity = world.getTileEntity(rightBlockPos);
                rightBlockState = world.getBlockState(rightBlockPos);

                if (rightEntity instanceof ChestTileEntity && rightBlockState.canProvidePower()) {
                    DoubleSidedInventory largeChest = new DoubleSidedInventory(
                            (ChestTileEntity) rightEntity, leftEntity);

                    entities.add(leftEntity);
                    entities.add(rightEntity);

                    return new InventoryData(entities, largeChest, InteractionResult.DROPOFF_FAIL);
                }

                rightBlockPos = new BlockPos(leftEntity.getPos().getX(), leftEntity.getPos().getY(),
                        leftEntity.getPos().getZ() + 1);
                rightEntity = world.getTileEntity(rightBlockPos);
                rightBlockState = world.getBlockState(rightBlockPos);

                if (rightEntity instanceof ChestTileEntity && rightBlockState.canProvidePower()) {
                    DoubleSidedInventory largeChest = new DoubleSidedInventory(
                            leftEntity, (ChestTileEntity) rightEntity);

                    entities.add(leftEntity);
                    entities.add(rightEntity);

                    return new InventoryData(entities, largeChest, InteractionResult.DROPOFF_FAIL);
                }
            } else { // ------------------------------------Check for regular chests------------------------------------
                if (rightEntity instanceof ChestTileEntity && !rightBlockState.canProvidePower()) {
                    DoubleSidedInventory largeChest = new DoubleSidedInventory(
                            (ChestTileEntity) rightEntity, leftEntity);

                    entities.add(leftEntity);
                    entities.add(rightEntity);

                    return new InventoryData(entities, largeChest, InteractionResult.DROPOFF_FAIL);
                }

                rightBlockPos = new BlockPos(leftEntity.getPos().getX() + 1, leftEntity.getPos().getY(),
                        leftEntity.getPos().getZ());
                rightEntity = world.getTileEntity(rightBlockPos);
                rightBlockState = world.getBlockState(rightBlockPos);

                if (rightEntity instanceof ChestTileEntity && !rightBlockState.canProvidePower()) {
                    DoubleSidedInventory largeChest = new DoubleSidedInventory(
                            leftEntity, (ChestTileEntity) rightEntity);

                    entities.add(leftEntity);
                    entities.add(rightEntity);

                    return new InventoryData(entities, largeChest, InteractionResult.DROPOFF_FAIL);
                }

                rightBlockPos = new BlockPos(leftEntity.getPos().getX(), leftEntity.getPos().getY(),
                        leftEntity.getPos().getZ() - 1);
                rightEntity = world.getTileEntity(rightBlockPos);
                rightBlockState = world.getBlockState(rightBlockPos);

                if (rightEntity instanceof ChestTileEntity && !rightBlockState.canProvidePower()) {
                    DoubleSidedInventory largeChest = new DoubleSidedInventory(
                            (ChestTileEntity) rightEntity, leftEntity);

                    entities.add(leftEntity);
                    entities.add(rightEntity);

                    return new InventoryData(entities, largeChest, InteractionResult.DROPOFF_FAIL);
                }

                rightBlockPos = new BlockPos(leftEntity.getPos().getX(), leftEntity.getPos().getY(),
                        leftEntity.getPos().getZ() + 1);
                rightEntity = world.getTileEntity(rightBlockPos);
                rightBlockState = world.getBlockState(rightBlockPos);

                if (rightEntity instanceof ChestTileEntity && !rightBlockState.canProvidePower()) {
                    DoubleSidedInventory largeChest = new DoubleSidedInventory(
                            leftEntity, (ChestTileEntity) rightEntity);

                    entities.add(leftEntity);
                    entities.add(rightEntity);

                    return new InventoryData(entities, largeChest, InteractionResult.DROPOFF_FAIL);
                }
            }
        }

        entities.add(leftEntity);

        return new InventoryData(entities, leftEntity, InteractionResult.DROPOFF_FAIL);
    }

    private InventoryData getInventoryData(EnderChestTileEntity entity) {
        List<TileEntity> entities = Collections.singletonList(entity);

        return new InventoryData(entities, player.getInventoryEnderChest(), InteractionResult.DROPOFF_FAIL);
    }

    public abstract class Slots {

        public static final int LAST = -1;
        public static final int FIRST = 0;
        public static final int FURNACE_FUEL = 1;
        public static final int FURNACE_OUT = 2;
        public static final int PLAYER_INVENTORY_FIRST = 9;
        public static final int PLAYER_INVENTORY_LAST = 36;

    }

}
