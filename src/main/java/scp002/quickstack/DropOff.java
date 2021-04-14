package scp002.quickstack;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scp002.quickstack.client.ClientProxy;
import scp002.quickstack.config.DropOffConfig;
import scp002.quickstack.message.PacketHandler;
import scp002.quickstack.util.LogMessageFactory;

@Mod(DropOff.MOD_ID)
public class DropOff {

    public static final String MOD_ID = "dropoff";
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
