package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.FilteredText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class SignBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_TEXT_LINE_WIDTH = 90;
   private static final int TEXT_LINE_HEIGHT = 10;
   @Nullable
   private UUID playerWhoMayEdit;
   private SignText frontText = this.createDefaultSignText();
   private SignText backText = this.createDefaultSignText();
   private boolean isWaxed;

   public SignBlockEntity(BlockPos p_155700_, BlockState p_155701_) {
      this(BlockEntityType.SIGN, p_155700_, p_155701_);
   }

   public SignBlockEntity(BlockEntityType p_249609_, BlockPos p_248914_, BlockState p_249550_) {
      super(p_249609_, p_248914_, p_249550_);
   }

   protected SignText createDefaultSignText() {
      return new SignText();
   }

   public boolean isFacingFrontText(Player p_277382_) {
      Block $$2 = this.getBlockState().getBlock();
      if ($$2 instanceof SignBlock signblock) {
         Vec3 vec3 = signblock.getSignHitboxCenterPosition(this.getBlockState());
         double d0 = p_277382_.getX() - ((double)this.getBlockPos().getX() + vec3.x);
         double d1 = p_277382_.getZ() - ((double)this.getBlockPos().getZ() + vec3.z);
         float f = signblock.getYRotationDegrees(this.getBlockState());
         float f1 = (float)(Mth.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
         return Mth.degreesDifferenceAbs(f, f1) <= 90.0F;
      } else {
         return false;
      }
   }

   public SignText getTextFacingPlayer(Player p_277444_) {
      return this.getText(this.isFacingFrontText(p_277444_));
   }

   public SignText getText(boolean p_277918_) {
      return p_277918_ ? this.frontText : this.backText;
   }

   public SignText getFrontText() {
      return this.frontText;
   }

   public SignText getBackText() {
      return this.backText;
   }

   public int getTextLineHeight() {
      return 10;
   }

   public int getMaxTextLineWidth() {
      return 90;
   }

   protected void saveAdditional(CompoundTag p_187515_) {
      super.saveAdditional(p_187515_);
      SignText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.frontText).resultOrPartial(LOGGER::error).ifPresent((p_277417_) -> {
         p_187515_.put("front_text", p_277417_);
      });
      SignText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.backText).resultOrPartial(LOGGER::error).ifPresent((p_277389_) -> {
         p_187515_.put("back_text", p_277389_);
      });
      p_187515_.putBoolean("is_waxed", this.isWaxed);
   }

   public void load(CompoundTag p_155716_) {
      super.load(p_155716_);
      if (p_155716_.contains("front_text")) {
         SignText.DIRECT_CODEC.parse(NbtOps.INSTANCE, p_155716_.getCompound("front_text")).resultOrPartial(LOGGER::error).ifPresent((p_278212_) -> {
            this.frontText = this.loadLines(p_278212_);
         });
      }

      if (p_155716_.contains("back_text")) {
         SignText.DIRECT_CODEC.parse(NbtOps.INSTANCE, p_155716_.getCompound("back_text")).resultOrPartial(LOGGER::error).ifPresent((p_278213_) -> {
            this.backText = this.loadLines(p_278213_);
         });
      }

      this.isWaxed = p_155716_.getBoolean("is_waxed");
   }

   private SignText loadLines(SignText p_278305_) {
      for(int i = 0; i < 4; ++i) {
         Component component = this.loadLine(p_278305_.getMessage(i, false));
         Component component1 = this.loadLine(p_278305_.getMessage(i, true));
         p_278305_ = p_278305_.setMessage(i, component, component1);
      }

      return p_278305_;
   }

   private Component loadLine(Component p_278307_) {
      Level level = this.level;
      if (level instanceof ServerLevel serverlevel) {
         try {
            return ComponentUtils.updateForEntity(createCommandSourceStack((Player)null, serverlevel, this.worldPosition), p_278307_, (Entity)null, 0);
         } catch (CommandSyntaxException commandsyntaxexception) {
         }
      }

      return p_278307_;
   }

   public void updateSignText(Player p_278048_, boolean p_278103_, List<FilteredText> p_277990_) {
      if (!this.isWaxed() && p_278048_.getUUID().equals(this.getPlayerWhoMayEdit()) && this.level != null) {
         this.updateText((p_277776_) -> {
            return this.setMessages(p_278048_, p_277990_, p_277776_);
         }, p_278103_);
         this.setAllowedPlayerEditor((UUID)null);
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      } else {
         LOGGER.warn("Player {} just tried to change non-editable sign", (Object)p_278048_.getName().getString());
      }
   }

   public boolean updateText(UnaryOperator<SignText> p_277877_, boolean p_277426_) {
      SignText signtext = this.getText(p_277426_);
      return this.setText(p_277877_.apply(signtext), p_277426_);
   }

   private SignText setMessages(Player p_277396_, List<FilteredText> p_277744_, SignText p_277359_) {
      for(int i = 0; i < p_277744_.size(); ++i) {
         FilteredText filteredtext = p_277744_.get(i);
         Style style = p_277359_.getMessage(i, p_277396_.isTextFilteringEnabled()).getStyle();
         if (p_277396_.isTextFilteringEnabled()) {
            p_277359_ = p_277359_.setMessage(i, Component.literal(filteredtext.filteredOrEmpty()).setStyle(style));
         } else {
            p_277359_ = p_277359_.setMessage(i, Component.literal(filteredtext.raw()).setStyle(style), Component.literal(filteredtext.filteredOrEmpty()).setStyle(style));
         }
      }

      return p_277359_;
   }

   public boolean setText(SignText p_277733_, boolean p_277720_) {
      return p_277720_ ? this.setFrontText(p_277733_) : this.setBackText(p_277733_);
   }

   private boolean setBackText(SignText p_277777_) {
      if (p_277777_ != this.backText) {
         this.backText = p_277777_;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   private boolean setFrontText(SignText p_278038_) {
      if (p_278038_ != this.frontText) {
         this.frontText = p_278038_;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   public boolean canExecuteClickCommands(boolean p_278276_, Player p_278240_) {
      return this.isWaxed() && this.getText(p_278276_).hasAnyClickCommands(p_278240_);
   }

   public boolean executeClickCommandsIfPresent(Player p_279304_, Level p_279201_, BlockPos p_278282_, boolean p_278254_) {
      boolean flag = false;

      for(Component component : this.getText(p_278254_).getMessages(p_279304_.isTextFilteringEnabled())) {
         Style style = component.getStyle();
         ClickEvent clickevent = style.getClickEvent();
         if (clickevent != null && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            p_279304_.getServer().getCommands().performPrefixedCommand(createCommandSourceStack(p_279304_, p_279201_, p_278282_), clickevent.getValue());
            flag = true;
         }
      }

      return flag;
   }

   private static CommandSourceStack createCommandSourceStack(@Nullable Player p_279428_, Level p_279359_, BlockPos p_279430_) {
      String s = p_279428_ == null ? "Sign" : p_279428_.getName().getString();
      Component component = (Component)(p_279428_ == null ? Component.literal("Sign") : p_279428_.getDisplayName());
      return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(p_279430_), Vec2.ZERO, (ServerLevel)p_279359_, 2, s, component, p_279359_.getServer(), p_279428_);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public void setAllowedPlayerEditor(@Nullable UUID p_155714_) {
      this.playerWhoMayEdit = p_155714_;
   }

   @Nullable
   public UUID getPlayerWhoMayEdit() {
      return this.playerWhoMayEdit;
   }

   private void markUpdated() {
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public boolean isWaxed() {
      return this.isWaxed;
   }

   public boolean setWaxed(boolean p_277344_) {
      if (this.isWaxed != p_277344_) {
         this.isWaxed = p_277344_;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   public boolean playerIsTooFarAwayToEdit(UUID p_277978_) {
      Player player = this.level.getPlayerByUUID(p_277978_);
      return player == null || player.distanceToSqr((double)this.getBlockPos().getX(), (double)this.getBlockPos().getY(), (double)this.getBlockPos().getZ()) > 64.0D;
   }

   public static void tick(Level p_277662_, BlockPos p_278050_, BlockState p_277927_, SignBlockEntity p_277928_) {
      UUID uuid = p_277928_.getPlayerWhoMayEdit();
      if (uuid != null) {
         p_277928_.clearInvalidPlayerWhoMayEdit(p_277928_, p_277662_, uuid);
      }

   }

   private void clearInvalidPlayerWhoMayEdit(SignBlockEntity p_277656_, Level p_277853_, UUID p_277849_) {
      if (p_277656_.playerIsTooFarAwayToEdit(p_277849_)) {
         p_277656_.setAllowedPlayerEditor((UUID)null);
      }

   }

   @Override
   public net.minecraft.world.phys.AABB getRenderBoundingBox() {
      return new net.minecraft.world.phys.AABB(this.getBlockPos(), this.getBlockPos().offset(1, 1, 1));
   }
}
