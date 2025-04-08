package net.minecraft.network.syncher;

import com.mojang.logging.LogUtils;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class SynchedEntityData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Object2IntMap<Class<? extends Entity>> ENTITY_ID_POOL = new Object2IntOpenHashMap<>();
   private static final int MAX_ID_VALUE = 254;
   private final Entity entity;
   private final Int2ObjectMap<SynchedEntityData.DataItem<?>> itemsById = new Int2ObjectOpenHashMap<>();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private boolean isDirty;

   public SynchedEntityData(Entity p_135351_) {
      this.entity = p_135351_;
   }

   public static <T> EntityDataAccessor<T> defineId(Class<? extends Entity> p_135354_, EntityDataSerializer<T> p_135355_) {
      if (true || LOGGER.isDebugEnabled()) { // Forge: This is very useful for mods that register keys on classes that are not their own
         try {
            Class<?> oclass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (!oclass.equals(p_135354_)) {
               // Forge: log at warn, mods should not add to classes that they don't own, and only add stacktrace when in debug is enabled as it is mostly not needed and consumes time
               if (LOGGER.isDebugEnabled()) LOGGER.warn("defineId called for: {} from {}", p_135354_, oclass, new RuntimeException());
               else LOGGER.warn("defineId called for: {} from {}", p_135354_, oclass);
            }
         } catch (ClassNotFoundException classnotfoundexception) {
         }
      }

      int j;
      if (ENTITY_ID_POOL.containsKey(p_135354_)) {
         j = ENTITY_ID_POOL.getInt(p_135354_) + 1;
      } else {
         int i = 0;
         Class<?> oclass1 = p_135354_;

         while(oclass1 != Entity.class) {
            oclass1 = oclass1.getSuperclass();
            if (ENTITY_ID_POOL.containsKey(oclass1)) {
               i = ENTITY_ID_POOL.getInt(oclass1) + 1;
               break;
            }
         }

         j = i;
      }

      if (j > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + j + "! (Max is 254)");
      } else {
         ENTITY_ID_POOL.put(p_135354_, j);
         return p_135355_.createAccessor(j);
      }
   }

   public <T> void define(EntityDataAccessor<T> p_135373_, T p_135374_) {
      int i = p_135373_.getId();
      if (i > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
      } else if (this.itemsById.containsKey(i)) {
         throw new IllegalArgumentException("Duplicate id value for " + i + "!");
      } else if (EntityDataSerializers.getSerializedId(p_135373_.getSerializer()) < 0) {
         throw new IllegalArgumentException("Unregistered serializer " + p_135373_.getSerializer() + " for " + i + "!");
      } else {
         this.createDataItem(p_135373_, p_135374_);
      }
   }

   private <T> void createDataItem(EntityDataAccessor<T> p_135386_, T p_135387_) {
      SynchedEntityData.DataItem<T> dataitem = new SynchedEntityData.DataItem<>(p_135386_, p_135387_);
      this.lock.writeLock().lock();
      this.itemsById.put(p_135386_.getId(), dataitem);
      this.lock.writeLock().unlock();
   }

   public <T> boolean hasItem(EntityDataAccessor<T> p_286294_) {
      return this.itemsById.containsKey(p_286294_.getId());
   }

   private <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> p_135380_) {
      this.lock.readLock().lock();

      SynchedEntityData.DataItem<T> dataitem;
      try {
         dataitem = (SynchedEntityData.DataItem<T>)this.itemsById.get(p_135380_.getId());
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting synched entity data");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Synched entity data");
         crashreportcategory.setDetail("Data ID", p_135380_);
         throw new ReportedException(crashreport);
      } finally {
         this.lock.readLock().unlock();
      }

      return dataitem;
   }

   public <T> T get(EntityDataAccessor<T> p_135371_) {
      return this.getItem(p_135371_).getValue();
   }

   public <T> void set(EntityDataAccessor<T> p_135382_, T p_135383_) {
      this.set(p_135382_, p_135383_, false);
   }

   public <T> void set(EntityDataAccessor<T> p_276368_, T p_276363_, boolean p_276370_) {
      SynchedEntityData.DataItem<T> dataitem = this.getItem(p_276368_);
      if (p_276370_ || ObjectUtils.notEqual(p_276363_, dataitem.getValue())) {
         dataitem.setValue(p_276363_);
         this.entity.onSyncedDataUpdated(p_276368_);
         dataitem.setDirty(true);
         this.isDirty = true;
      }

   }

   public boolean isDirty() {
      return this.isDirty;
   }

   @Nullable
   public List<SynchedEntityData.DataValue<?>> packDirty() {
      List<SynchedEntityData.DataValue<?>> list = null;
      if (this.isDirty) {
         this.lock.readLock().lock();

         for(SynchedEntityData.DataItem<?> dataitem : this.itemsById.values()) {
            if (dataitem.isDirty()) {
               dataitem.setDirty(false);
               if (list == null) {
                  list = new ArrayList<>();
               }

               list.add(dataitem.value());
            }
         }

         this.lock.readLock().unlock();
      }

      this.isDirty = false;
      return list;
   }

   @Nullable
   public List<SynchedEntityData.DataValue<?>> getNonDefaultValues() {
      List<SynchedEntityData.DataValue<?>> list = null;
      this.lock.readLock().lock();

      for(SynchedEntityData.DataItem<?> dataitem : this.itemsById.values()) {
         if (!dataitem.isSetToDefault()) {
            if (list == null) {
               list = new ArrayList<>();
            }

            list.add(dataitem.value());
         }
      }

      this.lock.readLock().unlock();
      return list;
   }

   public void assignValues(List<SynchedEntityData.DataValue<?>> p_135357_) {
      this.lock.writeLock().lock();

      try {
         for(SynchedEntityData.DataValue<?> datavalue : p_135357_) {
            SynchedEntityData.DataItem<?> dataitem = this.itemsById.get(datavalue.id);
            if (dataitem != null) {
               this.assignValue(dataitem, datavalue);
               this.entity.onSyncedDataUpdated(dataitem.getAccessor());
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

      this.entity.onSyncedDataUpdated(p_135357_);
   }

   private <T> void assignValue(SynchedEntityData.DataItem<T> p_135376_, SynchedEntityData.DataValue<?> p_254484_) {
      if (!Objects.equals(p_254484_.serializer(), p_135376_.accessor.getSerializer())) {
         throw new IllegalStateException(String.format(Locale.ROOT, "Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", p_135376_.accessor.getId(), this.entity, p_135376_.value, p_135376_.value.getClass(), p_254484_.value, p_254484_.value.getClass()));
      } else {
         p_135376_.setValue((T) p_254484_.value);
      }
   }

   public boolean isEmpty() {
      return this.itemsById.isEmpty();
   }

   public static class DataItem<T> {
      final EntityDataAccessor<T> accessor;
      T value;
      private final T initialValue;
      private boolean dirty;

      public DataItem(EntityDataAccessor<T> p_135394_, T p_135395_) {
         this.accessor = p_135394_;
         this.initialValue = p_135395_;
         this.value = p_135395_;
      }

      public EntityDataAccessor<T> getAccessor() {
         return this.accessor;
      }

      public void setValue(T p_135398_) {
         this.value = p_135398_;
      }

      public T getValue() {
         return this.value;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public void setDirty(boolean p_135402_) {
         this.dirty = p_135402_;
      }

      public boolean isSetToDefault() {
         return this.initialValue.equals(this.value);
      }

      public SynchedEntityData.DataValue<T> value() {
         return SynchedEntityData.DataValue.create(this.accessor, this.value);
      }
   }

   public static record DataValue<T>(int id, EntityDataSerializer<T> serializer, T value) {
      public static <T> SynchedEntityData.DataValue<T> create(EntityDataAccessor<T> p_254543_, T p_254138_) {
         EntityDataSerializer<T> entitydataserializer = p_254543_.getSerializer();
         return new SynchedEntityData.DataValue<>(p_254543_.getId(), entitydataserializer, entitydataserializer.copy(p_254138_));
      }

      public void write(FriendlyByteBuf p_253709_) {
         int i = EntityDataSerializers.getSerializedId(this.serializer);
         if (i < 0) {
            throw new EncoderException("Unknown serializer type " + this.serializer);
         } else {
            p_253709_.writeByte(this.id);
            p_253709_.writeVarInt(i);
            this.serializer.write(p_253709_, this.value);
         }
      }

      public static SynchedEntityData.DataValue<?> read(FriendlyByteBuf p_254314_, int p_254356_) {
         int i = p_254314_.readVarInt();
         EntityDataSerializer<?> entitydataserializer = EntityDataSerializers.getSerializer(i);
         if (entitydataserializer == null) {
            throw new DecoderException("Unknown serializer type " + i);
         } else {
            return read(p_254314_, p_254356_, entitydataserializer);
         }
      }

      private static <T> SynchedEntityData.DataValue<T> read(FriendlyByteBuf p_254224_, int p_253899_, EntityDataSerializer<T> p_254222_) {
         return new SynchedEntityData.DataValue<>(p_253899_, p_254222_, p_254222_.read(p_254224_));
      }
   }
}
