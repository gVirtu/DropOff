package tfar.quickstack.client;

import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.quickstack.config.DropOffConfig;

import java.util.ArrayList;
import java.util.List;

public class RenderWorldLastEventHandler {

    public static final RenderWorldLastEventHandler INSTANCE = new RenderWorldLastEventHandler();

    @SubscribeEvent
    public void onRenderWorldLastEvent(RenderLevelLastEvent event) {
        RendererCube.INSTANCE.tryToRender(event);
    }


  public static class RendererCube {

    public static final RendererCube INSTANCE = new RendererCube();
    private List<RendererCubeTarget> rendererCubeTargets = new ArrayList<>();
    private long currentTime;

    public void draw(List<RendererCubeTarget> rendererCubeTargets) {
      this.rendererCubeTargets = rendererCubeTargets;
      currentTime = System.currentTimeMillis();
    }

    /**
     * This method called by RenderWorldLastEvent handler.
     * It does nothing until the draw() method assign the necessary delay to the global field named currentTime.
     */
    void tryToRender(RenderLevelLastEvent event) {
      if (System.currentTimeMillis() >= currentTime + DropOffConfig.Client.highlightDelay.get() &&
              DropOffConfig.Client.highlightDelay.get() >= 0L) {
        return;
      }
      ClientUtils.renderBlocks(event,rendererCubeTargets);
    }
  }
}
