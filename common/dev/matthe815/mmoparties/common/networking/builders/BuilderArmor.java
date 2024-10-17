package dev.matthe815.mmoparties.common.networking.builders;

import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class BuilderArmor implements BuilderData {
    float armor;

    @Override
    public void OnWrite(ByteBuf buffer, Player player) {
        armor = player.getArmorValue();
        buffer.writeFloat(armor);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        armor = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(Player player) {
        return armor != player.getArmorValue();
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        @Override
        public int Render(GuiGraphics gui, BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderArmor builder = (BuilderArmor) data;
            return PartyList.Draw(new UISpec(gui, new UISpec(gui, 34, 9), new UISpec(gui, 25, 9), new UISpec(gui, 16, 9), xOffset, yOffset, 9, 9), builder.armor, builder.armor, compact,
                    ConfigHolder.CLIENT.showArmor.get() && builder.armor > 0);
        }
    }
}
