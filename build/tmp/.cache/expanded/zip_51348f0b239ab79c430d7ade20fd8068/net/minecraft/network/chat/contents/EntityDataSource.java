package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public record EntityDataSource(String selectorPattern, @Nullable EntitySelector compiledSelector) implements DataSource {
   public EntityDataSource(String p_237330_) {
      this(p_237330_, compileSelector(p_237330_));
   }

   @Nullable
   private static EntitySelector compileSelector(String p_237336_) {
      try {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(p_237336_));
         return entityselectorparser.parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
         return null;
      }
   }

   public Stream<CompoundTag> getData(CommandSourceStack p_237341_) throws CommandSyntaxException {
      if (this.compiledSelector != null) {
         List<? extends Entity> list = this.compiledSelector.findEntities(p_237341_);
         return list.stream().map(NbtPredicate::getEntityTagToCompare);
      } else {
         return Stream.empty();
      }
   }

   public String toString() {
      return "entity=" + this.selectorPattern;
   }

   public boolean equals(Object p_237339_) {
      if (this == p_237339_) {
         return true;
      } else {
         if (p_237339_ instanceof EntityDataSource) {
            EntityDataSource entitydatasource = (EntityDataSource)p_237339_;
            if (this.selectorPattern.equals(entitydatasource.selectorPattern)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.selectorPattern.hashCode();
   }
}