package scp002.quickstack.message;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import scp002.quickstack.DropOff;

public class PacketHandler {
  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    int id = 0;

    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DropOff.MOD_ID, channelName), () -> "1.0", s -> true, s -> true);

    INSTANCE.registerMessage(id++, C2SPacketRequestDropoff.class,
            C2SPacketRequestDropoff::encode,
            C2SPacketRequestDropoff::new,
            C2SPacketRequestDropoff::handle);

    INSTANCE.registerMessage(id++, S2CPacketReportPacket.class,
            S2CPacketReportPacket::encode,
            S2CPacketReportPacket::new,
            S2CPacketReportPacket::handle);

    INSTANCE.registerMessage(id, C2SFavoriteItemPacket.class,
            C2SFavoriteItemPacket::encode,
            C2SFavoriteItemPacket::new,
            C2SFavoriteItemPacket::handle);
  }
}
