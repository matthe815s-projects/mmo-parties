package deathtags.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import deathtags.core.MMOParties;
import deathtags.networking.EnumPartyGUIAction;
import deathtags.networking.MessageHandleMenuAction;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class InvitedScreen extends Screen {
    public InvitedScreen() {
        super(new TranslationTextComponent("Invited"));

    }

    private Button CreateButton(String text, int buttonNumber, Button.IPressable pressable) {
        int buttonY = 26 * (buttonNumber);

        return new Button((this.width - 200) / 2, buttonY, 200, 20, new TranslationTextComponent(text), button -> {
            this.onClose();
            pressable.onPress(button);
        });
    }

    @Override
    protected void init() {
        this.addButton(this.CreateButton("rpgparties.gui.accept", 2, p_onPress_1_ -> {
            MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.ACCEPT));
        }));

        this.addButton(this.CreateButton("rpgparties.gui.deny", 3, p_onPress_1_ -> {
            MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.DENY));
        }));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float ticks) {
        this.renderBackground(stack); // Background
        drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 0XFFFFFF);
        drawCenteredString(stack, this.font, new TranslationTextComponent("rpgparties.message.party.invite.from", MMOParties.partyInviter), this.width / 2, 20, 0XFFFFFF);
        super.render(stack, mouseX, mouseY, ticks);
    }

}
