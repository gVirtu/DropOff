package scp002.quickstack.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import scp002.quickstack.DropOff;
import scp002.quickstack.config.DropOffConfig;

public class ClientProxy {

    static KeyBinding dump;
    static KeyBinding deposit;

    public static void init(FMLClientSetupEvent event) {

        dump = new KeyBinding(DropOff.MOD_ID, GLFW.GLFW_KEY_X, DropOff.MOD_ID);
        deposit = new KeyBinding(DropOff.MOD_ID, GLFW.GLFW_KEY_C, DropOff.MOD_ID);
        ClientRegistry.registerKeyBinding(dump);
        ClientRegistry.registerKeyBinding(deposit);

        MinecraftForge.EVENT_BUS.register(RenderWorldLastEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
        MinecraftForge.EVENT_BUS.addListener(ClientProxy::onKeyInput);
        MinecraftForge.EVENT_BUS.register(new DropOffConfig.ConfigChangeEventHandler());
    }

    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (dump.isPressed()) {
            ClientUtils.sendNoSpectator(true);
        } else if (deposit.isPressed()) {
            ClientUtils.sendNoSpectator(false);
        }
    }
}
