package diacritics.owo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import diacritics.owo.network.C2SListeningPacket;
import diacritics.owo.network.C2SRequestPollListeningPacket;
import diacritics.owo.network.C2SStopListeningPacket;
import diacritics.owo.network.S2CListeningPacket;
import diacritics.owo.network.S2CPollListeningPacket;
import diacritics.owo.network.S2CStopListeningPacket;

public class Cranberry implements ModInitializer {
	public static final String MOD_ID = "cranberry";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(C2SListeningPacket.ID, C2SListeningPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(C2SRequestPollListeningPacket.ID, C2SRequestPollListeningPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(C2SStopListeningPacket.ID, C2SStopListeningPacket.CODEC);

		PayloadTypeRegistry.playS2C().register(S2CListeningPacket.ID, S2CListeningPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(S2CPollListeningPacket.ID, S2CPollListeningPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(S2CStopListeningPacket.ID, S2CStopListeningPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(C2SListeningPacket.ID, (payload, context) -> {
			PlayerLookup.all(context.server())
					.forEach(player -> ServerPlayNetworking.send(player, S2CListeningPacket.from(player.getUuid(), payload)));
		});

		ServerPlayNetworking.registerGlobalReceiver(C2SRequestPollListeningPacket.ID, (payload, context) -> {
			PlayerLookup.all(context.server())
					.forEach(player -> ServerPlayNetworking.send(player, new S2CPollListeningPacket()));
		});

		ServerPlayNetworking.registerGlobalReceiver(C2SStopListeningPacket.ID, (payload, context) -> {
			PlayerLookup.all(context.server())
					.forEach(player -> ServerPlayNetworking.send(player, new S2CStopListeningPacket(player.getUuid())));
		});
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
