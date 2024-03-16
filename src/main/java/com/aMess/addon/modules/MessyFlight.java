package com.aMess.addon.modules;

import com.aMess.addon.MessyCoding;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class MessyFlight extends Module {
    public SettingGroup sgFlightSettings = settings.getDefaultGroup();

    private final Setting<Integer> sprintBoost = sgFlightSettings.add(new IntSetting.Builder()
        .name("Sprint Boost")
        .description("How much does the sprint key boost your speed")
        .defaultValue(2)
        .min(0)
        .sliderMax(5)
        .build()
    );

    private final Setting<Double> flightSpeed = sgFlightSettings.add(new DoubleSetting.Builder()
        .name("Flight speed")
        .description("The speed at which you move in the air")
        .defaultValue(0.5)
        .min(0)
        .sliderMax(4.5)
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

    public MessyFlight() {
        super(MessyCoding.CATEGORY, "Messy Flight", "Lets you fly ?");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        assert mc.player != null;

        if ((!mc.player.isOnGround()) && !mc.options.jumpKey.isPressed() && !mc.options.sneakKey.isPressed()) {
            if (mc.player.age % tickInterval.get() == 0) {
                mc.player.setPosition(mc.player.getX(), mc.player.getY() - tickFall.get(), mc.player.getZ());
            }
            else {
                mc.player.setVelocity(mc.player.getVelocity().x, 0.007, mc.player.getVelocity().z);
            }

            if (mc.options.forwardKey.isPressed()) {
                if (mc.options.sprintKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().multiply(flightSpeed.get() * sprintBoost.get()));
                }
                else  {
                    mc.player.setVelocity(mc.player.getRotationVector().multiply(flightSpeed.get()));
                }
            }
            else if (mc.options.backKey.isPressed()) {
                if (mc.options.sprintKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().multiply(- flightSpeed.get() * sprintBoost.get()));
                }
                else {
                    mc.player.setVelocity(mc.player.getRotationVector().multiply(-flightSpeed.get()));
                }
            }
            else if (mc.options.rightKey.isPressed()) {
                if (mc.options.sprintKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().rotateY((float) -Math.PI / 2).multiply(flightSpeed.get() * sprintBoost.get()));
                } else {
                    mc.player.setVelocity(mc.player.getRotationVector().rotateY((float) -Math.PI / 2).multiply(flightSpeed.get()));
                }
            }
            else if (mc.options.leftKey.isPressed()) {
                if (mc.options.sprintKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().rotateY((float) Math.PI / 2).multiply(flightSpeed.get() * sprintBoost.get()));
                } else {
                    mc.player.setVelocity(mc.player.getRotationVector().rotateY((float) Math.PI / 2).multiply(flightSpeed.get()));
                }
            }
            else {
                mc.player.setVelocity(0, mc.player.getVelocity().y,0);
            }
        }

        else if (mc.options.jumpKey.isPressed()) {
            if (mc.player.age % tickInterval.get() == 0) {
                mc.player.setPosition(mc.player.getX(), mc.player.getY() - tickFall.get(), mc.player.getZ());
            } else {
                if (mc.options.sprintKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getVelocity().add(0, ySpeed.get() * sprintBoost.get(), 0));
                }
                else {
                    mc.player.setVelocity(mc.player.getVelocity().add(0, ySpeed.get(), 0));
                }
            }
        }

        else if (mc.options.sneakKey.isPressed()) {
            if (mc.options.sprintKey.isPressed()) {
                mc.player.setVelocity(mc.player.getVelocity().add(0, -ySpeed.get() * sprintBoost.get(), 0));
            }
            else {
                mc.player.setVelocity(mc.player.getVelocity().add(0, -ySpeed.get(), 0));
            }
        }
    }
    }
