package scp002.mod.dropoff;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import scp002.mod.dropoff.config.ConfigChangeEventHandler;
import scp002.mod.dropoff.gui.GuiOpenEventHandler;
import scp002.mod.dropoff.gui.GuiScreenEventHandler;
import scp002.mod.dropoff.render.RenderWorldLastEventHandler;

@Mod.EventBusSubscriber(value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(KeyInputEventHandler.INSTANCE.mainTaskKeyBinding);

        MinecraftForge.EVENT_BUS.register(RenderWorldLastEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new GuiOpenEventHandler());
        MinecraftForge.EVENT_BUS.register(GuiScreenEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(KeyInputEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(ConfigChangeEventHandler.INSTANCE);
    }
}
