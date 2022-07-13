package deathtags.api;

import deathtags.api.relation.EnumRelation;
import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

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
            return MMOParties.localParty.leader == Minecraft.getInstance().player;
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
                PlayerStats stats = MMOParties.GetStatsByName(serverPlayerEntity.getName().getString());
                if (!stats.InParty() || stats.party.leader.getName().getString() != serverPlayerEntity.getName().getString()) return; // No party if not in party
                parties.add(stats.party);
            });
            return parties;
        }

        /**
         * Get party by player instance.
         * @return
         */
        public static Party GetParty(ServerPlayer player)
        {
            return MMOParties.GetStatsByName(player.getName().getString()).party;
        }

        /**
         * Get the relation between two players.
         * @param player
         * @param target
         * @return
         */
        public static EnumRelation GetRelation(ServerPlayer player, ServerPlayer target)
        {
            PlayerStats playerStats = MMOParties.GetStatsByName( player.getName().getContents() );

            if (playerStats.InParty() && playerStats.party.IsMember( target )) return EnumRelation.PARTY; // The member is part of a part with the other.
            return EnumRelation.NONE;
        }
    }
}
