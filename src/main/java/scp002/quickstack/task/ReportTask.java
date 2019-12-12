package scp002.quickstack.task;

import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import scp002.quickstack.config.DropOffConfig;
import scp002.quickstack.render.RendererCube;
import scp002.quickstack.render.RendererCubeTarget;
import scp002.quickstack.util.ClientUtils;

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
            RendererCube.INSTANCE.draw(rendererCubeTargets);
        }

        if (DropOffConfig.Client.displayMessage.get()) {
            String message = String.valueOf(TextFormatting.RED) +
                    itemsCounter +
                    TextFormatting.RESET +
                    " items moved to " +
                    TextFormatting.RED +
                    affectedContainers +
                    TextFormatting.RESET +
                    " containers of " +
                    TextFormatting.RED +
                    totalContainers +
                    TextFormatting.RESET +
                    " checked in total.";

            ClientUtils.printToChat(message);
        }

        ClientUtils.playSound(SoundEvents.UI_BUTTON_CLICK);
    }

}
