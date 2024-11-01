package dev.matthe815.mmoparties.common.networking.builders;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;

public interface BuilderData {
    /**
     * Fired when a request is made to write data.
     * @param buffer
     * @return
     */
    void OnWrite(ByteBuf buffer, PlayerEntity player);
    void OnRead(ByteBuf buffer);
    boolean IsDifferent(PlayerEntity player);
}
