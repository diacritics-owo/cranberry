package diacritics.owo;

import org.lwjgl.glfw.GLFW;
import diacritics.owo.gui.screen.MusicScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class CranberryClient implements ClientModInitializer {
	private static KeyBinding open;

	@Override
	public void onInitializeClient() {
		if (!Cranberry.enabled) {
			return;
		}

		open = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.cranberry.open",
				InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "category.cranberry.keybindings"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (open.wasPressed()) {
				MinecraftClient.getInstance().setScreen(new MusicScreen());
			}
		});
	}
}
