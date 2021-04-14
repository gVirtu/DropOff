package scp002.quickstack.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import scp002.quickstack.config.DropOffConfig;
import scp002.quickstack.message.C2SPacketRequestDropoff;
import scp002.quickstack.message.PacketHandler;

import java.util.List;

public class ClientUtils {

  public static void printToChat(String message) {
    message = "[" + TextFormatting.BLUE + "DropOff" + TextFormatting.RESET + "]: " + message;

    ClientPlayerEntity player = Minecraft.getInstance().player;
    StringTextComponent textComponentString = new StringTextComponent(message);

    player.sendMessage(textComponentString, Util.DUMMY_UUID);
  }

  public static void playSound(SoundEvent soundEvent) {
    SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
    SimpleSound record = SimpleSound.master(soundEvent, 1.0f);

    soundHandler.play(record);
  }

  public static void sendNoSpectator(boolean dump) {
    if (Minecraft.getInstance().player.isSpectator()) {
      printToChat("Action not allowed in spectator mode.");
    } else {
      PacketHandler.INSTANCE.sendToServer(new C2SPacketRequestDropoff(DropOffConfig.Client.ignoreHotBar.get(),dump, DropOffConfig.blockEntityBlacklist,DropOffConfig.Client.minSlotCount.get()));
    }
  }

    private static final int GL_FRONT_AND_BACK = 1032;
    private static final int GL_LINE = 6913;
    private static final int GL_FILL = 6914;
    private static final int GL_LINES = 1;

    public static void renderBlocks(RenderWorldLastEvent e, List<RendererCubeTarget> rendererCubeTargets) {

      Vector3d vec3d = TileEntityRendererDispatcher.instance.renderInfo.getProjectedView();

      MatrixStack stack = e.getMatrixStack();
      stack.translate(-vec3d.x, -vec3d.y, -vec3d.z);

      RenderSystem.pushMatrix();
      RenderSystem.multMatrix(stack.getLast().getMatrix());

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      Profile.BLOCKS.apply(); // Sets GL state for block drawing

      rendererCubeTargets.forEach(rendererCubeTarget -> {
        buffer.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        renderBlockBounding(buffer, rendererCubeTarget.getBlockPos(), rendererCubeTarget.getColor(), 1);
        tessellator.draw();
      });

      Profile.BLOCKS.clean();
      RenderSystem.popMatrix();
    }

    private static void renderBlockBounding(BufferBuilder buffer, BlockPos pos, int color, int opacity) {

      final float size = 1.0f;

      float red = (color >> 16 & 0xff) / 255f;
      float green = (color >> 8 & 0xff) / 255f;
      float blue = (color & 0xff) / 255f;


      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();

      // TOP
      buffer.pos(x, y + size, z).color(red, green, blue, opacity).endVertex();
      buffer.pos(x + size, y + size, z).color(red, green, blue, opacity).endVertex();
      buffer.pos(x + size, y + size, z).color(red, green, blue, opacity).endVertex();
      buffer.pos(x + size, y + size, z + size).color(red, green, blue, opacity).endVertex();
      buffer.pos(x + size, y + size, z + size).color(red, green, blue, opacity).endVertex();
      buffer.pos(x, y + size, z + size).color(red, green, blue, opacity).endVertex();
      buffer.pos(x, y + size, z + size).color(red, green, blue, opacity).endVertex();
      buffer.pos(x, y + size, z).color(red, green, blue, opacity).endVertex();

      // BOTTOM
      buffer.pos(x + size, y, z).color(red, green, blue, opacity).endVertex();
      buffer.pos(x + size, y, z + size).color(red, green, blue, opacity).endVertex();
      buffer.pos(x + size, y, z + size).color(red, green, blue, opacity).endVertex();
      buffer.pos(x, y, z + size).color(red, green, blue, opacity).endVertex();
      buffer.pos(x, y, z + size).color(red, green, blue, opacity).endVertex();
      buffer.pos(x, y, z).color(red, green, blue, opacity).endVertex();
      buffer.pos(x, y, z).color(red, green, blue, opacity).endVertex();
      buffer.pos(x + size, y, z).color(red, green, blue, opacity).endVertex();

      // Edge 1
      buffer.pos(x + size, y, z + size).color(red, green, blue, opacity).endVertex();
      buffer.pos(x + size, y + size, z + size).color(red, green, blue, opacity).endVertex();

      // Edge 2
      buffer.pos(x + size, y, z).color(red, green, blue, opacity).endVertex();
      buffer.pos(x + size, y + size, z).color(red, green, blue, opacity).endVertex();

      // Edge 3
      buffer.pos(x, y, z + size).color(red, green, blue, opacity).endVertex();
      buffer.pos(x, y + size, z + size).color(red, green, blue, opacity).endVertex();

      // Edge 4
      buffer.pos(x, y, z).color(red, green, blue, opacity).endVertex();
      buffer.pos(x, y + size, z).color(red, green, blue, opacity).endVertex();
    }

    /**
     * OpenGL Profiles used for rendering blocks and entities
     */
    private enum Profile {
      BLOCKS {
        @Override
        public void apply() {
          RenderSystem.disableTexture();
          RenderSystem.disableDepthTest();
          RenderSystem.depthMask(false);
          RenderSystem.polygonMode(GL_FRONT_AND_BACK, GL_LINE);
          RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
          RenderSystem.enableBlend();
          RenderSystem.lineWidth(1);
        }

        @Override
        public void clean() {
          RenderSystem.polygonMode(GL_FRONT_AND_BACK, GL_FILL);
          RenderSystem.disableBlend();
          RenderSystem.enableDepthTest();
          RenderSystem.depthMask(true);
          RenderSystem.enableTexture();
        }
      };

      Profile() {
      }

      public abstract void apply();

      public abstract void clean();
    }
}
