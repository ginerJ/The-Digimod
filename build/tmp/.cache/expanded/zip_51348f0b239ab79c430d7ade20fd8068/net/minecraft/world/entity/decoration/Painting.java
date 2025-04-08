package net.minecraft.world.entity.decoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Painting extends HangingEntity implements VariantHolder<Holder<PaintingVariant>> {
   private static final EntityDataAccessor<Holder<PaintingVariant>> DATA_PAINTING_VARIANT_ID = SynchedEntityData.defineId(Painting.class, EntityDataSerializers.PAINTING_VARIANT);
   private static final ResourceKey<PaintingVariant> DEFAULT_VARIANT = PaintingVariants.KEBAB;
   public static final String VARIANT_TAG = "variant";

   private static Holder<PaintingVariant> getDefaultVariant() {
      return BuiltInRegistries.PAINTING_VARIANT.getHolderOrThrow(DEFAULT_VARIANT);
   }

   public Painting(EntityType<? extends Painting> p_31904_, Level p_31905_) {
      super(p_31904_, p_31905_);
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_PAINTING_VARIANT_ID, getDefaultVariant());
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_218896_) {
      if (DATA_PAINTING_VARIANT_ID.equals(p_218896_)) {
         this.recalculateBoundingBox();
      }

   }

   public void setVariant(Holder<PaintingVariant> p_218892_) {
      this.entityData.set(DATA_PAINTING_VARIANT_ID, p_218892_);
   }

   public Holder<PaintingVariant> getVariant() {
      return this.entityData.get(DATA_PAINTING_VARIANT_ID);
   }

   public static Optional<Painting> create(Level p_218888_, BlockPos p_218889_, Direction p_218890_) {
      Painting painting = new Painting(p_218888_, p_218889_);
      List<Holder<PaintingVariant>> list = new ArrayList<>();
      BuiltInRegistries.PAINTING_VARIANT.getTagOrEmpty(PaintingVariantTags.PLACEABLE).forEach(list::add);
      if (list.isEmpty()) {
         return Optional.empty();
      } else {
         painting.setDirection(p_218890_);
         list.removeIf((p_289458_) -> {
            painting.setVariant(p_289458_);
            return !painting.survives();
         });
         if (list.isEmpty()) {
            return Optional.empty();
         } else {
            int i = list.stream().mapToInt(Painting::variantArea).max().orElse(0);
            list.removeIf((p_218883_) -> {
               return variantArea(p_218883_) < i;
            });
            Optional<Holder<PaintingVariant>> optional = Util.getRandomSafe(list, painting.random);
            if (optional.isEmpty()) {
               return Optional.empty();
            } else {
               painting.setVariant(optional.get());
               painting.setDirection(p_218890_);
               return Optional.of(painting);
            }
         }
      }
   }

   private static int variantArea(Holder<PaintingVariant> p_218899_) {
      return p_218899_.value().getWidth() * p_218899_.value().getHeight();
   }

   private Painting(Level p_218874_, BlockPos p_218875_) {
      super(EntityType.PAINTING, p_218874_, p_218875_);
   }

   public Painting(Level p_218877_, BlockPos p_218878_, Direction p_218879_, Holder<PaintingVariant> p_218880_) {
      this(p_218877_, p_218878_);
      this.setVariant(p_218880_);
      this.setDirection(p_218879_);
   }

   public void addAdditionalSaveData(CompoundTag p_31935_) {
      storeVariant(p_31935_, this.getVariant());
      p_31935_.putByte("facing", (byte)this.direction.get2DDataValue());
      super.addAdditionalSaveData(p_31935_);
   }

   public void readAdditionalSaveData(CompoundTag p_31927_) {
      Holder<PaintingVariant> holder = loadVariant(p_31927_).orElseGet(Painting::getDefaultVariant);
      this.setVariant(holder);
      this.direction = Direction.from2DDataValue(p_31927_.getByte("facing"));
      super.readAdditionalSaveData(p_31927_);
      this.setDirection(this.direction);
   }

   public static void storeVariant(CompoundTag p_270928_, Holder<PaintingVariant> p_270667_) {
      p_270928_.putString("variant", p_270667_.unwrapKey().orElse(DEFAULT_VARIANT).location().toString());
   }

   public static Optional<Holder<PaintingVariant>> loadVariant(CompoundTag p_271010_) {
      return Optional.ofNullable(ResourceLocation.tryParse(p_271010_.getString("variant"))).map((p_248378_) -> {
         return ResourceKey.create(Registries.PAINTING_VARIANT, p_248378_);
      }).flatMap(BuiltInRegistries.PAINTING_VARIANT::getHolder);
   }

   public int getWidth() {
      return this.getVariant().value().getWidth();
   }

   public int getHeight() {
      return this.getVariant().value().getHeight();
   }

   public void dropItem(@Nullable Entity p_31925_) {
      if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
         if (p_31925_ instanceof Player) {
            Player player = (Player)p_31925_;
            if (player.getAbilities().instabuild) {
               return;
            }
         }

         this.spawnAtLocation(Items.PAINTING);
      }
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
   }

   public void moveTo(double p_31929_, double p_31930_, double p_31931_, float p_31932_, float p_31933_) {
      this.setPos(p_31929_, p_31930_, p_31931_);
   }

   public void lerpTo(double p_31917_, double p_31918_, double p_31919_, float p_31920_, float p_31921_, int p_31922_, boolean p_31923_) {
      this.setPos(p_31917_, p_31918_, p_31919_);
   }

   public Vec3 trackingPosition() {
      return Vec3.atLowerCornerOf(this.pos);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
   }

   public void recreateFromPacket(ClientboundAddEntityPacket p_218894_) {
      super.recreateFromPacket(p_218894_);
      this.setDirection(Direction.from3DDataValue(p_218894_.getData()));
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.PAINTING);
   }
}