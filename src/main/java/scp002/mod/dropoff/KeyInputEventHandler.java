package scp002.mod.dropoff;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import scp002.mod.dropoff.util.ClientUtils;

public class KeyInputEventHandler {

  static final KeyInputEventHandler INSTANCE = new KeyInputEventHandler();

  final KeyBinding dump;
  final KeyBinding deposit;

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
