package deathtags.gui.builders;

import deathtags.gui.PartyList;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class BuilderHealth implements BuilderData {
    float health;
    float maxHealth;

    @Override
    public void OnWrite(ByteBuf buffer, EntityPlayer player) {
        buffer.writeFloat(player.getHealth());
        buffer.writeFloat(player.getMaxHealth());
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
            return PartyList.Draw(builder.health, builder.maxHealth, new UISpec(PartyList.HEART_TEXTURE, xOffset, yOffset, 52, 0, 9, 9), 16, 9, compact, true);
        }
    }
}
