package diacritics.owo;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cranberry implements ModInitializer {
	public static final String MOD_ID = "cranberry";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("hello from cranberry!");
	}
}
