package deathtags.core;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = MMOParties.MODID)
public class ConfigHandler {
	public static ClientOptions Client_Options = new ClientOptions();
	public static ServerOptions Server_Options = new ServerOptions();
	public static DebugOptions Debug_Options = new DebugOptions();
	
	public static class ClientOptions {
		public boolean showShields = true;
		public boolean showAbsorption = true;
		public boolean showArmor = true;
		public boolean showHunger = false;
	}
	
	public static class ServerOptions {
		@Comment("Whether or not to allow party members to TP to each other.")
		public boolean allowPartyTP = true;
	}
	
	public static class DebugOptions {
		public boolean showPacketMessages = false;
		
		public boolean showMiscMessages = false;
		
		public boolean showUpdateMessages = false;
	}
}
