package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;

public class JukeboxBlockEntity extends BlockEntity implements Clearable, ContainerSingleItem {
   private static final int SONG_END_PADDING = 20;
   private final NonNullList<ItemStack> items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
   private int ticksSinceLastEvent;
   private long tickCount;
   private long recordStartedTick;
   private boolean isPlaying;

   public JukeboxBlockEntity(BlockPos p_155613_, BlockState p_155614_) {
      super(BlockEntityType.JUKEBOX, p_155613_, p_155614_);
   }

   public void load(CompoundTag p_155616_) {
      super.load(p_155616_);
      if (p_155616_.contains("RecordItem", 10)) {
         this.items.set(0, ItemStack.of(p_155616_.getCompound("RecordItem")));
      }

      this.isPlaying = p_155616_.getBoolean("IsPlaying");
      this.recordStartedTick = p_155616_.getLong("RecordStartTick");
      this.tickCount = p_155616_.getLong("TickCount");
   }

   protected void saveAdditional(CompoundTag p_187507_) {
      super.saveAdditional(p_187507_);
      if (!this.getFirstItem().isEmpty()) {
         p_187507_.put("RecordItem", this.getFirstItem().save(new CompoundTag()));
      }

      p_187507_.putBoolean("IsPlaying", this.isPlaying);
      p_187507_.putLong("RecordStartTick", this.recordStartedTick);
      p_187507_.putLong("TickCount", this.tickCount);
   }

   public boolean isRecordPlaying() {
      return !this.getFirstItem().isEmpty() && this.isPlaying;
   }

   private void setHasRecordBlockState(@Nullable Entity p_273308_, boolean p_273038_) {
      if (this.level.getBlockState(this.getBlockPos()) == this.getBlockState()) {
         this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(JukeboxBlock.HAS_RECORD, Boolean.valueOf(p_273038_)), 2);
         this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(p_273308_, this.getBlockState()));
      }

   }

   @VisibleForTesting
   public void startPlaying() {
      this.recordStartedTick = this.tickCount;
      this.isPlaying = true;
      this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
      this.level.levelEvent((Player)null, 1010, this.getBlockPos(), Item.getId(this.getFirstItem().getItem()));
      this.setChanged();
   }

   private void stopPlaying() {
      this.isPlaying = false;
      this.level.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
      this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
      this.level.levelEvent(1011, this.getBlockPos(), 0);
      this.setChanged();
   }

   private void tick(Level p_273615_, BlockPos p_273143_, BlockState p_273372_) {
      ++this.ticksSinceLastEvent;
      if (this.isRecordPlaying()) {
         Item item = this.getFirstItem().getItem();
         if (item instanceof RecordItem) {
            RecordItem recorditem = (RecordItem)item;
            if (this.shouldRecordStopPlaying(recorditem)) {
               this.stopPlaying();
            } else if (this.shouldSendJukeboxPlayingEvent()) {
               this.ticksSinceLastEvent = 0;
               p_273615_.gameEvent(GameEvent.JUKEBOX_PLAY, p_273143_, GameEvent.Context.of(p_273372_));
               this.spawnMusicParticles(p_273615_, p_273143_);
            }
         }
      }

      ++this.tickCount;
   }

   private boolean shouldRecordStopPlaying(RecordItem p_273267_) {
      return this.tickCount >= this.recordStartedTick + (long)p_273267_.getLengthInTicks() + 20L;
   }

   private boolean shouldSendJukeboxPlayingEvent() {
      return this.ticksSinceLastEvent >= 20;
   }

   public ItemStack getItem(int p_273280_) {
      return this.items.get(p_273280_);
   }

   public ItemStack removeItem(int p_273514_, int p_273414_) {
      ItemStack itemstack = Objects.requireNonNullElse(this.items.get(p_273514_), ItemStack.EMPTY);
      this.items.set(p_273514_, ItemStack.EMPTY);
      if (!itemstack.isEmpty()) {
         this.setHasRecordBlockState((Entity)null, false);
         this.stopPlaying();
      }

      return itemstack;
   }

   public void setItem(int p_273461_, ItemStack p_273584_) {
      if (p_273584_.is(ItemTags.MUSIC_DISCS) && this.level != null) {
         this.items.set(p_273461_, p_273584_);
         this.setHasRecordBlockState((Entity)null, true);
         this.startPlaying();
      }

   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean stillValid(Player p_273466_) {
      return Container.stillValidBlockEntity(this, p_273466_);
   }

   public boolean canPlaceItem(int p_273369_, ItemStack p_273689_) {
      return p_273689_.is(ItemTags.MUSIC_DISCS) && this.getItem(p_273369_).isEmpty();
   }

   public boolean canTakeItem(Container p_273497_, int p_273168_, ItemStack p_273785_) {
      return p_273497_.hasAnyMatching(ItemStack::isEmpty);
   }

   private void spawnMusicParticles(Level p_270782_, BlockPos p_270940_) {
      if (p_270782_ instanceof ServerLevel serverlevel) {
         Vec3 vec3 = Vec3.atBottomCenterOf(p_270940_).add(0.0D, (double)1.2F, 0.0D);
         float f = (float)p_270782_.getRandom().nextInt(4) / 24.0F;
         serverlevel.sendParticles(ParticleTypes.NOTE, vec3.x(), vec3.y(), vec3.z(), 0, (double)f, 0.0D, 0.0D, 1.0D);
      }

   }

   public void popOutRecord() {
      if (this.level != null && !this.level.isClientSide) {
         BlockPos blockpos = this.getBlockPos();
         ItemStack itemstack = this.getFirstItem();
         if (!itemstack.isEmpty()) {
            this.removeFirstItem();
            Vec3 vec3 = Vec3.atLowerCornerWithOffset(blockpos, 0.5D, 1.01D, 0.5D).offsetRandom(this.level.random, 0.7F);
            ItemStack itemstack1 = itemstack.copy();
            ItemEntity itementity = new ItemEntity(this.level, vec3.x(), vec3.y(), vec3.z(), itemstack1);
            itementity.setDefaultPickUpDelay();
            this.level.addFreshEntity(itementity);
         }
      }
   }

   public static void playRecordTick(Level p_239938_, BlockPos p_239939_, BlockState p_239940_, JukeboxBlockEntity p_239941_) {
      p_239941_.tick(p_239938_, p_239939_, p_239940_);
   }

   @VisibleForTesting
   public void setRecordWithoutPlaying(ItemStack p_272693_) {
      this.items.set(0, p_272693_);
      this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
      this.setChanged();
   }
}