package scp002.quickstack.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import scp002.quickstack.config.DropOffConfig;

import java.util.ArrayList;
import java.util.List;

public class RenderWorldLastEventHandler {

    public static final RenderWorldLastEventHandler INSTANCE = new RenderWorldLastEventHandler();

    @SubscribeEvent
    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
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
    void tryToRender(RenderWorldLastEvent event) {
      if (System.currentTimeMillis() >= currentTime + DropOffConfig.Client.highlightDelay.get() &&
              DropOffConfig.Client.highlightDelay.get() >= 0L) {
        return;
      }

      rendererCubeTargets.forEach(rendererCubeTarget -> render(event, rendererCubeTarget.getColor(), rendererCubeTarget.getBlockPos()));
    }

    private void render(RenderWorldLastEvent event, int color, BlockPos pos) {

      Vec3d viewPosition = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
      double x = pos.getX() - viewPosition.x;
      double y = pos.getY() - viewPosition.y;
      double z = pos.getZ() - viewPosition.z;

      double d = .001;

      GlStateManager.depthMask(false);
      GlStateManager.disableTexture();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.disableBlend();
      GlStateManager.disableDepthTest();

      WorldRenderer.drawBoundingBox(x - d, y - d, z - d,
              x + 1 + d, y + 1 + d, z + 1 + d,
              (color >> 16 & 0xff) / 256f, (color >> 8 & 0xff) / 256f, (color & 0xff) / 256f, 1);

      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture();
      GlStateManager.enableLighting();
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
    }
  }
}
