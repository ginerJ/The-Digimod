package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.FriendlyByteBuf;

public interface ArgumentTypeInfo<A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> {
   void serializeToNetwork(T p_235375_, FriendlyByteBuf p_235376_);

   T deserializeFromNetwork(FriendlyByteBuf p_235377_);

   void serializeToJson(T p_235373_, JsonObject p_235374_);

   T unpack(A p_235372_);

   public interface Template<A extends ArgumentType<?>> {
      A instantiate(CommandBuildContext p_235378_);

      ArgumentTypeInfo<A, ?> type();
   }
}