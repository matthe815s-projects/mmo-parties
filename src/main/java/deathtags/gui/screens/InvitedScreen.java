package deathtags.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import deathtags.core.MMOParties;
import deathtags.networking.EnumPartyGUIAction;
import deathtags.networking.MessageHandleMenuAction;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

    public static class InvitedToast implements IToast {
        public long lastChanged = 0;
        public boolean changed = true;
        @Override
        public Visibility render(MatrixStack p_230444_1_, ToastGui p_230444_2_, long p_230444_3_) {
            if (this.changed) {
                this.lastChanged = p_230444_3_;
                this.changed = false;
            }

            p_230444_2_.getMinecraft().getTextureManager().bind(TEXTURE);
            RenderSystem.color3f(1.0F, 1.0F, 1.0F);
            p_230444_2_.blit(p_230444_1_, 0, 0, 0, 0, this.width(), this.height());

            // Render an item
            p_230444_2_.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(new ItemStack(Items.PLAYER_HEAD), 6, 6);

            p_230444_2_.getMinecraft().font.draw(p_230444_1_, new TranslationTextComponent("rpgparties.toast.header", MMOParties.partyInviter), 30.0F, 7.0F, 0xBBBBBB);
            p_230444_2_.getMinecraft().font.draw(p_230444_1_, new TranslationTextComponent("rpgparties.toast.keybind", MMOParties.OPEN_GUI_KEY.getKey().getDisplayName().getString()), 30.0F, 18.0F, 0xFFFFFF);
            return p_230444_3_ - this.lastChanged >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
        }
    }

}
