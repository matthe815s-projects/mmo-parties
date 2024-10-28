package dev.matthe815.mmoparties.forge.config;

import dev.matthe815.mmoparties.forge.core.MMOParties;
import net.minecraftforge.common.config.Config;

@Config(modid = MMOParties.MODID)
public class ConfigHolder {
    public static class Common
    {
        public boolean friendlyFireDisabled = true;

        public boolean allowPartyTP = true;
        public boolean autoAssignParties = false;
        public boolean allowInviteAll = false;
        public boolean debugMode = false;
    }

    public static class Client
    {
        public boolean showAbsorption = false;
        public boolean showArmor = false;
        public boolean showHunger = true;
        public int uiYOffset = 3;
        public boolean useSimpleUI = false;
        public String  anchorPoint = "top-left";
        public boolean hideSelf = true;
        public boolean numbersAsPercentage = false;
        public String extraNumberType = "additional";
        public boolean hideGUI = false;
    }

    public static final Common COMMON = new Common();
    public static final Client CLIENT = new Client();
}
