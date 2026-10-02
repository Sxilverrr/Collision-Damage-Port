package com.sxilverr.collisiondamage.neoforge;

import com.sxilverr.collisiondamage.CollisionDamage;
import com.sxilverr.collisiondamage.config.ConfigSpec;
import com.sxilverr.collisiondamage.handler.CollisionDamageHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
*///?} else {
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
//?}

@Mod(CollisionDamage.MODID)
public class CollisionDamageNeoForge {

    private record Impact(double accel) implements CustomPacketPayload {

        //? if >=1.21.11 {
        /*static final Type<Impact> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CollisionDamage.MODID, "collision_s"));
        *///?} else {
        static final Type<Impact> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CollisionDamage.MODID, "collision_s"));
        //?}

        static final StreamCodec<RegistryFriendlyByteBuf, Impact> CODEC = StreamCodec.composite(ByteBufCodecs.DOUBLE, Impact::accel, Impact::new);

        @Override
        public Type<Impact> type() {
            return TYPE;
        }
    }

    public CollisionDamageNeoForge(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, ConfigSpec.SPEC);
        modEventBus.addListener(CollisionDamageNeoForge::registerPayloads);
        NeoForge.EVENT_BUS.addListener(CollisionDamageNeoForge::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(CollisionDamageNeoForge::onEntityTick);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(Impact.TYPE, Impact.CODEC, CollisionDamageNeoForge::handle);
    }

    private static void handle(Impact msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) CollisionDamageHelper.onPlayerImpact(player, msg.accel());
        });
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().isLocalPlayer()) return;
        double accel = CollisionDamageHelper.impact(event.getEntity());
        if (accel <= 0) return;
        //? if >=1.21.11 {
        /*ClientPacketDistributor.sendToServer(new Impact(accel));
        *///?} else {
        PacketDistributor.sendToServer(new Impact(accel));
        //?}
    }

    private static void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity entity) CollisionDamageHelper.tickMob(entity);
    }
}
