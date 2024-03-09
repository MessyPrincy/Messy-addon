package com.aMess.addon.modules;

import com.aMess.addon.MessyCoding;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;


//Mostly for personal use to get access to sent and received packets
public class PacketLogger extends Module {

    private final SettingGroup sgConfiguration = settings.createGroup("Configuration");
        public PacketLogger() {
        super(MessyCoding.CATEGORY, "Packet Logger", "Logs received and sent packets");
    }


    private final Setting<Boolean> chatLog = sgConfiguration.add(new BoolSetting.Builder()
        .name("Chat logs")
        .description("Enable/Disables the feature to log the packets in the chat (They are still logged in the console)")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> checkReceivedPacket = sgConfiguration.add(new BoolSetting.Builder()
        .name("Received Packets")
        .description("Enable/Disables the logger for S2C pac    kets")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> checkSendPacket = sgConfiguration.add(new BoolSetting.Builder()
        .name("Sent Packets")
        .description("Enable/Disables the logger for C2S packets")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> spamFilter = sgConfiguration.add(new BoolSetting.Builder()
        .name("Spam filter")
        .description("Removes spam packets (class_2676, class_2781, class_2604, class_2739, class_2684, class_2743, class_2761,class_2828, class_2848, class_2777, class_2672, class_2663, class_2626, class_2726, class_2637, class_2743, class_2739, class_2744, class_2604, class_2781, class_2726, class_2784, class_2684$class_2685, class_2684$class_2686, class_2684$class_2687, class_2828$class_2829, class_2828$class_2831, class_2828$class_2830)")
        .defaultValue(true)
        .build()
    );

    List<String> spamPackets = new ArrayList<String>();

    @Override
    public void onActivate() {
        assert mc.player != null;
        mc.player.sendMessage(Text.of("Logging packets"));

        if (spamFilter.get()) {
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
        if (checkReceivedPacket.get())
            if (!spamPackets.contains(event.packet.getClass().getCanonicalName())){
                LogUtils.getLogger().info(event.packet.toString());
                assert mc.player != null;
                if (chatLog.get())
                    ChatUtils.sendMsg(Formatting.DARK_GREEN,"Received packet: " + event.packet.toString());
        }
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (checkSendPacket.get())
            if (!spamPackets.contains(event.packet.getClass().getCanonicalName())){
                LogUtils.getLogger().info(event.packet.toString());
                assert mc.player != null;
                if (chatLog.get())
                    ChatUtils.sendMsg(Formatting.DARK_RED,"Sent packet: " + event.packet.toString());
        }
    }

}
