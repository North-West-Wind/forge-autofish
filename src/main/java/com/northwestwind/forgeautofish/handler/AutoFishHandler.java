package com.northwestwind.forgeautofish.handler;

import com.northwestwind.forgeautofish.AutoFish;
import com.northwestwind.forgeautofish.keybind.KeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AutoFish.MODID, value = Dist.CLIENT)
public class AutoFishHandler {

    private boolean autofishenabled;
    private boolean rodprotectenabled;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if(KeyBinds.autofish.isPressed()) {
            autofishenabled = !autofishenabled;
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("toggle.forgeautofish", (autofishenabled ? "\u00A7aEnabled" : "\u00A7cDisabled")), true);
            }
        }
        if(KeyBinds.rodprotect.isPressed()) {
            rodprotectenabled = !rodprotectenabled;
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("toggle.rodprotect", (rodprotectenabled ? "\u00A7aEnabled" : "\u00A7cDisabled")), true);
            }
        }
    }

    private boolean fished = false;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if(!autofishenabled) return;
        if(e.player.isSpectator()) return;
        if (Minecraft.getInstance().player != null && !e.player.getUniqueID().equals(Minecraft.getInstance().player.getUniqueID()))
            return;

        Hand handWithFishingRod;
        if(e.player.getHeldItemMainhand().getItem() instanceof FishingRodItem) {
            handWithFishingRod = Hand.MAIN_HAND;
        }
        else if(e.player.getHeldItemOffhand().getItem() instanceof FishingRodItem){
            handWithFishingRod = Hand.OFF_HAND;
        }
        else return;
        if(e.player.fishingBobber == null) {
            return;
        }
        if(fished) return;
        Vector3d vector = e.player.fishingBobber.getMotion();
        double x = vector.getX();
        double z = vector.getZ();
        double y = vector.getY();
        if(y < -0.05 && e.player.fishingBobber.isInWater() && x == 0 && z == 0) {
            ClientPlayNetHandler nethandler = Minecraft.getInstance().getConnection();
            if(nethandler != null) {
                nethandler.sendPacket(new CPlayerTryUseItemPacket(handWithFishingRod));
            } else {
                click(e.player.world, e.player, handWithFishingRod);
            }
            if(!fished) {
                fished = true;
                startTimer();
            }
            if(e.player.getHeldItem(handWithFishingRod).isEmpty()) return;
            ItemStack fishingRod = e.player.getHeldItem(handWithFishingRod);
            if(fishingRod.isEmpty()) return;
            if(fishingRod.getMaxDamage() - fishingRod.getDamage() < 3 && !e.player.isCreative()) {
                Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("toggle.forgeautofish", "\u00A7cDisabled"), true);
                autofishenabled = false;
                return;
            }
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    if(nethandler != null) {
                        nethandler.sendPacket(new CPlayerTryUseItemPacket(handWithFishingRod));
                    } else {
                        click(e.player.world, e.player, handWithFishingRod);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
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

    private void click(World world, PlayerEntity player, Hand hand) {
        player.getHeldItem(hand).useItemRightClick(world, player, hand);
    }
}