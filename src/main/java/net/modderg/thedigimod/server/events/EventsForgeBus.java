package net.modderg.thedigimod.server.events;

import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.*;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigimodConstants.*;
import net.modderg.thedigimod.server.commands.DigimodCommandSummonAll;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.projectiles.ProjectileDefault;

import static net.modderg.thedigimod.TheDigimodConstants.*;
import static net.modderg.thedigimod.server.events.TagLootableStuff.TryToAddDropToDigimon;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EventsForgeBus {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("summonAllDigimon")
                .requires(cs -> cs.hasPermission(2))
                .executes(DigimodCommandSummonAll::summonAllMobs)
        );
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        DigimonEntity killer;

        if(event.getSource().getEntity() instanceof DigimonEntity digimon)
            killer = digimon;
        else if(event.getSource().getEntity() instanceof ProjectileDefault proj && proj.getOwner() instanceof DigimonEntity digimon)
            killer = digimon;
        else return;

        EntityType<?> victimType = victim.getType();

        if(victim.getMobType().equals(MobType.ARTHROPOD) || victimType.equals(EntityType.MOOSHROOM))
            killer.gainSpecificXp(PLAN_INSEC_XP);

        else if (victim.getMobType().equals(MobType.WATER) || victimType.equals(EntityType.POLAR_BEAR) ||
                victimType.equals(EntityType.TADPOLE) || victimType.equals(EntityType.FROG) ||
                victimType.equals(EntityType.SNOW_GOLEM) || victimType.equals(EntityType.STRAY))
            killer.gainSpecificXp(AQUAN_XP);

        else if (victim.getMobType().equals(MobType.UNDEFINED) || victimType.equals(EntityType.ALLAY))
            killer.gainSpecificXp(HOLY_XP);

        else if (victim.getMobType().equals(MobType.ILLAGER) || victimType.equals(EntityType.IRON_GOLEM))
            killer.gainSpecificXp(MACHINE_XP);

        else if (victimType.equals(EntityType.CAMEL) || victimType.equals(EntityType.HUSK))
            killer.gainSpecificXp(EARTH_XP);

        else if (victimType.equals(EntityType.BAT) || victimType.equals(EntityType.PHANTOM) ||
                victimType.equals(EntityType.PARROT) || victimType.equals(EntityType.CHICKEN) ||
                victimType.equals(EntityType.GHAST))
            killer.gainSpecificXp(WIND_XP);

        else if (victimType.equals(EntityType.COW) || victimType.equals(EntityType.PIG) ||
                victimType.equals(EntityType.WOLF) || victimType.equals(EntityType.CAT) ||
                victimType.equals(EntityType.PANDA) || victimType.equals(EntityType.HOGLIN) ||
                victimType.equals(EntityType.SHEEP) || victimType.equals(EntityType.FOX) ||
                victimType.equals(EntityType.RABBIT) || victimType.equals(EntityType.LLAMA) ||
                victimType.equals(EntityType.HORSE) || victimType.equals(EntityType.DONKEY) ||
                victimType.equals(EntityType.MULE))
            killer.gainSpecificXp(BEAST_XP);

        else if (victimType.equals(EntityType.BLAZE) || victimType.equals(EntityType.MAGMA_CUBE)
                || victimType.equals(EntityType.STRIDER)|| victimType.equals(EntityType.ENDER_DRAGON))
            killer.gainSpecificXp(DRAGON_XP);

        else if (victimType.equals(EntityType.SLIME))
            killer.gainSpecificXp(POOP_XP);

        else if (victim.getMobType().equals(MobType.UNDEAD) || victimType.equals(EntityType.WITCH)
                ||victimType.equals(EntityType.WARDEN))
            killer.gainSpecificXp(NIGHTMARE_XP);

        else
            killer.gainSpecificXp(killer.getRandom().nextInt(10));
    }

    @SubscribeEvent
    public static void onEntityJoinServerWorld(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof DigimonEntity cd){
            cd.initInventory();
        }
    }

    public static RelaodListener THE_DIGIMON_RELOAD_LISTENER = new RelaodListener((new GsonBuilder()).create(),"digimon");

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(THE_DIGIMON_RELOAD_LISTENER);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {

        if(!(event.getEntity() instanceof DigimonEntity cd)) return;

        if(TagLootableStuff.keyToItemMap.keySet().isEmpty())
            TagLootableStuff.init();

        TryToAddDropToDigimon(event, cd);
    }
}