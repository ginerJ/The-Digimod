package net.minecraft.world.level.chunk;

import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;

public interface PalettedContainerRO<T> {
   T get(int p_238291_, int p_238292_, int p_238293_);

   void getAll(Consumer<T> p_238353_);

   void write(FriendlyByteBuf p_238417_);

   int getSerializedSize();

   boolean maybeHas(Predicate<T> p_238437_);

   void count(PalettedContainer.CountConsumer<T> p_238355_);

   PalettedContainer<T> recreate();

   PalettedContainerRO.PackedData<T> pack(IdMap<T> p_238441_, PalettedContainer.Strategy p_238442_);

   public static record PackedData<T>(List<T> paletteEntries, Optional<LongStream> storage) {
   }

   public interface Unpacker<T, C extends PalettedContainerRO<T>> {
      DataResult<C> read(IdMap<T> p_238364_, PalettedContainer.Strategy p_238365_, PalettedContainerRO.PackedData<T> p_238366_);
   }
}