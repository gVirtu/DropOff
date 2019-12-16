package scp002.quickstack.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import scp002.quickstack.DropOff;

public class KeyInputEventHandler {

  static final KeyInputEventHandler INSTANCE = new KeyInputEventHandler();

  static KeyBinding dump;
  static KeyBinding deposit;

  private KeyInputEventHandler() {
    dump = new KeyBinding(DropOff.MOD_NAME, GLFW.GLFW_KEY_X, DropOff.MOD_NAME);
    deposit = new KeyBinding(DropOff.MOD_NAME, GLFW.GLFW_KEY_C, DropOff.MOD_NAME);
  }

  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event) {
    if (dump.isPressed()) {
      ClientUtils.sendNoSpectator(true);
    } else if (deposit.isPressed()) {
      ClientUtils.sendNoSpectator(false);
    }
  }
}
