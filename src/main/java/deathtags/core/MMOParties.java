package deathtags.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import deathtags.commands.*;
import deathtags.events.EventHandler;
import deathtags.gui.HealthBar;
import deathtags.networking.MessageSendMemberData;
import deathtags.networking.MessageUpdateParty;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import scala.collection.concurrent.Debug;

@Mod(modid = MMOParties.MODID, version = MMOParties.VERSION, name = MMOParties.NAME)
public class MMOParties {

	public static final String MODID = "mmoparties";
	public static final String VERSION = "1.0.0";
	public static final String NAME = "RPG Parties";
	
	public static SimpleNetworkWrapper network;
	
	public static Party localParty;
	public static Map<EntityPlayerMP, PlayerStats> PlayerStats = new HashMap<EntityPlayerMP, PlayerStats>();
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) 
	{
		System.out.println(MODID + " is pre-loading!");
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) 
	{
		System.out.println(MODID + " is loading!");
		
	    network.registerMessage(MessageUpdateParty.Handler.class, MessageUpdateParty.class, 0, Side.CLIENT);
	    network.registerMessage(MessageSendMemberData.Handler.class, MessageSendMemberData.class, 1, Side.CLIENT);
	    
	    network.registerMessage(MessageUpdateParty.Handler.class, MessageUpdateParty.class, 0, Side.SERVER);
	    network.registerMessage(MessageSendMemberData.Handler.class, MessageSendMemberData.class, 1, Side.SERVER);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) 
	{
		System.out.println(MODID + " is post-loading!");
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		
	    if (event.getSide() == Side.CLIENT)
	        MinecraftForge.EVENT_BUS.register(new HealthBar(Minecraft.getMinecraft()));
	}
	
	@Mod.EventHandler
	public static void serverStarting(FMLServerStartingEvent event)
	{
		Debug.log("Registering commands");
		
		event.registerServerCommand(new PartyCommand());
		event.registerServerCommand(new MuteCommand());
	}
	
	/**
	 * Get the stat value of a player by their name.
	 * @param name
	 * @return
	 */
	public static PlayerStats GetStatsByName(String name)
	{
		for (Entry<EntityPlayerMP, deathtags.stats.PlayerStats> plr : PlayerStats.entrySet()) {
			if (plr.getKey().getName() == name)
				return plr.getValue();
		}
		
		return null;
	}
	
}
