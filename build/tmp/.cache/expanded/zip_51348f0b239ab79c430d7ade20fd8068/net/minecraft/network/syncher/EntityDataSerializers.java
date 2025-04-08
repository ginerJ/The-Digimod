package net.minecraft.network.syncher;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityDataSerializers {
   private static final CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> SERIALIZERS = CrudeIncrementalIntIdentityHashBiMap.create(16);
   public static final EntityDataSerializer<Byte> BYTE = EntityDataSerializer.simple((p_238118_, p_238119_) -> {
      p_238118_.writeByte(p_238119_);
   }, FriendlyByteBuf::readByte);
   public static final EntityDataSerializer<Integer> INT = EntityDataSerializer.simple(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt);
   public static final EntityDataSerializer<Long> LONG = EntityDataSerializer.simple(FriendlyByteBuf::writeVarLong, FriendlyByteBuf::readVarLong);
   public static final EntityDataSerializer<Float> FLOAT = EntityDataSerializer.simple(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat);
   public static final EntityDataSerializer<String> STRING = EntityDataSerializer.simple(FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf);
   public static final EntityDataSerializer<Component> COMPONENT = EntityDataSerializer.simple(FriendlyByteBuf::writeComponent, FriendlyByteBuf::readComponent);
   public static final EntityDataSerializer<Optional<Component>> OPTIONAL_COMPONENT = EntityDataSerializer.optional(FriendlyByteBuf::writeComponent, FriendlyByteBuf::readComponent);
   public static final EntityDataSerializer<ItemStack> ITEM_STACK = new EntityDataSerializer<ItemStack>() {
      public void write(FriendlyByteBuf p_238123_, ItemStack p_238124_) {
         p_238123_.writeItem(p_238124_);
      }

      public ItemStack read(FriendlyByteBuf p_238126_) {
         return p_238126_.readItem();
      }

      public ItemStack copy(ItemStack p_238121_) {
         return p_238121_.copy();
      }
   };
   public static final EntityDataSerializer<BlockState> BLOCK_STATE = EntityDataSerializer.simpleId(Block.BLOCK_STATE_REGISTRY);
   public static final EntityDataSerializer<Optional<BlockState>> OPTIONAL_BLOCK_STATE = new EntityDataSerializer.ForValueType<Optional<BlockState>>() {
      public void write(FriendlyByteBuf p_238128_, Optional<BlockState> p_238129_) {
         if (p_238129_.isPresent()) {
            p_238128_.writeVarInt(Block.getId(p_238129_.get()));
         } else {
            p_238128_.writeVarInt(0);
         }

      }

      public Optional<BlockState> read(FriendlyByteBuf p_238131_) {
         int i = p_238131_.readVarInt();
         return i == 0 ? Optional.empty() : Optional.of(Block.stateById(i));
      }
   };
   public static final EntityDataSerializer<Boolean> BOOLEAN = EntityDataSerializer.simple(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean);
   public static final EntityDataSerializer<ParticleOptions> PARTICLE = new EntityDataSerializer.ForValueType<ParticleOptions>() {
      public void write(FriendlyByteBuf p_238133_, ParticleOptions p_238134_) {
         p_238133_.writeId(BuiltInRegistries.PARTICLE_TYPE, p_238134_.getType());
         p_238134_.writeToNetwork(p_238133_);
      }

      public ParticleOptions read(FriendlyByteBuf p_238139_) {
         return this.readParticle(p_238139_, p_238139_.readById(BuiltInRegistries.PARTICLE_TYPE));
      }

      private <T extends ParticleOptions> T readParticle(FriendlyByteBuf p_238136_, ParticleType<T> p_238137_) {
         return p_238137_.getDeserializer().fromNetwork(p_238137_, p_238136_);
      }
   };
   public static final EntityDataSerializer<Rotations> ROTATIONS = new EntityDataSerializer.ForValueType<Rotations>() {
      public void write(FriendlyByteBuf p_238141_, Rotations p_238142_) {
         p_238141_.writeFloat(p_238142_.getX());
         p_238141_.writeFloat(p_238142_.getY());
         p_238141_.writeFloat(p_238142_.getZ());
      }

      public Rotations read(FriendlyByteBuf p_238144_) {
         return new Rotations(p_238144_.readFloat(), p_238144_.readFloat(), p_238144_.readFloat());
      }
   };
   public static final EntityDataSerializer<BlockPos> BLOCK_POS = EntityDataSerializer.simple(FriendlyByteBuf::writeBlockPos, FriendlyByteBuf::readBlockPos);
   public static final EntityDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = EntityDataSerializer.optional(FriendlyByteBuf::writeBlockPos, FriendlyByteBuf::readBlockPos);
   public static final EntityDataSerializer<Direction> DIRECTION = EntityDataSerializer.simpleEnum(Direction.class);
   public static final EntityDataSerializer<Optional<UUID>> OPTIONAL_UUID = EntityDataSerializer.optional(FriendlyByteBuf::writeUUID, FriendlyByteBuf::readUUID);
   public static final EntityDataSerializer<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS = EntityDataSerializer.optional(FriendlyByteBuf::writeGlobalPos, FriendlyByteBuf::readGlobalPos);
   public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG = new EntityDataSerializer<CompoundTag>() {
      public void write(FriendlyByteBuf p_238148_, CompoundTag p_238149_) {
         p_238148_.writeNbt(p_238149_);
      }

      public CompoundTag read(FriendlyByteBuf p_238151_) {
         return p_238151_.readNbt();
      }

      public CompoundTag copy(CompoundTag p_238146_) {
         return p_238146_.copy();
      }
   };
   public static final EntityDataSerializer<VillagerData> VILLAGER_DATA = new EntityDataSerializer.ForValueType<VillagerData>() {
      public void write(FriendlyByteBuf p_238153_, VillagerData p_238154_) {
         p_238153_.writeId(BuiltInRegistries.VILLAGER_TYPE, p_238154_.getType());
         p_238153_.writeId(BuiltInRegistries.VILLAGER_PROFESSION, p_238154_.getProfession());
         p_238153_.writeVarInt(p_238154_.getLevel());
      }

      public VillagerData read(FriendlyByteBuf p_238156_) {
         return new VillagerData(p_238156_.readById(BuiltInRegistries.VILLAGER_TYPE), p_238156_.readById(BuiltInRegistries.VILLAGER_PROFESSION), p_238156_.readVarInt());
      }
   };
   public static final EntityDataSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT = new EntityDataSerializer.ForValueType<OptionalInt>() {
      public void write(FriendlyByteBuf p_238158_, OptionalInt p_238159_) {
         p_238158_.writeVarInt(p_238159_.orElse(-1) + 1);
      }

      public OptionalInt read(FriendlyByteBuf p_243339_) {
         int i = p_243339_.readVarInt();
         return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
      }
   };
   public static final EntityDataSerializer<Pose> POSE = EntityDataSerializer.simpleEnum(Pose.class);
   public static final EntityDataSerializer<CatVariant> CAT_VARIANT = EntityDataSerializer.simpleId(BuiltInRegistries.CAT_VARIANT);
   public static final EntityDataSerializer<FrogVariant> FROG_VARIANT = EntityDataSerializer.simpleId(BuiltInRegistries.FROG_VARIANT);
   public static final EntityDataSerializer<Holder<PaintingVariant>> PAINTING_VARIANT = EntityDataSerializer.simpleId(BuiltInRegistries.PAINTING_VARIANT.asHolderIdMap());
   public static final EntityDataSerializer<Sniffer.State> SNIFFER_STATE = EntityDataSerializer.simpleEnum(Sniffer.State.class);
   public static final EntityDataSerializer<Vector3f> VECTOR3 = EntityDataSerializer.simple(FriendlyByteBuf::writeVector3f, FriendlyByteBuf::readVector3f);
   public static final EntityDataSerializer<Quaternionf> QUATERNION = EntityDataSerializer.simple(FriendlyByteBuf::writeQuaternion, FriendlyByteBuf::readQuaternion);

   public static void registerSerializer(EntityDataSerializer<?> p_135051_) {
      if (SERIALIZERS.add(p_135051_) >= 256) throw new RuntimeException("Vanilla DataSerializer ID limit exceeded");
   }

   @Nullable
   public static EntityDataSerializer<?> getSerializer(int p_135049_) {
      return net.minecraftforge.common.ForgeHooks.getSerializer(p_135049_, SERIALIZERS);
   }

   public static int getSerializedId(EntityDataSerializer<?> p_135053_) {
      return net.minecraftforge.common.ForgeHooks.getSerializerId(p_135053_, SERIALIZERS);
   }

   private EntityDataSerializers() {
   }

   static {
      registerSerializer(BYTE);
      registerSerializer(INT);
      registerSerializer(LONG);
      registerSerializer(FLOAT);
      registerSerializer(STRING);
      registerSerializer(COMPONENT);
      registerSerializer(OPTIONAL_COMPONENT);
      registerSerializer(ITEM_STACK);
      registerSerializer(BOOLEAN);
      registerSerializer(ROTATIONS);
      registerSerializer(BLOCK_POS);
      registerSerializer(OPTIONAL_BLOCK_POS);
      registerSerializer(DIRECTION);
      registerSerializer(OPTIONAL_UUID);
      registerSerializer(BLOCK_STATE);
      registerSerializer(OPTIONAL_BLOCK_STATE);
      registerSerializer(COMPOUND_TAG);
      registerSerializer(PARTICLE);
      registerSerializer(VILLAGER_DATA);
      registerSerializer(OPTIONAL_UNSIGNED_INT);
      registerSerializer(POSE);
      registerSerializer(CAT_VARIANT);
      registerSerializer(FROG_VARIANT);
      registerSerializer(OPTIONAL_GLOBAL_POS);
      registerSerializer(PAINTING_VARIANT);
      registerSerializer(SNIFFER_STATE);
      registerSerializer(VECTOR3);
      registerSerializer(QUATERNION);
   }
}
