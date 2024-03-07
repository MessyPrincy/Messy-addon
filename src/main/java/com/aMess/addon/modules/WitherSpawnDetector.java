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
        .name("")
        .description("  ")
        .defaultValue(true)
        .build()
    );
    private final Setting<String> customPanicMessage = sgWorldEvents.add(new StringSetting.Builder()
        .name("Panic message")
        .description("Custom panic Message")
        .defaultValue("Wither attack! Scared!")
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
        }
    }

    @EventHandler
    private void onPanicPacket(PacketEvent.Receive event) {
        if (event.packet instanceof WorldEventS2CPacket packet) {
            switch (packet.getEventId()) {
                case 1023:
                    if (withers.get()) panic();
                    break;
            }


        }
    }
}
