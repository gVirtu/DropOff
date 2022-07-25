package tfar.quickstack.client;

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

  DropOffGuiButton(int xPos, int yPos, Button.OnPress callback, boolean dump) {
    super(xPos, yPos, 10, 15, new TextComponent("^"),callback);
    this.dump = dump;
    if (dump)
    hoverText.add(new TranslatableComponent("dropoff.dump_nearby"));
    else
    hoverText.add(new TranslatableComponent("dropoff.quick_stack"));
  }

  @Override
  public void playDownSound(SoundManager soundManager) {
  }

  @Override
  public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    Minecraft mc = Minecraft.getInstance();
    this.visible = !(mc.screen instanceof CreativeModeInventoryScreen) || ((CreativeModeInventoryScreen)Minecraft.getInstance().screen).getSelectedTab() == CreativeModeTab.TAB_INVENTORY.getId();
    super.render(poseStack,mouseX, mouseY, partialTick);
    if (visible)
    renderToolTip(poseStack,mouseX,mouseY);
  }

  @Override
  public void renderToolTip(@NotNull PoseStack poseStack, int mouseX, int mouseY) {
    if (isHovered){
      RenderSystem.enableDepthTest();
      Minecraft.getInstance().screen.renderComponentTooltip(poseStack, hoverText, mouseX, mouseY);
      RenderSystem.disableDepthTest();
    }
  }
}
