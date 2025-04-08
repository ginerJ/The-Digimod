package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public class ShriekParticleOption implements ParticleOptions {
   public static final Codec<ShriekParticleOption> CODEC = RecordCodecBuilder.create((p_235952_) -> {
      return p_235952_.group(Codec.INT.fieldOf("delay").forGetter((p_235954_) -> {
         return p_235954_.delay;
      })).apply(p_235952_, ShriekParticleOption::new);
   });
   public static final ParticleOptions.Deserializer<ShriekParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ShriekParticleOption>() {
      public ShriekParticleOption fromCommand(ParticleType<ShriekParticleOption> p_235961_, StringReader p_235962_) throws CommandSyntaxException {
         p_235962_.expect(' ');
         int i = p_235962_.readInt();
         return new ShriekParticleOption(i);
      }

      public ShriekParticleOption fromNetwork(ParticleType<ShriekParticleOption> p_235964_, FriendlyByteBuf p_235965_) {
         return new ShriekParticleOption(p_235965_.readVarInt());
      }
   };
   private final int delay;

   public ShriekParticleOption(int p_235949_) {
      this.delay = p_235949_;
   }

   public void writeToNetwork(FriendlyByteBuf p_235956_) {
      p_235956_.writeVarInt(this.delay);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.delay);
   }

   public ParticleType<ShriekParticleOption> getType() {
      return ParticleTypes.SHRIEK;
   }

   public int getDelay() {
      return this.delay;
   }
}