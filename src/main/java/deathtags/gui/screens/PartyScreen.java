package deathtags.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import deathtags.config.ConfigHolder;
import deathtags.core.MMOParties;
import deathtags.gui.builders.BuilderLeader;
import deathtags.networking.EnumPartyGUIAction;
import deathtags.networking.MessageHandleMenuAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

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
        super(new TranslatableComponent(MMOParties.localParty == null ? "rpgparties.gui.title.invite" : "rpgparties.gui.title"));

        // If not in party, display the invite menu.
        if (MMOParties.localParty == null) menu = EnumPartyGUIAction.INVITE;
        else menu = EnumPartyGUIAction.KICK;
    }

    public PartyScreen(TranslatableComponent title, int menu) {
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

        if (menu == EnumPartyGUIAction.INVITE) buttonY = 26 * (this.children().size()); // Exception for the invite menu.

        return new Button((this.width - 200) / 2, buttonY, 200, 20, new TranslatableComponent(text), button -> {
            this.onClose();
            pressable.onPress(button);
        });
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
        this.addRenderableWidget(CreateButton("rpgparties.gui.invite", buttonNumber++, p_onPress_1_ -> Minecraft.getInstance().setScreen(new PartyScreen(new TranslatableComponent("rpgparties.gui.title.invite"), 1))));

        MMOParties.localParty.local_players.forEach(player -> {
            int height = 26 * (2 + MMOParties.localParty.local_players.indexOf(player));

            Widget widget = this.addRenderableWidget(CreateButton(player, 2 + MMOParties.localParty.local_players.indexOf(player), p_onPress_1_ -> {}));
            ((Button)widget).active = false; // Make the button look darker

            // Hide these options if not the leader or yourself
            if (!((BuilderLeader)MMOParties.localParty.data.get(Minecraft.getInstance().player.getName().getString()).additionalData[0]).isLeader || player == Minecraft.getInstance().player.getName().getString()) return;

            this.addRenderableWidget(new ImageButton((this.width + 200 + 20) / 2, height, 20, 20, 0, 46, 20, new ResourceLocation("mmoparties", "textures/icons.png"), button -> {
                this.onClose();
                MMOParties.network.sendToServer(new MessageHandleMenuAction(player, EnumPartyGUIAction.LEADER));
            }));

            this.addRenderableWidget(new ImageButton((this.width + 200 + 60) / 2, height, 20, 20, 20, 46, 20, new ResourceLocation("mmoparties", "textures/icons.png"), button -> {
                this.onClose();
                MMOParties.network.sendToServer(new MessageHandleMenuAction(player, EnumPartyGUIAction.KICK));
            }));
        });

        this.addRenderableWidget(CreateButton("rpgparties.gui.leave", 3 + MMOParties.localParty.local_players.size(), p_onPress_1_ -> MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.LEAVE))));

        if (!((BuilderLeader)MMOParties.localParty.data.get(Minecraft.getInstance().player.getName().getString()).additionalData[0]).isLeader) return; // Hide these options if not the leader.

        this.addRenderableWidget(CreateButton("rpgparties.gui.disband", 4 + MMOParties.localParty.local_players.size(), p_onPress_1_ -> MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.DISBAND))));
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
                Widget widget = this.addRenderableWidget(new Button((this.width) - 70, 8, 60, 20, new TranslatableComponent("rpgparties.gui.inviteall"), button -> {
                    MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.INVITE)); // Send UI event to the server.
                })); // invite all button

                ((Button)widget).active = ConfigHolder.COMMON.allowInviteAll.get(); // Disable if not allowed.

                // Add usable buttons for all players in a server.
                int i = 1;

                for (String player : GetApplicablePlayers()) {
                    this.addRenderableWidget(CreateButton(player, i++, p_onPress_1_ -> MMOParties.network.sendToServer(new MessageHandleMenuAction(player, EnumPartyGUIAction.INVITE)))); // Send UI event to the server.
                }

                break;

            case KICK: // Kick player
                DisplayMemberList();
                break;
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float ticks) {
        this.renderBackground(stack); // Background
        drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 0XFFFFFF);
        super.render(stack, mouseX, mouseY, ticks);
    }

}
