package deathtags.networking;

import deathtags.gui.screens.PartyScreen;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * A packet to be sent from the server to a client with a request to open the party UI.
 * Cannot be sent to server from client.
 * @since 2.2.0
 */
public class MessageOpenUI implements IMessage {
    public MessageOpenUI() {}

    @Override
    public void fromBytes(ByteBuf byteBuf) {

    }

    @Override
    public void toBytes(ByteBuf byteBuf) {

    }

    public static class Handler implements IMessageHandler<MessageOpenUI, IMessage>
    {
        @Override
        public IMessage onMessage(MessageOpenUI messageOpenUI, MessageContext messageContext) {
            if (messageContext.side != Side.CLIENT) return null; // Only allow from server.
            Minecraft.getMinecraft().displayGuiScreen(new PartyScreen());
            return null;
        }
    }
}