package com.aMess.addon.modules;

import com.aMess.addon.MessyCoding;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

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
    private final Setting<Boolean> autoTeleport = sgTeleportSettings.add(new BoolSetting.Builder()
        .name("Auto Teleport")
        .description("Automatically teleports you up or down using air blocks")
        .defaultValue(false)
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


    BlockPos playerPos;
    int y;
    BlockPos blockBelowPos;
    BlockPos blockAbovePos;
    @Override
    public void onActivate() {
        //To prevent java.lang.NullPointerException
        assert mc.player != null;
        playerPos = mc.player.getBlockPos();
        y = playerPos.getY();
        blockBelowPos = findAirBelowPlayer(playerPos);
        blockAbovePos = findAirAbovePlayer(playerPos);
    }
    @EventHandler
    public void onTick(TickEvent.Pre event) {
        assert mc.player != null;
        switch (mode.get()) {
            case Vertical -> {
                if (mc.options.sneakKey.isPressed()) {
                    int teleportDistance = verticalTeleportDistance.get();
                    if (autoTeleport.get()) {
                        if (blockBelowPos != null) {
                            y = blockBelowPos.down().getY();
                            teleportDistance = playerPos.getY() - y;
                            info("Air block is %s below you", teleportDistance);
                        }
                        else {
                            info("Couldn't find a suitable teleportation spot");
                            teleportDistance = 0;
                        }
                    }
                    for (int i = 0; i < 8; i++) {
                        PlayerMoveC2SPacket currentPosition;
                        currentPosition = new PlayerMoveC2SPacket.Full(mc.player.getX(),
                            mc.player.getY(),
                            mc.player.getZ(),
                            mc.player.getYaw(0),
                            mc.player.getPitch(0),
                            mc.player.isOnGround());
                        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(currentPosition);
                    }
                    PlayerMoveC2SPacket newPosition;
                    newPosition = new PlayerMoveC2SPacket.Full( mc.player.getX(),
                        mc.player.getY() - teleportDistance,
                        mc.player.getZ(),
                        mc.player.getYaw(0),
                        mc.player.getPitch(0),
                        mc.player.isOnGround());
                    mc.getNetworkHandler().sendPacket(newPosition);
                    mc.player.setPosition(mc.player.getX(), mc.player.getY() - teleportDistance, mc.player.getZ());
                    toggle();
                }
                else {
                    int teleportDistance = verticalTeleportDistance.get();
                    if (autoTeleport.get()) {
                        if (blockAbovePos != null) {
                            y = blockAbovePos.up().getY();
                            teleportDistance = playerPos.getY() - y;
                            teleportDistance = -teleportDistance;
                            info("Air block is %s above you", teleportDistance);
                        }
                        else {
                            info("Couldn't find a suitable teleportation spot");
                            teleportDistance = 0;
                        }
                    }
                    for (int i = 0; i < 8; i++) {
                        PlayerMoveC2SPacket currentPosition;
                        currentPosition = new PlayerMoveC2SPacket.Full(mc.player.getX(),
                            mc.player.getY(),
                            mc.player.getZ(),
                            mc.player.getYaw(0),
                            mc.player.getPitch(0),
                            mc.player.isOnGround());
                        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(currentPosition);
                    }
                    PlayerMoveC2SPacket newPosition;
                    newPosition = new PlayerMoveC2SPacket.Full( mc.player.getX(),
                        mc.player.getY() + teleportDistance,
                        mc.player.getZ(),
                        mc.player.getYaw(0),
                        mc.player.getPitch(0),
                        mc.player.isOnGround());
                    mc.getNetworkHandler().sendPacket(newPosition);
                    mc.player.setPosition(mc.player.getX(), mc.player.getY() + teleportDistance, mc.player.getZ());
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
                    Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(newPosition);
                    mc.player.setPosition(mc.player.getX() + 8, mc.player.getY(), mc.player.getZ());
                }
                mc.player.setPosition(mc.player.getX() + rest, mc.player.getY(), mc.player.getZ());
                toggle();
            }
            case Location -> {}
        }
    }

    // Structure blatantly copied from Gurkenwerfer_ Autoclip. Made small improvements for my usage
    public BlockPos findAirBelowPlayer(BlockPos playerPos) {
        assert mc.world != null;
        BlockPos currentPos = playerPos.down();
        BlockPos lastAirBlock2 = null;

        while (currentPos.getY() > -60) {
            if (mc.world.getBlockState(currentPos).isAir() && mc.world.getBlockState(currentPos.down()).isAir() && !mc.world.getBlockState(currentPos.down().down()).isAir()) {
                // Shift the last air block references
                lastAirBlock2 = currentPos;
                currentPos = currentPos.down(320);
            }
            currentPos = currentPos.down();
        }

        return lastAirBlock2;
    }

    public BlockPos findAirAbovePlayer(BlockPos playerPos) {
        assert mc.world != null;
        BlockPos currentPos = playerPos.up().up();
        BlockPos lastAirBlock2 = null;

        while (currentPos.getY() < 320) {
            if (mc.world.getBlockState(currentPos).isAir() && mc.world.getBlockState(currentPos.down()).isAir() && !mc.world.getBlockState(currentPos.down().down()).isAir()) {
                // Shift the last air block references\
                lastAirBlock2 = currentPos;
                currentPos = currentPos.up(320);
            }
            currentPos = currentPos.up();
        }

        return lastAirBlock2;
    }
    public enum Mode {
        Horizontal,
        Vertical,
        Location
    }
}

