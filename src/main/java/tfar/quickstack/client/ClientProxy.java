package tfar.quickstack.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import tfar.quickstack.DropOff;

public class ClientProxy {

    static KeyMapping dump;
    static KeyMapping deposit;

    public static void init(FMLClientSetupEvent event) {

        dump = new KeyMapping("dropoff.key.dump", GLFW.GLFW_KEY_X, DropOff.MOD_ID);
        deposit = new KeyMapping("dropoff.key.deposit", GLFW.GLFW_KEY_C, DropOff.MOD_ID);
        ClientRegistry.registerKeyBinding(dump);
        ClientRegistry.registerKeyBinding(deposit);

        MinecraftForge.EVENT_BUS.register(RenderWorldLastEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
        MinecraftForge.EVENT_BUS.addListener(ClientProxy::onKeyInput);
    }

    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (dump.isDown()) {
            ClientUtils.sendNoSpectator(true);
        } else if (deposit.isDown()) {
            ClientUtils.sendNoSpectator(false);
        }
    }
}
