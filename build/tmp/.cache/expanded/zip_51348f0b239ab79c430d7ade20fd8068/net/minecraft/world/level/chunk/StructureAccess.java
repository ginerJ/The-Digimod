package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface StructureAccess {
   @Nullable
   StructureStart getStartForStructure(Structure p_223434_);

   void setStartForStructure(Structure p_223437_, StructureStart p_223438_);

   LongSet getReferencesForStructure(Structure p_223439_);

   void addReferenceForStructure(Structure p_223435_, long p_223436_);

   Map<Structure, LongSet> getAllReferences();

   void setAllReferences(Map<Structure, LongSet> p_223440_);
}