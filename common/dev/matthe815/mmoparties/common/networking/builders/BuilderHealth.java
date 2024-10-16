package dev.matthe815.mmoparties.common.networking.builders;

import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class BuilderHealth implements BuilderData {
    float health;
    float maxHealth;

    @Override
    public void OnWrite(ByteBuf buffer, Player player) {
        health = player.getHealth();
        maxHealth = player.getMaxHealth();

        buffer.writeFloat(health);
        buffer.writeFloat(maxHealth);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        health = buffer.readFloat();
        maxHealth = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(Player player) {
        return health != player.getHealth() || maxHealth != player.getMaxHealth();
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        ResourceLocation FULL = new ResourceLocation("textures/gui/sprites/hud/heart/full.png");
        ResourceLocation HALF = new ResourceLocation("minecraft", "textures/gui/sprites/hud/heart/half.png");
        ResourceLocation BACKGROUND = new ResourceLocation("minecraft", "textures/gui/sprites/hud/heart/container.png");

        @Override
        public int Render(GuiGraphics gui, BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderHealth builder = (BuilderHealth) data;
            return PartyList.Draw(new UISpec(gui, FULL, HALF, BACKGROUND, xOffset, yOffset, 9, 9), builder.health, builder.maxHealth, compact, true);
        }
    }
}
