package deathtags.gui.screens;

import deathtags.core.MMOParties;
import deathtags.networking.EnumPartyGUIAction;
import deathtags.networking.MessageGUIInvitePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.List;

public class PartyScreen extends GuiScreen {
    private EnumPartyGUIAction menu;
    private List<Button> buttons = new ArrayList<>();

    public PartyScreen() {
        super();

        if (MMOParties.localParty == null) menu = EnumPartyGUIAction.INVITE; // If not in party, display the invite menu.
        else menu = EnumPartyGUIAction.KICK;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (buttons.get(button.id) != null) {
            buttons.get(button.id).OnPress(); // Press the button
        }
    }

    private GuiButton CreateButton(String text, int buttonNumber, IPressable pressable) {
        int buttonY = (24 * buttonNumber) + 20;
        Button button = new Button(buttons.size(), (this.width - 200) / 2, buttonY, 200, 80, (new TextComponentTranslation(text)).getFormattedText(), pressable);
        buttons.add(button);

        if (menu == EnumPartyGUIAction.INVITE) buttonY = (26 * (this.buttonList.size())) + 20; // Exception for the invite menu.

        return new GuiButton(buttonNumber, (this.width - 200) / 2, buttonY, 200, 20, (new TextComponentTranslation(text)).getFormattedText());
    }

    private String[] GetApplicablePlayers()
    {
        ArrayList<String> playerList = new ArrayList<>();

        // Changes how player list is generated based on if it's LAN or not.
        if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
            Minecraft.getMinecraft().getIntegratedServer().getPlayerList().getPlayers().forEach(player -> {
                playerList.add(player.getDisplayName().getUnformattedText());
            });
        } else {
            Minecraft.getMinecraft().getConnection().getPlayerInfoMap().forEach(player -> {
                playerList.add(player.getDisplayName().getUnformattedText());
            });
        }

        return playerList.toArray(new String[0]);
    }

    // Display the member UI
    private void DisplayMemberList()
    {
        int buttonNumber = 1;

        // Invite more players
        this.addButton(CreateButton("rpgparties.gui.invite", buttonNumber++, () -> {
            System.out.println("Invite clicked");
        }));

        MMOParties.localParty.local_players.forEach(player -> {
            int height = 26 * (2 + MMOParties.localParty.local_players.indexOf(player));

            GuiButton widget = this.addButton(CreateButton(player, 2 + MMOParties.localParty.local_players.indexOf(player), null));
            widget.enabled = false; // Make the button look darker

            if (!MMOParties.localParty.data.get(Minecraft.getMinecraft().player.getName()).leader || player == Minecraft.getMinecraft().player.getName()) return; // Hide these options if not the leader or yourself

        });
        if (!MMOParties.localParty.data.get(Minecraft.getMinecraft().player.getName()).leader) return; // Hide these options if not the leader.
    }

    @Override
    public void initGui() {
        // Create a different menu based on the specified option.
        switch (menu) {
            case NONE:
                break;

            case INVITE: // invite player
                // Add usable buttons for all players in a server.
                for (String player : GetApplicablePlayers()) {
                    this.addButton(CreateButton(player, this.buttons.size(), null)); // Send UI event to the server.
                }

                break;

            case KICK: // Kick player
                DisplayMemberList();
                break;
        }
    }

    @Override
    public void updateScreen() {
        drawCenteredString(this.fontRenderer, "Friends", this.width / 2, 8, 0XFFFFFF);
        super.updateScreen();
    }

}