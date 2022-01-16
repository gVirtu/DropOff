package scp002.quickstack.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class DropOffGuiButton extends ExtendedButton {

  public boolean dump;
  final List<Component> hoverText = new ArrayList<>();

  DropOffGuiButton(int xPos, int yPos, Button.OnPress callback, boolean b) {
    super(xPos, yPos, 10, 15, new TextComponent("^"),callback);
    this.dump = b;
    if (dump)
    hoverText.add(new TranslatableComponent("Dump to Nearby Chests"));
    else
    hoverText.add(new TranslatableComponent("Quick Stack to Nearby Chests"));
  }

  @Override
  public void playDownSound(SoundManager p_playDownSound_1_) {
  }

  @Override
  public void render(PoseStack matrices, int p_render_1_, int p_render_2_, float p_render_3_) {
    Minecraft mc = Minecraft.getInstance();
    this.visible = !(mc.screen instanceof CreativeModeInventoryScreen) || ((CreativeModeInventoryScreen)Minecraft.getInstance().screen).getSelectedTab() == CreativeModeTab.TAB_INVENTORY.getId();
    super.render(matrices,p_render_1_, p_render_2_, p_render_3_);
    if (visible)
    renderToolTip(matrices,p_render_1_,p_render_2_);
  }

  @Override
  public void renderToolTip(@NotNull PoseStack stack, int p_renderToolTip_1_, int p_renderToolTip_2_) {
    if (isHovered){
      RenderSystem.enableDepthTest();
      Minecraft.getInstance().screen.renderComponentTooltip(stack, hoverText, p_renderToolTip_1_, p_renderToolTip_2_);
      RenderSystem.disableDepthTest();
    }
  }
}
