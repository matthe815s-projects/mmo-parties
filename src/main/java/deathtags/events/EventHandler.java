package deathtags.events;

import deathtags.core.MMOParties;
import deathtags.stats.PlayerStats;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@EventBusSubscriber
public class EventHandler {
	
	@SubscribeEvent
	public void onPlayerJoined(PlayerLoggedInEvent event)
	{
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		
		// If there's no key (which there shouldn't be) add one.
		if (!MMOParties.PlayerStats.containsKey(player)) MMOParties.PlayerStats.put(player, new PlayerStats ( player ));
	}
	
	@SubscribeEvent
	public void onPlayerLeave(PlayerLoggedOutEvent event)
	{
		if ( MMOParties.GetStatsByName( event.player.getName() ) == null ) return; // No need to do anything if there's no player.
		
		PlayerStats playerStats = MMOParties.GetStatsByName( event.player.getName() );
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		
		// Leave a party if you're currently in one.
		if ( playerStats.InParty() ) playerStats.party.Leave(player);
		
		MMOParties.PlayerStats.remove(player);
	}

	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onPlayerHurt(LivingHurtEvent event)
	{
		if ( event.getEntity().world.isRemote ) return; // Perform on the server only.
		if (! (event.getEntityLiving() instanceof EntityPlayerMP) || ! (event.getSource().getTrueSource() instanceof EntityPlayerMP) ) return; // Only perform on players.
		
		EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
		EntityPlayerMP source = (EntityPlayerMP) event.getSource().getTrueSource();
		
		PlayerStats playerStats = MMOParties.GetStatsByName( player.getName() );
		PlayerStats sourceStats = MMOParties.GetStatsByName( source.getName() );
		
		if (!playerStats.InParty() || !sourceStats.InParty()) return; // Nothing matters if both players aren't in a party.
		if (playerStats.party.IsMember( source )) event.setCanceled(true); // Cancel received damage if it's from a party member.
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if ( event.player.world.isRemote ) return; // Server only.
		
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		PlayerStats stats = MMOParties.GetStatsByName( player.getName() );
		
		// Process teleporting.
		if (stats.teleportTicks > 0) {
			stats.teleportTicks --;
			
			if (stats.teleportTicks <= 0) stats.CommenceTeleport(); // Teleport the player.
		}

		if (stats.party != null) MMOParties.PlayerStats.get(player).party.SendPartyMemberData(player, false); // Sync the player.
	}
}
