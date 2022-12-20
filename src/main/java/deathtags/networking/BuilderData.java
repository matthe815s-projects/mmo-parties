package deathtags.networking;

import deathtags.gui.HealthBar;
import deathtags.gui.UISpec;
import deathtags.stats.PartyMemberData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public interface BuilderData {
    /**
     * Fired when a request is made to write data.
     * @param buffer
     * @return
     */
    void OnWrite(PacketBuffer buffer, PlayerEntity player);
    void OnRead(PacketBuffer buffer);
}
