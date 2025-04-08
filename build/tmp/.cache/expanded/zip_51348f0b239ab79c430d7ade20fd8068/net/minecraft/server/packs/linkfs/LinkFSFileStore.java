package net.minecraft.server.packs.linkfs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import javax.annotation.Nullable;

class LinkFSFileStore extends FileStore {
   private final String name;

   public LinkFSFileStore(String p_249242_) {
      this.name = p_249242_;
   }

   public String name() {
      return this.name;
   }

   public String type() {
      return "index";
   }

   public boolean isReadOnly() {
      return true;
   }

   public long getTotalSpace() {
      return 0L;
   }

   public long getUsableSpace() {
      return 0L;
   }

   public long getUnallocatedSpace() {
      return 0L;
   }

   public boolean supportsFileAttributeView(Class<? extends FileAttributeView> p_251407_) {
      return p_251407_ == BasicFileAttributeView.class;
   }

   public boolean supportsFileAttributeView(String p_250666_) {
      return "basic".equals(p_250666_);
   }

   @Nullable
   public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> p_251981_) {
      return (V)null;
   }

   public Object getAttribute(String p_249050_) throws IOException {
      throw new UnsupportedOperationException();
   }
}