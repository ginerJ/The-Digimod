package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.UUID;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeCommand {
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((p_212443_) -> {
      return Component.translatable("commands.attribute.failed.entity", p_212443_);
   });
   private static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ATTRIBUTE = new Dynamic2CommandExceptionType((p_212445_, p_212446_) -> {
      return Component.translatable("commands.attribute.failed.no_attribute", p_212445_, p_212446_);
   });
   private static final Dynamic3CommandExceptionType ERROR_NO_SUCH_MODIFIER = new Dynamic3CommandExceptionType((p_212448_, p_212449_, p_212450_) -> {
      return Component.translatable("commands.attribute.failed.no_modifier", p_212449_, p_212448_, p_212450_);
   });
   private static final Dynamic3CommandExceptionType ERROR_MODIFIER_ALREADY_PRESENT = new Dynamic3CommandExceptionType((p_136497_, p_136498_, p_136499_) -> {
      return Component.translatable("commands.attribute.failed.modifier_already_present", p_136499_, p_136498_, p_136497_);
   });

   public static void register(CommandDispatcher<CommandSourceStack> p_251026_, CommandBuildContext p_250936_) {
      p_251026_.register(Commands.literal("attribute").requires((p_212441_) -> {
         return p_212441_.hasPermission(2);
      }).then(Commands.argument("target", EntityArgument.entity()).then(Commands.argument("attribute", ResourceArgument.resource(p_250936_, Registries.ATTRIBUTE)).then(Commands.literal("get").executes((p_248109_) -> {
         return getAttributeValue(p_248109_.getSource(), EntityArgument.getEntity(p_248109_, "target"), ResourceArgument.getAttribute(p_248109_, "attribute"), 1.0D);
      }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_248104_) -> {
         return getAttributeValue(p_248104_.getSource(), EntityArgument.getEntity(p_248104_, "target"), ResourceArgument.getAttribute(p_248104_, "attribute"), DoubleArgumentType.getDouble(p_248104_, "scale"));
      }))).then(Commands.literal("base").then(Commands.literal("set").then(Commands.argument("value", DoubleArgumentType.doubleArg()).executes((p_248102_) -> {
         return setAttributeBase(p_248102_.getSource(), EntityArgument.getEntity(p_248102_, "target"), ResourceArgument.getAttribute(p_248102_, "attribute"), DoubleArgumentType.getDouble(p_248102_, "value"));
      }))).then(Commands.literal("get").executes((p_248112_) -> {
         return getAttributeBase(p_248112_.getSource(), EntityArgument.getEntity(p_248112_, "target"), ResourceArgument.getAttribute(p_248112_, "attribute"), 1.0D);
      }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_248106_) -> {
         return getAttributeBase(p_248106_.getSource(), EntityArgument.getEntity(p_248106_, "target"), ResourceArgument.getAttribute(p_248106_, "attribute"), DoubleArgumentType.getDouble(p_248106_, "scale"));
      })))).then(Commands.literal("modifier").then(Commands.literal("add").then(Commands.argument("uuid", UuidArgument.uuid()).then(Commands.argument("name", StringArgumentType.string()).then(Commands.argument("value", DoubleArgumentType.doubleArg()).then(Commands.literal("add").executes((p_248105_) -> {
         return addModifier(p_248105_.getSource(), EntityArgument.getEntity(p_248105_, "target"), ResourceArgument.getAttribute(p_248105_, "attribute"), UuidArgument.getUuid(p_248105_, "uuid"), StringArgumentType.getString(p_248105_, "name"), DoubleArgumentType.getDouble(p_248105_, "value"), AttributeModifier.Operation.ADDITION);
      })).then(Commands.literal("multiply").executes((p_248107_) -> {
         return addModifier(p_248107_.getSource(), EntityArgument.getEntity(p_248107_, "target"), ResourceArgument.getAttribute(p_248107_, "attribute"), UuidArgument.getUuid(p_248107_, "uuid"), StringArgumentType.getString(p_248107_, "name"), DoubleArgumentType.getDouble(p_248107_, "value"), AttributeModifier.Operation.MULTIPLY_TOTAL);
      })).then(Commands.literal("multiply_base").executes((p_248108_) -> {
         return addModifier(p_248108_.getSource(), EntityArgument.getEntity(p_248108_, "target"), ResourceArgument.getAttribute(p_248108_, "attribute"), UuidArgument.getUuid(p_248108_, "uuid"), StringArgumentType.getString(p_248108_, "name"), DoubleArgumentType.getDouble(p_248108_, "value"), AttributeModifier.Operation.MULTIPLY_BASE);
      })))))).then(Commands.literal("remove").then(Commands.argument("uuid", UuidArgument.uuid()).executes((p_248103_) -> {
         return removeModifier(p_248103_.getSource(), EntityArgument.getEntity(p_248103_, "target"), ResourceArgument.getAttribute(p_248103_, "attribute"), UuidArgument.getUuid(p_248103_, "uuid"));
      }))).then(Commands.literal("value").then(Commands.literal("get").then(Commands.argument("uuid", UuidArgument.uuid()).executes((p_248110_) -> {
         return getAttributeModifier(p_248110_.getSource(), EntityArgument.getEntity(p_248110_, "target"), ResourceArgument.getAttribute(p_248110_, "attribute"), UuidArgument.getUuid(p_248110_, "uuid"), 1.0D);
      }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_248111_) -> {
         return getAttributeModifier(p_248111_.getSource(), EntityArgument.getEntity(p_248111_, "target"), ResourceArgument.getAttribute(p_248111_, "attribute"), UuidArgument.getUuid(p_248111_, "uuid"), DoubleArgumentType.getDouble(p_248111_, "scale"));
      })))))))));
   }

   private static AttributeInstance getAttributeInstance(Entity p_252177_, Holder<Attribute> p_249942_) throws CommandSyntaxException {
      AttributeInstance attributeinstance = getLivingEntity(p_252177_).getAttributes().getInstance(p_249942_);
      if (attributeinstance == null) {
         throw ERROR_NO_SUCH_ATTRIBUTE.create(p_252177_.getName(), getAttributeDescription(p_249942_));
      } else {
         return attributeinstance;
      }
   }

   private static LivingEntity getLivingEntity(Entity p_136440_) throws CommandSyntaxException {
      if (!(p_136440_ instanceof LivingEntity)) {
         throw ERROR_NOT_LIVING_ENTITY.create(p_136440_.getName());
      } else {
         return (LivingEntity)p_136440_;
      }
   }

   private static LivingEntity getEntityWithAttribute(Entity p_252105_, Holder<Attribute> p_248921_) throws CommandSyntaxException {
      LivingEntity livingentity = getLivingEntity(p_252105_);
      if (!livingentity.getAttributes().hasAttribute(p_248921_)) {
         throw ERROR_NO_SUCH_ATTRIBUTE.create(p_252105_.getName(), getAttributeDescription(p_248921_));
      } else {
         return livingentity;
      }
   }

   private static int getAttributeValue(CommandSourceStack p_251776_, Entity p_249647_, Holder<Attribute> p_250986_, double p_251395_) throws CommandSyntaxException {
      LivingEntity livingentity = getEntityWithAttribute(p_249647_, p_250986_);
      double d0 = livingentity.getAttributeValue(p_250986_);
      p_251776_.sendSuccess(() -> {
         return Component.translatable("commands.attribute.value.get.success", getAttributeDescription(p_250986_), p_249647_.getName(), d0);
      }, false);
      return (int)(d0 * p_251395_);
   }

   private static int getAttributeBase(CommandSourceStack p_248780_, Entity p_251083_, Holder<Attribute> p_250388_, double p_250194_) throws CommandSyntaxException {
      LivingEntity livingentity = getEntityWithAttribute(p_251083_, p_250388_);
      double d0 = livingentity.getAttributeBaseValue(p_250388_);
      p_248780_.sendSuccess(() -> {
         return Component.translatable("commands.attribute.base_value.get.success", getAttributeDescription(p_250388_), p_251083_.getName(), d0);
      }, false);
      return (int)(d0 * p_250194_);
   }

   private static int getAttributeModifier(CommandSourceStack p_136464_, Entity p_136465_, Holder<Attribute> p_250680_, UUID p_136467_, double p_136468_) throws CommandSyntaxException {
      LivingEntity livingentity = getEntityWithAttribute(p_136465_, p_250680_);
      AttributeMap attributemap = livingentity.getAttributes();
      if (!attributemap.hasModifier(p_250680_, p_136467_)) {
         throw ERROR_NO_SUCH_MODIFIER.create(p_136465_.getName(), getAttributeDescription(p_250680_), p_136467_);
      } else {
         double d0 = attributemap.getModifierValue(p_250680_, p_136467_);
         p_136464_.sendSuccess(() -> {
            return Component.translatable("commands.attribute.modifier.value.get.success", p_136467_, getAttributeDescription(p_250680_), p_136465_.getName(), d0);
         }, false);
         return (int)(d0 * p_136468_);
      }
   }

   private static int setAttributeBase(CommandSourceStack p_248556_, Entity p_248620_, Holder<Attribute> p_249456_, double p_252212_) throws CommandSyntaxException {
      getAttributeInstance(p_248620_, p_249456_).setBaseValue(p_252212_);
      p_248556_.sendSuccess(() -> {
         return Component.translatable("commands.attribute.base_value.set.success", getAttributeDescription(p_249456_), p_248620_.getName(), p_252212_);
      }, false);
      return 1;
   }

   private static int addModifier(CommandSourceStack p_136470_, Entity p_136471_, Holder<Attribute> p_251636_, UUID p_136473_, String p_136474_, double p_136475_, AttributeModifier.Operation p_136476_) throws CommandSyntaxException {
      AttributeInstance attributeinstance = getAttributeInstance(p_136471_, p_251636_);
      AttributeModifier attributemodifier = new AttributeModifier(p_136473_, p_136474_, p_136475_, p_136476_);
      if (attributeinstance.hasModifier(attributemodifier)) {
         throw ERROR_MODIFIER_ALREADY_PRESENT.create(p_136471_.getName(), getAttributeDescription(p_251636_), p_136473_);
      } else {
         attributeinstance.addPermanentModifier(attributemodifier);
         p_136470_.sendSuccess(() -> {
            return Component.translatable("commands.attribute.modifier.add.success", p_136473_, getAttributeDescription(p_251636_), p_136471_.getName());
         }, false);
         return 1;
      }
   }

   private static int removeModifier(CommandSourceStack p_136459_, Entity p_136460_, Holder<Attribute> p_250830_, UUID p_136462_) throws CommandSyntaxException {
      AttributeInstance attributeinstance = getAttributeInstance(p_136460_, p_250830_);
      if (attributeinstance.removePermanentModifier(p_136462_)) {
         p_136459_.sendSuccess(() -> {
            return Component.translatable("commands.attribute.modifier.remove.success", p_136462_, getAttributeDescription(p_250830_), p_136460_.getName());
         }, false);
         return 1;
      } else {
         throw ERROR_NO_SUCH_MODIFIER.create(p_136460_.getName(), getAttributeDescription(p_250830_), p_136462_);
      }
   }

   private static Component getAttributeDescription(Holder<Attribute> p_250602_) {
      return Component.translatable(p_250602_.value().getDescriptionId());
   }
}