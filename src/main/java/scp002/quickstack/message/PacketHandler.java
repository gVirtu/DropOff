package scp002.quickstack.message;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import scp002.quickstack.DropOff;

public class PacketHandler {
  public static SimpleChannel INSTANCE;

  public static void registerMessages() {
    int id = 0;

    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DropOff.MOD_ID, DropOff.MOD_ID), () -> "1.0", s -> true, s -> true);

    INSTANCE.registerMessage(id++, C2SPacketRequestDropoff.class,
            C2SPacketRequestDropoff::encode,
            C2SPacketRequestDropoff::new,
            C2SPacketRequestDropoff::handle);

    INSTANCE.registerMessage(id++, S2CReportPacket.class,
            S2CReportPacket::encode,
            S2CReportPacket::new,
            S2CReportPacket::handle);

    INSTANCE.registerMessage(id, C2SFavoriteItemPacket.class,
            C2SFavoriteItemPacket::encode,
            C2SFavoriteItemPacket::new,
            C2SFavoriteItemPacket::handle);
  }
}
