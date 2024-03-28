package com.aMess.addon.modules;

import com.aMess.addon.MessyCoding;
import com.mojang.brigadier.arguments.StringArgumentType;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.*;

public class ModeratorTracker extends Module {
    private final SettingGroup sgInformation = settings.createGroup("Information");
    private final SettingGroup sgNonModVanillaActions = settings.createGroup("Non mod actions");
    private final SettingGroup sgVanillaActions = settings.createGroup("Vanilla actions");
    private final SettingGroup sgHiddenActions = settings.createGroup("Hidden actions");


    public ModeratorTracker() {
        super(MessyCoding.CATEGORY, "Moderator Tracker", "Tracks various moderator actions");
    }

    private final Setting<String> modIdentifier = sgInformation.add(new StringSetting.Builder()
        .name("Mod Identification")
        .description("How to identify a mod, usually with the [Tag] before their name")
        .defaultValue("color=#67D47B")
        .build()
    );
    private final Setting<Boolean> normalJoin = sgNonModVanillaActions.add(new BoolSetting.Builder()
        .name("Non-Moderator Joins")
        .description("Automatically sends you a message if a player joins joins")
        .defaultValue(false)
        .build()
    );
    private final Setting<String> customJoinMessage = sgNonModVanillaActions.add(new StringSetting.Builder()
        .name("Join Message")
        .description("What message to send, the display name will always be last")
        .defaultValue("Well met")
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
    private void onJoinMessagePacket(PacketEvent.Receive event) {
        if (event.packet instanceof GameMessageS2CPacket packet) {
            assert mc.player !=null;
            moderatorIdentification.add(modIdentifier.get().trim());
            if (packet.content().toString().toLowerCase().contains("joined the")) {
                if (modJoin.get() && packet.content().toString().contains(moderatorIdentification.get(0))) {
                    String packetContent = packet.content().toString();
                    String[] separatingScopes = packetContent.split(",");
                    String isolatingDisplayNameScope = separatingScopes[3];
                    int cutter = isolatingDisplayNameScope.indexOf("}");
                    String displayName = isolatingDisplayNameScope.substring(10, cutter).trim();
                    if (publicNotifier.get()) {
                        if (packet.content().toString().contains("Gurkenwerfer_")) {
                            List<String> messageContent =
                                List.of(
                                    "Oi Gurk's here! Get the Club Mate!",
                                    "Hey! Es ist der Gurkenmann. Wie geht es dir?"
                                );
                            mc.player.networkHandler.sendChatMessage(messageContent.get((int) (Math.random() * messageContent.size())));
                        }
                        else {
                            List<String> messageContent =
                                List.of(
                                    "Quick, everybody act natural! " + displayName + " incoming!",
                                    "Oh no! " + displayName + " is here D: HIDE!",
                                    "Emergency protocol engaged! " + displayName + " sighting imminent! HIDE!!",
                                    "Sound the alarms, " + displayName + " detected!",
                                    "Abort mission! " + displayName + " on the horizon, maintain decorum!",
                                    "This is not a drill! " + displayName + " spotted, let's keep it clean, people!"
                                );
                            mc.player.networkHandler.sendChatMessage(messageContent.get((int) (Math.random() * messageContent.size())));
                        }
                    } else {
                        info("%s has joined! D: HIDE!", displayName);
                    }
                }
                else if (modJoin.get() && packet.content().toString().contains("Roach606") && publicNotifier.get()) {
                    mc.player.networkHandler.sendChatMessage("I-I cannot believe it. He Who Shall Not Be Named has joined... RUN!! PANIC!!");
                    mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.literal("Roach joined!")));
                }
            }
            else if (packet.content().toString().contains(moderatorIdentification.get(0)) && packet.content().toString().toLowerCase().contains("left the") && modLeave.get()) {
                String packetContent = packet.content().toString();
                String[] separatingScopes = packetContent.split(",");
                String isolatingDisplayNameScope = separatingScopes[3];
                int cutter = isolatingDisplayNameScope.indexOf("}");
                String displayName = isolatingDisplayNameScope.substring(10, cutter).trim();
                if (publicNotifier.get()) {
                    List<String> messageContent =
                        List.of(
                            "Phew, " + displayName + "'s gone! Time to let loose and relax a bit.",
                            "Finally, freedom reigns! " + displayName + "'s departure calls for celebration.",
                            displayName + "'s departure means we can relax without constantly looking over our shoulders.",
                            displayName + " has left, we're safe!",
                            displayName + "'s gone! Let's revel in the liberty.",
                            "Now that " + displayName + "'s left, we can play without the fear of getting reprimanded. Let's enjoy the liberation!"
                        );
                    mc.player.networkHandler.sendChatMessage(messageContent.get((int) (Math.random() * messageContent.size())));
                } else {
                    info("%s has left, good news!", displayName);
                }
            }
        }
        moderatorIdentification.clear();
    }

    @EventHandler
    private void onJoinPacket(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerListS2CPacket packet) {
            if (normalJoin.get()) {
                if (packet.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
                    for (PlayerListS2CPacket.Entry player : packet.getEntries()) {
                        PlayerListEntry newPlayer = Objects.requireNonNull(mc.getNetworkHandler()).getPlayerListEntry(player.profileId());
                        if (newPlayer == null) continue;
                        info(newPlayer.getProfile().getName());
                        info(newPlayer.getProfile().getName() + "has joined");
                    }
                }
            }
        }

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
                        }
                    }
                }
            }
        }
    }
}

