package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class MultiPlayerGameMode {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Minecraft minecraft;
   private final ClientPacketListener connection;
   private BlockPos destroyBlockPos = new BlockPos(-1, -1, -1);
   private ItemStack destroyingItem = ItemStack.EMPTY;
   private float destroyProgress;
   private float destroyTicks;
   private int destroyDelay;
   private boolean isDestroying;
   private GameType localPlayerMode = GameType.DEFAULT_MODE;
   @Nullable
   private GameType previousLocalPlayerMode;
   private int carriedIndex;

   public MultiPlayerGameMode(Minecraft p_105203_, ClientPacketListener p_105204_) {
      this.minecraft = p_105203_;
      this.connection = p_105204_;
   }

   public void adjustPlayer(Player p_105222_) {
      this.localPlayerMode.updatePlayerAbilities(p_105222_.getAbilities());
   }

   public void setLocalMode(GameType p_171806_, @Nullable GameType p_171807_) {
      this.localPlayerMode = p_171806_;
      this.previousLocalPlayerMode = p_171807_;
      this.localPlayerMode.updatePlayerAbilities(this.minecraft.player.getAbilities());
   }

   public void setLocalMode(GameType p_105280_) {
      if (p_105280_ != this.localPlayerMode) {
         this.previousLocalPlayerMode = this.localPlayerMode;
      }

      this.localPlayerMode = p_105280_;
      this.localPlayerMode.updatePlayerAbilities(this.minecraft.player.getAbilities());
   }

   public boolean canHurtPlayer() {
      return this.localPlayerMode.isSurvival();
   }

   public boolean destroyBlock(BlockPos p_105268_) {
      if (minecraft.player.getMainHandItem().onBlockStartBreak(p_105268_, minecraft.player)) return false;
      if (this.minecraft.player.blockActionRestricted(this.minecraft.level, p_105268_, this.localPlayerMode)) {
         return false;
      } else {
         Level level = this.minecraft.level;
         BlockState blockstate = level.getBlockState(p_105268_);
         if (!this.minecraft.player.getMainHandItem().getItem().canAttackBlock(blockstate, level, p_105268_, this.minecraft.player)) {
            return false;
         } else {
            Block block = blockstate.getBlock();
            if (block instanceof GameMasterBlock && !this.minecraft.player.canUseGameMasterBlocks()) {
               return false;
            } else if (blockstate.isAir()) {
               return false;
            } else {
               FluidState fluidstate = level.getFluidState(p_105268_);
               boolean flag = blockstate.onDestroyedByPlayer(level, p_105268_, minecraft.player, false, fluidstate);
               if (flag) {
                  block.destroy(level, p_105268_, blockstate);
               }

               return flag;
            }
         }
      }
   }

   public boolean startDestroyBlock(BlockPos p_105270_, Direction p_105271_) {
      if (this.minecraft.player.blockActionRestricted(this.minecraft.level, p_105270_, this.localPlayerMode)) {
         return false;
      } else if (!this.minecraft.level.getWorldBorder().isWithinBounds(p_105270_)) {
         return false;
      } else {
         if (this.localPlayerMode.isCreative()) {
            BlockState blockstate = this.minecraft.level.getBlockState(p_105270_);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, p_105270_, blockstate, 1.0F);
            this.startPrediction(this.minecraft.level, (p_233757_) -> {
               if (!net.minecraftforge.common.ForgeHooks.onLeftClickBlock(this.minecraft.player, p_105270_, p_105271_, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK).isCanceled())
               this.destroyBlock(p_105270_);
               return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, p_105270_, p_105271_, p_233757_);
            });
            this.destroyDelay = 5;
         } else if (!this.isDestroying || !this.sameDestroyTarget(p_105270_)) {
            if (this.isDestroying) {
               this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, p_105271_));
            }
            net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event = net.minecraftforge.common.ForgeHooks.onLeftClickBlock(this.minecraft.player, p_105270_, p_105271_, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK);

            BlockState blockstate1 = this.minecraft.level.getBlockState(p_105270_);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, p_105270_, blockstate1, 0.0F);
            this.startPrediction(this.minecraft.level, (p_233728_) -> {
               boolean flag = !blockstate1.isAir();
               if (flag && this.destroyProgress == 0.0F) {
                  if (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY)
                  blockstate1.attack(this.minecraft.level, p_105270_, this.minecraft.player);
               }

               ServerboundPlayerActionPacket packet = new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, p_105270_, p_105271_, p_233728_);
               if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY) return packet;
               if (flag && blockstate1.getDestroyProgress(this.minecraft.player, this.minecraft.player.level(), p_105270_) >= 1.0F) {
                  this.destroyBlock(p_105270_);
               } else {
                  this.isDestroying = true;
                  this.destroyBlockPos = p_105270_;
                  this.destroyingItem = this.minecraft.player.getMainHandItem();
                  this.destroyProgress = 0.0F;
                  this.destroyTicks = 0.0F;
                  this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, this.getDestroyStage());
               }

               return packet;
            });
         }

         return true;
      }
   }

   public void stopDestroyBlock() {
      if (this.isDestroying) {
         BlockState blockstate = this.minecraft.level.getBlockState(this.destroyBlockPos);
         this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, this.destroyBlockPos, blockstate, -1.0F);
         this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, Direction.DOWN));
         this.isDestroying = false;
         this.destroyProgress = 0.0F;
         this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, -1);
         this.minecraft.player.resetAttackStrengthTicker();
      }

   }

   public boolean continueDestroyBlock(BlockPos p_105284_, Direction p_105285_) {
      this.ensureHasSentCarriedItem();
      if (this.destroyDelay > 0) {
         --this.destroyDelay;
         return true;
      } else if (this.localPlayerMode.isCreative() && this.minecraft.level.getWorldBorder().isWithinBounds(p_105284_)) {
         this.destroyDelay = 5;
         BlockState blockstate1 = this.minecraft.level.getBlockState(p_105284_);
         this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, p_105284_, blockstate1, 1.0F);
         this.startPrediction(this.minecraft.level, (p_233753_) -> {
            if (!net.minecraftforge.common.ForgeHooks.onLeftClickBlock(this.minecraft.player, p_105284_, p_105285_, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK).isCanceled())
            this.destroyBlock(p_105284_);
            return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, p_105284_, p_105285_, p_233753_);
         });
         return true;
      } else if (this.sameDestroyTarget(p_105284_)) {
         BlockState blockstate = this.minecraft.level.getBlockState(p_105284_);
         if (blockstate.isAir()) {
            this.isDestroying = false;
            return false;
         } else {
            this.destroyProgress += blockstate.getDestroyProgress(this.minecraft.player, this.minecraft.player.level(), p_105284_);
            if (this.destroyTicks % 4.0F == 0.0F) {
               SoundType soundtype = blockstate.getSoundType(this.minecraft.level, p_105284_, this.minecraft.player);
               this.minecraft.getSoundManager().play(new SimpleSoundInstance(soundtype.getHitSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F, SoundInstance.createUnseededRandom(), p_105284_));
            }

            ++this.destroyTicks;
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, p_105284_, blockstate, Mth.clamp(this.destroyProgress, 0.0F, 1.0F));
            if (net.minecraftforge.common.ForgeHooks.onClientMineHold(this.minecraft.player, p_105284_, p_105285_).getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY) return true;
            if (this.destroyProgress >= 1.0F) {
               this.isDestroying = false;
               this.startPrediction(this.minecraft.level, (p_233739_) -> {
                  this.destroyBlock(p_105284_);
                  return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, p_105284_, p_105285_, p_233739_);
               });
               this.destroyProgress = 0.0F;
               this.destroyTicks = 0.0F;
               this.destroyDelay = 5;
            }

            this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, this.getDestroyStage());
            return true;
         }
      } else {
         return this.startDestroyBlock(p_105284_, p_105285_);
      }
   }

   private void startPrediction(ClientLevel p_233730_, PredictiveAction p_233731_) {
      try (BlockStatePredictionHandler blockstatepredictionhandler = p_233730_.getBlockStatePredictionHandler().startPredicting()) {
         int i = blockstatepredictionhandler.currentSequence();
         Packet<ServerGamePacketListener> packet = p_233731_.predict(i);
         this.connection.send(packet);
      }

   }

   public float getPickRange() {
      return (float) this.minecraft.player.getBlockReach();
   }

   public void tick() {
      this.ensureHasSentCarriedItem();
      if (this.connection.getConnection().isConnected()) {
         this.connection.getConnection().tick();
      } else {
         this.connection.getConnection().handleDisconnection();
      }

   }

   private boolean sameDestroyTarget(BlockPos p_105282_) {
      ItemStack itemstack = this.minecraft.player.getMainHandItem();
      return p_105282_.equals(this.destroyBlockPos) && ItemStack.isSameItemSameTags(itemstack, this.destroyingItem) && !destroyingItem.shouldCauseBlockBreakReset(itemstack);
   }

   private void ensureHasSentCarriedItem() {
      int i = this.minecraft.player.getInventory().selected;
      if (i != this.carriedIndex) {
         this.carriedIndex = i;
         this.connection.send(new ServerboundSetCarriedItemPacket(this.carriedIndex));
      }

   }

   public InteractionResult useItemOn(LocalPlayer p_233733_, InteractionHand p_233734_, BlockHitResult p_233735_) {
      this.ensureHasSentCarriedItem();
      if (!this.minecraft.level.getWorldBorder().isWithinBounds(p_233735_.getBlockPos())) {
         return InteractionResult.FAIL;
      } else {
         MutableObject<InteractionResult> mutableobject = new MutableObject<>();
         this.startPrediction(this.minecraft.level, (p_233745_) -> {
            mutableobject.setValue(this.performUseItemOn(p_233733_, p_233734_, p_233735_));
            return new ServerboundUseItemOnPacket(p_233734_, p_233735_, p_233745_);
         });
         return mutableobject.getValue();
      }
   }

   private InteractionResult performUseItemOn(LocalPlayer p_233747_, InteractionHand p_233748_, BlockHitResult p_233749_) {
      BlockPos blockpos = p_233749_.getBlockPos();
      ItemStack itemstack = p_233747_.getItemInHand(p_233748_);
      net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks.onRightClickBlock(p_233747_, p_233748_, blockpos, p_233749_);
      if (event.isCanceled()) {
         return event.getCancellationResult();
      }
      if (this.localPlayerMode == GameType.SPECTATOR) {
         return InteractionResult.SUCCESS;
      } else {
         UseOnContext useoncontext = new UseOnContext(p_233747_, p_233748_, p_233749_);
         if (event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
            InteractionResult result = itemstack.onItemUseFirst(useoncontext);
            if (result != InteractionResult.PASS) {
               return result;
            }
         }
         boolean flag = !p_233747_.getMainHandItem().doesSneakBypassUse(p_233747_.level(), blockpos, p_233747_) || !p_233747_.getOffhandItem().doesSneakBypassUse(p_233747_.level(), blockpos, p_233747_);
         boolean flag1 = p_233747_.isSecondaryUseActive() && flag;
            BlockState blockstate = this.minecraft.level.getBlockState(blockpos);
            if (!this.connection.isFeatureEnabled(blockstate.getBlock().requiredFeatures())) {
               return InteractionResult.FAIL;
            }

         if (event.getUseBlock() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY && !flag1)) {
            InteractionResult interactionresult = blockstate.use(this.minecraft.level, p_233747_, p_233748_, p_233749_);
            if (interactionresult.consumesAction()) {
               return interactionresult;
            }
         }

         if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY) {
            return InteractionResult.PASS;
         }
         if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (!itemstack.isEmpty() && !p_233747_.getCooldowns().isOnCooldown(itemstack.getItem()))) {
            InteractionResult interactionresult1;
            if (this.localPlayerMode.isCreative()) {
               int i = itemstack.getCount();
               interactionresult1 = itemstack.useOn(useoncontext);
               itemstack.setCount(i);
            } else {
               interactionresult1 = itemstack.useOn(useoncontext);
            }

            return interactionresult1;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public InteractionResult useItem(Player p_233722_, InteractionHand p_233723_) {
      if (this.localPlayerMode == GameType.SPECTATOR) {
         return InteractionResult.PASS;
      } else {
         this.ensureHasSentCarriedItem();
         this.connection.send(new ServerboundMovePlayerPacket.PosRot(p_233722_.getX(), p_233722_.getY(), p_233722_.getZ(), p_233722_.getYRot(), p_233722_.getXRot(), p_233722_.onGround()));
         MutableObject<InteractionResult> mutableobject = new MutableObject<>();
         this.startPrediction(this.minecraft.level, (p_233720_) -> {
            ServerboundUseItemPacket serverbounduseitempacket = new ServerboundUseItemPacket(p_233723_, p_233720_);
            ItemStack itemstack = p_233722_.getItemInHand(p_233723_);
            if (p_233722_.getCooldowns().isOnCooldown(itemstack.getItem())) {
               mutableobject.setValue(InteractionResult.PASS);
               return serverbounduseitempacket;
            } else {
               InteractionResult cancelResult = net.minecraftforge.common.ForgeHooks.onItemRightClick(p_233722_, p_233723_);
               if (cancelResult != null) {
                  mutableobject.setValue(cancelResult);
                  return serverbounduseitempacket;
               }
               InteractionResultHolder<ItemStack> interactionresultholder = itemstack.use(this.minecraft.level, p_233722_, p_233723_);
               ItemStack itemstack1 = interactionresultholder.getObject();
               if (itemstack1 != itemstack) {
                  p_233722_.setItemInHand(p_233723_, itemstack1);
                  if (itemstack1.isEmpty())
                     net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(p_233722_, itemstack, p_233723_);
               }

               mutableobject.setValue(interactionresultholder.getResult());
               return serverbounduseitempacket;
            }
         });
         return mutableobject.getValue();
      }
   }

   public LocalPlayer createPlayer(ClientLevel p_105247_, StatsCounter p_105248_, ClientRecipeBook p_105249_) {
      return this.createPlayer(p_105247_, p_105248_, p_105249_, false, false);
   }

   public LocalPlayer createPlayer(ClientLevel p_105251_, StatsCounter p_105252_, ClientRecipeBook p_105253_, boolean p_105254_, boolean p_105255_) {
      return new LocalPlayer(this.minecraft, p_105251_, this.connection, p_105252_, p_105253_, p_105254_, p_105255_);
   }

   public void attack(Player p_105224_, Entity p_105225_) {
      this.ensureHasSentCarriedItem();
      this.connection.send(ServerboundInteractPacket.createAttackPacket(p_105225_, p_105224_.isShiftKeyDown()));
      if (this.localPlayerMode != GameType.SPECTATOR) {
         p_105224_.attack(p_105225_);
         p_105224_.resetAttackStrengthTicker();
      }

   }

   public InteractionResult interact(Player p_105227_, Entity p_105228_, InteractionHand p_105229_) {
      this.ensureHasSentCarriedItem();
      this.connection.send(ServerboundInteractPacket.createInteractionPacket(p_105228_, p_105227_.isShiftKeyDown(), p_105229_));
      return this.localPlayerMode == GameType.SPECTATOR ? InteractionResult.PASS : p_105227_.interactOn(p_105228_, p_105229_);
   }

   public InteractionResult interactAt(Player p_105231_, Entity p_105232_, EntityHitResult p_105233_, InteractionHand p_105234_) {
      this.ensureHasSentCarriedItem();
      Vec3 vec3 = p_105233_.getLocation().subtract(p_105232_.getX(), p_105232_.getY(), p_105232_.getZ());
      this.connection.send(ServerboundInteractPacket.createInteractionPacket(p_105232_, p_105231_.isShiftKeyDown(), p_105234_, vec3));
      if (this.localPlayerMode == GameType.SPECTATOR) return InteractionResult.PASS; // don't fire for spectators to match non-specific EntityInteract
      InteractionResult cancelResult = net.minecraftforge.common.ForgeHooks.onInteractEntityAt(p_105231_, p_105232_, p_105233_, p_105234_);
      if(cancelResult != null) return cancelResult;
      return this.localPlayerMode == GameType.SPECTATOR ? InteractionResult.PASS : p_105232_.interactAt(p_105231_, vec3, p_105234_);
   }

   public void handleInventoryMouseClick(int p_171800_, int p_171801_, int p_171802_, ClickType p_171803_, Player p_171804_) {
      AbstractContainerMenu abstractcontainermenu = p_171804_.containerMenu;
      if (p_171800_ != abstractcontainermenu.containerId) {
         LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", p_171800_, abstractcontainermenu.containerId);
      } else {
         NonNullList<Slot> nonnulllist = abstractcontainermenu.slots;
         int i = nonnulllist.size();
         List<ItemStack> list = Lists.newArrayListWithCapacity(i);

         for(Slot slot : nonnulllist) {
            list.add(slot.getItem().copy());
         }

         abstractcontainermenu.clicked(p_171801_, p_171802_, p_171803_, p_171804_);
         Int2ObjectMap<ItemStack> int2objectmap = new Int2ObjectOpenHashMap<>();

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack = list.get(j);
            ItemStack itemstack1 = nonnulllist.get(j).getItem();
            if (!ItemStack.matches(itemstack, itemstack1)) {
               int2objectmap.put(j, itemstack1.copy());
            }
         }

         this.connection.send(new ServerboundContainerClickPacket(p_171800_, abstractcontainermenu.getStateId(), p_171801_, p_171802_, p_171803_, abstractcontainermenu.getCarried().copy(), int2objectmap));
      }
   }

   public void handlePlaceRecipe(int p_105218_, Recipe<?> p_105219_, boolean p_105220_) {
      this.connection.send(new ServerboundPlaceRecipePacket(p_105218_, p_105219_, p_105220_));
   }

   public void handleInventoryButtonClick(int p_105209_, int p_105210_) {
      this.connection.send(new ServerboundContainerButtonClickPacket(p_105209_, p_105210_));
   }

   public void handleCreativeModeItemAdd(ItemStack p_105242_, int p_105243_) {
      if (this.localPlayerMode.isCreative() && this.connection.isFeatureEnabled(p_105242_.getItem().requiredFeatures())) {
         this.connection.send(new ServerboundSetCreativeModeSlotPacket(p_105243_, p_105242_));
      }

   }

   public void handleCreativeModeItemDrop(ItemStack p_105240_) {
      if (this.localPlayerMode.isCreative() && !p_105240_.isEmpty() && this.connection.isFeatureEnabled(p_105240_.getItem().requiredFeatures())) {
         this.connection.send(new ServerboundSetCreativeModeSlotPacket(-1, p_105240_));
      }

   }

   public void releaseUsingItem(Player p_105278_) {
      this.ensureHasSentCarriedItem();
      this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
      p_105278_.releaseUsingItem();
   }

   public boolean hasExperience() {
      return this.localPlayerMode.isSurvival();
   }

   public boolean hasMissTime() {
      return !this.localPlayerMode.isCreative();
   }

   public boolean hasInfiniteItems() {
      return this.localPlayerMode.isCreative();
   }

   public boolean hasFarPickRange() {
      return this.localPlayerMode.isCreative();
   }

   public boolean isServerControlledInventory() {
      return this.minecraft.player.isPassenger() && this.minecraft.player.getVehicle() instanceof HasCustomInventoryScreen;
   }

   public boolean isAlwaysFlying() {
      return this.localPlayerMode == GameType.SPECTATOR;
   }

   @Nullable
   public GameType getPreviousPlayerMode() {
      return this.previousLocalPlayerMode;
   }

   public GameType getPlayerMode() {
      return this.localPlayerMode;
   }

   public boolean isDestroying() {
      return this.isDestroying;
   }

   public int getDestroyStage() {
      return this.destroyProgress > 0.0F ? (int)(this.destroyProgress * 10.0F) : -1;
   }

   public void handlePickItem(int p_105207_) {
      this.connection.send(new ServerboundPickItemPacket(p_105207_));
   }
}
