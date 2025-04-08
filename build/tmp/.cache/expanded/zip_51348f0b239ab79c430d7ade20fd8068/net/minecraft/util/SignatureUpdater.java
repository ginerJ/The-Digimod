package net.minecraft.util;

import java.security.SignatureException;

@FunctionalInterface
public interface SignatureUpdater {
   void update(SignatureUpdater.Output p_216345_) throws SignatureException;

   @FunctionalInterface
   public interface Output {
      void update(byte[] p_216347_) throws SignatureException;
   }
}