package com.aMess.addon.modules;

import com.aMess.addon.MessyCoding;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import meteordevelopment.meteorclient.settings.StringSetting;
import java.util.List;

public class WitherSpawnDetector extends Module {

    private final SettingGroup sgWorldEvents = settings.createGroup("World Events");

    public WitherSpawnDetector() {
        super(MessyCoding.CATEGORY, "Wither Spawn Detector", "Checks when a wither spawns & sends a message");


    }
    //Thanks to Gurkenwerfer_ for basically telling me everything
    private final Setting<Boolean> withers = sgWorldEvents.add(new BoolSetting.Builder()
        .name("Wither Panic")
        .description("Sends a chat message when a wither spawns.")
        .defaultValue(true)
        .build()
    );
    private final Setting<String> customPanicMessage = sgWorldEvents.add(new StringSetting.Builder()
        .name("Panic message")
        .description("Custom panic Message")
        .defaultValue("AAAAH!! PANIC!!")
        .build()
    );
    public void panic() {
        List<String> messageContent =
            List.of(
                "Wither spawned unexpectedly! Not ready! Panic!",
                "Help! Wither wrecking my world! Freaking out!",
                "Bad! Wither loose! Trapped! Panic!",
                customPanicMessage.get());

        try {
            assert mc.player != null;
            mc.player.networkHandler.sendChatMessage(messageContent.get((int) (Math.random() * messageContent.size())));
        } catch (NullPointerException e) {
            MessyCoding.LOG.error("Error sending panic message: " + e);
        }
    }

    @EventHandler
    private void onPanicPacket(PacketEvent.Receive event) {
        if (event.packet instanceof WorldEventS2CPacket packet) {
            // if a switch statement only has one case, it's better to use an if statement
            if (packet.getEventId() == 1023) {
                if (withers.get()) panic();
            }
        }
    }
}
