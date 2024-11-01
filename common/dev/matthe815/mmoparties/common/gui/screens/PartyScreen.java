package dev.matthe815.mmoparties.common.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.matthe815.mmoparties.common.networking.builders.BuilderLeader;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.forge.networking.EnumPartyGUIAction;
import dev.matthe815.mmoparties.forge.networking.MessageHandleMenuAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Screen object that handles rendering the control screen for parties.
 * The visible screen is based on the MENU property and is set automatically on open.
 * @since 2.0.0
 */
public class PartyScreen extends Screen {
    // Determines which mean to render.
    private EnumPartyGUIAction menu;
    private List<Button> buttons = new ArrayList<>();

    public PartyScreen() {
        super(new StringTextComponent("My Party"));

        // If not in party, display the invite menu.
        if (MMOParties.localParty == null) menu = EnumPartyGUIAction.INVITE;
        else menu = EnumPartyGUIAction.KICK;
    }

    public PartyScreen(EnumPartyGUIAction menu) {
        super(new StringTextComponent("My Party"));
        this.menu = EnumPartyGUIAction.values()[menu.ordinal()];
    }

    private Button CreateButton(String text, int buttonNumber, Button.IPressable pressable) {
        int buttonY = (24 * buttonNumber) + 20;

        Button button = new Button((this.width - 200) / 2, buttonY, 200, 20, (new TranslationTextComponent(text)), butt -> {
            this.onClose();
            pressable.onPress(butt);
        });

        return button;
    }

    private Button CreateSubButton(String text, int xOffset, int buttonNumber, Button.IPressable pressable) {
        int buttonY = (24 * buttonNumber) + 20;
        Button button = new Button(((this.width + 200) / 2) + xOffset, buttonY, 20, 20, (new TranslationTextComponent(text)), pressable);
        buttons.add(button);

        if (menu == EnumPartyGUIAction.INVITE) buttonY = (26 * (this.buttons.size())) + 20; // Exception for the invite menu.

        return button;
    }

    private String[] GetApplicablePlayers()
    {
        ArrayList<String> playerList = new ArrayList<>();

        // Changes how player list is generated based on if it's LAN or not.
        if (Minecraft.getInstance().isLocalServer()) {
            Minecraft.getInstance().getSingleplayerServer().getPlayerList().getPlayers().forEach(player -> {
                playerList.add(player.getDisplayName().getString());
            });
        } else {
            if (Minecraft.getInstance().getConnection() == null) return new String[0];

            Minecraft.getInstance().getConnection().getOnlinePlayers().forEach(player -> {
                playerList.add(player.getProfile().getName());
            });
        }

        return playerList.toArray(new String[0]);
    }

    // Display the member UI
    private void DisplayMemberList()
    {
        int buttonNumber = 1;

        // Invite more players
        this.addButton(CreateButton("rpgparties.gui.invite", buttonNumber++, (button) -> {
            Minecraft.getInstance().setScreen(new PartyScreen(EnumPartyGUIAction.INVITE));
        }));

        MMOParties.localParty.local_players.forEach(player -> {
            int height = 26 * (2 + MMOParties.localParty.local_players.indexOf(player));

            Button widget = this.addButton(CreateButton(player, 2 + MMOParties.localParty.local_players.indexOf(player), (button) -> {}));
            widget.active = false; // Make the button loo darker

            if (!((BuilderLeader)MMOParties.localParty.data.get(Minecraft.getInstance().player.getName().getString()).additionalData[0]).isLeader && MMOParties.localParty.data.get(player).leader)
                return;

            this.addButton(CreateSubButton("K",20, height, (button) -> {
                MMOParties.network.sendToServer(new MessageHandleMenuAction(player, EnumPartyGUIAction.KICK));
            }));

            this.addButton(CreateSubButton("L",40, height, (button) -> {
                MMOParties.network.sendToServer(new MessageHandleMenuAction(player, EnumPartyGUIAction.LEADER));
            }));
        });

        this.addButton(CreateButton("rpgparties.gui.leave", 3 + MMOParties.localParty.local_players.size(), (button) -> {
            Minecraft.getInstance().setScreen(new PartyScreen(EnumPartyGUIAction.INVITE));
            MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.LEAVE));
        }));

        if (!((BuilderLeader)MMOParties.localParty.data.get(Minecraft.getInstance().player.getName().getString()).additionalData[0]).isLeader) return; // Hide these options if not the leader.

        this.addButton(CreateButton("rpgparties.gui.disband", 4 + MMOParties.localParty.local_players.size(), (button) -> {
            Minecraft.getInstance().setScreen(new PartyScreen(EnumPartyGUIAction.INVITE));
            MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.DISBAND));
        }));
    }

    @Override
    public void init() {
        // Create a different menu based on the specified option.
        switch (menu) {
            case NONE:
                break;

            case INVITE: // invite player
                // Add usable buttons for all players in a server.
                Widget widget = this.addButton(new Button((this.width) - 70, 8, 60, 20, new TranslationTextComponent("rpgparties.gui.inviteall"), button -> {
                    MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.INVITE)); // Send UI event to the server.
                })); // invite all button

                widget.active = ConfigHolder.COMMON.allowInviteAll.get(); // Disable if not allowed.

                // Add usable buttons for all players in a server.
                int i = 1;

                for (String player : GetApplicablePlayers()) {
                    this.addButton(CreateButton(player, i++, p_onPress_1_ -> MMOParties.network.sendToServer(new MessageHandleMenuAction(player, EnumPartyGUIAction.INVITE)))); // Send UI event to the server.
                }
                break;

            case KICK: // Kick player
                DisplayMemberList();
                break;
        }
    }

    @Override
    public void render(MatrixStack stack, int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        super.render(stack, p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);

        drawCenteredString(stack, this.font, (this.menu == EnumPartyGUIAction.INVITE ? new TranslationTextComponent("rpgparties.gui.title.invite").getString() :
                new TranslationTextComponent("rpgparties.gui.title").getString()), this.width / 2, 14, 0XFFFFFF);
    }
}
