package scp002.quickstack.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import scp002.quickstack.DropOff;
import scp002.quickstack.config.DropOffConfig;
import scp002.quickstack.message.C2SPacketRequestDropoff;
import scp002.quickstack.message.PacketHandler;

import java.util.List;
import java.util.stream.Collectors;

public class ClientUtils {

  public static void printToChat(String message) {
    message = "[" + TextFormatting.BLUE + DropOff.MOD_NAME + TextFormatting.RESET + "]: " + message;

    ClientPlayerEntity player = Minecraft.getInstance().player;
    StringTextComponent textComponentString = new StringTextComponent(message);

    player.sendMessage(textComponentString);
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
      PacketHandler.INSTANCE.sendToServer(new C2SPacketRequestDropoff(DropOffConfig.Client.ignoreHotBar.get(),dump, nwkvnjk(),DropOffConfig.Client.minSlotCount.get()));
    }
  }

  public static List<TileEntityType<?>> nwkvnjk(){
    return DropOffConfig.Client.blacklistedTes.get()
            .stream()
            .map(ResourceLocation::new)
            .map(((ForgeRegistry<TileEntityType<?>>) ForgeRegistries.TILE_ENTITIES)::getValue)
            .collect(Collectors.toList());
  }

  public static void drawBoundingBox(WorldRenderer renderer, MatrixStack stack,BlockPos pos, int color) {

    ActiveRenderInfo camera = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();

    Vec3d vec3d = camera.getProjectedView();
    double x = vec3d.getX();
    double y = vec3d.getY();
    double z = vec3d.getZ();

    IRenderTypeBuffer.Impl irendertypebuffer$impl = renderer.field_228415_m_.func_228487_b_();
    IVertexBuilder ivertexbuilder2 = irendertypebuffer$impl.getBuffer(RenderType.func_228659_m_());
    drawBlockOutline(stack, ivertexbuilder2, x, y, z, pos,color);
  }

  private static void drawBlockOutline(MatrixStack stack, IVertexBuilder iVertexBuilder, double x, double y, double z, BlockPos p_228429_10_, int color) {
    drawBox(stack, iVertexBuilder, VoxelShapes.fullCube(), (double)p_228429_10_.getX() - x, (double)p_228429_10_.getY() - y, (double)p_228429_10_.getZ() - z,(color >> 16 & 0xff) / 256f, (color >> 8 & 0xff) / 256f, (color & 0xff) / 256f, 1);
  }

  private static void drawBox(MatrixStack stack, IVertexBuilder iVertexBuilder, VoxelShape p_228445_2_, double p_228445_3_, double p_228445_5_, double p_228445_7_, float red, float green, float blue, float alpha) {
    Matrix4f matrix4f = stack.func_227866_c_().func_227870_a_();
    p_228445_2_.forEachEdge((p_230013_12_, p_230013_14_, p_230013_16_, p_230013_18_, p_230013_20_, p_230013_22_) -> {
      iVertexBuilder.func_227888_a_(matrix4f, (float)(p_230013_12_ + p_228445_3_), (float)(p_230013_14_ + p_228445_5_), (float)(p_230013_16_ + p_228445_7_)).func_227885_a_(red, green, blue, alpha).endVertex();
      iVertexBuilder.func_227888_a_(matrix4f, (float)(p_230013_18_ + p_228445_3_), (float)(p_230013_20_ + p_228445_5_), (float)(p_230013_22_ + p_228445_7_)).func_227885_a_(red, green, blue, alpha).endVertex();
    });
  }
}
