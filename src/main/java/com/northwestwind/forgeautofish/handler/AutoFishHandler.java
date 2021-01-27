package com.northwestwind.forgeautofish.handler;

import com.google.common.collect.Lists;
import com.northwestwind.forgeautofish.AutoFish;
import com.northwestwind.forgeautofish.config.Config;
import com.northwestwind.forgeautofish.config.gui.SettingsScreen;
import com.northwestwind.forgeautofish.keybind.KeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = AutoFish.MODID, value = Dist.CLIENT)
public class AutoFishHandler {

    public static boolean autofishenabled = Config.AUTO_FISH.get(), rodprotectenabled = Config.ROD_PROTECT.get(), autoreplaceenabled = Config.AUTO_REPLACE.get(), itemfilter = Config.ALL_FILTERS.get(), fished = false;
    public static long recastDelay = Config.RECAST_DELAY.get(), reelInDelay = Config.REEL_IN_DELAY.get();
    private final List<Item> shouldDrop = Lists.newArrayList();
    private boolean processingDrop, waitingReelIn, droppingItem, countingTick, casted;
    private int tick;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onItemToss(final ItemTossEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!event.getPlayer().getUniqueID().equals(player.getUniqueID())) return;
        droppingItem = false;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onItemFished(final ItemFishedEvent event) {
        if (!itemfilter) return;
        List<ItemStack> stacks = event.getDrops();
        List<Item> shouldDrop = Lists.newArrayList();
        for (ItemStack stack : stacks)
            if (Config.FILTER.get().contains(stack.getItem().getRegistryName().toString()))
                shouldDrop.add(stack.getItem());
        this.shouldDrop.addAll(shouldDrop);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (KeyBinds.autofish.isPressed()) {
            Config.setAutoFish(!autofishenabled);
            if (Minecraft.getInstance().player != null)
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("toggle.forgeautofish", new TranslationTextComponent("toggle.enable."+autofishenabled).getString()), true);
        } else if (KeyBinds.rodprotect.isPressed()) {
            Config.setRodProtect(!rodprotectenabled);
            if (Minecraft.getInstance().player != null)
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("toggle.rodprotect", new TranslationTextComponent("toggle.enable."+rodprotectenabled).getString()), true);
        } else if (KeyBinds.autoreplace.isPressed()) {
            Config.setAutoReplace(!autoreplaceenabled);
            if (Minecraft.getInstance().player != null)
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("warning.autoreplace", new TranslationTextComponent("toggle.enable."+autoreplaceenabled).getString()), true);
        } else if (KeyBinds.itemfilter.isPressed()) {
            Config.enableFilter(!itemfilter);
            if (Minecraft.getInstance().player != null)
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("toggle.itemfilter", new TranslationTextComponent("toggle.enable."+itemfilter).getString()), true);
        } else if (KeyBinds.settings.isPressed() && e.getAction() == GLFW.GLFW_PRESS)
            Minecraft.getInstance().displayGuiScreen(new SettingsScreen());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (countingTick) tick++;
        if (Minecraft.getInstance().player == null) return;
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (shouldDrop.size() > 0 && !processingDrop) dropItem(player);
        if (!autofishenabled || !e.player.getUniqueID().equals(player.getUniqueID()) || player.fishingBobber == null || fished || droppingItem)
            return;
        Hand handWithFishingRod;
        if (player.getHeldItemMainhand().getItem() instanceof FishingRodItem) handWithFishingRod = Hand.MAIN_HAND;
        else if (player.getHeldItemOffhand().getItem() instanceof FishingRodItem) handWithFishingRod = Hand.OFF_HAND;
        else return;
        Vector3d vector = player.fishingBobber.getMotion();
        double x = vector.getX();
        double z = vector.getZ();
        double y = vector.getY();
        if (y < -0.075 && player.fishingBobber.isInWater() && x == 0 && z == 0 && !waitingReelIn) {
            if (reelInDelay > 0) {
                waitingReelIn = true;
                new Thread(() -> {
                    try {
                        Thread.sleep(reelInDelay);
                    } catch (InterruptedException ignored) {
                    } finally {
                        reelIn(handWithFishingRod, player);
                        waitingReelIn = false;
                    }
                }).start();
            } else reelIn(handWithFishingRod, player);
        }
    }

    private void dropItem(ClientPlayerEntity player) {
        processingDrop = true;
        List<Item> shouldRemove = Lists.newArrayList();
        int backup = player.inventory.currentItem;
        for (final Item item : shouldDrop) {
            for (final ItemStack stack : player.inventory.mainInventory) {
                if (!stack.getItem().equals(item)) continue;
                if (player.inventory.getSlotFor(stack) == player.inventory.currentItem) continue;
                int slot = player.inventory.getSlotFor(stack);
                if (slot >= 0 && slot < 9) {
                    droppingItem = true;
                    new Thread(() -> {
                        try {
                            player.inventory.currentItem = slot;
                            int oldTick = tick;
                            countingTick = true;
                            while (oldTick >= tick && !Thread.currentThread().isInterrupted()) {
                                Thread.sleep(50);
                            }
                            countingTick = false;
                            tick = 0;
                            player.drop(false);
                            while (!Thread.currentThread().isInterrupted()) {
                                Thread.sleep(50);
                                if (!droppingItem) {
                                    player.inventory.currentItem = backup;
                                    Thread.sleep(recastDelay);
                                    if (casted) break;
                                    click(player.world, player, (player.inventory.getStackInSlot(backup).getItem() instanceof FishingRodItem) ? Hand.MAIN_HAND : Hand.OFF_HAND, Minecraft.getInstance().playerController);
                                    casted = true;
                                    break;
                                }
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                } else {
                    PlayerController playerController = Minecraft.getInstance().playerController;
                    if (playerController == null) continue;
                    playerController.windowClick(player.container.windowId, player.inventory.getSlotFor(stack), 0, ClickType.THROW, player);
                }
                shouldRemove.add(item);
            }
        }
        shouldDrop.removeAll(shouldRemove);
        processingDrop = false;
    }

    private void reelIn(Hand hand, ClientPlayerEntity player) {
        click(player.world, player, hand, Minecraft.getInstance().playerController);
        casted = false;
        if (!fished) {
            fished = true;
            startTimer();
        }
        ItemStack fishingRod = player.getHeldItem(hand);
        if (fishingRod.isEmpty()) return;
        if (fishingRod.getMaxDamage() - fishingRod.getDamage() < 3 && !player.isCreative() && rodprotectenabled) {
            player.sendStatusMessage(new TranslationTextComponent("toggle.forgeautofish", "\u00A7cDisabled"), true);
            autofishenabled = false;
            return;
        }
        new Thread(() -> {
            try {
                Thread.sleep(recastDelay);
                if (droppingItem || casted) return;
                click(player.world, player, hand, Minecraft.getInstance().playerController);
                casted = true;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
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

    private void click(World world, PlayerEntity player, Hand hand, @Nullable PlayerController controller) {
        if (controller == null) return;
        controller.processRightClick(player, world, hand);
    }

}