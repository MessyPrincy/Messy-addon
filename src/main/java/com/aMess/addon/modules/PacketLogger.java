package com.aMess.addon.modules;

import com.aMess.addon.MessyCoding;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;


//Mostly for personal use to get access to sent and received packets
public class PacketLogger extends Module {

    private final SettingGroup sgConfiguration = settings.createGroup("Configuration");
    private final SettingGroup sgSpam = settings.createGroup("Spam filters");

    public PacketLogger() {
        super(MessyCoding.CATEGORY, "Packet Logger", "Logs received and sent packets");
    }

    private final Setting<Boolean> chatLog = sgConfiguration.add(new BoolSetting.Builder()
        .name("Chat logs")
        .description("Enable/Disables the feature to log the packets in the chat (They are still logged in the console) Useful to prevent crashes")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> checkReceivedPacket = sgConfiguration.add(new BoolSetting.Builder()
        .name("Received Packets")
        .description("Enable/Disables the logger for S2C packets")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> checkSendPacket = sgConfiguration.add(new BoolSetting.Builder()
        .name("Sent Packets")
        .description("Enable/Disables the logger for C2S packets")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> spamFilter = sgSpam.add(new BoolSetting.Builder()
        .name("Spam filter")
        .description("Removes spam packets (class_2772, class_2676, class_2781, class_2604, class_2739, class_2684, class_2743, class_2761,class_2828, class_2848, class_2777, class_2672, class_2663, class_2626, class_2726, class_2637, class_2743, class_2739, class_2744, class_2604, class_2781, class_2726, class_2784, class_2684$class_2685, class_2684$class_2686, class_2684$class_2687, class_2828$class_2829, class_2828$class_2831, class_2828$class_2830)")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> automaticSpamFilter = sgSpam.add(new BoolSetting.Builder()
        .name("Automatic Spam Filter")
        .description("Automatically removes packets that are being spammed in the logs (Not recommended)")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> automaticSpamFilterStrength = sgSpam.add(new IntSetting.Builder()
        .name("Automatic Spam Filter Strength")
        .description("The number of packets to detect duplicates within.")
        .defaultValue(2)
        .min(2)
        .sliderMax(200)
        .build()
    );
    List<String> spamPackets = Collections.synchronizedList(new ArrayList<>()); //What packets to mute
    List<String> Packets = Collections.synchronizedList(new ArrayList<>());//To check recent packets
    Set<String> isDuplicate = new HashSet<>(); //To check for duplicates

    @Override
    public void onActivate() {
        spamPackets.clear();
        assert mc.player != null;
        mc.player.sendMessage(Text.of("Logging packets"), false);
        if (spamFilter.get()) {
            spamPackets.add("net.minecraft.class_2772");
            spamPackets.add("net.minecraft.class_2743");
            spamPackets.add("net.minecraft.class_2739");
            spamPackets.add("net.minecraft.class_2744");
            spamPackets.add("net.minecraft.class_2743");
            spamPackets.add("net.minecraft.class_2604");
            spamPackets.add("net.minecraft.class_2781");
            spamPackets.add("net.minecraft.class_2626");
            spamPackets.add("net.minecraft.class_2726");
            spamPackets.add("net.minecraft.class_2637");
            spamPackets.add("net.minecraft.class_2684");
            spamPackets.add("net.minecraft.class_2781");
            spamPackets.add("net.minecraft.class_2676");
            spamPackets.add("net.minecraft.class_2604");
            spamPackets.add("net.minecraft.class_2739");
            spamPackets.add("net.minecraft.class_2777");
            spamPackets.add("net.minecraft.class_2784");
            spamPackets.add("net.minecraft.class_2663");
            spamPackets.add("net.minecraft.class_2672");
            spamPackets.add("net.minecraft.class_2761");
            spamPackets.add("net.minecraft.class_2828");
            spamPackets.add("net.minecraft.class_2848");
            spamPackets.add("net.minecraft.class_2684.class_2685");
            spamPackets.add("net.minecraft.class_2684.class_2686");
            spamPackets.add("net.minecraft.class_2684.class_2687");
            spamPackets.add("net.minecraft.class_2828.class_2829");
            spamPackets.add("net.minecraft.class_2828.class_2831");
            spamPackets.add("net.minecraft.class_2828.class_2830");

        }
    }


    @EventHandler
    private void onReceivedPacket(PacketEvent.Receive event) {
        if (checkReceivedPacket.get()) {
            if (automaticSpamFilter.get()) {
                assert mc.player != null;
                if (Packets.size() == automaticSpamFilterStrength.get()) {
                    for (String element : Packets) {
                        if (!isDuplicate.add(element) && isDuplicate.size() <= automaticSpamFilterStrength.get() && !spamPackets.contains(element)) {
                            spamPackets.add(element);
                            ChatUtils.sendMsg(Formatting.AQUA, "Received packet: %s filtered for spam", element);
                        }
                    }
                    Packets.clear();
                    isDuplicate.clear();
                }

                else if (Packets.size() > automaticSpamFilterStrength.get()) {
                    Packets.clear();
                    ChatUtils.sendMsg(Formatting.RED, "Too many packets received at the same time");
                }

                else {
                    Packets.add(event.packet.getClass().getCanonicalName());
                }
            }

            if (!spamPackets.contains(event.packet.getClass().getCanonicalName())) {
                assert mc.player != null;
                if (chatLog.get()) {
                    ChatUtils.sendMsg(Formatting.DARK_GREEN, "Received packet: %s", event.packet.toString());
                }
                else {
                    LogUtils.getLogger().info(event.packet.toString());
                }
            }
        }
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (checkSendPacket.get()) {
            if (automaticSpamFilter.get()) {
                assert mc.player != null;
                    if (Packets.size() == automaticSpamFilterStrength.get()) {
                        for (String element : Packets) {
                            if (!isDuplicate.add(element) && isDuplicate.size() <= automaticSpamFilterStrength.get() && !spamPackets.contains(element)) {
                                spamPackets.add(element);
                                ChatUtils.sendMsg(Formatting.AQUA, "Sent packet: %s filtered for spam", element);
                            }
                        }
                        Packets.clear();
                        isDuplicate.clear();
                    }
                    else if (Packets.size() > automaticSpamFilterStrength.get()) {
                        Packets.clear();
                        ChatUtils.sendMsg(Formatting.RED, "Too many packets sent at the same time");
                    }
                    else {
                        Packets.add(event.packet.getClass().getCanonicalName());
                    }
            }
            if (!spamPackets.contains(event.packet.getClass().getCanonicalName())) {
                assert mc.player != null;
                if (chatLog.get()) {
                    ChatUtils.sendMsg(Formatting.DARK_RED, "Sent packet: %s", event.packet.toString());
                }
                else {
                    LogUtils.getLogger().info(event.packet.toString());
                }
            }
        }
    }
}
