package deathtags.connectors;

import deathtags.core.MMOParties;
import deathtags.stats.Party;
import harmonised.pmmo.pmmo_saved_data.PmmoSavedData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.ModList;

public class ProjectMMOConnector {
    public static boolean IsLoaded()
    {
        return ModList.get().isLoaded("projectmmo");
    }

    public static void JoinParty(ServerPlayerEntity player)
    {

        harmonised.pmmo.party.Party party = PmmoSavedData.get().getParty(player.getUUID());

        for ( ServerPlayerEntity member : party.getOnlineMembers(player.getServer()) ) {
            if (MMOParties.GetStatsByName( member.getName().getContents() ).InParty()) {
                MMOParties.GetStatsByName( member.getName().getContents() ).party.Join ( player, true ); // Join a party if it exists.
                break;
            }
        }

        if ( !MMOParties.GetStatsByName( player.getName().getContents() ).InParty() ) Party.Create( player ); // Create a party if nonexistent
    }
}
