package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.TaskChainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandSourceStack implements SharedSuggestionProvider, net.minecraftforge.common.extensions.IForgeCommandSourceStack {
   public static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(Component.translatable("permissions.requires.player"));
   public static final SimpleCommandExceptionType ERROR_NOT_ENTITY = new SimpleCommandExceptionType(Component.translatable("permissions.requires.entity"));
   public final CommandSource source;
   private final Vec3 worldPosition;
   private final ServerLevel level;
   private final int permissionLevel;
   private final String textName;
   private final Component displayName;
   private final MinecraftServer server;
   private final boolean silent;
   @Nullable
   private final Entity entity;
   @Nullable
   private final ResultConsumer<CommandSourceStack> consumer;
   private final EntityAnchorArgument.Anchor anchor;
   private final Vec2 rotation;
   private final CommandSigningContext signingContext;
   private final TaskChainer chatMessageChainer;
   private final IntConsumer returnValueConsumer;

   public CommandSourceStack(CommandSource p_81302_, Vec3 p_81303_, Vec2 p_81304_, ServerLevel p_81305_, int p_81306_, String p_81307_, Component p_81308_, MinecraftServer p_81309_, @Nullable Entity p_81310_) {
      this(p_81302_, p_81303_, p_81304_, p_81305_, p_81306_, p_81307_, p_81308_, p_81309_, p_81310_, false, (p_81361_, p_81362_, p_81363_) -> {
      }, EntityAnchorArgument.Anchor.FEET, CommandSigningContext.ANONYMOUS, TaskChainer.immediate(p_81309_), (p_280930_) -> {
      });
   }

   protected CommandSourceStack(CommandSource p_282943_, Vec3 p_282023_, Vec2 p_282896_, ServerLevel p_282659_, int p_283075_, String p_282379_, Component p_282469_, MinecraftServer p_281590_, @Nullable Entity p_281515_, boolean p_282415_, @Nullable ResultConsumer<CommandSourceStack> p_282042_, EntityAnchorArgument.Anchor p_282332_, CommandSigningContext p_283585_, TaskChainer p_282376_, IntConsumer p_282226_) {
      this.source = p_282943_;
      this.worldPosition = p_282023_;
      this.level = p_282659_;
      this.silent = p_282415_;
      this.entity = p_281515_;
      this.permissionLevel = p_283075_;
      this.textName = p_282379_;
      this.displayName = p_282469_;
      this.server = p_281590_;
      this.consumer = p_282042_;
      this.anchor = p_282332_;
      this.rotation = p_282896_;
      this.signingContext = p_283585_;
      this.chatMessageChainer = p_282376_;
      this.returnValueConsumer = p_282226_;
   }

   public CommandSourceStack withSource(CommandSource p_165485_) {
      return this.source == p_165485_ ? this : new CommandSourceStack(p_165485_, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer, this.returnValueConsumer);
   }

   public CommandSourceStack withEntity(Entity p_81330_) {
      return this.entity == p_81330_ ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, p_81330_.getName().getString(), p_81330_.getDisplayName(), this.server, p_81330_, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer, this.returnValueConsumer);
   }

   public CommandSourceStack withPosition(Vec3 p_81349_) {
      return this.worldPosition.equals(p_81349_) ? this : new CommandSourceStack(this.source, p_81349_, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer, this.returnValueConsumer);
   }

   public CommandSourceStack withRotation(Vec2 p_81347_) {
      return this.rotation.equals(p_81347_) ? this : new CommandSourceStack(this.source, this.worldPosition, p_81347_, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer, this.returnValueConsumer);
   }

   public CommandSourceStack withCallback(ResultConsumer<CommandSourceStack> p_81335_) {
      return Objects.equals(this.consumer, p_81335_) ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, p_81335_, this.anchor, this.signingContext, this.chatMessageChainer, this.returnValueConsumer);
   }

   public CommandSourceStack withCallback(ResultConsumer<CommandSourceStack> p_81337_, BinaryOperator<ResultConsumer<CommandSourceStack>> p_81338_) {
      ResultConsumer<CommandSourceStack> resultconsumer = p_81338_.apply(this.consumer, p_81337_);
      return this.withCallback(resultconsumer);
   }

   public CommandSourceStack withSuppressedOutput() {
      return !this.silent && !this.source.alwaysAccepts() ? new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, true, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer, this.returnValueConsumer) : this;
   }

   public CommandSourceStack withPermission(int p_81326_) {
      return p_81326_ == this.permissionLevel ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, p_81326_, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer, this.returnValueConsumer);
   }

   public CommandSourceStack withMaximumPermission(int p_81359_) {
      return p_81359_ <= this.permissionLevel ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, p_81359_, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer, this.returnValueConsumer);
   }

   public CommandSourceStack withAnchor(EntityAnchorArgument.Anchor p_81351_) {
      return p_81351_ == this.anchor ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, p_81351_, this.signingContext, this.chatMessageChainer, this.returnValueConsumer);
   }

   public CommandSourceStack withLevel(ServerLevel p_81328_) {
      if (p_81328_ == this.level) {
         return this;
      } else {
         double d0 = DimensionType.getTeleportationScale(this.level.dimensionType(), p_81328_.dimensionType());
         Vec3 vec3 = new Vec3(this.worldPosition.x * d0, this.worldPosition.y, this.worldPosition.z * d0);
         return new CommandSourceStack(this.source, vec3, this.rotation, p_81328_, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer, this.returnValueConsumer);
      }
   }

   public CommandSourceStack facing(Entity p_81332_, EntityAnchorArgument.Anchor p_81333_) {
      return this.facing(p_81333_.apply(p_81332_));
   }

   public CommandSourceStack facing(Vec3 p_81365_) {
      Vec3 vec3 = this.anchor.apply(this);
      double d0 = p_81365_.x - vec3.x;
      double d1 = p_81365_.y - vec3.y;
      double d2 = p_81365_.z - vec3.z;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      float f = Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
      float f1 = Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
      return this.withRotation(new Vec2(f, f1));
   }

   public CommandSourceStack withSigningContext(CommandSigningContext p_230894_) {
      return p_230894_ == this.signingContext ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, p_230894_, this.chatMessageChainer, this.returnValueConsumer);
   }

   public CommandSourceStack withChatMessageChainer(TaskChainer p_242228_) {
      return p_242228_ == this.chatMessageChainer ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, p_242228_, this.returnValueConsumer);
   }

   public CommandSourceStack withReturnValueConsumer(IntConsumer p_282875_) {
      return p_282875_ == this.returnValueConsumer ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer, p_282875_);
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public String getTextName() {
      return this.textName;
   }

   public boolean hasPermission(int p_81370_) {
      return this.permissionLevel >= p_81370_;
   }

   public Vec3 getPosition() {
      return this.worldPosition;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public Entity getEntityOrException() throws CommandSyntaxException {
      if (this.entity == null) {
         throw ERROR_NOT_ENTITY.create();
      } else {
         return this.entity;
      }
   }

   public ServerPlayer getPlayerOrException() throws CommandSyntaxException {
      Entity entity = this.entity;
      if (entity instanceof ServerPlayer) {
         return (ServerPlayer)entity;
      } else {
         throw ERROR_NOT_PLAYER.create();
      }
   }

   @Nullable
   public ServerPlayer getPlayer() {
      Entity entity = this.entity;
      ServerPlayer serverplayer1;
      if (entity instanceof ServerPlayer serverplayer) {
         serverplayer1 = serverplayer;
      } else {
         serverplayer1 = null;
      }

      return serverplayer1;
   }

   public boolean isPlayer() {
      return this.entity instanceof ServerPlayer;
   }

   public Vec2 getRotation() {
      return this.rotation;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityAnchorArgument.Anchor getAnchor() {
      return this.anchor;
   }

   public CommandSigningContext getSigningContext() {
      return this.signingContext;
   }

   public TaskChainer getChatMessageChainer() {
      return this.chatMessageChainer;
   }

   public IntConsumer getReturnValueConsumer() {
      return this.returnValueConsumer;
   }

   public boolean shouldFilterMessageTo(ServerPlayer p_243268_) {
      ServerPlayer serverplayer = this.getPlayer();
      if (p_243268_ == serverplayer) {
         return false;
      } else {
         return serverplayer != null && serverplayer.isTextFilteringEnabled() || p_243268_.isTextFilteringEnabled();
      }
   }

   public void sendChatMessage(OutgoingChatMessage p_251464_, boolean p_252146_, ChatType.Bound p_250406_) {
      if (!this.silent) {
         ServerPlayer serverplayer = this.getPlayer();
         if (serverplayer != null) {
            serverplayer.sendChatMessage(p_251464_, p_252146_, p_250406_);
         } else {
            this.source.sendSystemMessage(p_250406_.decorate(p_251464_.content()));
         }

      }
   }

   public void sendSystemMessage(Component p_243331_) {
      if (!this.silent) {
         ServerPlayer serverplayer = this.getPlayer();
         if (serverplayer != null) {
            serverplayer.sendSystemMessage(p_243331_);
         } else {
            this.source.sendSystemMessage(p_243331_);
         }

      }
   }

   public void sendSuccess(Supplier<Component> p_288979_, boolean p_289007_) {
      boolean flag = this.source.acceptsSuccess() && !this.silent;
      boolean flag1 = p_289007_ && this.source.shouldInformAdmins() && !this.silent;
      if (flag || flag1) {
         Component component = p_288979_.get();
         if (flag) {
            this.source.sendSystemMessage(component);
         }

         if (flag1) {
            this.broadcastToAdmins(component);
         }

      }
   }

   private void broadcastToAdmins(Component p_81367_) {
      Component component = Component.translatable("chat.type.admin", this.getDisplayName(), p_81367_).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
      if (this.server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
         for(ServerPlayer serverplayer : this.server.getPlayerList().getPlayers()) {
            if (serverplayer != this.source && this.server.getPlayerList().isOp(serverplayer.getGameProfile())) {
               serverplayer.sendSystemMessage(component);
            }
         }
      }

      if (this.source != this.server && this.server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
         this.server.sendSystemMessage(component);
      }

   }

   public void sendFailure(Component p_81353_) {
      if (this.source.acceptsFailure() && !this.silent) {
         this.source.sendSystemMessage(Component.empty().append(p_81353_).withStyle(ChatFormatting.RED));
      }

   }

   public void onCommandComplete(CommandContext<CommandSourceStack> p_81343_, boolean p_81344_, int p_81345_) {
      if (this.consumer != null) {
         this.consumer.onCommandComplete(p_81343_, p_81344_, p_81345_);
      }

   }

   public Collection<String> getOnlinePlayerNames() {
      return Lists.newArrayList(this.server.getPlayerNames());
   }

   public Collection<String> getAllTeams() {
      return this.server.getScoreboard().getTeamNames();
   }

   public Stream<ResourceLocation> getAvailableSounds() {
      return BuiltInRegistries.SOUND_EVENT.stream().map(SoundEvent::getLocation);
   }

   public Stream<ResourceLocation> getRecipeNames() {
      return this.server.getRecipeManager().getRecipeIds();
   }

   public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> p_212324_) {
      return Suggestions.empty();
   }

   public CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends Registry<?>> p_212330_, SharedSuggestionProvider.ElementSuggestionType p_212331_, SuggestionsBuilder p_212332_, CommandContext<?> p_212333_) {
      return this.registryAccess().registry(p_212330_).map((p_212328_) -> {
         this.suggestRegistryElements(p_212328_, p_212331_, p_212332_);
         return p_212332_.buildFuture();
      }).orElseGet(Suggestions::empty);
   }

   public Set<ResourceKey<Level>> levels() {
      return this.server.levelKeys();
   }

   public RegistryAccess registryAccess() {
      return this.server.registryAccess();
   }

   public FeatureFlagSet enabledFeatures() {
      return this.level.enabledFeatures();
   }
}
