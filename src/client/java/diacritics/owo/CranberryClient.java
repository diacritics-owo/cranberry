package diacritics.owo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.SystemUtils;
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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Util.OperatingSystem;

public class CranberryClient implements ClientModInitializer {
	public static final ClientConfig CONFIG = ClientConfig.createAndLoad();
	public static OperatingSystem OS = OperatingSystem.UNKNOWN;

	public static final Map<UUID, Pair<Media.Track, Artwork>> LISTENING = new HashMap<>();
	private static String lastId = null;
	private static Artwork icon = new Artwork(CranberryHelpers.ICON_SIZE.width(), CranberryHelpers.ICON_SIZE.height());

	private static KeyBinding open;

	// TODO: os overrides
	@Override
	public void onInitializeClient() {
		OS = SystemUtils.IS_OS_MAC ? OperatingSystem.OSX
				: (SystemUtils.IS_OS_LINUX ? OperatingSystem.LINUX
						: (SystemUtils.IS_OS_WINDOWS ? OperatingSystem.WINDOWS : OperatingSystem.UNKNOWN));

		switch (OS) {
			case OperatingSystem.OSX:
				Cranberry.LOGGER.info("macos - cranberry will be enabled");
				break;
			default:
				Cranberry.LOGGER.info("unsupported operating system ({}) - cranberry will be disabled", OS.getName());
				return;
		}

		System.load(FabricLoader.getInstance().getModContainer(Cranberry.MOD_ID).get()
				.findPath("assets/cranberry/libCranberry.dylib").get().toAbsolutePath().toString());

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

		ClientPlayNetworking.registerGlobalReceiver(S2CListeningPacket.ID, (payload, context) -> {
			if (!CONFIG.receiveStatus())
				return;

			if (!payload.player().equals(context.player().getUuid())) {
				LISTENING.put(payload.player(), Pair.of(payload.track(), Artwork.from(payload.artwork())));
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(S2CPollListeningPacket.ID, (payload, context) -> {
			sendListeningPacket();
		});

		ClientPlayNetworking.registerGlobalReceiver(S2CStopListeningPacket.ID, (payload, context) -> {
			LISTENING.remove(payload.player());
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

				ClientPlayNetworking.send(new C2SListeningPacket(track.nonNull(), icon.artworkData()));
			}
		}
	}

	public static void sendRequestPollPacket() {
		if (!CONFIG.receiveStatus())
			return;

		if (MinecraftClient.getInstance().getNetworkHandler() != null
				&& MinecraftClient.getInstance().getNetworkHandler().isConnectionOpen()) {
			ClientPlayNetworking.send(new C2SRequestPollListeningPacket());
		}
	}

	public static void sendStopListeningPacket() {
		ClientPlayNetworking.send(new C2SStopListeningPacket());
	}
}
