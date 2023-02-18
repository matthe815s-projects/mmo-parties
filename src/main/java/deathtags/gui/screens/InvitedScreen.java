package deathtags.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import deathtags.core.MMOParties;
import deathtags.networking.EnumPartyGUIAction;
import deathtags.networking.MessageHandleMenuAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class InvitedScreen extends Screen {
    public InvitedScreen() {
        super(new TranslatableComponent("rpgparties.gui.title.invite"));

    }

    public static void ShowToast() {
        Minecraft.getInstance().getToasts().addToast(new InvitedScreen.InvitedToast());
    }

    private Button CreateButton(String text, int buttonNumber, Button.OnPress pressable) {
        int buttonY = 26 * (buttonNumber);

        return new Button((this.width - 200) / 2, buttonY, 200, 20, new TranslatableComponent(text), button -> {
            this.onClose();
            pressable.onPress(button);
        });
    }

    @Override
    protected void init() {
        this.addRenderableWidget(this.CreateButton("rpgparties.gui.accept", 2, p_onPress_1_ -> {
            MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.ACCEPT));
            MMOParties.partyInviter = null;
        }));

        this.addRenderableWidget(this.CreateButton("rpgparties.gui.deny", 3, p_onPress_1_ -> {
            MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.DENY));
            MMOParties.partyInviter = null;
        }));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float ticks) {
        this.renderBackground(stack); // Background
        drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 0XFFFFFF);
        drawCenteredString(stack, this.font, new TranslatableComponent("rpgparties.message.party.invite.from", MMOParties.partyInviter), this.width / 2, 20, 0XFFFFFF);
        super.render(stack, mouseX, mouseY, ticks);
    }

    public static class InvitedToast implements Toast {
        public long lastChanged = 0;
        public boolean changed = true;
        @Override
        public Visibility render(PoseStack p_230444_1_, ToastComponent p_230444_2_, long p_230444_3_) {
            if (this.changed) {
                this.lastChanged = p_230444_3_;
                this.changed = false;
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            p_230444_2_.blit(p_230444_1_, 0, 0, 0, 0, this.width(), this.height());

            // Render an item
            p_230444_2_.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(new ItemStack(Items.PLAYER_HEAD), 6, 6);

            p_230444_2_.getMinecraft().font.draw(p_230444_1_, new TranslatableComponent("rpgparties.toast.header", MMOParties.partyInviter), 30.0F, 7.0F, 0xBBBBBB);
            p_230444_2_.getMinecraft().font.draw(p_230444_1_, new TranslatableComponent("rpgparties.toast.keybind", MMOParties.OPEN_GUI_KEY.getKey().getDisplayName().getString()), 30.0F, 18.0F, 0xFFFFFF);
            return p_230444_3_ - this.lastChanged >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }
    }

}
