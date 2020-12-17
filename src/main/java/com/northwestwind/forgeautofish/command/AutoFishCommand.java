package com.northwestwind.forgeautofish.command;

import com.northwestwind.forgeautofish.config.Config;
import com.northwestwind.forgeautofish.handler.AutoFishHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class AutoFishCommand {
    public static void setRecastDelay(PlayerEntity player, Long delay) {
        AutoFishHandler.delay = delay;
        Config.setDelay(delay);
        if (player != null) player.sendStatusMessage(new TranslationTextComponent("commands.recastdelay", delay), false);
    }
}
