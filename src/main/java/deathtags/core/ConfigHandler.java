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
		public boolean useSimpleUI = false;
		public int uiYOffset = 3;
	}
	
	public static class ServerOptions {
		@Comment("Whether or not to allow party members to TP to each other.")
		public boolean allowPartyTP = true;

		@Comment("Whether or not players in a party will take friendly fire.")
		public boolean friendlyFireDisabled = true;

		@Comment("Whether or not to autoassign parties at server join.")
		public boolean autoAssignParties = false;
	}
	
	public static class DebugOptions {
		public boolean showPacketMessages = false;
		
		public boolean showMiscMessages = false;
		
		public boolean showUpdateMessages = false;
	}
}
