package dev.matthe815.mmoparties.fabric.events;

import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.screens.PartyScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class EventClientFabric {
    /**
     * Handles removing any temporary data when leaving a world/server
     */
    public static void registerEvents() {
        // Register server disconnect handler
        ClientLoginConnectionEvents.DISCONNECT.register((handler, client) -> {
            // Clear local party data on logout
            MMOPartiesCommon.localParty = null;
        });

        // Register key input handler (for GUI opening)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MMOPartiesCommon.OPEN_GUI_KEY.isDown()) {
                // Handle opening the party menu when the key is pressed
                if (MMOPartiesCommon.localParty != null && MMOPartiesCommon.partyInviter != null) {
                    MMOPartiesCommon.partyInviter = null; // Clear the inviter if you are already in a party
                }

                // Open the party invitation menu if you have an invite
                if (MMOPartiesCommon.partyInviter != null) {
                    dev.matthe815.mmoparties.common.events.EventClient.OpenInvitationScreen();
                } else {
                    // Otherwise, open the party menu screen
                    Minecraft.getInstance().setScreen(new PartyScreen());
                }
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            PartyList.onRenderGameOverlay(drawContext);
        });
    }
}
