package deathtags.gui.screens;

import deathtags.core.MMOParties;
import deathtags.networking.EnumPartyGUIAction;
import deathtags.networking.MessageHandleMenuAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.util.text.TextComponentTranslation;
import java.util.ArrayList;
import java.util.List;

public class InvitedScreen extends GuiScreen {
    private List<Button> buttons = new ArrayList<>();

    public InvitedScreen() {
        super();
    }

    public static void ShowToast() {
        Minecraft.getMinecraft().getToastGui().add(new InvitedScreen.InvitedToast());
    }

    private GuiButton CreateButton(String text, int buttonNumber, IPressable pressable) {
        int buttonY = (26 * buttonNumber);
        Button button = new Button(buttons.size(), (this.width - 200) / 2, buttonY, 200, 80, (new TextComponentTranslation(text)).getFormattedText(), pressable);
        buttons.add(button);

        return new GuiButton(button.id, (this.width - 200) / 2, buttonY, 200, 20, (new TextComponentTranslation(text)).getFormattedText());
    }

    @Override
    public void initGui() {
        this.addButton(this.CreateButton("rpgparties.gui.accept", 2, () -> {
            MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.ACCEPT));
            MMOParties.partyInviter = null;
        }));

        this.addButton(this.CreateButton("rpgparties.gui.deny", 3, () -> {
            MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.DENY));
            MMOParties.partyInviter = null;
        }));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float ticks) {
        drawCenteredString(this.fontRenderer, new TextComponentTranslation("rpgparties.gui.title.invite").getFormattedText(), this.width / 2, 8, 0XFFFFFF);
        drawCenteredString(this.fontRenderer, new TextComponentTranslation("rpgparties.message.party.invite.from", MMOParties.partyInviter).getFormattedText(), this.width / 2, 20, 0XFFFFFF);
        super.drawScreen(mouseX, mouseY, ticks);
    }

    public static class InvitedToast implements IToast {
        public long lastChanged = 0;
        public boolean changed = true;

        @Override
        public Visibility draw(GuiToast p_230444_2_, long p_230444_3_) {
            if (this.changed) {
                this.lastChanged = p_230444_3_;
                this.changed = false;
            }

            p_230444_2_.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
            p_230444_2_.drawTexturedModalRect(0, 0, 0, 0, 160, 32);

            // Render an item

            p_230444_2_.getMinecraft().fontRenderer.drawString(new TextComponentTranslation("rpgparties.toast.header", MMOParties.partyInviter).getFormattedText(), 30, 7, 0xBBBBBB);
            p_230444_2_.getMinecraft().fontRenderer.drawString(new TextComponentTranslation("rpgparties.toast.keybind", Character.toString((char)(MMOParties.OPEN_GUI_KEY.getKeyCode()+87))).getFormattedText(), 30, 18, 0xFFFFFF);
            return p_230444_3_ - this.lastChanged >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
        }
    }

}
