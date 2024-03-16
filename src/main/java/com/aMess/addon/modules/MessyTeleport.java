package com.aMess.addon.modules;

import com.aMess.addon.MessyCoding;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class MessyTeleport extends Module {
    public SettingGroup sgTeleportSettings = settings.getDefaultGroup();
    private final Setting<Mode> mode = sgTeleportSettings.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The mode for Flight.")
        .defaultValue(Mode.Horizontal)
        .build()
    );
    private final Setting<Integer> verticalTeleportDistance = sgTeleportSettings.add(new IntSetting.Builder()
        .name("Vertical Teleport Distance")
        .description("How many blocks to teleport upward or downward")
        .defaultValue(2)
        .min(0)
        .sliderMax(200)
        .build()
    );

    private final Setting<Integer> horizontalTeleportDistance = sgTeleportSettings.add(new IntSetting.Builder()
        .name("Horizontal Teleport Distance")
        .description("How many blocks to teleport in a direction")
        .defaultValue(8)
        .min(0)
        .sliderMax(10000)
        .build()
    );
    public MessyTeleport() {
        super(MessyCoding.CATEGORY, "Messy Teleport", "Teleport testing");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        assert mc.player != null;
        switch (mode.get()) {
            case Vertical -> {
                if (mc.options.sneakKey.isPressed()) {
                    for (int i = 0; i < 10; i++) {
                        PlayerMoveC2SPacket currentPosition;
                        currentPosition = new PlayerMoveC2SPacket.Full( mc.player.getX(),
                            mc.player.getY(),
                            mc.player.getZ(),
                            mc.player.getYaw(0),
                            mc.player.getPitch(0),
                            mc.player.isOnGround());
                        mc.getNetworkHandler().sendPacket(currentPosition);
                    }
                    PlayerMoveC2SPacket newPosition;
                    newPosition = new PlayerMoveC2SPacket.Full( mc.player.getX(),
                        mc.player.getY() - verticalTeleportDistance.get(),
                        mc.player.getZ(),
                        mc.player.getYaw(0),
                        mc.player.getPitch(0),
                        mc.player.isOnGround());
                    mc.getNetworkHandler().sendPacket(newPosition);
                    mc.player.setPosition(mc.player.getX(), mc.player.getY() - verticalTeleportDistance.get(), mc.player.getZ());
                    toggle();
                }
                else {
                    for (int i = 0; i < 10; i++) {
                        PlayerMoveC2SPacket currentPosition;
                        currentPosition = new PlayerMoveC2SPacket.Full( mc.player.getX(),
                            mc.player.getY(),
                            mc.player.getZ(),
                            mc.player.getYaw(0),
                            mc.player.getPitch(0),
                            mc.player.isOnGround());
                        mc.getNetworkHandler().sendPacket(currentPosition);
                    }
                    PlayerMoveC2SPacket newPosition;
                    newPosition = new PlayerMoveC2SPacket.Full( mc.player.getX(),
                        mc.player.getY() + verticalTeleportDistance.get(),
                        mc.player.getZ(),
                        mc.player.getYaw(0),
                        mc.player.getPitch(0),
                        mc.player.isOnGround());
                    mc.getNetworkHandler().sendPacket(newPosition);
                    mc.player.setPosition(mc.player.getX(), mc.player.getY() - verticalTeleportDistance.get(), mc.player.getZ());
                    toggle();
                }
            }
            case Horizontal -> {
                int numberOfSetPositions = horizontalTeleportDistance.get() / 8;
                int rest = horizontalTeleportDistance.get() - (numberOfSetPositions * 8);
                for (int i = 0 ; i < numberOfSetPositions ; i++) {
                    PlayerMoveC2SPacket newPosition;
                    newPosition = new PlayerMoveC2SPacket.Full( mc.player.getX() + 8,
                        mc.player.getY(),
                        mc.player.getZ(),
                        mc.player.getYaw(0),
                        mc.player.getPitch(0),
                        mc.player.isOnGround());
                    mc.getNetworkHandler().sendPacket(newPosition);
                    mc.player.setPosition(mc.player.getX() + 8, mc.player.getY(), mc.player.getZ());
                }
                mc.player.setPosition(mc.player.getX() + rest, mc.player.getY(), mc.player.getZ());
                toggle();
            }
            case Location -> {}
        }
    }

    public enum Mode {
        Horizontal,
        Vertical,
        Location
    }
}

