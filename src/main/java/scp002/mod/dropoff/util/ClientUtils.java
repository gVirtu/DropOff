package scp002.mod.dropoff.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import scp002.mod.dropoff.DropOff;
import scp002.mod.dropoff.message.C2SPacketRequestDropoff;
import scp002.mod.dropoff.message.PacketHandler;

public class ClientUtils {

    public static void printToChat(String message) {
        message = "[" + TextFormatting.BLUE + DropOff.MOD_NAME + TextFormatting.RESET + "]: " + message;

        ClientPlayerEntity player = Minecraft.getInstance().player;
        StringTextComponent textComponentString = new StringTextComponent(message);

        player.sendMessage(textComponentString);
    }

    public static void playSound(SoundEvent soundEvent) {
        SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
        SimpleSound record = SimpleSound.master(soundEvent, 1.0f);

        soundHandler.play(record);
    }

    public static void sendNoSpectator() {
        if (Minecraft.getInstance().player.isSpectator()) {
            printToChat("Action do not allowed in spectator mode.");
        } else {
            PacketHandler.INSTANCE.sendToServer(new C2SPacketRequestDropoff());
        }
    }

}
