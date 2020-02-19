package scp002.quickstack.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import scp002.quickstack.DropOff;
import scp002.quickstack.config.DropOffConfig;

@Mod.EventBusSubscriber(value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {

        KeyInputEventHandler.dump = new KeyBinding(DropOff.MOD_ID, GLFW.GLFW_KEY_X, DropOff.MOD_ID);
        KeyInputEventHandler.deposit = new KeyBinding(DropOff.MOD_ID, GLFW.GLFW_KEY_C, DropOff.MOD_ID);
        ClientRegistry.registerKeyBinding(KeyInputEventHandler.dump);
        ClientRegistry.registerKeyBinding(KeyInputEventHandler.deposit);

        MinecraftForge.EVENT_BUS.register(RenderWorldLastEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
        MinecraftForge.EVENT_BUS.register(KeyInputEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new DropOffConfig.ConfigChangeEventHandler());
    }
}
