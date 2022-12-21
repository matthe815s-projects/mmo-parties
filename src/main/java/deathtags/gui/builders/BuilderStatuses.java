package deathtags.gui.builders;

import deathtags.gui.PartyList;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;

public class BuilderStatuses implements BuilderData {
    Effect[] effects;

    @Override
    public void OnWrite(PacketBuffer buffer, PlayerEntity player) {
        buffer.writeInt(player.getActiveEffects().size());
        player.getActiveEffects().forEach(effectInstance -> {
            buffer.writeInt(Effect.getId(effectInstance.getEffect()));
        });
    }

    @Override
    public void OnRead(PacketBuffer buffer) {
        int count = buffer.readInt();
        effects = new Effect[count]; // Make a holder for the effects.

        // Loop through the provided IDs and grab the effect.
        for (int i=0;i<count;i++) {
            int id = buffer.readInt();
            effects[i] = Effect.byId(id);
        }
    }

    @Override
    public boolean IsDifferent(PlayerEntity player) {
        return effects.length != player.getActiveEffects().size();
    }

    public static class Renderer implements PartyList.NuggetBar {

        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderStatuses statuses = (BuilderStatuses) data;

            for (int i=0;i<statuses.effects.length;i++) {
                PartyList.DrawResource(new UISpec(PartyList.TEXTURE_ICON, xOffset + (10 * i), yOffset, 0, 0, 9, 9));
                ResourceLocation location = new ResourceLocation(statuses.effects[i].getRegistryName().getNamespace(), "textures/mob_effect/" + statuses.effects[i].getRegistryName().getPath() + ".png");
                System.out.println(location.toString());
                PartyList.DrawResource(
                        new UISpec(location,
                                xOffset, yOffset, 0, 0, 9, 9));
            }

            return 0;
        }
    }
}
