package scp002.quickstack.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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

  public static void drawBoundingBox(BlockPos pos,int color) {
    Vec3d viewPosition = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
    double x = pos.getX() - viewPosition.x;
    double y = pos.getY() - viewPosition.y;
    double z = pos.getZ() - viewPosition.z;

    double d = .0005;

    GlStateManager.disableTexture();
    GlStateManager.disableLighting();
    GlStateManager.disableBlend();
    GlStateManager.disableDepthTest();

    WorldRenderer.drawBoundingBox(x - d, y - d, z - d,
            x + 1 + d, y + 1 + d, z + 1 + d,
            (color >> 16 & 0xff) / 256f, (color >> 8 & 0xff) / 256f, (color & 0xff) / 256f, 1);

    GlStateManager.enableDepthTest();
    GlStateManager.enableTexture();
    GlStateManager.enableLighting();
  }
}
