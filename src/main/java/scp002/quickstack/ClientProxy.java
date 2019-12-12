package scp002.quickstack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import scp002.quickstack.config.DropOffConfig;
import scp002.quickstack.gui.GuiEventHandler;
import scp002.quickstack.render.RenderWorldLastEventHandler;

@Mod.EventBusSubscriber(value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(KeyInputEventHandler.INSTANCE.dump);
        ClientRegistry.registerKeyBinding(KeyInputEventHandler.INSTANCE.deposit);

        MinecraftForge.EVENT_BUS.register(RenderWorldLastEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
        MinecraftForge.EVENT_BUS.register(KeyInputEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(DropOffConfig.ConfigChangeEventHandler.INSTANCE);
    }
}
