package diacritics.owo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.lwjgl.glfw.GLFW;
import com.ibm.icu.impl.Pair;
import diacritics.owo.config.ClientConfig;
import diacritics.owo.gui.screen.MusicScreen;
import diacritics.owo.network.C2SListeningPacket;
import diacritics.owo.network.C2SRequestPollListeningPacket;
import diacritics.owo.network.C2SStopListeningPacket;
import diacritics.owo.network.S2CListeningPacket;
import diacritics.owo.network.S2CPollListeningPacket;
import diacritics.owo.network.S2CStopListeningPacket;
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

// TODO: use the listener to update a global track instance
public class CranberryClient implements ClientModInitializer {
	public static final ClientConfig CONFIG = ClientConfig.createAndLoad();

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

		CONFIG.subscribeToSendStatus(send -> {
			if (send) {
				sendListeningPacket();
			} else {
				sendStopListeningPacket();
			}
		});

		CONFIG.subscribeToReceiveStatus(receive -> {
			if (receive) {
				sendRequestPollPacket();
			} else {
				LISTENING.clear();
			}
		});

		initializeListeners();

		Cranberry.UWU.registerClientbound(S2CListeningPacket.class, (message, access) -> {
			if (!CONFIG.receiveStatus())
				return;

			// TODO: update more efficiently
			if (!message.player().equals(access.player().getUuid())) {
				LISTENING.put(message.player(), Pair.of(message.track(), Artwork.from(message.artwork())));
			}
		});

		Cranberry.UWU.registerClientbound(S2CPollListeningPacket.class, (message, access) -> {
			sendListeningPacket();
		});

		Cranberry.UWU.registerClientbound(S2CStopListeningPacket.class, (message, access) -> {
			LISTENING.remove(message.player());
		});
	}

	public static native void initializeListeners();

	public static void sendListeningPacket() {
		if (!CONFIG.sendStatus())
			return;

		if (MinecraftClient.getInstance().getNetworkHandler() != null
				&& MinecraftClient.getInstance().getNetworkHandler().isConnectionOpen()) {
			Media.Track track = Media.track();

			if (!track.valid()) {
				sendStopListeningPacket();
			} else {
				if (!icon.cached()) {
					icon.reload();
				}

				Cranberry.UWU.clientHandle().send(new C2SListeningPacket(track.nonNull(), icon.artworkData()));
			}
		}
	}

	public static void sendRequestPollPacket() {
		if (!CONFIG.receiveStatus())
			return;

		if (MinecraftClient.getInstance().getNetworkHandler() != null
				&& MinecraftClient.getInstance().getNetworkHandler().isConnectionOpen()) {
			Cranberry.UWU.clientHandle().send(new C2SRequestPollListeningPacket());
		}
	}

	public static void sendStopListeningPacket() {
		Cranberry.UWU.clientHandle().send(new C2SStopListeningPacket());
	}

	static {
		System.load(FabricLoader.getInstance().getModContainer(Cranberry.MOD_ID).get()
				.findPath("assets/cranberry/libCranberry.dylib").get().toAbsolutePath().toString());
	}
}
