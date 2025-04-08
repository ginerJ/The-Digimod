package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public interface BehaviorControl<E extends LivingEntity> {
   Behavior.Status getStatus();

   boolean tryStart(ServerLevel p_259494_, E p_259608_, long p_260186_);

   void tickOrStop(ServerLevel p_259926_, E p_260016_, long p_259089_);

   void doStop(ServerLevel p_259056_, E p_259620_, long p_260105_);

   String debugString();
}