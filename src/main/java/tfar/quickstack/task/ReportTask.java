package tfar.quickstack.task;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import tfar.quickstack.config.DropOffConfig;
import tfar.quickstack.client.RenderWorldLastEventHandler;
import tfar.quickstack.client.RendererCubeTarget;
import tfar.quickstack.client.ClientUtils;

import java.util.List;

public class ReportTask implements Runnable {

    private final int itemsCounter;
    private final int affectedContainers;
    private final int totalContainers;
    private final List<RendererCubeTarget> rendererCubeTargets;

    public ReportTask(int itemsCounter, int affectedContainers, int totalContainers,
                      List<RendererCubeTarget> rendererCubeTargets) {
        this.itemsCounter = itemsCounter;
        this.affectedContainers = affectedContainers;
        this.totalContainers = totalContainers;
        this.rendererCubeTargets = rendererCubeTargets;
    }

    @Override
    public void run() {
        if (DropOffConfig.Client.highlightContainers.get()) {
            RenderWorldLastEventHandler.RendererCube.INSTANCE.draw(rendererCubeTargets);
        }

        if (DropOffConfig.Client.displayMessage.get()) {
            String message = String.valueOf(ChatFormatting.RED) +
                    itemsCounter +
                    ChatFormatting.RESET +
                    " items moved to " +
                    ChatFormatting.RED +
                    affectedContainers +
                    ChatFormatting.RESET +
                    " containers of " +
                    ChatFormatting.RED +
                    totalContainers +
                    ChatFormatting.RESET +
                    " checked in total.";

            ClientUtils.printToChat(message);
        }

        ClientUtils.playSound(SoundEvents.UI_BUTTON_CLICK);
    }

}
