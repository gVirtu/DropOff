package scp002.mod.dropoff.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import scp002.mod.dropoff.util.ClientUtils;

public class GuiScreenEventHandler {

    public static final GuiScreenEventHandler INSTANCE = new GuiScreenEventHandler();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onActionPreformed(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (!(event.getGui() instanceof InventoryScreen || event.getGui() instanceof CreativeScreen) ||
                !event.getButton().getClass().getName().equals("vazkii.quark.management.client.gui.GuiButtonChest") ||
                !Screen.hasShiftDown()) {
            return;
        }

        event.setCanceled(true);

        ClientUtils.sendNoSpectator();
    }

}
