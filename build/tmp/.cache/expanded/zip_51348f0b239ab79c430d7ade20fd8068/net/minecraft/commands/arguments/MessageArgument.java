package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;

public class MessageArgument implements SignedArgument<MessageArgument.Message> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

   public static MessageArgument message() {
      return new MessageArgument();
   }

   public static Component getMessage(CommandContext<CommandSourceStack> p_96836_, String p_96837_) throws CommandSyntaxException {
      MessageArgument.Message messageargument$message = p_96836_.getArgument(p_96837_, MessageArgument.Message.class);
      return messageargument$message.resolveComponent(p_96836_.getSource());
   }

   public static void resolveChatMessage(CommandContext<CommandSourceStack> p_249433_, String p_248718_, Consumer<PlayerChatMessage> p_249460_) throws CommandSyntaxException {
      MessageArgument.Message messageargument$message = p_249433_.getArgument(p_248718_, MessageArgument.Message.class);
      CommandSourceStack commandsourcestack = p_249433_.getSource();
      Component component = messageargument$message.resolveComponent(commandsourcestack);
      CommandSigningContext commandsigningcontext = commandsourcestack.getSigningContext();
      PlayerChatMessage playerchatmessage = commandsigningcontext.getArgument(p_248718_);
      if (playerchatmessage != null) {
         resolveSignedMessage(p_249460_, commandsourcestack, playerchatmessage.withUnsignedContent(component));
      } else {
         resolveDisguisedMessage(p_249460_, commandsourcestack, PlayerChatMessage.system(messageargument$message.text).withUnsignedContent(component));
      }

   }

   private static void resolveSignedMessage(Consumer<PlayerChatMessage> p_250000_, CommandSourceStack p_252335_, PlayerChatMessage p_249420_) {
      MinecraftServer minecraftserver = p_252335_.getServer();
      CompletableFuture<FilteredText> completablefuture = filterPlainText(p_252335_, p_249420_);
      CompletableFuture<Component> completablefuture1 = minecraftserver.getChatDecorator().decorate(p_252335_.getPlayer(), p_249420_.decoratedContent());
      p_252335_.getChatMessageChainer().append((p_247979_) -> {
         return CompletableFuture.allOf(completablefuture, completablefuture1).thenAcceptAsync((p_247970_) -> {
            PlayerChatMessage playerchatmessage = p_249420_.withUnsignedContent(completablefuture1.join()).filter(completablefuture.join().mask());
            p_250000_.accept(playerchatmessage);
         }, p_247979_);
      });
   }

   private static void resolveDisguisedMessage(Consumer<PlayerChatMessage> p_249162_, CommandSourceStack p_248759_, PlayerChatMessage p_252332_) {
      MinecraftServer minecraftserver = p_248759_.getServer();
      CompletableFuture<Component> completablefuture = minecraftserver.getChatDecorator().decorate(p_248759_.getPlayer(), p_252332_.decoratedContent());
      p_248759_.getChatMessageChainer().append((p_247974_) -> {
         return completablefuture.thenAcceptAsync((p_247965_) -> {
            p_249162_.accept(p_252332_.withUnsignedContent(p_247965_));
         }, p_247974_);
      });
   }

   private static CompletableFuture<FilteredText> filterPlainText(CommandSourceStack p_252063_, PlayerChatMessage p_251184_) {
      ServerPlayer serverplayer = p_252063_.getPlayer();
      return serverplayer != null && p_251184_.hasSignatureFrom(serverplayer.getUUID()) ? serverplayer.getTextFilter().processStreamMessage(p_251184_.signedContent()) : CompletableFuture.completedFuture(FilteredText.passThrough(p_251184_.signedContent()));
   }

   public MessageArgument.Message parse(StringReader p_96834_) throws CommandSyntaxException {
      return MessageArgument.Message.parseText(p_96834_, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Message {
      final String text;
      private final MessageArgument.Part[] parts;

      public Message(String p_96844_, MessageArgument.Part[] p_96845_) {
         this.text = p_96844_;
         this.parts = p_96845_;
      }

      public String getText() {
         return this.text;
      }

      public MessageArgument.Part[] getParts() {
         return this.parts;
      }

      Component resolveComponent(CommandSourceStack p_232197_) throws CommandSyntaxException {
         return this.toComponent(p_232197_, net.minecraftforge.common.ForgeHooks.canUseEntitySelectors(p_232197_));
      }

      public Component toComponent(CommandSourceStack p_96850_, boolean p_96851_) throws CommandSyntaxException {
         if (this.parts.length != 0 && p_96851_) {
            MutableComponent mutablecomponent = Component.literal(this.text.substring(0, this.parts[0].getStart()));
            int i = this.parts[0].getStart();

            for(MessageArgument.Part messageargument$part : this.parts) {
               Component component = messageargument$part.toComponent(p_96850_);
               if (i < messageargument$part.getStart()) {
                  mutablecomponent.append(this.text.substring(i, messageargument$part.getStart()));
               }

               if (component != null) {
                  mutablecomponent.append(component);
               }

               i = messageargument$part.getEnd();
            }

            if (i < this.text.length()) {
               mutablecomponent.append(this.text.substring(i));
            }

            return mutablecomponent;
         } else {
            return Component.literal(this.text);
         }
      }

      public static MessageArgument.Message parseText(StringReader p_96847_, boolean p_96848_) throws CommandSyntaxException {
         String s = p_96847_.getString().substring(p_96847_.getCursor(), p_96847_.getTotalLength());
         if (!p_96848_) {
            p_96847_.setCursor(p_96847_.getTotalLength());
            return new MessageArgument.Message(s, new MessageArgument.Part[0]);
         } else {
            List<MessageArgument.Part> list = Lists.newArrayList();
            int i = p_96847_.getCursor();

            while(true) {
               int j;
               EntitySelector entityselector;
               while(true) {
                  if (!p_96847_.canRead()) {
                     return new MessageArgument.Message(s, list.toArray(new MessageArgument.Part[0]));
                  }

                  if (p_96847_.peek() == '@') {
                     j = p_96847_.getCursor();

                     try {
                        EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_96847_);
                        entityselector = entityselectorparser.parse();
                        break;
                     } catch (CommandSyntaxException commandsyntaxexception) {
                        if (commandsyntaxexception.getType() != EntitySelectorParser.ERROR_MISSING_SELECTOR_TYPE && commandsyntaxexception.getType() != EntitySelectorParser.ERROR_UNKNOWN_SELECTOR_TYPE) {
                           throw commandsyntaxexception;
                        }

                        p_96847_.setCursor(j + 1);
                     }
                  } else {
                     p_96847_.skip();
                  }
               }

               list.add(new MessageArgument.Part(j - i, p_96847_.getCursor() - i, entityselector));
            }
         }
      }
   }

   public static class Part {
      private final int start;
      private final int end;
      private final EntitySelector selector;

      public Part(int p_96856_, int p_96857_, EntitySelector p_96858_) {
         this.start = p_96856_;
         this.end = p_96857_;
         this.selector = p_96858_;
      }

      public int getStart() {
         return this.start;
      }

      public int getEnd() {
         return this.end;
      }

      public EntitySelector getSelector() {
         return this.selector;
      }

      @Nullable
      public Component toComponent(CommandSourceStack p_96861_) throws CommandSyntaxException {
         return EntitySelector.joinNames(this.selector.findEntities(p_96861_));
      }
   }
}
