package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class SculkShriekerBlockEntity extends BlockEntity implements GameEventListener.Holder<VibrationSystem.Listener>, VibrationSystem {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int WARNING_SOUND_RADIUS = 10;
   private static final int WARDEN_SPAWN_ATTEMPTS = 20;
   private static final int WARDEN_SPAWN_RANGE_XZ = 5;
   private static final int WARDEN_SPAWN_RANGE_Y = 6;
   private static final int DARKNESS_RADIUS = 40;
   private static final int SHRIEKING_TICKS = 90;
   private static final Int2ObjectMap<SoundEvent> SOUND_BY_LEVEL = Util.make(new Int2ObjectOpenHashMap<>(), (p_222866_) -> {
      p_222866_.put(1, SoundEvents.WARDEN_NEARBY_CLOSE);
      p_222866_.put(2, SoundEvents.WARDEN_NEARBY_CLOSER);
      p_222866_.put(3, SoundEvents.WARDEN_NEARBY_CLOSEST);
      p_222866_.put(4, SoundEvents.WARDEN_LISTENING_ANGRY);
   });
   private int warningLevel;
   private final VibrationSystem.User vibrationUser = new SculkShriekerBlockEntity.VibrationUser();
   private VibrationSystem.Data vibrationData = new VibrationSystem.Data();
   private final VibrationSystem.Listener vibrationListener = new VibrationSystem.Listener(this);

   public SculkShriekerBlockEntity(BlockPos p_222835_, BlockState p_222836_) {
      super(BlockEntityType.SCULK_SHRIEKER, p_222835_, p_222836_);
   }

   public VibrationSystem.Data getVibrationData() {
      return this.vibrationData;
   }

   public VibrationSystem.User getVibrationUser() {
      return this.vibrationUser;
   }

   public void load(CompoundTag p_222868_) {
      super.load(p_222868_);
      if (p_222868_.contains("warning_level", 99)) {
         this.warningLevel = p_222868_.getInt("warning_level");
      }

      if (p_222868_.contains("listener", 10)) {
         VibrationSystem.Data.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, p_222868_.getCompound("listener"))).resultOrPartial(LOGGER::error).ifPresent((p_281147_) -> {
            this.vibrationData = p_281147_;
         });
      }

   }

   protected void saveAdditional(CompoundTag p_222878_) {
      super.saveAdditional(p_222878_);
      p_222878_.putInt("warning_level", this.warningLevel);
      VibrationSystem.Data.CODEC.encodeStart(NbtOps.INSTANCE, this.vibrationData).resultOrPartial(LOGGER::error).ifPresent((p_222871_) -> {
         p_222878_.put("listener", p_222871_);
      });
   }

   @Nullable
   public static ServerPlayer tryGetPlayer(@Nullable Entity p_222862_) {
      if (p_222862_ instanceof ServerPlayer serverplayer1) {
         return serverplayer1;
      } else {
         if (p_222862_ != null) {
            LivingEntity $$6 = p_222862_.getControllingPassenger();
            if ($$6 instanceof ServerPlayer) {
               ServerPlayer serverplayer = (ServerPlayer)$$6;
               return serverplayer;
            }
         }

         if (p_222862_ instanceof Projectile projectile) {
            Entity entity = projectile.getOwner();
            if (entity instanceof ServerPlayer serverplayer3) {
               return serverplayer3;
            }
         }

         if (p_222862_ instanceof ItemEntity itementity) {
            Entity entity1 = itementity.getOwner();
            if (entity1 instanceof ServerPlayer serverplayer2) {
               return serverplayer2;
            }
         }

         return null;
      }
   }

   public void tryShriek(ServerLevel p_222842_, @Nullable ServerPlayer p_222843_) {
      if (p_222843_ != null) {
         BlockState blockstate = this.getBlockState();
         if (!blockstate.getValue(SculkShriekerBlock.SHRIEKING)) {
            this.warningLevel = 0;
            if (!this.canRespond(p_222842_) || this.tryToWarn(p_222842_, p_222843_)) {
               this.shriek(p_222842_, p_222843_);
            }
         }
      }
   }

   private boolean tryToWarn(ServerLevel p_222875_, ServerPlayer p_222876_) {
      OptionalInt optionalint = WardenSpawnTracker.tryWarn(p_222875_, this.getBlockPos(), p_222876_);
      optionalint.ifPresent((p_222838_) -> {
         this.warningLevel = p_222838_;
      });
      return optionalint.isPresent();
   }

   private void shriek(ServerLevel p_222845_, @Nullable Entity p_222846_) {
      BlockPos blockpos = this.getBlockPos();
      BlockState blockstate = this.getBlockState();
      p_222845_.setBlock(blockpos, blockstate.setValue(SculkShriekerBlock.SHRIEKING, Boolean.valueOf(true)), 2);
      p_222845_.scheduleTick(blockpos, blockstate.getBlock(), 90);
      p_222845_.levelEvent(3007, blockpos, 0);
      p_222845_.gameEvent(GameEvent.SHRIEK, blockpos, GameEvent.Context.of(p_222846_));
   }

   private boolean canRespond(ServerLevel p_222873_) {
      return this.getBlockState().getValue(SculkShriekerBlock.CAN_SUMMON) && p_222873_.getDifficulty() != Difficulty.PEACEFUL && p_222873_.getGameRules().getBoolean(GameRules.RULE_DO_WARDEN_SPAWNING);
   }

   public void tryRespond(ServerLevel p_222840_) {
      if (this.canRespond(p_222840_) && this.warningLevel > 0) {
         if (!this.trySummonWarden(p_222840_)) {
            this.playWardenReplySound(p_222840_);
         }

         Warden.applyDarknessAround(p_222840_, Vec3.atCenterOf(this.getBlockPos()), (Entity)null, 40);
      }

   }

   private void playWardenReplySound(Level p_281300_) {
      SoundEvent soundevent = SOUND_BY_LEVEL.get(this.warningLevel);
      if (soundevent != null) {
         BlockPos blockpos = this.getBlockPos();
         int i = blockpos.getX() + Mth.randomBetweenInclusive(p_281300_.random, -10, 10);
         int j = blockpos.getY() + Mth.randomBetweenInclusive(p_281300_.random, -10, 10);
         int k = blockpos.getZ() + Mth.randomBetweenInclusive(p_281300_.random, -10, 10);
         p_281300_.playSound((Player)null, (double)i, (double)j, (double)k, soundevent, SoundSource.HOSTILE, 5.0F, 1.0F);
      }

   }

   private boolean trySummonWarden(ServerLevel p_222881_) {
      return this.warningLevel < 4 ? false : SpawnUtil.trySpawnMob(EntityType.WARDEN, MobSpawnType.TRIGGERED, p_222881_, this.getBlockPos(), 20, 5, 6, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER).isPresent();
   }

   public VibrationSystem.Listener getListener() {
      return this.vibrationListener;
   }

   class VibrationUser implements VibrationSystem.User {
      private static final int LISTENER_RADIUS = 8;
      private final PositionSource positionSource = new BlockPositionSource(SculkShriekerBlockEntity.this.worldPosition);

      public VibrationUser() {
      }

      public int getListenerRadius() {
         return 8;
      }

      public PositionSource getPositionSource() {
         return this.positionSource;
      }

      public TagKey<GameEvent> getListenableEvents() {
         return GameEventTags.SHRIEKER_CAN_LISTEN;
      }

      public boolean canReceiveVibration(ServerLevel p_281256_, BlockPos p_281528_, GameEvent p_282632_, GameEvent.Context p_282914_) {
         return !SculkShriekerBlockEntity.this.getBlockState().getValue(SculkShriekerBlock.SHRIEKING) && SculkShriekerBlockEntity.tryGetPlayer(p_282914_.sourceEntity()) != null;
      }

      public void onReceiveVibration(ServerLevel p_283372_, BlockPos p_281679_, GameEvent p_282474_, @Nullable Entity p_282286_, @Nullable Entity p_281384_, float p_283119_) {
         SculkShriekerBlockEntity.this.tryShriek(p_283372_, SculkShriekerBlockEntity.tryGetPlayer(p_281384_ != null ? p_281384_ : p_282286_));
      }

      public void onDataChanged() {
         SculkShriekerBlockEntity.this.setChanged();
      }

      public boolean requiresAdjacentChunksToBeTicking() {
         return true;
      }
   }
}