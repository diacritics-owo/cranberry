package diacritics.owo;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import diacritics.owo.network.C2SListeningPacket;
import diacritics.owo.network.C2SRequestPollListeningPacket;
import diacritics.owo.network.C2SStopListeningPacket;
import diacritics.owo.network.S2CListeningPacket;
import diacritics.owo.network.S2CPollListeningPacket;
import diacritics.owo.network.S2CStopListeningPacket;
import diacritics.owo.util.CranberryHelpers;
import io.wispforest.owo.network.OwoNetChannel;

public class Cranberry implements ModInitializer {
	public static final String MOD_ID = "cranberry";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static OwoNetChannel UWU = OwoNetChannel.create(identifier("uwu"));

	@Override
	public void onInitialize() {
		UWU.registerServerbound(C2SListeningPacket.class, (message, access) -> {
			if (message.artwork().data().length() != CranberryHelpers.ICON_DATA_LENGTH) {
				LOGGER.warn("rejected packet from player {} (uuid {}) due to incorrect icon data length {} (expected {})",
						access.player().getGameProfile().getName(), access.player().getUuid(), message.artwork().data().length(),
						CranberryHelpers.ICON_DATA_LENGTH);
				return;
			}

			UWU.serverHandle(access.runtime()).send(S2CListeningPacket.from(access.player().getUuid(), message));
		});

		UWU.registerServerbound(C2SRequestPollListeningPacket.class, (message, access) -> {
			UWU.serverHandle(access.runtime()).send(new S2CPollListeningPacket());
		});

		UWU.registerServerbound(C2SStopListeningPacket.class, (message, access) -> {
			UWU.serverHandle(access.runtime()).send(new S2CStopListeningPacket(access.player().getUuid()));
		});
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
