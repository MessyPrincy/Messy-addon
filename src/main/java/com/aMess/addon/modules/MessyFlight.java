package com.aMess.addon.modules;

import com.aMess.addon.MessyCoding;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class MessyFlight extends Module {
    public SettingGroup sgFlightSettings = this.settings.getDefaultGroup();

    private final Setting<Integer> sprintBoost = sgFlightSettings.add(new IntSetting.Builder()
        .name("Sprint Boost")
        .description("How much does the sprint key boost your speed")
        .defaultValue(2)
        .min(0)
        .sliderMax(20)
        .build()
    );

    private final Setting<Double> flightSpeed = sgFlightSettings.add(new DoubleSetting.Builder()
        .name("Flight speed")
        .description("The speed at which you move in the air")
        .defaultValue(0.5)
        .min(0)
        .sliderMax(10)
        .build()
    );
    private final Setting<Double> ySpeed = sgFlightSettings.add(new DoubleSetting.Builder()
        .name("Y speed")
        .description("The speed at which you move up")
        .defaultValue(0.1)
        .min(0)
        .sliderMax(0.5)
        .build()
    );
    private final Setting<Integer> tickInterval = sgFlightSettings.add(new IntSetting.Builder()
        .name("Anti-kick Tick Interval")
        .description("The tick interval at which you are reset to prevent getting kicked")
        .defaultValue(20)
        .min(0)
        .sliderMax(60)
        .build()
    );
    private final Setting<Double> tickFall = sgFlightSettings.add(new DoubleSetting.Builder()
        .name("Tick fall")
        .description("The distance at which to fall to prevent getting kicked")
        .defaultValue(0.05)
        .min(0)
        .sliderMax(0.5)
        .build()
    );
    private final Setting<Boolean> vehicleFlight = sgFlightSettings.add(new BoolSetting.Builder()
        .name("Vehicle Flight")
        .description("Fly with a vehicle")
        .defaultValue(false)
        .build()
    );

    public MessyFlight() {
        super(MessyCoding.CATEGORY, "Messy Flight", "Lets you fly ?");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        assert mc.player != null;

        float yaw = mc.player.getYaw();
        double radiansYaw = Math.toRadians(yaw);
        double cameraX = -Math.sin(radiansYaw);
        double cameraZ = Math.cos(radiansYaw);
        Vec3d direction = new Vec3d(cameraX, 0, cameraZ).normalize();

        Vec3d movementDirection = new Vec3d(0, 0, 0);

        Entity vehicle = mc.player.getVehicle();
        assert vehicle != null;

        Entity player = mc.player;
        KeyBinding sneakKey = mc.options.sneakKey;
        KeyBinding jumpKey = mc.options.jumpKey;
        if (vehicleFlight.get() && mc.player.hasVehicle()) {
            player = vehicle;
        }
        if ((!mc.player.isOnGround()) && !mc.options.jumpKey.isPressed() && !mc.options.sneakKey.isPressed()) {
            if (mc.player.age % tickInterval.get() == 0) {
                player.setPosition(player.getX(), player.getY() - tickFall.get(), player.getZ());
            } else {
                player.setVelocity(player.getVelocity().x, 0.007, player.getVelocity().z);
            }

            if (mc.options.forwardKey.isPressed()) {
                if (mc.options.sprintKey.isPressed()) {
                    player.setVelocity(movementDirection.add(direction.multiply(flightSpeed.get() * sprintBoost.get())));
                } else {
                    player.setVelocity(movementDirection.add(direction.multiply(flightSpeed.get())));
                }
            } else if (mc.options.backKey.isPressed()) {
                if (mc.options.sprintKey.isPressed()) {
                    player.setVelocity(movementDirection.add(direction.multiply(-flightSpeed.get() * sprintBoost.get())));
                } else {
                    player.setVelocity(movementDirection.add(direction.multiply(-flightSpeed.get())));
                }
            } else if (mc.options.rightKey.isPressed()) {
                if (mc.options.sprintKey.isPressed()) {
                    player.setVelocity(movementDirection.add(direction.rotateY((float) -Math.PI / 2).normalize().multiply(flightSpeed.get() * sprintBoost.get())));
                } else {
                    player.setVelocity(movementDirection.add(direction.rotateY((float) -Math.PI / 2).normalize().multiply(flightSpeed.get())));
                }
            } else if (mc.options.leftKey.isPressed()) {
                if (mc.options.sprintKey.isPressed()) {
                    player.setVelocity(movementDirection.add(direction.rotateY((float) Math.PI / 2).normalize().multiply(flightSpeed.get() * sprintBoost.get())));
                } else {
                    player.setVelocity(movementDirection.add(direction.rotateY((float) Math.PI / 2).normalize().multiply(flightSpeed.get())));
                }
            } else {
                player.setVelocity(0, player.getVelocity().y, 0);
            }
        } else if (jumpKey.isPressed()) {
            if (mc.player.age % tickInterval.get() == 0) {
                player.setPosition(player.getX(), player.getY() - tickFall.get(), player.getZ());
            } else {
                if (mc.options.sprintKey.isPressed()) {
                    player.setVelocity(player.getVelocity().add(0, ySpeed.get() * sprintBoost.get(), 0));
                } else {
                    player.setVelocity(player.getVelocity().add(0, ySpeed.get(), 0));
                }
            }
        } else if (sneakKey.isPressed()) {
            if (mc.options.sprintKey.isPressed()) {
                player.setVelocity(player.getVelocity().add(0, -ySpeed.get() * sprintBoost.get(), 0));
            } else {
                player.setVelocity(player.getVelocity().add(0, -ySpeed.get(), 0));
            }
        }
    }
}
