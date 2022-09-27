package deathtags.connectors;

import com.google.common.base.Charsets;
import deathtags.core.MMOParties;
import deathtags.networking.MessageUpdateParty;
import deathtags.networking.PartyPacketDataBuilder;
import deathtags.stats.Party;
import deathtags.stats.PartyMemberData;
import deathtags.stats.PlayerPing;
import dev.matthe815.minenet.Minenet;
import dev.matthe815.minenet.network.NetworkHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.ModList;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CraftNetConnector {
    public static boolean IsLoaded()
    {
        return ModList.get().isLoaded("minenet");
    }

    public static void GetRouting(ByteBuffer packet)
    {
        int type = packet.get();

        switch (type) {
            case 0:
                MessageUpdateParty updateParty = new MessageUpdateParty(GetNulledTerminatedString(packet));
                MMOParties.localParty = new Party();
                MMOParties.localParty.local_players = new ArrayList<String>(Arrays.asList(updateParty.members.split(",")));

                MMOParties.localParty.local_players.forEach(s -> {
                    MMOParties.localParty.data.put(s, new PartyMemberData(new PartyPacketDataBuilder().SetPlayer(s).SetHealth(5)));
                });
                SendUpdate();
                System.out.println("Party created");
                break;
            case 1: // Player update packet.
                packet.get();
                packet.get();
                packet.get();
                PartyPacketDataBuilder partyData = new PartyPacketDataBuilder();
                partyData.SetHealth(packet.getFloat());
                System.out.println(Minenet.bytesToHex(packet.array()));
                partyData.SetMaxHealth(packet.getFloat());
                System.out.println(Minenet.bytesToHex(packet.array()));
                partyData.SetArmor(packet.getFloat());
                System.out.println(Minenet.bytesToHex(packet.array()));
                partyData.SetAbsorption(packet.getFloat());
                System.out.println(Minenet.bytesToHex(packet.array()));
                partyData.SetHunger(packet.getFloat());
                System.out.println(Minenet.bytesToHex(packet.array()));
                partyData.SetLeader(((int)packet.get()) == 1 ? true : false);
                System.out.println(Minenet.bytesToHex(packet.array()));
                partyData.SetPlayer(GetNulledTerminatedString(packet));

                System.out.println(partyData.health);
                System.out.println(partyData.playerId);

                MMOParties.localParty.data.put(partyData.playerId, new PartyMemberData(partyData));
                System.out.println(packet.array());
                System.out.println("Party updated");
                break;
        }
    }

    public static void SendUpdate()
    {
        if (Minecraft.getInstance().player == null) return;

        PlayerEntity player = Minecraft.getInstance().player;

        if (!MMOParties.localParty.pings.containsKey( player.getName().getContents() ))
            MMOParties.localParty.pings.put(player.getName().getContents(), new PlayerPing(player, 0, 0, 0, true, 0, 0, 0, 0));

        MMOParties.localParty.pings.get( player.getName().getContents() ).Update(player.getHealth(), player.getMaxHealth(), player.getArmorValue(),
                MMOParties.localParty.leader==player, player.getAbsorptionAmount(), 0, 0);

        ByteBuf buf = Unpooled.buffer(128);
        buf.writeBytes(new byte[]{(byte)(0)});
        buf.writeBytes(new byte[]{(byte)7});
        buf.writeBytes(new byte[]{(byte)1});
        buf.writeFloat(player.getHealth());
        buf.writeFloat(player.getMaxHealth());
        buf.writeFloat(player.getArmorValue());
        buf.writeFloat(player.getAbsorptionAmount());
        buf.writeFloat(player.getFoodData().getFoodLevel());
        buf.setByte(0, buf.writerIndex());
        NetworkHandler.EnqueuePacket(buf);
    }

    public static String GetNulledTerminatedString(ByteBuffer buffer) {
        String fin = "";
        byte c;
        while ((c = buffer.get()) != 0x00 || buffer.position() >= buffer.array().length) {
            fin+=(char)c;
        }
        return fin;
    }
}
