package com.sxilverr.collisiondamage.config;

import com.sxilverr.collisiondamage.CollisionDamage;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.util.Mth;

@me.shedaniel.autoconfig.annotation.Config(name = CollisionDamage.MODID)
public class CollisionDamageConfig implements ConfigData {

    private static ConfigHolder<CollisionDamageConfig> holder;

    @ConfigEntry.Gui.Tooltip
    public double accelerationThreshold = 12.0D;

    @ConfigEntry.Gui.Tooltip
    public double damageMultiplier = 1.0D;

    @ConfigEntry.Gui.Tooltip
    public boolean damageTypeWall = true;

    @ConfigEntry.Gui.Tooltip
    public double maxDamage = 0.0D;

    @ConfigEntry.Gui.Tooltip
    public boolean ignoreWhenRiding = false;

    @ConfigEntry.Gui.Tooltip
    public boolean ignoreInWater = false;

    @ConfigEntry.Gui.Tooltip
    public boolean includeYAxis = false;

    @ConfigEntry.Gui.Tooltip
    public boolean damageVehicle = true;

    @ConfigEntry.Gui.Tooltip
    public boolean globalCollisionDamage = false;

    @Override
    public void validatePostLoad() {
        accelerationThreshold = Mth.clamp(accelerationThreshold, 5.0D, 100.0D);
        damageMultiplier = Mth.clamp(damageMultiplier, 0.0D, 100.0D);
        maxDamage = Mth.clamp(maxDamage, 0.0D, 1000.0D);
    }

    public static void register() {
        holder = AutoConfig.register(CollisionDamageConfig.class, GsonConfigSerializer::new);
        sync();
    }

    public static void sync() {
        CollisionDamageConfig config = holder.getConfig();
        Config.accelerationThreshold = config.accelerationThreshold;
        Config.damageMultiplier = config.damageMultiplier;
        Config.damageTypeWall = config.damageTypeWall;
        Config.maxDamage = config.maxDamage;
        Config.ignoreWhenRiding = config.ignoreWhenRiding;
        Config.ignoreInWater = config.ignoreInWater;
        Config.includeYAxis = config.includeYAxis;
        Config.damageVehicle = config.damageVehicle;
        Config.globalCollisionDamage = config.globalCollisionDamage;
    }
}
