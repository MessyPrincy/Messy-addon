package com.aMess.addon.modules;

import com.aMess.addon.MessyCoding;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;


//Mostly for personal use to get access to sent and received packets
public class PacketLogger extends Module {

    public PacketLogger() {
        super(MessyCoding.CATEGORY, "Packet Logger", "Logs received and sent packets");
    }

    @EventHandler
    private void onReceivedPacket(PacketEvent.Receive event) {
        LogUtils.getLogger().info(event.packet.toString());
        assert mc.player != null;
        mc.player.sendMessage(Text.of(event.packet.toString()));
    }

    @EventHandler
    private void onSentPacket(PacketEvent.Sent event) {
        LogUtils.getLogger().info(event.packet.toString());
        assert mc.player != null;
        mc.player.sendMessage(Text.of(event.packet.toString()));
    }

}
