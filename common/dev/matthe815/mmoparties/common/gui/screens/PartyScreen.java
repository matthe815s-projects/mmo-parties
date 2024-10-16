package dev.matthe815.mmoparties.common.gui.screens;

import dev.matthe815.mmoparties.common.networking.builders.BuilderLeader;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.forge.networking.EnumPartyGUIAction;
import dev.matthe815.mmoparties.forge.networking.MessageHandleMenuAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;

/**
 * A Screen object that handles rendering the control screen for parties.
 * The visible screen is based on the MENU property and is set automatically on open.
 * @since 2.0.0
 */
public class PartyScreen extends Screen {
    // Determines which mean to render.
    private EnumPartyGUIAction menu;

    public PartyScreen() {
        super(Component.translatable(MMOParties.localParty == null ? "rpgparties.gui.title.invite" : "rpgparties.gui.title"));

        // If not in party, display the invite menu.
        if (MMOParties.localParty == null) menu = EnumPartyGUIAction.INVITE;
        else menu = EnumPartyGUIAction.KICK;
    }

    public PartyScreen(MutableComponent title, int menu) {
        super(title);
        this.menu = EnumPartyGUIAction.values()[menu];
    }

    /**
     * Create a button and automatically set the centered offset based on the supplied text
     * and button number.
     * @param text
     * @param buttonNumber
     * @param pressable
     * @return
     */
    private Button CreateButton(String text, int buttonNumber, Button.OnPress pressable) {
        int buttonY = 26 * (buttonNumber);

        Button button = Button.builder(Component.translatable(text), butt -> {
            this.onClose();
            pressable.onPress(butt);
        }).size(200, 20).pos((this.width - 200) / 2, buttonY).build();

        return button;
    }

    private String[] GetApplicablePlayers()
    {
        ArrayList<String> playerList = new ArrayList<>();

        Minecraft.getInstance().getConnection().getOnlinePlayers().forEach(player -> {
            playerList.add(player.getProfile().getName());
        });

        return playerList.toArray(new String[0]);
    }

    // Display the member UI
    private void DisplayMemberList()
    {
        int buttonNumber = 1;

        // Invite more players
        this.addRenderableWidget(CreateButton("rpgparties.gui.invite", buttonNumber++, p_onPress_1_ -> Minecraft.getInstance().setScreen(new PartyScreen(Component.translatable("rpgparties.gui.title.invite"), 1))));

        MMOParties.localParty.local_players.forEach(player -> {
            int height = 26 * (2 + MMOParties.localParty.local_players.indexOf(player));
            if (MMOParties.localParty.data.get(Minecraft.getInstance().player.getName().getString()).additionalData == null) return;

            Button widget = this.addRenderableWidget(CreateButton(player, 2 + MMOParties.localParty.local_players.indexOf(player), p_onPress_1_ -> {}));
            widget.active = false; // Make the button look darker

            // Hide these options if not the leader or yourself
            if (!((BuilderLeader)MMOParties.localParty.data.get(Minecraft.getInstance().player.getName().getString()).additionalData[0]).isLeader || player == Minecraft.getInstance().player.getName().getString()) return;

            this.addRenderableWidget(widget);
        });

        this.addRenderableWidget(CreateButton("rpgparties.gui.leave", 3 + MMOParties.localParty.local_players.size(), p_onPress_1_ -> MMOParties.network.send(new MessageHandleMenuAction("", EnumPartyGUIAction.LEAVE), Minecraft.getInstance().getConnection().getConnection())));

        // Hide these options if not the leader.
        if (!((BuilderLeader)MMOParties.localParty.data.get(Minecraft.getInstance().player.getName().getString()).additionalData[0]).isLeader) return;

        this.addRenderableWidget(CreateButton("rpgparties.gui.disband", 4 + MMOParties.localParty.local_players.size(), p_onPress_1_ -> MMOParties.network.send(new MessageHandleMenuAction("", EnumPartyGUIAction.DISBAND), Minecraft.getInstance().getConnection().getConnection())));
    }

    @Override
    protected void init() {
        // Don't allow this if not in a server or you're not the leader of your party.
        if (Minecraft.getInstance().getConnection() == null) { this.onClose(); return; }

        // Create a different menu based on the specified option.
        switch (menu) {
            case NONE:
                break;

            case INVITE: // invite player
                // Add usable buttons for all players in a server.
                int i = 1;

                for (String player : GetApplicablePlayers()) {
                    this.addRenderableWidget(CreateButton(player, i++, p_onPress_1_ -> MMOParties.network.send(new MessageHandleMenuAction(player, EnumPartyGUIAction.INVITE), Minecraft.getInstance().getConnection().getConnection()))); // Send UI event to the server.
                }

                break;

            case KICK: // Kick player
                DisplayMemberList();
                break;
        }
    }

    @Override
    public void render(GuiGraphics stack, int mouseX, int mouseY, float ticks) {
        this.renderBackground(stack, mouseX, mouseY, ticks); // Background
        stack.drawCenteredString(this.font, this.title.getString(), this.width / 2, 8, 0XFFFFFF);
        super.render(stack, mouseX, mouseY, ticks);
    }

}
