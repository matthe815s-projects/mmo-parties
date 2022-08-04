package deathtags.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import deathtags.core.MMOParties;
import deathtags.networking.EnumPartyGUIAction;
import deathtags.networking.MessageGUIInvitePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public class PartyScreen extends Screen {
    private EnumPartyGUIAction menu;

    public PartyScreen() {
        super(new TextComponent(MMOParties.localParty == null ? "Party > Invite" : "My Party") {
        });
        if (MMOParties.localParty == null) menu = EnumPartyGUIAction.INVITE; // If not in party, display the invite menu.
        else menu = EnumPartyGUIAction.KICK;
    }

    public PartyScreen(TextComponent title, int menu) {
        super(title);
        this.menu = EnumPartyGUIAction.values()[menu];
    }

    private Button CreateButton(String text, int buttonNumber, Button.OnPress pressable) {
        int buttonY = 26 * (buttonNumber);

        if (menu == EnumPartyGUIAction.INVITE) buttonY = 26 * (this.children().size()); // Exception for the invite menu.

        return new Button((this.width - 200) / 2, buttonY, 200, 20, new TextComponent(text), button -> {
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
        this.addWidget(CreateButton("Invite", buttonNumber++, p_onPress_1_ -> Minecraft.getInstance().setScreen(new PartyScreen(new TextComponent("Party > Invite"), 1))));

        MMOParties.localParty.local_players.forEach(player -> {
            int height = 26 * (2 + MMOParties.localParty.local_players.indexOf(player));

            this.addWidget(CreateButton(player, 2 + MMOParties.localParty.local_players.indexOf(player), p_onPress_1_ -> {}));

            if (!MMOParties.localParty.data.get(Minecraft.getInstance().player.getName().getString()).leader || player == Minecraft.getInstance().player.getName().getString()) return; // Hide these options if not the leader or yourself

            this.addWidget(new ImageButton((this.width + 200 + 20) / 2, height, 20, 20, 0, 46, 20, new ResourceLocation("mmoparties", "textures/icons.png"), button -> {
                this.onClose();
                MMOParties.network.sendToServer(new MessageGUIInvitePlayer(player, EnumPartyGUIAction.LEADER));
            }));

            this.addWidget(new ImageButton((this.width + 200 + 60) / 2, height, 20, 20, 20, 46, 20, new ResourceLocation("mmoparties", "textures/icons.png"), button -> {
                this.onClose();
                MMOParties.network.sendToServer(new MessageGUIInvitePlayer(player, EnumPartyGUIAction.KICK));
            }));
        });

        if (!MMOParties.localParty.data.get(Minecraft.getInstance().player.getName().getString()).leader) return; // Hide these options if not the leader.

        this.addWidget(CreateButton("Disband", this.children().size()+1, p_onPress_1_ -> MMOParties.network.sendToServer(new MessageGUIInvitePlayer("", EnumPartyGUIAction.DISBAND))));
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
                Widget widget = this.addWidget(new Button((this.width) - 70, 8, 60, 20, new TextComponent("Invite All"), button -> {
                    for (String player : GetApplicablePlayers()) {
                        MMOParties.network.sendToServer(new MessageGUIInvitePlayer("", EnumPartyGUIAction.INVITE)); // Send UI event to the server.
                    }
                })); // invite all button

                // Add usable buttons for all players in a server.
                int i = 1;

                for (String player : GetApplicablePlayers()) {
                    this.addWidget(CreateButton(player, i++, p_onPress_1_ -> MMOParties.network.sendToServer(new MessageGUIInvitePlayer(player, EnumPartyGUIAction.INVITE)))); // Send UI event to the server.
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
