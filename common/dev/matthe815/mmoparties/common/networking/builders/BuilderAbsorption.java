package dev.matthe815.mmoparties.common.networking.builders;

import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class BuilderAbsorption implements BuilderData {
    float absorption;

    @Override
    public void OnWrite(ByteBuf buffer, Player player) {
        absorption = player.getAbsorptionAmount();
        buffer.writeFloat(absorption);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        absorption = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(Player player) {
        return absorption != player.getAbsorptionAmount();
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        ResourceLocation FULL = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/hud/heart/full.png");
        ResourceLocation HALF = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/hud/heart/half.png");
        ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/hud/heart/container.png");

        @Override
        public int Render(GuiGraphics gui, BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderAbsorption builder = (BuilderAbsorption) data;
            return PartyList.Draw(new UISpec(gui, FULL, HALF, BACKGROUND, xOffset, yOffset, 9, 9), builder.absorption, builder.absorption, compact,
                    ConfigHolder.CLIENT.showAbsorption.get() && builder.absorption > 0);
        }
    }
}
