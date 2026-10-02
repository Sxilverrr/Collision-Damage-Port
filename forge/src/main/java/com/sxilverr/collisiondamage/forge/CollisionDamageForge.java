package com.sxilverr.collisiondamage.forge;

import com.sxilverr.collisiondamage.CollisionDamage;
import com.sxilverr.collisiondamage.config.ConfigSpec;
import com.sxilverr.collisiondamage.handler.CollisionDamageHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

@Mod(CollisionDamage.MODID)
public class CollisionDamageForge {

    private static final String PROTOCOL_VERSION = "1";

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CollisionDamage.MODID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private record Impact(double accel) {
    }

    public CollisionDamageForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigSpec.SPEC);
        CHANNEL.registerMessage(0, Impact.class,
                (msg, buf) -> buf.writeDouble(msg.accel()),
                (FriendlyByteBuf buf) -> new Impact(buf.readDouble()),
                CollisionDamageForge::handle);
        MinecraftForge.EVENT_BUS.addListener(CollisionDamageForge::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(CollisionDamageForge::onLivingTick);
    }

    private static void handle(Impact msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) CollisionDamageHelper.onPlayerImpact(ctx.get().getSender(), msg.accel());
        });
        ctx.get().setPacketHandled(true);
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !event.player.isLocalPlayer()) return;
        double accel = CollisionDamageHelper.impact(event.player);
        if (accel > 0) CHANNEL.sendToServer(new Impact(accel));
    }

    private static void onLivingTick(LivingEvent.LivingTickEvent event) {
        CollisionDamageHelper.tickMob(event.getEntity());
    }
}
