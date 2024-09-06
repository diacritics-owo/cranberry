package diacritics.owo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

public class Cranberry implements ModInitializer {
	public static final String MOD_ID = "cranberry";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean enabled = false;

	public static File enableFile =
			new File(FabricLoader.getInstance().getConfigDir().resolve(".cranberryenable").toString());
	public static File disableFile =
			new File(FabricLoader.getInstance().getConfigDir().resolve(".cranberrydisable").toString());

	@Override
	public void onInitialize() {
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
	}
}
