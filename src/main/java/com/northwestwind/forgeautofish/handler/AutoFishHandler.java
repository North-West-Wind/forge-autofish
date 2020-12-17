package com.northwestwind.forgeautofish.handler;

import com.northwestwind.forgeautofish.AutoFish;
import com.northwestwind.forgeautofish.command.AutoFishCommand;
import com.northwestwind.forgeautofish.config.Config;
import com.northwestwind.forgeautofish.keybind.KeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AutoFish.MODID, value = Dist.CLIENT)
public class AutoFishHandler {

    private boolean autofishenabled = Config.autofish.get(), rodprotectenabled = Config.rodprotect.get(), autoreplaceenabled = Config.autoreplace.get(), fished = false;
    public static long delay = Config.delay.get();
    public static String prefix = "?";

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (KeyBinds.autofish.isPressed()) {
            autofishenabled = !autofishenabled;
            Config.setAutofish(autofishenabled);
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("toggle.forgeautofish", (autofishenabled ? "\u00A7aEnabled" : "\u00A7cDisabled")), true);
            }
        } else if (KeyBinds.rodprotect.isPressed()) {
            rodprotectenabled = !rodprotectenabled;
            Config.setRodprotect(rodprotectenabled);
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("toggle.rodprotect", (rodprotectenabled ? "\u00A7aEnabled" : "\u00A7cDisabled")), true);
            }
        } else if (KeyBinds.autoreplace.isPressed()) {
            autoreplaceenabled = !autoreplaceenabled;
            Config.setAutoreplace(autoreplaceenabled);
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("warning.autoreplace", (autoreplaceenabled ? "\u00A7aEnabled" : "\u00A7cDisabled")), true);
                //Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("toggle.autoreplace", (autoreplaceenabled ? "\u00A7aEnabled" : "\u00A7cDisabled")), true);
            }
        } else if (KeyBinds.command.isPressed()) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("showprefix.forgeautofish", prefix), true);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (!autofishenabled || e.player == null || Minecraft.getInstance().player == null || e.player.isSpectator() || (Minecraft.getInstance().player != null && !e.player.getUniqueID().equals(Minecraft.getInstance().player.getUniqueID())) || e.player.fishingBobber == null || fished)
            return;
        Hand handWithFishingRod;
        if (e.player.getHeldItemMainhand().getItem() instanceof FishingRodItem) handWithFishingRod = Hand.MAIN_HAND;
        else if (e.player.getHeldItemOffhand().getItem() instanceof FishingRodItem) handWithFishingRod = Hand.OFF_HAND;
        else return;
        Vec3d vector = e.player.fishingBobber.getMotion();
        double x = vector.getX();
        double z = vector.getZ();
        double y = vector.getY();
        if (y < -0.05 && e.player.fishingBobber.isInWater() && x == 0 && z == 0) {
            ClientPlayNetHandler nethandler = Minecraft.getInstance().getConnection();
            click(e.player.world, e.player, handWithFishingRod, nethandler);
            if (!fished) {
                fished = true;
                startTimer();
            }
            ItemStack fishingRod = e.player.getHeldItem(handWithFishingRod);
            if (fishingRod.isEmpty()) return;
            if (fishingRod.getMaxDamage() - fishingRod.getDamage() < 3 && !e.player.isCreative() && rodprotectenabled) {
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("toggle.forgeautofish", "\u00A7cDisabled"), true);
                autofishenabled = false;
                return;
            }
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    click(e.player.world, e.player, handWithFishingRod, nethandler);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onClientChat(final ClientChatEvent event) {
        PlayerEntity player = Minecraft.getInstance().player;
        String message = event.getMessage();
        if (!message.startsWith(prefix)) return;
        String[] args = message.split(" +");
        if (message.startsWith(prefix + "setrecastdelay")) {
            event.setCanceled(true);
            if (args.length < 2) AutoFishCommand.setRecastDelay(player, 1000L);
            else try {
                long delay = Long.parseLong(args[1]);
                AutoFishCommand.setRecastDelay(player, delay);
            } catch (NumberFormatException e) {
                if (player != null)
                    player.sendStatusMessage(new TranslationTextComponent("error.parselong", args[1]), false);
            }
        }
    }

    private void startTimer() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                fished = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void click(World world, PlayerEntity player, Hand hand, ClientPlayNetHandler nethandler) {
        if (nethandler != null) nethandler.sendPacket(new CPlayerTryUseItemPacket(hand));
        else player.getHeldItem(hand).useItemRightClick(world, player, hand);
    }
}