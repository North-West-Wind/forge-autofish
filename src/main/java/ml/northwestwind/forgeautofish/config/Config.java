package ml.northwestwind.forgeautofish.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import ml.northwestwind.forgeautofish.AutoFish;
import ml.northwestwind.forgeautofish.handler.AutoFishHandler;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;
import java.util.List;

public class Config {

    public static final long[] RECAST_DELAY_RANGE = { 20L, 1L, 600L };
    public static final long[] REEL_IN_DELAY_RANGE = { 0L, 0L, 600L };
    public static final long[] THROW_DELAY_RANGE = { 10L, 5L, 600L };

    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT;

    public static ForgeConfigSpec.LongValue RECAST_DELAY, REEL_IN_DELAY, THROW_DELAY;
    public static ForgeConfigSpec.BooleanValue AUTO_FISH, ROD_PROTECT, AUTO_REPLACE, ALL_FILTERS;
    public static ForgeConfigSpec.ConfigValue<List<String>> FILTER, PRIORITIZE;

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
        RECAST_DELAY = CLIENT_BUILDER.comment("Sets the delay before casting the fishing rod again (in ticks).", "Minimum is 1 tick to allow Auto Replace to take effect.").defineInRange("forgeautofish.recastdelay", RECAST_DELAY_RANGE[0], RECAST_DELAY_RANGE[1], RECAST_DELAY_RANGE[2]);
        REEL_IN_DELAY = CLIENT_BUILDER.comment("Sets the delay before reeling in the fishing rod after catching a fish (in ticks).").defineInRange("forgeautofish.reelindelay", REEL_IN_DELAY_RANGE[0], REEL_IN_DELAY_RANGE[1], REEL_IN_DELAY_RANGE[2]);
        THROW_DELAY = CLIENT_BUILDER.comment("Sets the delay between each item throw in filtering (in ticks).").defineInRange("forgeautofish.throwdelay", THROW_DELAY_RANGE[0], THROW_DELAY_RANGE[1], THROW_DELAY_RANGE[2]);
        AUTO_FISH = CLIENT_BUILDER.comment("Sets the default status of the Auto Fish feature").define("forgeautofish.autofish", true);
        ROD_PROTECT = CLIENT_BUILDER.comment("Sets whether should the mod be turned off when the fishing rod is about to break.").define("forgeautofish.rodprotect", true);
        AUTO_REPLACE = CLIENT_BUILDER.comment("Does nothing currently").define("forgeautofish.autoreplace", true);
        ALL_FILTERS = CLIENT_BUILDER.comment("Toggles the entire item filter").define("forgeautofish.filter.all", true);
        FILTER = CLIENT_BUILDER.comment("Sets item filter").define("forgeautofish.filter.items", Lists.newArrayList("minecraft:rotten_flesh"));
        PRIORITIZE = CLIENT_BUILDER.comment("Puts these items to top of filter.").define("forgeautofish.filter.prioritize", Lists.newArrayList("minecraft:cod", "minecraft:salmon", "minecraft:tropical_fish", "minecraft:pufferfish", "minecraft:bow", "minecraft:enchanted_book", "minecraft:fishing_rod", "minecraft:name_tag", "minecraft:nautilus_shell", "minecraft:saddle", "minecraft:lily_pad", "minecraft:bowl", "minecraft:leather", "minecraft:leather_boots", "minecraft:rotten_flesh", "minecraft:stick", "minecraft:string", "minecraft:water_bottle", "minecraft:bone", "minecraft:ink_sac", "minecraft:tripwire_hook", "minecraft:bamboo", "minecraft:cocoa_beans"));
    }

    public static void setRecastDelay(long recastDelay) {
        AutoFishHandler.recastDelay = recastDelay;
        Config.RECAST_DELAY.set(recastDelay);
        Config.RECAST_DELAY.save();
        AutoFish.LOGGER.info("Set Recast Delay: " + recastDelay);
    }

    public static void setAutoFish(boolean autoFish) {
        AutoFishHandler.autofish = autoFish;
        Config.AUTO_FISH.set(autoFish);
        Config.AUTO_FISH.save();
        AutoFish.LOGGER.info("Toggle AutoFish: " + autoFish);
    }

    public static void setRodProtect(boolean rodProtect) {
        AutoFishHandler.rodprotect = rodProtect;
        Config.ROD_PROTECT.set(rodProtect);
        Config.ROD_PROTECT.save();
        AutoFish.LOGGER.info("Toggle Rod Protect: " + rodProtect);
    }

    public static void setAutoReplace(boolean autoReplace) {
        AutoFishHandler.autoreplace = autoReplace;
        Config.AUTO_REPLACE.set(autoReplace);
        Config.AUTO_REPLACE.save();
        AutoFish.LOGGER.info("Toggle Auto Replace: " + autoReplace);
    }

    public static void enableFilter(boolean filter) {
        AutoFishHandler.itemfilter = filter;
        ALL_FILTERS.set(filter);
        ALL_FILTERS.save();
        AutoFish.LOGGER.info("Toggle Filter: " + filter);
    }

    public static void setFILTER(List<String> list) {
        Config.FILTER.set(list);
        Config.FILTER.save();
        AutoFish.LOGGER.info("Received new Filter");
    }

    public static void setReelInDelay(long reelInDelay) {
        AutoFishHandler.reelInDelay = reelInDelay;
        Config.REEL_IN_DELAY.set(reelInDelay);
        Config.REEL_IN_DELAY.save();
        AutoFish.LOGGER.info("Set Reel In Delay: " + reelInDelay);
    }

    public static void setThrowDelay(long throwDelay) {
        AutoFishHandler.throwDelay = throwDelay;
        Config.THROW_DELAY.set(throwDelay);
        Config.THROW_DELAY.save();
        AutoFish.LOGGER.info("Set Throw Delay: " + throwDelay);
    }
}
