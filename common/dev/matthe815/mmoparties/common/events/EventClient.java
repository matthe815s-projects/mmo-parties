package dev.matthe815.mmoparties.common.events;

import dev.matthe815.mmoparties.common.gui.screens.PartyScreen;
import dev.matthe815.mmoparties.forge.screens.InvitedScreenForge;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EventClient {
    /**
     * Handles opening of the party screens
     */
    public static void OpenPartyScreen() {
        Minecraft.getInstance().setScreen(new PartyScreen());
    }

    /**
     * Handles opening of the invitation screen.
     * Opens automatically when an invite is received.
     * @link deathtags.networking.MessagePartyInvite
     */
    public static void OpenInvitationScreen() { Minecraft.getInstance().setScreen(new InvitedScreenForge()); }
}
