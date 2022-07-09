package deathtags.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
    public static class Common
    {
        private static final boolean friendlyFireDisabledDefault = true;

        public final ForgeConfigSpec.ConfigValue<Boolean> friendlyFireDisabled;

        public final ForgeConfigSpec.ConfigValue<Boolean> showAbsorption;
        public final ForgeConfigSpec.ConfigValue<Boolean> showArmor;
        public final ForgeConfigSpec.ConfigValue<Boolean> showHunger;

        public final ForgeConfigSpec.ConfigValue<Boolean> allowPartyTP;

        public Common(ForgeConfigSpec.Builder builder)
        {
            builder.push("friendly-fire");
            this.friendlyFireDisabled = builder.comment("Whether or not you can take friendly fire from players in your party.")
                            .define("Friendly Fire Disabled", friendlyFireDisabledDefault);
            builder.push("display-options");
            this.showAbsorption = builder.comment("Whether or not to display absorption hearts")
                            .define("Show Absorption", true);
            this.showArmor = builder.comment("Whether or not to display armor")
                    .define("Show Absorption", true);
            this.showHunger = builder.comment("Whether or not to display hunger")
                    .define("Show Hunger", true);
            builder.push("server-options");
            this.allowPartyTP = builder.comment("Whether or not to allow players to teleport to eachother within a party")
                            .define("Allow teleportation", true);
            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static
    {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
