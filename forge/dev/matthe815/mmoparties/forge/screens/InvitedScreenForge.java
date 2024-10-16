package dev.matthe815.mmoparties.forge.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.common.gui.screens.InvitedScreenCommon;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.forge.networking.EnumPartyGUIAction;
import dev.matthe815.mmoparties.forge.networking.MessageHandleMenuAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class InvitedScreenForge extends InvitedScreenCommon {
    public InvitedScreenForge() {
        super();
    }

    @Override
    protected void init() {
        this.addRenderableWidget(this.CreateButton("rpgparties.gui.accept", 2, p_onPress_1_ -> {
            assert Minecraft.getInstance().player != null;
            MMOParties.network.send(new MessageHandleMenuAction("", EnumPartyGUIAction.ACCEPT), Minecraft.getInstance().player.connection.getConnection());
            MMOParties.partyInviter = null;
        }));

        this.addRenderableWidget(this.CreateButton("rpgparties.gui.deny", 3, p_onPress_1_ -> {
            assert Minecraft.getInstance().player != null;
            MMOParties.network.send(new MessageHandleMenuAction("", EnumPartyGUIAction.DENY), Minecraft.getInstance().player.connection.getConnection());
            MMOParties.partyInviter = null;
        }));
    }
}
