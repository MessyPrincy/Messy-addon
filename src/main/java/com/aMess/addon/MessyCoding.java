package com.aMess.addon;

import com.aMess.addon.modules.ModeratorTracker;
import com.aMess.addon.modules.PacketLogger;
import com.aMess.addon.modules.WitherSpawnDetector;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class MessyCoding extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Messy Coding");
    public static final HudGroup HUD_GROUP = new HudGroup("");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Addon Template");

        // Modules
        Modules.get().add(new WitherSpawnDetector());

        Modules.get().add(new PacketLogger());

        Modules.get().add(new ModeratorTracker());

    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.aMess.addon";
    }
}
