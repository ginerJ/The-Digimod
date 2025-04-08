package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantCommand {
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((p_137029_) -> {
      return Component.translatable("commands.enchant.failed.entity", p_137029_);
   });
   private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((p_137027_) -> {
      return Component.translatable("commands.enchant.failed.itemless", p_137027_);
   });
   private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType((p_137020_) -> {
      return Component.translatable("commands.enchant.failed.incompatible", p_137020_);
   });
   private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType((p_137022_, p_137023_) -> {
      return Component.translatable("commands.enchant.failed.level", p_137022_, p_137023_);
   });
   private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(Component.translatable("commands.enchant.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_251241_, CommandBuildContext p_251038_) {
      p_251241_.register(Commands.literal("enchant").requires((p_137013_) -> {
         return p_137013_.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("enchantment", ResourceArgument.resource(p_251038_, Registries.ENCHANTMENT)).executes((p_248131_) -> {
         return enchant(p_248131_.getSource(), EntityArgument.getEntities(p_248131_, "targets"), ResourceArgument.getEnchantment(p_248131_, "enchantment"), 1);
      }).then(Commands.argument("level", IntegerArgumentType.integer(0)).executes((p_248132_) -> {
         return enchant(p_248132_.getSource(), EntityArgument.getEntities(p_248132_, "targets"), ResourceArgument.getEnchantment(p_248132_, "enchantment"), IntegerArgumentType.getInteger(p_248132_, "level"));
      })))));
   }

   private static int enchant(CommandSourceStack p_249815_, Collection<? extends Entity> p_248848_, Holder<Enchantment> p_251252_, int p_249941_) throws CommandSyntaxException {
      Enchantment enchantment = p_251252_.value();
      if (p_249941_ > enchantment.getMaxLevel()) {
         throw ERROR_LEVEL_TOO_HIGH.create(p_249941_, enchantment.getMaxLevel());
      } else {
         int i = 0;

         for(Entity entity : p_248848_) {
            if (entity instanceof LivingEntity) {
               LivingEntity livingentity = (LivingEntity)entity;
               ItemStack itemstack = livingentity.getMainHandItem();
               if (!itemstack.isEmpty()) {
                  if (enchantment.canEnchant(itemstack) && EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantments(itemstack).keySet(), enchantment)) {
                     itemstack.enchant(enchantment, p_249941_);
                     ++i;
                  } else if (p_248848_.size() == 1) {
                     throw ERROR_INCOMPATIBLE.create(itemstack.getItem().getName(itemstack).getString());
                  }
               } else if (p_248848_.size() == 1) {
                  throw ERROR_NO_ITEM.create(livingentity.getName().getString());
               }
            } else if (p_248848_.size() == 1) {
               throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
            }
         }

         if (i == 0) {
            throw ERROR_NOTHING_HAPPENED.create();
         } else {
            if (p_248848_.size() == 1) {
               p_249815_.sendSuccess(() -> {
                  return Component.translatable("commands.enchant.success.single", enchantment.getFullname(p_249941_), p_248848_.iterator().next().getDisplayName());
               }, true);
            } else {
               p_249815_.sendSuccess(() -> {
                  return Component.translatable("commands.enchant.success.multiple", enchantment.getFullname(p_249941_), p_248848_.size());
               }, true);
            }

            return i;
         }
      }
   }
}