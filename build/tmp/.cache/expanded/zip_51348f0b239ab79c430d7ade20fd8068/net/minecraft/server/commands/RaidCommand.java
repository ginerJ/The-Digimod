package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.phys.Vec3;

public class RaidCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_180469_) {
      p_180469_.register(Commands.literal("raid").requires((p_180498_) -> {
         return p_180498_.hasPermission(3);
      }).then(Commands.literal("start").then(Commands.argument("omenlvl", IntegerArgumentType.integer(0)).executes((p_180502_) -> {
         return start(p_180502_.getSource(), IntegerArgumentType.getInteger(p_180502_, "omenlvl"));
      }))).then(Commands.literal("stop").executes((p_180500_) -> {
         return stop(p_180500_.getSource());
      })).then(Commands.literal("check").executes((p_180496_) -> {
         return check(p_180496_.getSource());
      })).then(Commands.literal("sound").then(Commands.argument("type", ComponentArgument.textComponent()).executes((p_180492_) -> {
         return playSound(p_180492_.getSource(), ComponentArgument.getComponent(p_180492_, "type"));
      }))).then(Commands.literal("spawnleader").executes((p_180488_) -> {
         return spawnLeader(p_180488_.getSource());
      })).then(Commands.literal("setomen").then(Commands.argument("level", IntegerArgumentType.integer(0)).executes((p_180481_) -> {
         return setBadOmenLevel(p_180481_.getSource(), IntegerArgumentType.getInteger(p_180481_, "level"));
      }))).then(Commands.literal("glow").executes((p_180471_) -> {
         return glow(p_180471_.getSource());
      })));
   }

   private static int glow(CommandSourceStack p_180473_) throws CommandSyntaxException {
      Raid raid = getRaid(p_180473_.getPlayerOrException());
      if (raid != null) {
         for(Raider raider : raid.getAllRaiders()) {
            raider.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1000, 1));
         }
      }

      return 1;
   }

   private static int setBadOmenLevel(CommandSourceStack p_180475_, int p_180476_) throws CommandSyntaxException {
      Raid raid = getRaid(p_180475_.getPlayerOrException());
      if (raid != null) {
         int i = raid.getMaxBadOmenLevel();
         if (p_180476_ > i) {
            p_180475_.sendFailure(Component.literal("Sorry, the max bad omen level you can set is " + i));
         } else {
            int j = raid.getBadOmenLevel();
            raid.setBadOmenLevel(p_180476_);
            p_180475_.sendSuccess(() -> {
               return Component.literal("Changed village's bad omen level from " + j + " to " + p_180476_);
            }, false);
         }
      } else {
         p_180475_.sendFailure(Component.literal("No raid found here"));
      }

      return 1;
   }

   private static int spawnLeader(CommandSourceStack p_180483_) {
      p_180483_.sendSuccess(() -> {
         return Component.literal("Spawned a raid captain");
      }, false);
      Raider raider = EntityType.PILLAGER.create(p_180483_.getLevel());
      if (raider == null) {
         p_180483_.sendFailure(Component.literal("Pillager failed to spawn"));
         return 0;
      } else {
         raider.setPatrolLeader(true);
         raider.setItemSlot(EquipmentSlot.HEAD, Raid.getLeaderBannerInstance());
         raider.setPos(p_180483_.getPosition().x, p_180483_.getPosition().y, p_180483_.getPosition().z);
         raider.finalizeSpawn(p_180483_.getLevel(), p_180483_.getLevel().getCurrentDifficultyAt(BlockPos.containing(p_180483_.getPosition())), MobSpawnType.COMMAND, (SpawnGroupData)null, (CompoundTag)null);
         p_180483_.getLevel().addFreshEntityWithPassengers(raider);
         return 1;
      }
   }

   private static int playSound(CommandSourceStack p_180478_, @Nullable Component p_180479_) {
      if (p_180479_ != null && p_180479_.getString().equals("local")) {
         ServerLevel serverlevel = p_180478_.getLevel();
         Vec3 vec3 = p_180478_.getPosition().add(5.0D, 0.0D, 0.0D);
         serverlevel.playSeededSound((Player)null, vec3.x, vec3.y, vec3.z, SoundEvents.RAID_HORN, SoundSource.NEUTRAL, 2.0F, 1.0F, serverlevel.random.nextLong());
      }

      return 1;
   }

   private static int start(CommandSourceStack p_180485_, int p_180486_) throws CommandSyntaxException {
      ServerPlayer serverplayer = p_180485_.getPlayerOrException();
      BlockPos blockpos = serverplayer.blockPosition();
      if (serverplayer.serverLevel().isRaided(blockpos)) {
         p_180485_.sendFailure(Component.literal("Raid already started close by"));
         return -1;
      } else {
         Raids raids = serverplayer.serverLevel().getRaids();
         Raid raid = raids.createOrExtendRaid(serverplayer);
         if (raid != null) {
            raid.setBadOmenLevel(p_180486_);
            raids.setDirty();
            p_180485_.sendSuccess(() -> {
               return Component.literal("Created a raid in your local village");
            }, false);
         } else {
            p_180485_.sendFailure(Component.literal("Failed to create a raid in your local village"));
         }

         return 1;
      }
   }

   private static int stop(CommandSourceStack p_180490_) throws CommandSyntaxException {
      ServerPlayer serverplayer = p_180490_.getPlayerOrException();
      BlockPos blockpos = serverplayer.blockPosition();
      Raid raid = serverplayer.serverLevel().getRaidAt(blockpos);
      if (raid != null) {
         raid.stop();
         p_180490_.sendSuccess(() -> {
            return Component.literal("Stopped raid");
         }, false);
         return 1;
      } else {
         p_180490_.sendFailure(Component.literal("No raid here"));
         return -1;
      }
   }

   private static int check(CommandSourceStack p_180494_) throws CommandSyntaxException {
      Raid raid = getRaid(p_180494_.getPlayerOrException());
      if (raid != null) {
         StringBuilder stringbuilder = new StringBuilder();
         stringbuilder.append("Found a started raid! ");
         p_180494_.sendSuccess(() -> {
            return Component.literal(stringbuilder.toString());
         }, false);
         StringBuilder stringbuilder1 = new StringBuilder();
         stringbuilder1.append("Num groups spawned: ");
         stringbuilder1.append(raid.getGroupsSpawned());
         stringbuilder1.append(" Bad omen level: ");
         stringbuilder1.append(raid.getBadOmenLevel());
         stringbuilder1.append(" Num mobs: ");
         stringbuilder1.append(raid.getTotalRaidersAlive());
         stringbuilder1.append(" Raid health: ");
         stringbuilder1.append(raid.getHealthOfLivingRaiders());
         stringbuilder1.append(" / ");
         stringbuilder1.append(raid.getTotalHealth());
         p_180494_.sendSuccess(() -> {
            return Component.literal(stringbuilder1.toString());
         }, false);
         return 1;
      } else {
         p_180494_.sendFailure(Component.literal("Found no started raids"));
         return 0;
      }
   }

   @Nullable
   private static Raid getRaid(ServerPlayer p_180467_) {
      return p_180467_.serverLevel().getRaidAt(p_180467_.blockPosition());
   }
}