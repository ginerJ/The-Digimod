package net.modderg.thedigimod.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;

public class DigiSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TheDigiMod.MOD_ID);

    public static final RegistryObject<SoundEvent> BABY_STEPS = SOUNDS.register("baby_steps",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "baby_steps")));

    public static final RegistryObject<SoundEvent> ROOKIE_STEPS = SOUNDS.register("rookie_steps",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "rookie_steps")));

    public static final RegistryObject<SoundEvent> CHAMPION_STEPS = SOUNDS.register("champion_steps",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "champion_steps")));

    public static final RegistryObject<SoundEvent> ULTIMATE_STEPS = SOUNDS.register("ultimate_steps",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "ultimate_steps")));

    //Moves
    public static final RegistryObject<SoundEvent> MOVE_HIT = SOUNDS.register("move_hit",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "move_hit")));

    public static final RegistryObject<SoundEvent> DEFAULT_SHOOT = SOUNDS.register("default_shoot",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "default_shoot")));

    //Gui
    public static final RegistryObject<SoundEvent> R_KEY_SOUND = SOUNDS.register("r_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "r_sound")));

    public static final RegistryObject<SoundEvent> COMMAND_SOUND = SOUNDS.register("button_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "button_sound")));

    public static final RegistryObject<SoundEvent> OPEN_GUI_SOUND = SOUNDS.register("open_gui",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "open_gui")));

    public static final RegistryObject<SoundEvent> BAG_OPEN_SOUND = SOUNDS.register("bag_open",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "bag_open")));

    public static final RegistryObject<SoundEvent> MEMORY_SAVE_SOUND = SOUNDS.register("memory_save",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "memory_save")));

    public static final RegistryObject<SoundEvent> PLACE_GOOD_SOUND = SOUNDS.register("place_good",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "place_good")));

    //LevelUpEvo

    public static final RegistryObject<SoundEvent> EVOLUTION_SOUND = SOUNDS.register("evo_sound",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "evo_sound")));

    public static final RegistryObject<SoundEvent> XP_GAIN_SOUND = SOUNDS.register("xp_up",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "xp_up")));

    public static final RegistryObject<SoundEvent> DIGITRON_SOUND = SOUNDS.register("digitron",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "digitron")));

    public static final RegistryObject<SoundEvent> LEVEL_UP_SOUND = SOUNDS.register("level_up",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TheDigiMod.MOD_ID, "level_up")));
}
