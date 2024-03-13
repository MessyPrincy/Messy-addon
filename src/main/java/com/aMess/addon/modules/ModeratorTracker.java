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
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.*;

public class ModeratorTracker extends Module {
    private final SettingGroup sgInformation = settings.createGroup("Information");
    private final SettingGroup sgVanillaActions = settings.createGroup("Vanilla actions");
    private final SettingGroup sgHiddenActions = settings.createGroup("Hidden actions");


    public ModeratorTracker() {
        super(MessyCoding.CATEGORY, "Moderator Tracker", "Tracks various moderator actions");
    }

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
    private final Setting<Boolean> gamemodeChange = sgHiddenActions.add(new BoolSetting.Builder()
        .name("Gamemode change")
        .description("Detects if a gamemode is changed, and verifies if the moderator is in vanish or not")
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
                        mc.player.networkHandler.sendChatMessage("Oh no! A mod is here D: HIDE!");
                        if (packet.content().toString().contains("Gurkenwerfer_")) {
                            mc.player.networkHandler.sendChatMessage("Oh wait it's Gurk. Get the Club Mate!");
                        }
                    } else {
                        ChatUtils.sendMsg(Formatting.DARK_PURPLE, "A mod has joined! D: HIDE!");
                    }
                }
            }
            else if (packet.content().toString().toLowerCase().contains("left the") && modLeave.get()) {
                if (packet.content().toString().contains(moderatorIdentification.get(0))) {
                    assert mc.player != null;
                    if (publicNotifier.get()) {
                        mc.player.networkHandler.sendChatMessage("A mod has left, we're safe!");
                    } else {
                        info("A mod has left, good news!");
                    }
                }
            }
        }
        moderatorIdentification.clear();
    }

    @EventHandler
    private void onGameModeChange(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerListS2CPacket packet) {
            if (gamemodeChange.get()) {
                for (PlayerListS2CPacket.Entry player : packet.getEntries()) {
                    if (packet.getActions().contains(PlayerListS2CPacket.Action.UPDATE_GAME_MODE)) {
                        PlayerListEntry suspiciousPlayer = Objects.requireNonNull(mc.getNetworkHandler()).getPlayerListEntry(player.profileId());
                        if (suspiciousPlayer == null) continue;
                        GameMode gameMode = player.gameMode();
                        if (suspiciousPlayer.getGameMode() != gameMode) {
                            info("%s changed mode to %s", suspiciousPlayer.getProfile().getName(), player.gameMode());
                            assert mc.world != null;
                            if (!mc.world.getPlayers().toString().contains(suspiciousPlayer.getProfile().getName())) {
                                info("%s is doing  shenanigans in vanish");
                            }
                        }
                    } else continue;
                }
            }
        }
    }
}

