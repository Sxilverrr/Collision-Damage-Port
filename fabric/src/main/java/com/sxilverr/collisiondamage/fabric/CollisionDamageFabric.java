package com.sxilverr.collisiondamage.fabric;

import com.sxilverr.collisiondamage.CollisionDamage;
import com.sxilverr.collisiondamage.config.CollisionDamageConfig;
import com.sxilverr.collisiondamage.config.Config;
import com.sxilverr.collisiondamage.handler.CollisionDamageHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
//? if >=1.21.1 {
/*import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
*///?}

public final class CollisionDamageFabric implements ModInitializer {

    //? if >=1.21.11 {
    /*static final CustomPacketPayload.Type<Impact> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CollisionDamage.MODID, "collision_s"));
    *///?} else if >=1.21.1 {
    /*static final CustomPacketPayload.Type<Impact> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CollisionDamage.MODID, "collision_s"));
    *///?} else {
    static final ResourceLocation IMPACT = new ResourceLocation(CollisionDamage.MODID, "collision_s");
    //?}

    //? if >=1.21.1 {
    /*record Impact(double accel) implements CustomPacketPayload {

        static final StreamCodec<RegistryFriendlyByteBuf, Impact> CODEC = StreamCodec.composite(ByteBufCodecs.DOUBLE, Impact::accel, Impact::new);

        @Override
        public Type<Impact> type() {
            return TYPE;
        }
    }
    *///?}

    @Override
    public void onInitialize() {
        CollisionDamageConfig.register();
        //? if >=1.21.1 {
        /*PayloadTypeRegistry.playC2S().register(TYPE, Impact.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) ->
                context.server().execute(() -> CollisionDamageHelper.onPlayerImpact(context.player(), payload.accel())));
        *///?} else {
        ServerPlayNetworking.registerGlobalReceiver(IMPACT, (server, player, handler, buf, responseSender) -> {
            double accel = buf.readDouble();
            server.execute(() -> CollisionDamageHelper.onPlayerImpact(player, accel));
        });
        //?}
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            CollisionDamageConfig.sync();
            if (!Config.globalCollisionDamage) return;
            for (ServerLevel level : server.getAllLevels()) {
                for (Entity entity : level.getAllEntities()) {
                    if (entity instanceof LivingEntity living) CollisionDamageHelper.tickMob(living);
                }
            }
        });
    }
}
