package dev.matthe815.mmoparties.fabric.events;

import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.common.events.EventCommon;
import dev.matthe815.mmoparties.common.stats.Party;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;

public class EventCommonFabric {
    /**
     * Global player instance, gets filled when the first player joins a server with global parties enabled.
     * All players that join after will be put into this party.
     */
    public static Party globalParty = null;

    public static void registerEvents() {
        // Player joined event (called when a player joins the server)
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            System.out.println("Join");
            EventCommon.onPlayerJoined(handler.player);
        });

        // Player left event (called when a player leaves the server)
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            // Handle player leaving
            EventCommon.onPlayerLeave(handler.player);
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            server.getPlayerList().getPlayers().forEach(serverPlayer -> {
                PlayerStats stats = MMOPartiesCommon.GetStats(serverPlayer);
                if (stats == null) return; // Don't know why there wouldn't be a stats but Minecraft Forge is weird.

                stats.TickTeleport(); // Tick a teleport step.

                if (stats.party != null) stats.party.SendPartyMemberData(serverPlayer, false, false); // Sync the player.
            });
        });

        // Player hurt event (for canceling PvP damage)
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof Player && source.getDirectEntity() instanceof Player) {
                // Call the event method to check if damage should be canceled
                return EventCommon.OnPlayerHurt(entity, source.getDirectEntity());
            }
            return true;
        });
    }
}
