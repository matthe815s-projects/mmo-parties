package dev.matthe815.mmoparties.common.networking.builders;

import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class BuilderHealth implements BuilderData {
    float health;
    float maxHealth;

    @Override
    public void OnWrite(ByteBuf buffer, EntityPlayer player) {
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
    public boolean IsDifferent(EntityPlayer player) {
        return health != player.getHealth() || maxHealth != player.getMaxHealth();
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderHealth builder = (BuilderHealth) data;
            return PartyList.Draw(new UISpec(new UISpec(52, 0), new UISpec(61, 0), new UISpec(16, 0), xOffset, yOffset, 9, 9), builder.health, builder.maxHealth, compact, true);
        }
    }
}
