package scp002.mod.dropoff.gui;

import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import scp002.mod.dropoff.config.DropOffConfig;
import scp002.mod.dropoff.util.ClientUtils;

import javax.annotation.Nonnull;

public class GuiEventHandler {

  @SubscribeEvent
  public void onGuiOpen(GuiScreenEvent.InitGuiEvent event) {
    if (!(event.getGui() instanceof InventoryScreen || event.getGui() instanceof CreativeScreen) ||
            !DropOffConfig.Client.showInventoryButton.get()) {
      return;
    }


    int xPos = event.getGui().width / 2 + 50;
    int yPos = event.getGui().height / 2 - 18;

    Button dump = new DropOffGuiButton(xPos, yPos, this::actionPerformed, true);
    Button deposit = new DropOffGuiButton(xPos + 12, yPos, this::actionPerformed, false);
    event.addWidget(dump);
    event.addWidget(deposit);
  }

  protected void actionPerformed(@Nonnull net.minecraft.client.gui.widget.button.Button button) {
    ClientUtils.sendNoSpectator(((DropOffGuiButton) button).dump);
  }
}
