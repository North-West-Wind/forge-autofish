package com.northwestwind.forgeautofish.handler;

import com.northwestwind.forgeautofish.AutoFish;
import com.northwestwind.forgeautofish.keybind.KeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
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

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if(KeyBinds.autofish.isPressed()) {
            autofishenabled = !autofishenabled;
            Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("toggle.forgeautofish", (autofishenabled ? "\u00A7aEnabled" : "\u00A7cDisabled")), true);
        }
    }

    private boolean fished = false;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPlayerTick(TickEvent.PlayerTickEvent e) throws InterruptedException {
        if(!autofishenabled) return;
        if(!(e.player.getHeldItemMainhand().getItem() instanceof FishingRodItem)) {
            return;
        }
        if(e.player.fishingBobber == null) {
            return;
        }
        if(fished) return;
        Vector3d vector = e.player.fishingBobber.getMotion();
        double x = vector.getX();
        double z = vector.getZ();
        double y = vector.getY();
        if(y < -0.1 && e.player.fishingBobber.isInWater() && x == 0 && z == 0) {
            ClientPlayNetHandler nethandler = Minecraft.getInstance().getConnection();
            if(nethandler != null) {
                nethandler.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            } else {
                rightClick(e.player.world, e.player, Hand.MAIN_HAND);
            }
            if(!fished) {
                fished = true;
                startTimer();
            }
            if(e.player.getHeldItemMainhand().isEmpty()) return;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        if(nethandler != null) {
                            nethandler.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        } else {
                            rightClick(e.player.world, e.player, Hand.MAIN_HAND);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void startTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    fished = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void rightClick(World world, PlayerEntity player, Hand hand) {
        player.getHeldItemMainhand().useItemRightClick(world, player, hand);
    }
}
