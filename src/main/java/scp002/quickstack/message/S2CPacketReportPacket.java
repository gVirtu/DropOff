package scp002.quickstack.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import scp002.quickstack.render.RendererCubeTarget;
import scp002.quickstack.task.ReportTask;
import scp002.quickstack.util.ByteBufUtilsExt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2CPacketReportPacket {

    private int itemsCounter;
    private int affectedContainers;
    private int totalContainers;
    private List<RendererCubeTarget> rendererCubeTargets = new ArrayList<>();

    /**
     * Leave public default constructor for Netty.
     */
    @SuppressWarnings("unused")
    public S2CPacketReportPacket() {
        //
    }

    public S2CPacketReportPacket(PacketBuffer buf){
        itemsCounter = buf.readInt();
        affectedContainers = buf.readInt();
        totalContainers = buf.readInt();

        ByteBufUtilsExt byteBufUtilsExt = new ByteBufUtilsExt(buf);
        rendererCubeTargets = byteBufUtilsExt.readRendererCubeTargets();
    }

    S2CPacketReportPacket(int itemsCounter, int affectedContainers, int totalContainers,
                          List<RendererCubeTarget> rendererCubeTargets) {
        this.itemsCounter = itemsCounter;
        this.affectedContainers = affectedContainers;
        this.totalContainers = totalContainers;
        this.rendererCubeTargets = rendererCubeTargets;
    }

    public void encode(PacketBuffer buf) {
        buf.writeInt(itemsCounter);
        buf.writeInt(affectedContainers);
        buf.writeInt(totalContainers);

        ByteBufUtilsExt byteBufUtilsExt = new ByteBufUtilsExt(buf);
        byteBufUtilsExt.writeRendererCubeTargets(rendererCubeTargets);
    }

        public void handle(Supplier<NetworkEvent.Context> ctx) {
            ReportTask reportTask = new ReportTask(itemsCounter, affectedContainers,
                    totalContainers, rendererCubeTargets);

            reportTask.run();
            ctx.get().setPacketHandled(true);
        }
    }
