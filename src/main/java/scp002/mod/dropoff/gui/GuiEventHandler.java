package scp002.mod.dropoff.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import scp002.mod.dropoff.config.DropOffConfig;
import scp002.mod.dropoff.message.C2SFavoriteItemPacket;
import scp002.mod.dropoff.message.PacketHandler;
import scp002.mod.dropoff.util.ClientUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

  @SubscribeEvent
  @SuppressWarnings("unchecked")
  public <T extends Container> void onGuiOpen(GuiScreenEvent.MouseClickedEvent.Pre event) {
    if (event.getGui() instanceof InventoryScreen && Screen.hasControlDown()){
      ContainerScreen<T> containerScreen = (ContainerScreen<T>)event.getGui();
      double mouseX = event.getMouseX();
      double mouseY = event.getMouseY();

      Slot slot = getSelectedSlot(containerScreen,mouseX,mouseY);
      if (slot != null) {
        event.setCanceled(true);
        PacketHandler.INSTANCE.sendToServer(new C2SFavoriteItemPacket(slot.slotNumber));
      }
    }
  }

  @Nullable
  private <T extends Container> Slot getSelectedSlot(ContainerScreen<T> containerScreen, double mouseX, double mouseY) {
    for(int i = 0; i < containerScreen.getContainer().inventorySlots.size(); ++i) {
      Slot slot = containerScreen.getContainer().inventorySlots.get(i);
      if (containerScreen.isSlotSelected(slot, mouseX, mouseY) && slot.isEnabled()) {
        return slot;
      }
    }
    return null;
  }

  @SubscribeEvent
  public void tooltip(ItemTooltipEvent e){
    ItemStack stack = e.getItemStack();
    if (stack.hasTag() && stack.getTag().getBoolean("favorite")){
      e.getToolTip().add(new StringTextComponent("Favorited!"));
    }
  }
}
