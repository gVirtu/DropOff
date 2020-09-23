package scp002.quickstack.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.ArrayList;
import java.util.List;

class DropOffGuiButton extends ExtendedButton {

  public boolean dump;
  final List<ITextProperties> hoverText = new ArrayList<>();

  DropOffGuiButton(int xPos, int yPos, Button.IPressable callback, boolean b) {
    super(xPos, yPos, 10, 15, new StringTextComponent("^"),callback);
    this.dump = b;
    if (dump)
    hoverText.add(new TranslationTextComponent("Dump to Nearby Chests"));
    else
    hoverText.add(new TranslationTextComponent("Quick Stack to Nearby Chests"));
  }

  @Override
  public void playDownSound(SoundHandler p_playDownSound_1_) {
  }

  @Override
  public void render(MatrixStack matrices,int p_render_1_, int p_render_2_, float p_render_3_) {
    Minecraft mc = Minecraft.getInstance();
    this.visible = !(mc.currentScreen instanceof CreativeScreen) || ((CreativeScreen)Minecraft.getInstance().currentScreen).getSelectedTabIndex() == ItemGroup.INVENTORY.getIndex();
    super.render(matrices,p_render_1_, p_render_2_, p_render_3_);
    if (visible)
    renderToolTip(matrices,p_render_1_,p_render_2_);
  }

  @Override
  public void renderToolTip(MatrixStack stack,int p_renderToolTip_1_, int p_renderToolTip_2_) {
    if (isHovered){
      RenderSystem.enableDepthTest();
      Minecraft mc = Minecraft.getInstance();
      int guiwidth = mc.currentScreen.width;
      int guiheight = mc.currentScreen.height;
      GuiUtils.drawHoveringText(stack,hoverText,p_renderToolTip_1_+ 10,p_renderToolTip_2_ -10,guiwidth,guiheight,100,mc.fontRenderer);
      RenderSystem.disableDepthTest();
    }
  }
}
