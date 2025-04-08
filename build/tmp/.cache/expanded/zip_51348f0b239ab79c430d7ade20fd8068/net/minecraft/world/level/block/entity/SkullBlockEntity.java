package net.minecraft.world.level.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockEntity extends BlockEntity {
   public static final String TAG_SKULL_OWNER = "SkullOwner";
   public static final String TAG_NOTE_BLOCK_SOUND = "note_block_sound";
   @Nullable
   private static GameProfileCache profileCache;
   @Nullable
   private static MinecraftSessionService sessionService;
   @Nullable
   private static Executor mainThreadExecutor;
   @Nullable
   private GameProfile owner;
   @Nullable
   private ResourceLocation noteBlockSound;
   private int animationTickCount;
   private boolean isAnimating;

   public SkullBlockEntity(BlockPos p_155731_, BlockState p_155732_) {
      super(BlockEntityType.SKULL, p_155731_, p_155732_);
   }

   public static void setup(Services p_222886_, Executor p_222887_) {
      profileCache = p_222886_.profileCache();
      sessionService = p_222886_.sessionService();
      mainThreadExecutor = p_222887_;
   }

   public static void clear() {
      profileCache = null;
      sessionService = null;
      mainThreadExecutor = null;
   }

   protected void saveAdditional(CompoundTag p_187518_) {
      super.saveAdditional(p_187518_);
      if (this.owner != null) {
         CompoundTag compoundtag = new CompoundTag();
         NbtUtils.writeGameProfile(compoundtag, this.owner);
         p_187518_.put("SkullOwner", compoundtag);
      }

      if (this.noteBlockSound != null) {
         p_187518_.putString("note_block_sound", this.noteBlockSound.toString());
      }

   }

   public void load(CompoundTag p_155745_) {
      super.load(p_155745_);
      if (p_155745_.contains("SkullOwner", 10)) {
         this.setOwner(NbtUtils.readGameProfile(p_155745_.getCompound("SkullOwner")));
      } else if (p_155745_.contains("ExtraType", 8)) {
         String s = p_155745_.getString("ExtraType");
         if (!StringUtil.isNullOrEmpty(s)) {
            this.setOwner(new GameProfile((UUID)null, s));
         }
      }

      if (p_155745_.contains("note_block_sound", 8)) {
         this.noteBlockSound = ResourceLocation.tryParse(p_155745_.getString("note_block_sound"));
      }

   }

   public static void animation(Level p_261710_, BlockPos p_262153_, BlockState p_262021_, SkullBlockEntity p_261594_) {
      if (p_261710_.hasNeighborSignal(p_262153_)) {
         p_261594_.isAnimating = true;
         ++p_261594_.animationTickCount;
      } else {
         p_261594_.isAnimating = false;
      }

   }

   public float getAnimation(float p_262053_) {
      return this.isAnimating ? (float)this.animationTickCount + p_262053_ : (float)this.animationTickCount;
   }

   @Nullable
   public GameProfile getOwnerProfile() {
      return this.owner;
   }

   @Nullable
   public ResourceLocation getNoteBlockSound() {
      return this.noteBlockSound;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public void setOwner(@Nullable GameProfile p_59770_) {
      synchronized(this) {
         this.owner = p_59770_;
      }

      this.updateOwnerProfile();
   }

   private void updateOwnerProfile() {
      updateGameprofile(this.owner, (p_155747_) -> {
         this.owner = p_155747_;
         this.setChanged();
      });
   }

   public static void updateGameprofile(@Nullable GameProfile p_155739_, Consumer<GameProfile> p_155740_) {
      if (p_155739_ != null && !StringUtil.isNullOrEmpty(p_155739_.getName()) && (!p_155739_.isComplete() || !p_155739_.getProperties().containsKey("textures")) && profileCache != null && sessionService != null) {
         profileCache.getAsync(p_155739_.getName(), (p_182470_) -> {
            Util.backgroundExecutor().execute(() -> {
               Util.ifElse(p_182470_, (p_276255_) -> {
                  Property property = Iterables.getFirst(p_276255_.getProperties().get("textures"), (Property)null);
                  if (property == null) {
                     MinecraftSessionService minecraftsessionservice = sessionService;
                     if (minecraftsessionservice == null) {
                        return;
                     }

                     p_276255_ = minecraftsessionservice.fillProfileProperties(p_276255_, true);
                  }

                  GameProfile gameprofile = p_276255_;
                  Executor executor = mainThreadExecutor;
                  if (executor != null) {
                     executor.execute(() -> {
                        GameProfileCache gameprofilecache = profileCache;
                        if (gameprofilecache != null) {
                           gameprofilecache.add(gameprofile);
                           p_155740_.accept(gameprofile);
                        }

                     });
                  }

               }, () -> {
                  Executor executor = mainThreadExecutor;
                  if (executor != null) {
                     executor.execute(() -> {
                        p_155740_.accept(p_155739_);
                     });
                  }

               });
            });
         });
      } else {
         p_155740_.accept(p_155739_);
      }
   }
}