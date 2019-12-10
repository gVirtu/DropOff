package scp002.mod.dropoff.message;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import scp002.mod.dropoff.inventory.InteractionResult;
import scp002.mod.dropoff.inventory.InventoryData;
import scp002.mod.dropoff.render.RendererCubeTarget;
import scp002.mod.dropoff.task.MainTask;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class C2SPacketRequestDropoff {

    /**
     * Leave public default constructor for Netty.
     */
    @SuppressWarnings("WeakerAccess")
    public C2SPacketRequestDropoff() {
        //
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        MainTask mainTask = new MainTask(player);
        mainTask.run();

        List<InventoryData> inventoryDataList = mainTask.getInventoryDataList();
        List<RendererCubeTarget> rendererCubeTargets = new ArrayList<>();
        int affectedContainers = 0;

        for (InventoryData inventoryData : inventoryDataList) {
            Color color;

            if (inventoryData.getInteractionResult() == InteractionResult.DROPOFF_SUCCESS) {
                ++affectedContainers;
                color = new Color(0, 255, 0);
            } else {
                color = new Color(255, 0, 0);
            }

            for (TileEntity entity : inventoryData.getEntities()) {
                RendererCubeTarget rendererCubeTarget = new RendererCubeTarget(entity.getPos(), color);

                rendererCubeTargets.add(rendererCubeTarget);
            }
        }



         PacketHandler.INSTANCE.sendTo(new S2CPacketReportPacket(mainTask.getItemsCounter(), affectedContainers, inventoryDataList.size(),
                rendererCubeTargets), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }
}

