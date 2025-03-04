package net.modderg.thedigimod.server.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.modderg.thedigimod.server.entity.DigimonEntity;

public class DigimodCommandSummonAll {

    public static int summonAllMobs(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        Level level = player.level();

        if(level instanceof ServerLevel world)
            for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues()) {
                String namespace = ForgeRegistries.ENTITY_TYPES.getKey(entityType).getNamespace();
                if (namespace.equals("thedigimod"))
                    if (entityType.create(world) instanceof DigimonEntity mob) {
                        mob.setPos(player.position().add(mob.getRandom().nextInt(10), 0 , mob.getRandom().nextInt(10)));
                        world.addFreshEntity(mob);
                        mob.finalizeSpawn(world, world.getCurrentDifficultyAt(mob.blockPosition()), net.minecraft.world.entity.MobSpawnType.COMMAND, null, null);
                    }
            }

        return 1;
    }
}
