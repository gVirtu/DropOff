package scp002.mod.dropoff.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import scp002.mod.dropoff.config.DropOffConfig;

public class GuiOpenEventHandler {

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (!(event.getGui() instanceof InventoryScreen || event.getGui() instanceof CreativeScreen)||
                !DropOffConfig.Client.showInventoryButton.get()) {
            return;
        }

        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player.abilities.isCreativeMode) {
            event.setGui(new DropOffGuiContainerCreative(player));
        } else {
            event.setGui(new DropOffGuiInventory(player));
        }
    }

}
