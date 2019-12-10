package scp002.mod.dropoff.gui;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemGroup;
import scp002.mod.dropoff.config.DropOffConfig;
import scp002.mod.dropoff.util.ClientUtils;

import javax.annotation.Nonnull;
import java.io.IOException;

class DropOffGuiContainerCreative extends CreativeScreen {

  private final DropOffGuiButton dropOffGuiButton;

  DropOffGuiContainerCreative(ClientPlayerEntity player) {
    super(player);

    dropOffGuiButton = new DropOffGuiButton(this::actionPerformed);
  }

  @Override
  public void init() {
    super.init();

    int xPos = super.width / 2 + DropOffConfig.Client.creativeInventoryButtonXOffset.get();
    int yPos = super.height / 2 + DropOffConfig.Client.creativeInventoryButtonYOffset.get();

    dropOffGuiButton.x = xPos;
    dropOffGuiButton.y = yPos;

    super.buttons.add(dropOffGuiButton);
  }

  protected void actionPerformed(@Nonnull Button button) {
    if (button == dropOffGuiButton) {
      ClientUtils.sendNoSpectator();
    }
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    super.render(mouseX, mouseY, partialTicks);

    dropOffGuiButton.visible = super.getSelectedTabIndex() == ItemGroup.INVENTORY.getIndex();

    if (dropOffGuiButton.isHovered()) {
      super.renderTooltip(dropOffGuiButton.hoverText, mouseX, mouseY, super.font);
    }
  }

}
