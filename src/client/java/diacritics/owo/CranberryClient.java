package diacritics.owo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.lwjgl.glfw.GLFW;
import com.ibm.icu.impl.Pair;
import diacritics.owo.gui.screen.MusicScreen;
import diacritics.owo.network.C2SListeningPacket;
import diacritics.owo.network.S2CListeningPacket;
import diacritics.owo.util.Artwork;
import diacritics.owo.util.CranberryHelpers;
import diacritics.owo.util.Media;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class CranberryClient implements ClientModInitializer {
	public static final Map<UUID, Pair<Media.Track, Artwork>> LISTENING = new HashMap<>();
	private static String lastId = null;
	private static Artwork icon = new Artwork(CranberryHelpers.ICON_SIZE.width(), CranberryHelpers.ICON_SIZE.height());

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

		initializeListeners();

		Cranberry.UWU.registerClientbound(S2CListeningPacket.class, (message, access) -> {
			// TODO: update more efficiently
			if (!message.player().equals(access.player().getUuid())) {
				LISTENING.put(message.player(), Pair.of(message.track(), Artwork.from(message.artwork())));
			}
		});
	}

	public static native void initializeListeners();

	// TODO: send packet on world enter
	// TODO: use the listener to update a global track instance
	public static void sendPacket() {
		if (MinecraftClient.getInstance().getNetworkHandler() != null
				&& MinecraftClient.getInstance().getNetworkHandler().isConnectionOpen()) {
			Media.Track track = Media.track();

			if (!icon.cached()) {
				icon.reload();
			}

			Cranberry.UWU.clientHandle().send(new C2SListeningPacket(track.nonNull(), icon.artworkData()));
		}
	}

	static {
		System.load(FabricLoader.getInstance().getModContainer(Cranberry.MOD_ID).get()
				.findPath("assets/cranberry/libCranberry.dylib").get().toAbsolutePath().toString());
	}
}
