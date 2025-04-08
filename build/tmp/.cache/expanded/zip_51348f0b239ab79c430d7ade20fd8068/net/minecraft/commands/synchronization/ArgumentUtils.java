package net.minecraft.commands.synchronization;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;

public class ArgumentUtils {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final byte NUMBER_FLAG_MIN = 1;
   private static final byte NUMBER_FLAG_MAX = 2;

   public static int createNumberFlags(boolean p_235428_, boolean p_235429_) {
      int i = 0;
      if (p_235428_) {
         i |= 1;
      }

      if (p_235429_) {
         i |= 2;
      }

      return i;
   }

   public static boolean numberHasMin(byte p_235403_) {
      return (p_235403_ & 1) != 0;
   }

   public static boolean numberHasMax(byte p_235431_) {
      return (p_235431_ & 2) != 0;
   }

   private static <A extends ArgumentType<?>> void serializeCap(JsonObject p_235408_, ArgumentTypeInfo.Template<A> p_235409_) {
      serializeCap(p_235408_, p_235409_.type(), p_235409_);
   }

   private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void serializeCap(JsonObject p_235411_, ArgumentTypeInfo<A, T> p_235412_, ArgumentTypeInfo.Template<A> p_235413_) {
      p_235412_.serializeToJson((T)p_235413_, p_235411_);
   }

   private static <T extends ArgumentType<?>> void serializeArgumentToJson(JsonObject p_235405_, T p_235406_) {
      ArgumentTypeInfo.Template<T> template = ArgumentTypeInfos.unpack(p_235406_);
      p_235405_.addProperty("type", "argument");
      p_235405_.addProperty("parser", BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getKey(template.type()).toString());
      JsonObject jsonobject = new JsonObject();
      serializeCap(jsonobject, template);
      if (jsonobject.size() > 0) {
         p_235405_.add("properties", jsonobject);
      }

   }

   public static <S> JsonObject serializeNodeToJson(CommandDispatcher<S> p_235415_, CommandNode<S> p_235416_) {
      JsonObject jsonobject = new JsonObject();
      if (p_235416_ instanceof RootCommandNode) {
         jsonobject.addProperty("type", "root");
      } else if (p_235416_ instanceof LiteralCommandNode) {
         jsonobject.addProperty("type", "literal");
      } else if (p_235416_ instanceof ArgumentCommandNode) {
         ArgumentCommandNode<?, ?> argumentcommandnode = (ArgumentCommandNode)p_235416_;
         serializeArgumentToJson(jsonobject, argumentcommandnode.getType());
      } else {
         LOGGER.error("Could not serialize node {} ({})!", p_235416_, p_235416_.getClass());
         jsonobject.addProperty("type", "unknown");
      }

      JsonObject jsonobject1 = new JsonObject();

      for(CommandNode<S> commandnode : p_235416_.getChildren()) {
         jsonobject1.add(commandnode.getName(), serializeNodeToJson(p_235415_, commandnode));
      }

      if (jsonobject1.size() > 0) {
         jsonobject.add("children", jsonobject1);
      }

      if (p_235416_.getCommand() != null) {
         jsonobject.addProperty("executable", true);
      }

      if (p_235416_.getRedirect() != null) {
         Collection<String> collection = p_235415_.getPath(p_235416_.getRedirect());
         if (!collection.isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(String s : collection) {
               jsonarray.add(s);
            }

            jsonobject.add("redirect", jsonarray);
         }
      }

      return jsonobject;
   }

   public static <T> Set<ArgumentType<?>> findUsedArgumentTypes(CommandNode<T> p_235418_) {
      Set<CommandNode<T>> set = Sets.newIdentityHashSet();
      Set<ArgumentType<?>> set1 = Sets.newHashSet();
      findUsedArgumentTypes(p_235418_, set1, set);
      return set1;
   }

   private static <T> void findUsedArgumentTypes(CommandNode<T> p_235420_, Set<ArgumentType<?>> p_235421_, Set<CommandNode<T>> p_235422_) {
      if (p_235422_.add(p_235420_)) {
         if (p_235420_ instanceof ArgumentCommandNode) {
            ArgumentCommandNode<?, ?> argumentcommandnode = (ArgumentCommandNode)p_235420_;
            p_235421_.add(argumentcommandnode.getType());
         }

         p_235420_.getChildren().forEach((p_235426_) -> {
            findUsedArgumentTypes(p_235426_, p_235421_, p_235422_);
         });
         CommandNode<T> commandnode = p_235420_.getRedirect();
         if (commandnode != null) {
            findUsedArgumentTypes(commandnode, p_235421_, p_235422_);
         }

      }
   }
}