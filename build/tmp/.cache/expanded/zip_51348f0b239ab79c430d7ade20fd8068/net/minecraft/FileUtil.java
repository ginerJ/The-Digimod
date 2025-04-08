package net.minecraft;

import com.mojang.serialization.DataResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

public class FileUtil {
   private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
   private static final int MAX_FILE_NAME = 255;
   private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);
   private static final Pattern STRICT_PATH_SEGMENT_CHECK = Pattern.compile("[-._a-z0-9]+");

   public static String findAvailableName(Path p_133731_, String p_133732_, String p_133733_) throws IOException {
      for(char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
         p_133732_ = p_133732_.replace(c0, '_');
      }

      p_133732_ = p_133732_.replaceAll("[./\"]", "_");
      if (RESERVED_WINDOWS_FILENAMES.matcher(p_133732_).matches()) {
         p_133732_ = "_" + p_133732_ + "_";
      }

      Matcher matcher = COPY_COUNTER_PATTERN.matcher(p_133732_);
      int j = 0;
      if (matcher.matches()) {
         p_133732_ = matcher.group("name");
         j = Integer.parseInt(matcher.group("count"));
      }

      if (p_133732_.length() > 255 - p_133733_.length()) {
         p_133732_ = p_133732_.substring(0, 255 - p_133733_.length());
      }

      while(true) {
         String s = p_133732_;
         if (j != 0) {
            String s1 = " (" + j + ")";
            int i = 255 - s1.length();
            if (p_133732_.length() > i) {
               s = p_133732_.substring(0, i);
            }

            s = s + s1;
         }

         s = s + p_133733_;
         Path path = p_133731_.resolve(s);

         try {
            Path path1 = Files.createDirectory(path);
            Files.deleteIfExists(path1);
            return p_133731_.relativize(path1).toString();
         } catch (FileAlreadyExistsException filealreadyexistsexception) {
            ++j;
         }
      }
   }

   public static boolean isPathNormalized(Path p_133729_) {
      Path path = p_133729_.normalize();
      return path.equals(p_133729_);
   }

   public static boolean isPathPortable(Path p_133735_) {
      for(Path path : p_133735_) {
         if (RESERVED_WINDOWS_FILENAMES.matcher(path.toString()).matches()) {
            return false;
         }
      }

      return true;
   }

   public static Path createPathToResource(Path p_133737_, String p_133738_, String p_133739_) {
      String s = p_133738_ + p_133739_;
      Path path = Paths.get(s);
      if (path.endsWith(p_133739_)) {
         throw new InvalidPathException(s, "empty resource name");
      } else {
         return p_133737_.resolve(path);
      }
   }

   public static String getFullResourcePath(String p_179923_) {
      return FilenameUtils.getFullPath(p_179923_).replace(File.separator, "/");
   }

   public static String normalizeResourcePath(String p_179925_) {
      return FilenameUtils.normalize(p_179925_).replace(File.separator, "/");
   }

   public static DataResult<List<String>> decomposePath(String p_248866_) {
      int i = p_248866_.indexOf(47);
      if (i == -1) {
         DataResult dataresult;
         switch (p_248866_) {
            case "":
            case ".":
            case "..":
               dataresult = DataResult.error(() -> {
                  return "Invalid path '" + p_248866_ + "'";
               });
               break;
            default:
               dataresult = !isValidStrictPathSegment(p_248866_) ? DataResult.error(() -> {
                  return "Invalid path '" + p_248866_ + "'";
               }) : DataResult.success(List.of(p_248866_));
         }

         return dataresult;
      } else {
         List<String> list = new ArrayList<>();
         int j = 0;
         boolean flag = false;

         while(true) {
            String s = p_248866_.substring(j, i);
            switch (p_248866_.substring(j, i)) {
               case "":
               case ".":
               case "..":
                  return DataResult.error(() -> {
                     return "Invalid segment '" + s + "' in path '" + p_248866_ + "'";
                  });
            }

            if (!isValidStrictPathSegment(s)) {
               return DataResult.error(() -> {
                  return "Invalid segment '" + s + "' in path '" + p_248866_ + "'";
               });
            }

            list.add(s);
            if (flag) {
               return DataResult.success(list);
            }

            j = i + 1;
            i = p_248866_.indexOf(47, j);
            if (i == -1) {
               i = p_248866_.length();
               flag = true;
            }
         }
      }
   }

   public static Path resolvePath(Path p_251522_, List<String> p_251495_) {
      int i = p_251495_.size();
      Path path;
      switch (i) {
         case 0:
            path = p_251522_;
            break;
         case 1:
            path = p_251522_.resolve(p_251495_.get(0));
            break;
         default:
            String[] astring = new String[i - 1];

            for(int j = 1; j < i; ++j) {
               astring[j - 1] = p_251495_.get(j);
            }

            path = p_251522_.resolve(p_251522_.getFileSystem().getPath(p_251495_.get(0), astring));
      }

      return path;
   }

   public static boolean isValidStrictPathSegment(String p_249814_) {
      return STRICT_PATH_SEGMENT_CHECK.matcher(p_249814_).matches();
   }

   public static void validatePath(String... p_249502_) {
      if (p_249502_.length == 0) {
         throw new IllegalArgumentException("Path must have at least one element");
      } else {
         for(String s : p_249502_) {
            if (s.equals("..") || s.equals(".") || !isValidStrictPathSegment(s)) {
               throw new IllegalArgumentException("Illegal segment " + s + " in path " + Arrays.toString((Object[])p_249502_));
            }
         }

      }
   }

   public static void createDirectoriesSafe(Path p_259902_) throws IOException {
      Files.createDirectories(Files.exists(p_259902_) ? p_259902_.toRealPath() : p_259902_);
   }
}