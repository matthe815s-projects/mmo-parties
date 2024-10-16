package dev.matthe815.mmoparties.common.config;

public class ConfigCommon {
    public static class Common
    {
        public Boolean friendlyFireDisabled;
        public Boolean allowPartyTP;
        public Boolean autoAssignParties;
        public Boolean allowInviteAll;
        public Boolean debugMode;
    }

    public static class Client
    {
        public Boolean showAbsorption;
        public Boolean showArmor;
        public Boolean showHunger;
        public Integer uiYOffset;
        public Boolean useSimpleUI;
        public String  anchorPoint;
        public Boolean hideSelf;
        public Boolean numbersAsPercentage;
        public String extraNumberType;
        public Boolean hideGUI;
    }

    public static Client CLIENT = new Client();
    public static Common COMMON = new Common();
}
