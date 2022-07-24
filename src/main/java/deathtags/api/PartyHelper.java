package deathtags.api;

import deathtags.api.relation.EnumRelation;
import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

/**
 * The base external API for interacting with parties within the mod.
 */
public class PartyHelper {
    public static class Client {
        /**
         * Get the client's local party. Can be null.
         * @return
         */
        public static Party GetParty()
        {
            return MMOParties.localParty;
        }

        /**
         * Return if the player is the leader of their party.
         * @return
         */
        public static boolean IsPartyLeader()
        {
            return ((EntityPlayer) MMOParties.localParty.leader) == Minecraft.getMinecraft().player;
        }

        /**
         * Return player's party size, returns zero if not in party.
         * @return
         */
        public static int PartySize()
        {
            if (GetParty() == null) return 0;
            return GetParty().local_players.size();
        }
    }

    /**
     * The server controller for the party system.
     */
    public static class Server {
        public static MinecraftServer server;
        /**
         * Get a list of all active parties.
         * @return
         */
        public static List<Party> GetParties()
        {
            List<Party> parties = new ArrayList<>();
            server.getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
                PlayerStats stats = MMOParties.GetStatsByName(serverPlayerEntity.getName());
                if (!stats.InParty() || stats.party.leader.getName() != serverPlayerEntity.getName()) return; // No party if not in party
                parties.add(stats.party);
            });
            return parties;
        }

        /**
         * Get party by player instance.
         * @return
         */
        public static Party GetParty(EntityPlayerMP player)
        {
            return MMOParties.GetStatsByName(player.getName()).party;
        }

        /**
         * Get the relation between two players.
         * @param player
         * @param target
         * @return
         */
        public static EnumRelation GetRelation(EntityPlayerMP player, EntityPlayerMP target)
        {
            PlayerStats playerStats = MMOParties.GetStatsByName( player.getName() );

            if (playerStats.InParty() && playerStats.party.IsMember( target )) return EnumRelation.PARTY; // The member is part of a part with the other.
            return EnumRelation.NONE;
        }
    }
}
