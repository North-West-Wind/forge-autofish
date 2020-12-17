package com.northwestwind.forgeautofish.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class Config {

    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT;

    public static ForgeConfigSpec.LongValue delay;
    public static ForgeConfigSpec.BooleanValue autofish;
    public static ForgeConfigSpec.BooleanValue rodprotect;
    public static ForgeConfigSpec.BooleanValue autoreplace;

    static {
        init();
        CLIENT = CLIENT_BUILDER.build();
    }

    public static void loadConfig(String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        CLIENT.setConfig(file);
    }

    public static void init() {
        delay = CLIENT_BUILDER.comment("Sets the delay before casting the fishing rod again (in milliseconds).").defineInRange("forgeautofish.recastdelay", 1000L, 100L, 30000L);
        autofish = CLIENT_BUILDER.comment("Sets the default status of the Auto Fish feature").define("forgeautofish.autofish", true);
        rodprotect = CLIENT_BUILDER.comment("Sets whether should the mod be turned off when the fishing rod is about to break.").define("forgeautofish.rodprotect", true);
        autoreplace = CLIENT_BUILDER.comment("Does nothing currently").define("forgeautofish.autoreplace", true);
    }

    public static void setDelay(Long delay) {
        Config.delay.set(delay);
        Config.delay.save();
    }

    public static void setAutofish(boolean autofish) {
        Config.autofish.set(autofish);
        Config.autofish.save();
    }

    public static void setRodprotect(boolean rodprotect) {
        Config.rodprotect.set(rodprotect);
        Config.rodprotect.save();
    }

    public static void setAutoreplace(boolean autoreplace) {
        Config.autoreplace.set(autoreplace);
        Config.autoreplace.save();
    }
}
