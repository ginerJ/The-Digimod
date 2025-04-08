package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SummonCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
   private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));

   public static void register(CommandDispatcher<CommandSourceStack> p_250343_, CommandBuildContext p_250122_) {
      p_250343_.register(Commands.literal("summon").requires((p_138819_) -> {
         return p_138819_.hasPermission(2);
      }).then(Commands.argument("entity", ResourceArgument.resource(p_250122_, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_248175_) -> {
         return spawnEntity(p_248175_.getSource(), ResourceArgument.getSummonableEntityType(p_248175_, "entity"), p_248175_.getSource().getPosition(), new CompoundTag(), true);
      }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_248173_) -> {
         return spawnEntity(p_248173_.getSource(), ResourceArgument.getSummonableEntityType(p_248173_, "entity"), Vec3Argument.getVec3(p_248173_, "pos"), new CompoundTag(), true);
      }).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((p_248174_) -> {
         return spawnEntity(p_248174_.getSource(), ResourceArgument.getSummonableEntityType(p_248174_, "entity"), Vec3Argument.getVec3(p_248174_, "pos"), CompoundTagArgument.getCompoundTag(p_248174_, "nbt"), false);
      })))));
   }

   public static Entity createEntity(CommandSourceStack p_270582_, Holder.Reference<EntityType<?>> p_270277_, Vec3 p_270366_, CompoundTag p_270197_, boolean p_270947_) throws CommandSyntaxException {
      BlockPos blockpos = BlockPos.containing(p_270366_);
      if (!Level.isInSpawnableBounds(blockpos)) {
         throw INVALID_POSITION.create();
      } else {
         CompoundTag compoundtag = p_270197_.copy();
         compoundtag.putString("id", p_270277_.key().location().toString());
         ServerLevel serverlevel = p_270582_.getLevel();
         Entity entity = EntityType.loadEntityRecursive(compoundtag, serverlevel, (p_138828_) -> {
            p_138828_.moveTo(p_270366_.x, p_270366_.y, p_270366_.z, p_138828_.getYRot(), p_138828_.getXRot());
            return p_138828_;
         });
         if (entity == null) {
            throw ERROR_FAILED.create();
         } else {
            if (p_270947_ && entity instanceof Mob) {
               ((Mob)entity).finalizeSpawn(p_270582_.getLevel(), p_270582_.getLevel().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, (SpawnGroupData)null, (CompoundTag)null);
            }

            if (!serverlevel.tryAddFreshEntityWithPassengers(entity)) {
               throw ERROR_DUPLICATE_UUID.create();
            } else {
               return entity;
            }
         }
      }
   }

   private static int spawnEntity(CommandSourceStack p_249752_, Holder.Reference<EntityType<?>> p_251948_, Vec3 p_251429_, CompoundTag p_250568_, boolean p_250229_) throws CommandSyntaxException {
      Entity entity = createEntity(p_249752_, p_251948_, p_251429_, p_250568_, p_250229_);
      p_249752_.sendSuccess(() -> {
         return Component.translatable("commands.summon.success", entity.getDisplayName());
      }, true);
      return 1;
   }
}