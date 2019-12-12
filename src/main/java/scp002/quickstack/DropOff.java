package scp002.quickstack;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scp002.quickstack.message.PacketHandler;
import scp002.quickstack.util.LogMessageFactory;
import scp002.quickstack.config.DropOffConfig;

@SuppressWarnings("unused")
@Mod(DropOff.MOD_ID)
public class DropOff {

    public static final String MOD_ID = "dropoff";
    public static final String MOD_NAME = "DropOff";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID, LogMessageFactory.INSTANCE);

    static final String MOD_VERSION = "1.12.2-1.0.2b";
    static final String GUI_FACTORY = "scp002.mod.dropoff.gui.GuiFactory";

    DropOff dropOff;

    public DropOff(){
        dropOff = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DropOffConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, DropOffConfig.SERVER_SPEC);
    }



    private void preInit(FMLCommonSetupEvent event) {
        LOGGER.info("Beginning pre-initialization...");
        PacketHandler.registerMessages(MOD_ID);
    }
}
