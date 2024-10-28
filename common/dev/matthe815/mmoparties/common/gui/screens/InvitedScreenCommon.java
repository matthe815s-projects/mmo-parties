package dev.matthe815.mmoparties.common.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;

public class InvitedScreenCommon extends Screen {
    public InvitedScreenCommon() {
        super(new TranslatableComponent("rpgparties.gui.title.invite"));
    }

    public static void ShowToast() {
        Minecraft.getInstance().getToasts().addToast(new InvitedScreenCommon.InvitedToast());
    }

    protected Button CreateButton(String text, int buttonNumber, Button.OnPress pressable) {
        int buttonY = 26 * (buttonNumber);

        return new Button((this.width - 200) / 2, buttonY, 200, 20, new TranslatableComponent(text), button -> {
            this.onClose();
            pressable.onPress(button);
        });
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float ticks) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 0XFFFFFF);
        drawCenteredString(stack, this.font, new TranslatableComponent("rpgparties.message.party.invite.from", MMOPartiesCommon.partyInviter), this.width / 2, 20, 0XFFFFFF);
        super.render(stack, mouseX, mouseY, ticks);
    }

    public static class InvitedToast implements Toast {
        public long lastChanged = 0;
        public boolean changed = true;
        @Override
        public Visibility render(PoseStack gui, ToastComponent p_230444_2_, long p_230444_3_) {
            if (this.changed) {
                this.lastChanged = p_230444_3_;
                this.changed = false;
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().gui.blit(gui, 0, 0, 0, 0, this.width(), this.height());

            // Render an item
            // p_230444_2_.getMinecraft().getItemRenderer().render(new ItemStack(Items.PLAYER_HEAD), 6, 6);

            Minecraft.getInstance().font.draw(gui, new TranslatableComponent("rpgparties.toast.header", MMOPartiesCommon.partyInviter), 30, 7, 0xBBBBBB);
            Minecraft.getInstance().font.draw(gui, new TranslatableComponent("rpgparties.toast.keybind", MMOPartiesCommon.OPEN_GUI_KEY.getKey().getDisplayName().getString()), 30, 18, 0xFFFFFF);
            return p_230444_3_ - this.lastChanged >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }
    }

}
