package tfar.quickstack.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.quickstack.DropOff;
import tfar.quickstack.config.DropOffConfig;
import tfar.quickstack.message.C2SFavoriteItemPacket;
import tfar.quickstack.message.PacketHandler;
import tfar.quickstack.util.Utils;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.client.gui.GuiComponent.fill;

public class GuiEventHandler {

	@SubscribeEvent
	public void onGuiOpen(ScreenEvent.InitScreenEvent.Post event) {
		if (!canDisplay(event.getScreen()) || !DropOffConfig.Client.showInventoryButton.get()) {
			return;
		}

		AbstractContainerScreen containerScreen = (AbstractContainerScreen) event.getScreen();

		boolean isCreative = Minecraft.getInstance().player.getAbilities().instabuild;

		int xPos = containerScreen.getGuiLeft() + 80 +
						(isCreative ? DropOffConfig.Client.creativeInventoryButtonXOffset.get() :
										DropOffConfig.Client.survivalInventoryButtonXOffset.get());
		int yPos = containerScreen.getGuiTop() + 80 + (isCreative ?
						DropOffConfig.Client.creativeInventoryButtonYOffset.get() :
						DropOffConfig.Client.survivalInventoryButtonYOffset.get());
		if (DropOffConfig.Client.enableDump.get()) {
			Button dump = new DropOffGuiButton(xPos, yPos, this::actionPerformed, true);
			event.addListener(dump);
		}
		Button deposit = new DropOffGuiButton(xPos + 12, yPos, this::actionPerformed, false);
		event.addListener(deposit);
	}

	protected void actionPerformed(@Nonnull Button button) {
		ClientUtils.sendNoSpectator(((DropOffGuiButton) button).dump);
	}

	@SubscribeEvent
	@SuppressWarnings("unchecked")
	public <T extends AbstractContainerMenu> void onItemClick(ScreenEvent.MouseClickedEvent.Pre event) {
		if (!canDisplay(event.getScreen()) || !(event.getScreen() instanceof InventoryScreen) || !Screen.hasControlDown()) return;
		InventoryScreen containerScreen = (InventoryScreen) event.getScreen();

		Slot slot = containerScreen.getSlotUnderMouse();
		if (slot != null && slot.hasItem())
		{
			event.setCanceled(true);
			PacketHandler.INSTANCE.sendToServer(new C2SFavoriteItemPacket(containerScreen.getSlotUnderMouse().index));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	@SuppressWarnings("unchecked")
	public <T extends AbstractContainerMenu> void drawFavorites(ContainerScreenEvent.DrawBackground event) {
		AbstractContainerScreen<T> containerScreen = (AbstractContainerScreen<T>) event.getContainerScreen();
		if (!canDisplay(containerScreen)) return;
		T playerContainer = containerScreen.getMenu();
		PoseStack matrices = event.getPoseStack();

		for (int k = 0; k < 3; ++k) {
			for (int j = 0; j < 9; ++j) {
				Slot slot = playerContainer.slots.get(j + (k + 1) * 9);
				if (slot == null) continue;
				ItemStack stack = slot.getItem();
				if (Utils.isFavorited(stack)) {
					int xoffset = 8;
					int yoffset = 84;
					fill(matrices,containerScreen.getGuiLeft() + j * 18 + xoffset,
									containerScreen.getGuiTop() + k * 18 + yoffset,
									containerScreen.getGuiLeft() + j * 18 + 16 + xoffset,
									containerScreen.getGuiTop() + k * 18 + 16 + yoffset,
									0xFFFFBB00);
				}
			}
		}
		List<ItemStack> stacks = playerContainer.getItems();

		for (int i = 0; i < 9; ++i) {
			ItemStack stack = stacks.get(i + 36);
			if (Utils.isFavorited(stack)) {
				int xoffset = 8;
				int yoffset = 142;
				fill(matrices,containerScreen.getGuiLeft() + i * 18 + xoffset,
								containerScreen.getGuiTop() + yoffset,
								containerScreen.getGuiLeft() + i * 18 + 16 + xoffset,
								containerScreen.getGuiTop() + 16 + yoffset,
								0xFFFFBB00);
			}
		}

		RenderSystem.clearColor(1, 1, 1, 1);
	}

	public boolean canDisplay(Screen screen) {
		return screen instanceof AbstractContainerScreen && canDisplay((AbstractContainerScreen) screen);
	}

	private static final Set<Class<?>> bad_classes = new HashSet<>();

	public <T extends AbstractContainerMenu> boolean canDisplay(AbstractContainerScreen<T> screen) {
		if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) return true;
		if (screen instanceof HorseInventoryScreen) return false;
		try {
			return DropOffConfig.Client.whitelistedContainers.get().contains(screen.getMenu().getType().getRegistryName().toString());
		} catch (Exception e) {
			Class<?> clazz = screen.getMenu().getClass();
			if (bad_classes.contains(clazz)) {
				DropOff.LOGGER.error(clazz + " does not have a container type registered to it! " +
								"This is a bug in the other mod and should be reported to them.  The buttons will not display in this gui!");
				bad_classes.add(clazz);
			}
			return false;
		}
	}

	@SubscribeEvent
	public void tooltip(ItemTooltipEvent e) {
		ItemStack stack = e.getItemStack();
		if (stack.hasTag() && stack.getTag().getBoolean("favorite")) {
			e.getToolTip().add(new TextComponent("Favorited!"));
		}
	}
}
