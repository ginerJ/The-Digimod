package net.minecraft.commands;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;

public interface CommandBuildContext {
   <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> p_227134_);

   static CommandBuildContext simple(final HolderLookup.Provider p_255702_, final FeatureFlagSet p_255968_) {
      return new CommandBuildContext() {
         public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> p_255791_) {
            return p_255702_.lookupOrThrow(p_255791_).filterFeatures(p_255968_);
         }
      };
   }

   static CommandBuildContext.Configurable configurable(final RegistryAccess p_255925_, final FeatureFlagSet p_255945_) {
      return new CommandBuildContext.Configurable() {
         CommandBuildContext.MissingTagAccessPolicy missingTagAccessPolicy = CommandBuildContext.MissingTagAccessPolicy.FAIL;

         public void missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy p_256626_) {
            this.missingTagAccessPolicy = p_256626_;
         }

         public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> p_256616_) {
            Registry<T> registry = p_255925_.registryOrThrow(p_256616_);
            final HolderLookup.RegistryLookup<T> registrylookup = registry.asLookup();
            final HolderLookup.RegistryLookup<T> registrylookup1 = registry.asTagAddingLookup();
            HolderLookup.RegistryLookup<T> registrylookup2 = new HolderLookup.RegistryLookup.Delegate<T>() {
               protected HolderLookup.RegistryLookup<T> parent() {
                  HolderLookup.RegistryLookup holderlookup$registrylookup;
                  switch (missingTagAccessPolicy) {
                     case FAIL:
                        holderlookup$registrylookup = registrylookup;
                        break;
                     case CREATE_NEW:
                        holderlookup$registrylookup = registrylookup1;
                        break;
                     default:
                        throw new IncompatibleClassChangeError();
                  }

                  return holderlookup$registrylookup;
               }
            };
            return registrylookup2.filterFeatures(p_255945_);
         }
      };
   }

   public interface Configurable extends CommandBuildContext {
      void missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy p_256669_);
   }

   public static enum MissingTagAccessPolicy {
      CREATE_NEW,
      FAIL;
   }
}