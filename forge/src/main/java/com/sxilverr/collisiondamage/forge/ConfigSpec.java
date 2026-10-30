package com.sxilverr.collisiondamage.forge;

import com.sxilverr.collisiondamage.CollisionDamage;
import com.sxilverr.collisiondamage.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = CollisionDamage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigSpec {

    private static final String KEY = CollisionDamage.MODID + ".configuration.";
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue ACCELERATION_THRESHOLD = BUILDER
            .comment("How large the player's deceleration must be before they will begin taking damage. (Measured in meters per second)")
            .translation(KEY + "accelerationThreshold")
            .defineInRange("accelerationThreshold", 12.0D, 5.0D, 100.0D);

    private static final ForgeConfigSpec.DoubleValue DAMAGE_MULTIPLIER = BUILDER
            .comment("Multiplies the damage taken when over the threshold. Default 1.0x is 1 damage per 1m/s/s over threshold.")
            .translation(KEY + "damageMultiplier")
            .defineInRange("damageMultiplier", 1.0D, 0.0D, 100.0D);

    private static final ForgeConfigSpec.BooleanValue DAMAGE_TYPE_WALL = BUILDER
            .comment("Use damage type FLY_INTO_WALL? If false, will instead use FALL. (Set this to false if you want stuff like feather-falling to affect collision damage as well)")
            .translation(KEY + "damageTypeWall")
            .define("damageTypeWall", true);

    private static final ForgeConfigSpec.DoubleValue MAX_DAMAGE = BUILDER
            .comment("Maximum damage per hit. Set to 0 for no cap.")
            .translation(KEY + "maxDamage")
            .defineInRange("maxDamage", 0.0D, 0.0D, 1000.0D);

    private static final ForgeConfigSpec.BooleanValue IGNORE_WHEN_RIDING = BUILDER
            .comment("Skip collision damage while riding (boats, horses, etc).")
            .translation(KEY + "ignoreWhenRiding")
            .define("ignoreWhenRiding", false);

    private static final ForgeConfigSpec.BooleanValue IGNORE_IN_WATER = BUILDER
            .comment("Skip collision damage while in water.")
            .translation(KEY + "ignoreInWater")
            .define("ignoreInWater", false);

    private static final ForgeConfigSpec.BooleanValue INCLUDE_Y_AXIS = BUILDER
            .comment("Include Y axis collision damage from upward impacts.")
            .translation(KEY + "includeYAxis")
            .define("includeYAxis", false);

    private static final ForgeConfigSpec.BooleanValue DAMAGE_VEHICLE = BUILDER
            .comment("Damage the vehicle when the rider triggers collision damage.")
            .translation(KEY + "damageVehicle")
            .define("damageVehicle", true);

    private static final ForgeConfigSpec.BooleanValue GLOBAL_COLLISION_DAMAGE = BUILDER
            .comment("Apply collision damage to all living entities.")
            .translation(KEY + "globalCollisionDamage")
            .define("globalCollisionDamage", false);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) return;
        Config.accelerationThreshold = ACCELERATION_THRESHOLD.get();
        Config.damageMultiplier = DAMAGE_MULTIPLIER.get();
        Config.damageTypeWall = DAMAGE_TYPE_WALL.get();
        Config.maxDamage = MAX_DAMAGE.get();
        Config.ignoreWhenRiding = IGNORE_WHEN_RIDING.get();
        Config.ignoreInWater = IGNORE_IN_WATER.get();
        Config.includeYAxis = INCLUDE_Y_AXIS.get();
        Config.damageVehicle = DAMAGE_VEHICLE.get();
        Config.globalCollisionDamage = GLOBAL_COLLISION_DAMAGE.get();
    }
}
