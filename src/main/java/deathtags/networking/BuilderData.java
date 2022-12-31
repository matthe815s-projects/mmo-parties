package deathtags.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;

public interface BuilderData {
    /**
     * Fired when a request is made to write data.
     * @param buffer
     * @return
     */
    void OnWrite(ByteBuf buffer, Player player);
    void OnRead(ByteBuf buffer);
    boolean IsDifferent(Player player);
}
