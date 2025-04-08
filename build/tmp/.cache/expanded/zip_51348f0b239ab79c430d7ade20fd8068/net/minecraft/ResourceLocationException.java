package net.minecraft;

import org.apache.commons.lang3.StringEscapeUtils;

public class ResourceLocationException extends RuntimeException {
   public ResourceLocationException(String p_135421_) {
      super(StringEscapeUtils.escapeJava(p_135421_));
   }

   public ResourceLocationException(String p_135423_, Throwable p_135424_) {
      super(StringEscapeUtils.escapeJava(p_135423_), p_135424_);
   }
}