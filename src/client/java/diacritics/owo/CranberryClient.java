package diacritics.owo;

import java.io.File;
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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class CranberryClient implements ClientModInitializer {
	public static final ClientConfig CONFIG = ClientConfig.createAndLoad();

	public static final Map<UUID, Pair<Media.Track, Artwork>> LISTENING = new HashMap<>();
	private static String lastId = null;
	private static Artwork icon = new Artwork(CranberryHelpers.ICON_SIZE.width(), CranberryHelpers.ICON_SIZE.height());

	public static boolean enabled = false;
	public static File enableFile = new File(
			FabricLoader.getInstance().getConfigDir().resolve(".cranberryenable").toString());
	public static File disableFile = new File(
			FabricLoader.getInstance().getConfigDir().resolve(".cranberrydisable").toString());

	private static KeyBinding open;

	@Override
	public void onInitializeClient() {
		if (enableFile.exists() || disableFile.exists()) {
			enabled = enableFile.exists();
			Cranberry.LOGGER.info("detected an override file - cranberry will be {}abled", enabled ? "en" : "dis");
		} else {
			if (System.getProperty("os.name").toLowerCase().contains("mac")) {
				Cranberry.LOGGER.info("os detected as macos - cranberry will enable itself");
				enabled = true;
			} else {
				Cranberry.LOGGER.warn("os not detected as macos - cranberry will not be enabled");
			}

			Cranberry.LOGGER.info(
					"os detection can be overriden by creating a file called .cranberryenable or .cranberrydisable in the config folder");
		}

		if (!enabled) {
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

			System.out.println(payload.track().title());
			// TODO: update more efficiently
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

				// TODO: is nonnull necessary?
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
