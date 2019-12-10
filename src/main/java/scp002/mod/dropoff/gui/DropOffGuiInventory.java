package scp002.mod.dropoff.gui;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import scp002.mod.dropoff.DropOff;
import scp002.mod.dropoff.config.DropOffConfig;
import scp002.mod.dropoff.message.MainMessage;
import scp002.mod.dropoff.util.ClientUtils;

import javax.annotation.Nonnull;
import java.io.IOException;

class DropOffGuiInventory extends InventoryScreen {

    private final DropOffGuiButton dropOffGuiButton;

    DropOffGuiInventory(ClientPlayerEntity player) {
        super(player);

        dropOffGuiButton = new DropOffGuiButton(this::actionPerformed);
    }

    @Override
    public void init() {
        super.init();

        int xPos = super.width / 2 + DropOffConfig.Client.survivalInventoryButtonXOffset.get();
        int yPos = super.height / 2 + DropOffConfig.Client.survivalInventoryButtonYOffset.get();

        dropOffGuiButton.x = xPos;
        dropOffGuiButton.y = yPos;

        super.buttons.add(dropOffGuiButton);
    }

    protected void actionPerformed(@Nonnull Button button) {
        if (button == dropOffGuiButton) {
            ClientUtils.sendNoSpectator();
        } else {

        }
    }


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (dropOffGuiButton.isHovered()) {
            super.renderTooltip(dropOffGuiButton.hoverText, mouseX, mouseY, super.font);
        }    }
}
