package tfar.quickstack;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.quickstack.client.ClientProxy;
import tfar.quickstack.config.DropOffConfig;
import tfar.quickstack.message.PacketHandler;
import tfar.quickstack.util.LogMessageFactory;

@Mod(DropOff.MOD_ID)
public class DropOff {

    public static final String MOD_ID = "quickstack";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID, LogMessageFactory.INSTANCE);

    public DropOff() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::preInit);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DropOffConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, DropOffConfig.SERVER_SPEC);
        bus.addListener(DropOffConfig::onConfigChanged);
        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(ClientProxy::init);
        }
    }

    private void preInit(FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }
}
