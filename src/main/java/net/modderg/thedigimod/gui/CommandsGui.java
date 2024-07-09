package net.modderg.thedigimod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.packet.*;
import net.modderg.thedigimod.sound.DigiSounds;

public class CommandsGui {

    public static void renderCommands(DigiviceScreen screen, Entity target) {

        renderCommandAttackButton(screen, target);
        renderCommandSitButton(screen, target);
        renderFollowButton(screen, target);
        renderCommandFlyButton(screen, target);
        renderCommandEatButton(screen, target);
        renderCommandTpButton(screen, target);
        renderCommandWanderButton(screen, target);
        renderCommandWorkButton(screen, target);
        renderCommandInventoryButton(screen, target);
    }


    private static final ResourceLocation ATTACKBUTTON =new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/button_attack.png");
    public static void renderCommandAttackButton(DigiviceScreen screen, Entity entity) {

        ImageButton attButton = new ImageButton(2, 2, 20, 20, 0, 0, 0, ATTACKBUTTON, 20, 20,
                (button) -> {
                    Minecraft.getInstance().gui.setOverlayMessage(Component.literal("attack!"), false);
                    Minecraft.getInstance().player.playNotifySound(DigiSounds.COMMAND_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
                    if(entity != null) {
                        PacketInit.sendToServer(new CToSAttackCommandPacket(entity.getId()));
                    }
        });

        screen.addRenderableWidget(attButton);
    }

    private static final ResourceLocation SITBUTTON =new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/button_sit.png");
    public static void renderCommandSitButton(DigiviceScreen screen, Entity entity) {

        ImageButton attButton = new ImageButton(24, 2, 20, 20, 0, 0, 0, SITBUTTON, 20, 20,
                (button) -> {
                    Minecraft.getInstance().gui.setOverlayMessage(Component.literal("sit!"), false);
                    PacketInit.sendToServer(new CToSSitCommandPacket(entity != null ? entity.getId() : -1));
                    Minecraft.getInstance().player.playNotifySound(DigiSounds.COMMAND_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
                });

        screen.addRenderableWidget(attButton);
    }

    private static final ResourceLocation FOLLOWBUTTON =new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/button_stand.png");
    public static void renderFollowButton(DigiviceScreen screen, Entity entity) {

        ImageButton attButton = new ImageButton(46, 2, 20, 20, 0, 0, 0, FOLLOWBUTTON, 20, 20,
                (button) -> {
                    Minecraft.getInstance().gui.setOverlayMessage(Component.literal("follow me!"), false);
                    PacketInit.sendToServer(new CToSStandCommandPacket(entity != null ? entity.getId() : -1));
                    Minecraft.getInstance().player.playNotifySound(DigiSounds.COMMAND_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
                });

        screen.addRenderableWidget(attButton);
    }

    private static final ResourceLocation FLYBUTTON =new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/button_fly.png");
    public static void renderCommandFlyButton(DigiviceScreen screen, Entity entity) {

        ImageButton attButton = new ImageButton(35, 24, 20, 20, 0, 0, 0, FLYBUTTON, 20, 20,
                (button) -> {
                    Minecraft.getInstance().gui.setOverlayMessage(Component.literal("fly!"), false);
                    PacketInit.sendToServer(new CToSFlyCommandPacket(entity != null ? entity.getId() : -1));
                    Minecraft.getInstance().player.playNotifySound(DigiSounds.COMMAND_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
                });

        screen.addRenderableWidget(attButton);
    }
//13, 24, 20, 20
    private static final ResourceLocation EATBUTTON =new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/button_eat.png");
    public static void renderCommandEatButton(DigiviceScreen screen, Entity entity) {

        ImageButton attButton = new ImageButton(13, 24, 20, 20, 0, 0, 0, EATBUTTON, 20, 20,
                (button) -> {
                    Minecraft.getInstance().gui.setOverlayMessage(Component.literal("take some food"), false);
                    PacketInit.sendToServer(new CToSEatCommandPacket(entity != null ? entity.getId() : -1));
                    Minecraft.getInstance().player.playNotifySound(DigiSounds.COMMAND_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
                });

        screen.addRenderableWidget(attButton);
    }

    private static final ResourceLocation TELEPORTBUTTON =new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/button_teleport.png");
    public static void renderCommandTpButton(DigiviceScreen screen, Entity entity) {

        ImageButton attButton = new ImageButton(57, 24, 20, 20, 0, 0, 0, TELEPORTBUTTON, 20, 20,
                (button) -> {
                    Minecraft.getInstance().gui.setOverlayMessage(Component.literal("come here!"), false);
                    PacketInit.sendToServer(new CToSTpCommandPacket(entity != null ? entity.getId() : -1));
                    Minecraft.getInstance().player.playNotifySound(DigiSounds.COMMAND_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
                });

        screen.addRenderableWidget(attButton);
    }

    private static final ResourceLocation WANDERBUTTON =new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/button_wander.png");
    public static void renderCommandWanderButton(DigiviceScreen screen, Entity entity) {

        ImageButton attButton = new ImageButton(68, 2, 20, 20, 0, 0, 0, WANDERBUTTON, 20, 20,
                (button) -> {
                    Minecraft.getInstance().gui.setOverlayMessage(Component.literal("you can wander"), false);
                    PacketInit.sendToServer(new CToSWanderCommandPacket(entity != null ? entity.getId() : -1));
                    Minecraft.getInstance().player.playNotifySound(DigiSounds.COMMAND_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
                });

        screen.addRenderableWidget(attButton);
    }

    private static final ResourceLocation WORKBUTTON =new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/button_work.png");
    public static void renderCommandWorkButton(DigiviceScreen screen, Entity entity) {

                ImageButton attButton = new ImageButton(79, 24, 20, 20, 0, 0, 0, WORKBUTTON, 20, 20,
                (button) -> {
                    Minecraft.getInstance().gui.setOverlayMessage(Component.literal("go work!"), false);
                    PacketInit.sendToServer(new CToSWorkCommandPacket(entity != null ? entity.getId() : -1));
                    Minecraft.getInstance().player.playNotifySound(DigiSounds.COMMAND_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
                });

        screen.addRenderableWidget(attButton);
    }

    private static final ResourceLocation CHESTBUTTON =new ResourceLocation(TheDigiMod.MOD_ID, "textures/gui/button_chest.png");
    public static void renderCommandInventoryButton(DigiviceScreen screen, Entity entity) {

                ImageButton attButton = new ImageButton(90, 2, 20, 20, 0, 0, 0, CHESTBUTTON, 20, 20,
                (button) -> {
                    PacketInit.sendToServer(new CToSOpenDigiInventoryPacket(entity != null ? entity.getId() : -1));
                    Minecraft.getInstance().player.playNotifySound(DigiSounds.COMMAND_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
                });

        screen.addRenderableWidget(attButton);
    }
}
