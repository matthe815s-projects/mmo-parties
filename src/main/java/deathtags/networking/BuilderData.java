package deathtags.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

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
