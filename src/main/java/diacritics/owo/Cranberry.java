package diacritics.owo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import diacritics.owo.network.C2SListeningPacket;
import diacritics.owo.network.C2SRequestPollListeningPacket;
import diacritics.owo.network.C2SStopListeningPacket;
import diacritics.owo.network.S2CListeningPacket;
import diacritics.owo.network.S2CPollListeningPacket;
import diacritics.owo.network.S2CStopListeningPacket;
import io.wispforest.owo.network.OwoNetChannel;
import java.io.File;

public class Cranberry implements ModInitializer {
	public static final String MOD_ID = "cranberry";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean enabled = false;

	public static File enableFile = new File(
			FabricLoader.getInstance().getConfigDir().resolve(".cranberryenable").toString());
	public static File disableFile = new File(
			FabricLoader.getInstance().getConfigDir().resolve(".cranberrydisable").toString());

	public static OwoNetChannel UWU = OwoNetChannel.create(identifier("uwu"));

	@Override
	public void onInitialize() {
		// TODO: move to client initializer
		if (enableFile.exists() || disableFile.exists()) {
			enabled = enableFile.exists();
			LOGGER.info("detected an override file - cranberry will be {}abled", enabled ? "en" : "dis");
		} else {
			if (System.getProperty("os.name").toLowerCase().contains("mac")) {
				LOGGER.info("os detected as macos - cranberry will enable itself");
				enabled = true;
			} else {
				LOGGER.warn("os not detected as macos - cranberry will not be enabled");
			}

			LOGGER.info(
					"os detection can be overriden by creating a file called .cranberryenable or .cranberrydisable in the config folder");
		}

		if (enabled) {
			UWU.registerServerbound(C2SListeningPacket.class, (message, access) -> {
				// TODO: validate data length
				UWU.serverHandle(access.runtime()).send(S2CListeningPacket.from(access.player().getUuid(), message));
			});

			// TODO: configurable
			UWU.registerServerbound(C2SRequestPollListeningPacket.class, (message, access) -> {
				UWU.serverHandle(access.runtime()).send(new S2CPollListeningPacket());
			});

			UWU.registerServerbound(C2SStopListeningPacket.class, (message, access) -> {
				UWU.serverHandle(access.runtime()).send(new S2CStopListeningPacket(access.player().getUuid()));
			});
		}
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
