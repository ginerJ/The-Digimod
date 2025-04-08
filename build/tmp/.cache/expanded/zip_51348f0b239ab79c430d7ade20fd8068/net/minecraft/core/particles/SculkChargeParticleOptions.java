package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public record SculkChargeParticleOptions(float roll) implements ParticleOptions {
   public static final Codec<SculkChargeParticleOptions> CODEC = RecordCodecBuilder.create((p_235920_) -> {
      return p_235920_.group(Codec.FLOAT.fieldOf("roll").forGetter((p_235922_) -> {
         return p_235922_.roll;
      })).apply(p_235920_, SculkChargeParticleOptions::new);
   });
   public static final ParticleOptions.Deserializer<SculkChargeParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<SculkChargeParticleOptions>() {
      public SculkChargeParticleOptions fromCommand(ParticleType<SculkChargeParticleOptions> p_235933_, StringReader p_235934_) throws CommandSyntaxException {
         p_235934_.expect(' ');
         float f = p_235934_.readFloat();
         return new SculkChargeParticleOptions(f);
      }

      public SculkChargeParticleOptions fromNetwork(ParticleType<SculkChargeParticleOptions> p_235936_, FriendlyByteBuf p_235937_) {
         return new SculkChargeParticleOptions(p_235937_.readFloat());
      }
   };

   public ParticleType<SculkChargeParticleOptions> getType() {
      return ParticleTypes.SCULK_CHARGE;
   }

   public void writeToNetwork(FriendlyByteBuf p_235924_) {
      p_235924_.writeFloat(this.roll);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.roll);
   }
}