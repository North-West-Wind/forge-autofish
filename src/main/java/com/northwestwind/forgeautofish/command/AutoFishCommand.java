package com.northwestwind.forgeautofish.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.northwestwind.forgeautofish.config.Config;
import com.northwestwind.forgeautofish.handler.AutoFishHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class AutoFishCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("setrecastdelay").executes(ctx -> setRecastDelay(ctx.getSource(), 1000L)).then(Commands.argument("delay", LongArgumentType.longArg(100L, 30000L)).executes(source -> setRecastDelay(source.getSource(), LongArgumentType.getLong(source, "delay")))));
    }

    private static int setRecastDelay(CommandSource source, Long delay) {
        AutoFishHandler.delay = delay;
        Config.setDelay(delay);
        source.sendFeedback(new TranslationTextComponent("commands.recastdelay", delay), true);
        return 0;
    }
}
