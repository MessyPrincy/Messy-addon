package com.aMess.addon.modules;

import com.aMess.addon.MessyCoding;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModeratorTracker extends Module {
    private final SettingGroup sgInformation = settings.createGroup("Prefix");
    private final SettingGroup sgVanillaActions = settings.createGroup("Vanilla actions");
    public ModeratorTracker() {
        super(MessyCoding.CATEGORY, "Moderator Tracker", "Tracks various moderator actions");
    }
    private final Setting<String> prefix = sgInformation.add(new StringSetting.Builder()
        .name("Prefix")
        .description("Choose what prefix to add before your public message. Leave blank if none are desired")
        .defaultValue("/p")
        .build()
    );
    private final Setting<String> modIdentifier = sgInformation.add(new StringSetting.Builder()
        .name("Mod Identification")
        .description("How to identify a mod, usually with the [Tag] before their name")
        .defaultValue("MC Mod")
        .build()
    );
    private final Setting<Boolean> modJoin = sgVanillaActions.add(new BoolSetting.Builder()
        .name("Moderator Joins")
        .description("Automatically sends you a message if a moderator joins")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> modLeave = sgVanillaActions.add(new BoolSetting.Builder()
        .name("Moderator Leaves")
        .description("Automatically sends you a message if a moderator leaves")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> publicNotifier = sgVanillaActions.add(new BoolSetting.Builder()
        .name("Public notification")
        .description("Automatically sends a public message if a moderator leaves of joins")
        .defaultValue(false)
        .build()
    );

    List<String> moderatorIdentification = Collections.synchronizedList(new ArrayList<>());

    @EventHandler
    private void onJoinPacket(PacketEvent.Receive event) {
        if (event.packet instanceof GameMessageS2CPacket packet) {
            moderatorIdentification.add(modIdentifier.get().trim());
            if (packet.content().toString().toLowerCase().contains("joined the") && modJoin.get()) {
                if (packet.content().toString().contains(moderatorIdentification.get(0))) {
                    assert mc.player != null;
                    if (publicNotifier.get()) {
                        mc.player.networkHandler.sendChatMessage(prefix.get().trim() + " Oh no! A mod is here D: HIDE!");
                        if (packet.content().toString().contains("Gurkenwerfer_")) {
                            mc.player.networkHandler.sendChatMessage(prefix.get().trim() + " Oh wait it's just Gurk. We're safe!... For now");
                        }
                    }
                    else {
                        ChatUtils.sendMsg(Formatting.DARK_PURPLE, "A mod has joined! D: HIDE!");
                    }
                }
            }
            else if (packet.content().toString().toLowerCase().contains("left the") && modLeave.get()) {
                if (packet.content().toString().contains(moderatorIdentification.get(0))) {
                    assert mc.player != null;
                    if (publicNotifier.get()) {
                        mc.player.networkHandler.sendChatMessage(prefix.get().trim() + " A mod has left, thank god!");
                    }
                    else {
                        ChatUtils.sendMsg(Formatting.DARK_PURPLE, "A mod has left, good news!");
                    }
                }
            }
        }
        moderatorIdentification.clear();
    }
}
