package deathtags.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
    public static class Common
    {
        public final ForgeConfigSpec.ConfigValue<Boolean> friendlyFireDisabled;

        public final ForgeConfigSpec.ConfigValue<Boolean> allowPartyTP;
        public final ForgeConfigSpec.ConfigValue<Boolean> autoAssignParties;
        public final ForgeConfigSpec.ConfigValue<Boolean> allowInviteAll;
        public final ForgeConfigSpec.ConfigValue<Boolean> debugMode;

        public Common(ForgeConfigSpec.Builder builder)
        {
            builder.push("friendly-fire");
            this.friendlyFireDisabled = builder.comment("Whether or not you can take friendly fire from players in your party.")
                            .define("Friendly Fire Disabled", true);
            builder.push("server-options");
            this.allowPartyTP = builder.comment("Whether or not to allow players to teleport to eachother within a party")
                            .define("Allow teleportation", true);
            this.autoAssignParties = builder.comment("Whether or not to auto-assign all players to a party")
                            .define("Auto-assign Parties", false);
            this.allowInviteAll = builder.comment("Whether or not to allow invite all")
                            .define("Allow Invite All", true);
            this.debugMode = builder.comment("Whether or not debug mode is enabled")
                            .define("Enable Debug Mode", false);
            builder.pop();
        }
    }

    public static class Client
    {
        public final ForgeConfigSpec.ConfigValue<Boolean> showAbsorption;
        public final ForgeConfigSpec.ConfigValue<Boolean> showArmor;
        public final ForgeConfigSpec.ConfigValue<Boolean> showHunger;
        public final ForgeConfigSpec.ConfigValue<Integer> uiYOffset;
        public final ForgeConfigSpec.ConfigValue<Boolean> useSimpleUI;
        public final ForgeConfigSpec.ConfigValue<String>  anchorPoint;
        public final ForgeConfigSpec.ConfigValue<Boolean> hideSelf;
        public final ForgeConfigSpec.ConfigValue<Boolean> numbersAsPercentage;
        public final ForgeConfigSpec.ConfigValue<Boolean> hideGUI;

        public Client(ForgeConfigSpec.Builder builder)
        {
            builder.push("display-options");
            this.showAbsorption = builder.comment("Whether or not to display absorption hearts")
                    .define("Show Absorption", true);
            this.showArmor = builder.comment("Whether or not to display armor")
                    .define("Show Armor", true);
            this.showHunger = builder.comment("Whether or not to display hunger")
                    .define("Show Hunger", true);
            this.uiYOffset = builder.comment("The vertical offset at which to display the party list at.")
                    .define("Y Offset", 2);
            this.useSimpleUI = builder.comment("Whether or not to use the simple UI")
                    .define("Use Simple UI", false);
            this.anchorPoint = builder.comment("The point to anchor the UI to.")
                    .define("UI Anchor", "top-left");
            this.hideSelf = builder.comment("Whether or not to hide yourself in the party list")
                    .define("Hide Self", true);
            this.hideGUI = builder.comment("Whether or not to hide your own GUI.")
                    .define("Hide GUI", false);
            this.numbersAsPercentage = builder.comment("Whether or not to render all HUD numbers as percentages instead.")
                    .define("Numbers As Percentages", false);
        }
    }

    public static final Common COMMON;
    public static final Client CLIENT;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static
    {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();

        Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();
    }
}
