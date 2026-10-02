package com.sxilverr.collisiondamage.handler;

import com.sxilverr.collisiondamage.config.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class CollisionDamageHelper {

    private static final class State {
        double prevSpeed;
        boolean prevCeiling;
    }

    private static final Map<Entity, State> STATES = Collections.synchronizedMap(new WeakHashMap<>());

    private CollisionDamageHelper() {
    }

    public static double impact(LivingEntity entity) {
        Entity source = entity.isPassenger() ? entity.getRootVehicle() : entity;
        Vec3 motion = source.getDeltaMovement();
        double squareSum = motion.x * motion.x + motion.z * motion.z;
        if (Config.includeYAxis && motion.y > 0) squareSum += motion.y * motion.y;
        double speed = ((int) (Math.sqrt(squareSum) * 20 * 100)) / 100.0;

        State state = STATES.computeIfAbsent(entity, key -> new State());
        double accel = state.prevSpeed - speed;
        state.prevSpeed = speed;

        if (entity.isFallFlying()) return 0;
        if (Config.ignoreWhenRiding && entity.isPassenger()) return 0;
        if (Config.ignoreInWater && entity.isInWater()) return 0;

        boolean ceiling = source.verticalCollision && !source.verticalCollisionBelow;
        boolean newCeiling = ceiling && !state.prevCeiling;
        state.prevCeiling = ceiling;
        boolean collided = source.horizontalCollision || (Config.includeYAxis && newCeiling);
        return collided && accel > Config.accelerationThreshold ? accel : 0;
    }

    public static void tickMob(LivingEntity entity) {
        if (Config.globalCollisionDamage && !(entity instanceof Player) && entity.level() instanceof ServerLevel) {
            applyCollisionDamage(entity, impact(entity));
        }
    }

    public static void onPlayerImpact(ServerPlayer player, double accel) {
        applyCollisionDamage(player, accel);
        Entity vehicle = player.getRootVehicle();
        if (Config.damageVehicle && vehicle != player && !(Config.globalCollisionDamage && vehicle instanceof LivingEntity)) {
            applyCollisionDamage(vehicle, accel);
        }
    }

    private static void applyCollisionDamage(Entity target, double accel) {
        if (accel <= Config.accelerationThreshold) return;

        float damage = Math.round((accel - Config.accelerationThreshold) * 4 * Config.damageMultiplier) / 4F;
        if (Config.maxDamage > 0) damage = Math.min(damage, (float) Config.maxDamage);
        if (damage <= 0 || !(target.level() instanceof ServerLevel level)) return;

        target.playSound(damage > 4 ? SoundEvents.GENERIC_BIG_FALL : SoundEvents.GENERIC_SMALL_FALL, 1.0F, 1.0F);
        DamageSource source = Config.damageTypeWall ? target.damageSources().flyIntoWall() : target.damageSources().fall();
        //? if >=1.21.2 {
        /*target.hurtServer(level, source, damage);
        *///?} else {
        target.hurt(source, damage);
        //?}
    }
}
