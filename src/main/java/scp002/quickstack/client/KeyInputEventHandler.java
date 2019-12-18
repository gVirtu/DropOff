package scp002.quickstack.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyInputEventHandler {

  static final KeyInputEventHandler INSTANCE = new KeyInputEventHandler();

  static KeyBinding dump;
  static KeyBinding deposit;

  private KeyInputEventHandler() {
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
