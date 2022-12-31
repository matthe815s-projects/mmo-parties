package deathtags.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public interface BuilderData {
    /**
     * Fired when a request is made to write data.
     * @param buffer
     * @return
     */
    void OnWrite(ByteBuf buffer, EntityPlayer player);
    void OnRead(ByteBuf buffer);
    boolean IsDifferent(EntityPlayer player);
}
