package com.aMess.addon;

import com.aMess.addon.modules.*;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessyCoding extends MeteorAddon {
    public static final Logger LOG = LoggerFactory.getLogger("Messy");
    public static final Category CATEGORY = new Category("Messy Coding");
    public static final HudGroup HUD_GROUP = new HudGroup("Messy Coding");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Messy Coding");

        // Modules
        Modules.get().add(new WitherSpawnDetector());

        Modules.get().add(new PacketLogger());

        Modules.get().add(new ModeratorTracker());

        Modules.get().add(new MessyFlight());

        Modules.get().add(new MessyTeleport());

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
