package com.sxilverr.collisiondamage.fabric;

import com.sxilverr.collisiondamage.config.CollisionDamageConfig;
import com.sxilverr.collisiondamage.handler.CollisionDamageHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//? if <1.21.1 {
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
//?}

public final class CollisionDamageFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            CollisionDamageConfig.sync();
            double accel = CollisionDamageHelper.impact(client.player);
            if (accel <= 0) return;
            //? if >=1.21.1 {
            /*ClientPlayNetworking.send(new CollisionDamageFabric.Impact(accel));
            *///?} else {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeDouble(accel);
            ClientPlayNetworking.send(CollisionDamageFabric.IMPACT, buf);
            //?}
        });
    }
}
