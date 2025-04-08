package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.contents.BlockDataSource;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.EntityDataSource;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.StorageDataSource;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

public interface Component extends Message, FormattedText {
   Style getStyle();

   ComponentContents getContents();

   default String getString() {
      return FormattedText.super.getString();
   }

   default String getString(int p_130669_) {
      StringBuilder stringbuilder = new StringBuilder();
      this.visit((p_130673_) -> {
         int i = p_130669_ - stringbuilder.length();
         if (i <= 0) {
            return STOP_ITERATION;
         } else {
            stringbuilder.append(p_130673_.length() <= i ? p_130673_ : p_130673_.substring(0, i));
            return Optional.empty();
         }
      });
      return stringbuilder.toString();
   }

   List<Component> getSiblings();

   default MutableComponent plainCopy() {
      return MutableComponent.create(this.getContents());
   }

   default MutableComponent copy() {
      return new MutableComponent(this.getContents(), new ArrayList<>(this.getSiblings()), this.getStyle());
   }

   FormattedCharSequence getVisualOrderText();

   default <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_130679_, Style p_130680_) {
      Style style = this.getStyle().applyTo(p_130680_);
      Optional<T> optional = this.getContents().visit(p_130679_, style);
      if (optional.isPresent()) {
         return optional;
      } else {
         for(Component component : this.getSiblings()) {
            Optional<T> optional1 = component.visit(p_130679_, style);
            if (optional1.isPresent()) {
               return optional1;
            }
         }

         return Optional.empty();
      }
   }

   default <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_130677_) {
      Optional<T> optional = this.getContents().visit(p_130677_);
      if (optional.isPresent()) {
         return optional;
      } else {
         for(Component component : this.getSiblings()) {
            Optional<T> optional1 = component.visit(p_130677_);
            if (optional1.isPresent()) {
               return optional1;
            }
         }

         return Optional.empty();
      }
   }

   default List<Component> toFlatList() {
      return this.toFlatList(Style.EMPTY);
   }

   default List<Component> toFlatList(Style p_178406_) {
      List<Component> list = Lists.newArrayList();
      this.visit((p_178403_, p_178404_) -> {
         if (!p_178404_.isEmpty()) {
            list.add(literal(p_178404_).withStyle(p_178403_));
         }

         return Optional.empty();
      }, p_178406_);
      return list;
   }

   default boolean contains(Component p_240571_) {
      if (this.equals(p_240571_)) {
         return true;
      } else {
         List<Component> list = this.toFlatList();
         List<Component> list1 = p_240571_.toFlatList(this.getStyle());
         return Collections.indexOfSubList(list, list1) != -1;
      }
   }

   static Component nullToEmpty(@Nullable String p_130675_) {
      return (Component)(p_130675_ != null ? literal(p_130675_) : CommonComponents.EMPTY);
   }

   static MutableComponent literal(String p_237114_) {
      return MutableComponent.create(new LiteralContents(p_237114_));
   }

   static MutableComponent translatable(String p_237116_) {
      return MutableComponent.create(new TranslatableContents(p_237116_, (String)null, TranslatableContents.NO_ARGS));
   }

   static MutableComponent translatable(String p_237111_, Object... p_237112_) {
      return MutableComponent.create(new TranslatableContents(p_237111_, (String)null, p_237112_));
   }

   static MutableComponent translatableWithFallback(String p_265747_, @Nullable String p_265287_) {
      return MutableComponent.create(new TranslatableContents(p_265747_, p_265287_, TranslatableContents.NO_ARGS));
   }

   static MutableComponent translatableWithFallback(String p_265449_, @Nullable String p_265281_, Object... p_265785_) {
      return MutableComponent.create(new TranslatableContents(p_265449_, p_265281_, p_265785_));
   }

   static MutableComponent empty() {
      return MutableComponent.create(ComponentContents.EMPTY);
   }

   static MutableComponent keybind(String p_237118_) {
      return MutableComponent.create(new KeybindContents(p_237118_));
   }

   static MutableComponent nbt(String p_237106_, boolean p_237107_, Optional<Component> p_237108_, DataSource p_237109_) {
      return MutableComponent.create(new NbtContents(p_237106_, p_237107_, p_237108_, p_237109_));
   }

   static MutableComponent score(String p_237100_, String p_237101_) {
      return MutableComponent.create(new ScoreContents(p_237100_, p_237101_));
   }

   static MutableComponent selector(String p_237103_, Optional<Component> p_237104_) {
      return MutableComponent.create(new SelectorContents(p_237103_, p_237104_));
   }

   public static class Serializer implements JsonDeserializer<MutableComponent>, JsonSerializer<Component> {
      private static final Gson GSON = Util.make(() -> {
         GsonBuilder gsonbuilder = new GsonBuilder();
         gsonbuilder.disableHtmlEscaping();
         gsonbuilder.registerTypeHierarchyAdapter(Component.class, new Component.Serializer());
         gsonbuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
         gsonbuilder.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
         return gsonbuilder.create();
      });
      private static final Field JSON_READER_POS = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("pos");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException nosuchfieldexception) {
            throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", nosuchfieldexception);
         }
      });
      private static final Field JSON_READER_LINESTART = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("lineStart");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException nosuchfieldexception) {
            throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", nosuchfieldexception);
         }
      });

      public MutableComponent deserialize(JsonElement p_130694_, Type p_130695_, JsonDeserializationContext p_130696_) throws JsonParseException {
         if (p_130694_.isJsonPrimitive()) {
            return Component.literal(p_130694_.getAsString());
         } else if (!p_130694_.isJsonObject()) {
            if (p_130694_.isJsonArray()) {
               JsonArray jsonarray1 = p_130694_.getAsJsonArray();
               MutableComponent mutablecomponent1 = null;

               for(JsonElement jsonelement : jsonarray1) {
                  MutableComponent mutablecomponent2 = this.deserialize(jsonelement, jsonelement.getClass(), p_130696_);
                  if (mutablecomponent1 == null) {
                     mutablecomponent1 = mutablecomponent2;
                  } else {
                     mutablecomponent1.append(mutablecomponent2);
                  }
               }

               return mutablecomponent1;
            } else {
               throw new JsonParseException("Don't know how to turn " + p_130694_ + " into a Component");
            }
         } else {
            JsonObject jsonobject = p_130694_.getAsJsonObject();
            MutableComponent mutablecomponent;
            if (jsonobject.has("text")) {
               String s = GsonHelper.getAsString(jsonobject, "text");
               mutablecomponent = s.isEmpty() ? Component.empty() : Component.literal(s);
            } else if (jsonobject.has("translate")) {
               String s2 = GsonHelper.getAsString(jsonobject, "translate");
               String s1 = GsonHelper.getAsString(jsonobject, "fallback", (String)null);
               if (jsonobject.has("with")) {
                  JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "with");
                  Object[] aobject = new Object[jsonarray.size()];

                  for(int i = 0; i < aobject.length; ++i) {
                     aobject[i] = unwrapTextArgument(this.deserialize(jsonarray.get(i), p_130695_, p_130696_));
                  }

                  mutablecomponent = Component.translatableWithFallback(s2, s1, aobject);
               } else {
                  mutablecomponent = Component.translatableWithFallback(s2, s1);
               }
            } else if (jsonobject.has("score")) {
               JsonObject jsonobject1 = GsonHelper.getAsJsonObject(jsonobject, "score");
               if (!jsonobject1.has("name") || !jsonobject1.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               mutablecomponent = Component.score(GsonHelper.getAsString(jsonobject1, "name"), GsonHelper.getAsString(jsonobject1, "objective"));
            } else if (jsonobject.has("selector")) {
               Optional<Component> optional = this.parseSeparator(p_130695_, p_130696_, jsonobject);
               mutablecomponent = Component.selector(GsonHelper.getAsString(jsonobject, "selector"), optional);
            } else if (jsonobject.has("keybind")) {
               mutablecomponent = Component.keybind(GsonHelper.getAsString(jsonobject, "keybind"));
            } else {
               if (!jsonobject.has("nbt")) {
                  throw new JsonParseException("Don't know how to turn " + p_130694_ + " into a Component");
               }

               String s3 = GsonHelper.getAsString(jsonobject, "nbt");
               Optional<Component> optional1 = this.parseSeparator(p_130695_, p_130696_, jsonobject);
               boolean flag = GsonHelper.getAsBoolean(jsonobject, "interpret", false);
               DataSource datasource;
               if (jsonobject.has("block")) {
                  datasource = new BlockDataSource(GsonHelper.getAsString(jsonobject, "block"));
               } else if (jsonobject.has("entity")) {
                  datasource = new EntityDataSource(GsonHelper.getAsString(jsonobject, "entity"));
               } else {
                  if (!jsonobject.has("storage")) {
                     throw new JsonParseException("Don't know how to turn " + p_130694_ + " into a Component");
                  }

                  datasource = new StorageDataSource(new ResourceLocation(GsonHelper.getAsString(jsonobject, "storage")));
               }

               mutablecomponent = Component.nbt(s3, flag, optional1, datasource);
            }

            if (jsonobject.has("extra")) {
               JsonArray jsonarray2 = GsonHelper.getAsJsonArray(jsonobject, "extra");
               if (jsonarray2.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int j = 0; j < jsonarray2.size(); ++j) {
                  mutablecomponent.append(this.deserialize(jsonarray2.get(j), p_130695_, p_130696_));
               }
            }

            mutablecomponent.setStyle(p_130696_.deserialize(p_130694_, Style.class));
            return mutablecomponent;
         }
      }

      private static Object unwrapTextArgument(Object p_237121_) {
         if (p_237121_ instanceof Component component) {
            if (component.getStyle().isEmpty() && component.getSiblings().isEmpty()) {
               ComponentContents componentcontents = component.getContents();
               if (componentcontents instanceof LiteralContents) {
                  LiteralContents literalcontents = (LiteralContents)componentcontents;
                  return literalcontents.text();
               }
            }
         }

         return p_237121_;
      }

      private Optional<Component> parseSeparator(Type p_178416_, JsonDeserializationContext p_178417_, JsonObject p_178418_) {
         return p_178418_.has("separator") ? Optional.of(this.deserialize(p_178418_.get("separator"), p_178416_, p_178417_)) : Optional.empty();
      }

      private void serializeStyle(Style p_130710_, JsonObject p_130711_, JsonSerializationContext p_130712_) {
         JsonElement jsonelement = p_130712_.serialize(p_130710_);
         if (jsonelement.isJsonObject()) {
            JsonObject jsonobject = (JsonObject)jsonelement;

            for(Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               p_130711_.add(entry.getKey(), entry.getValue());
            }
         }

      }

      public JsonElement serialize(Component p_130706_, Type p_130707_, JsonSerializationContext p_130708_) {
         JsonObject jsonobject = new JsonObject();
         if (!p_130706_.getStyle().isEmpty()) {
            this.serializeStyle(p_130706_.getStyle(), jsonobject, p_130708_);
         }

         if (!p_130706_.getSiblings().isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(Component component : p_130706_.getSiblings()) {
               jsonarray.add(this.serialize(component, Component.class, p_130708_));
            }

            jsonobject.add("extra", jsonarray);
         }

         ComponentContents componentcontents = p_130706_.getContents();
         if (componentcontents == ComponentContents.EMPTY) {
            jsonobject.addProperty("text", "");
         } else if (componentcontents instanceof LiteralContents) {
            LiteralContents literalcontents = (LiteralContents)componentcontents;
            jsonobject.addProperty("text", literalcontents.text());
         } else if (componentcontents instanceof TranslatableContents) {
            TranslatableContents translatablecontents = (TranslatableContents)componentcontents;
            jsonobject.addProperty("translate", translatablecontents.getKey());
            String s = translatablecontents.getFallback();
            if (s != null) {
               jsonobject.addProperty("fallback", s);
            }

            if (translatablecontents.getArgs().length > 0) {
               JsonArray jsonarray1 = new JsonArray();

               for(Object object : translatablecontents.getArgs()) {
                  if (object instanceof Component) {
                     jsonarray1.add(this.serialize((Component)object, object.getClass(), p_130708_));
                  } else {
                     jsonarray1.add(new JsonPrimitive(String.valueOf(object)));
                  }
               }

               jsonobject.add("with", jsonarray1);
            }
         } else if (componentcontents instanceof ScoreContents) {
            ScoreContents scorecontents = (ScoreContents)componentcontents;
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("name", scorecontents.getName());
            jsonobject1.addProperty("objective", scorecontents.getObjective());
            jsonobject.add("score", jsonobject1);
         } else if (componentcontents instanceof SelectorContents) {
            SelectorContents selectorcontents = (SelectorContents)componentcontents;
            jsonobject.addProperty("selector", selectorcontents.getPattern());
            this.serializeSeparator(p_130708_, jsonobject, selectorcontents.getSeparator());
         } else if (componentcontents instanceof KeybindContents) {
            KeybindContents keybindcontents = (KeybindContents)componentcontents;
            jsonobject.addProperty("keybind", keybindcontents.getName());
         } else {
            if (!(componentcontents instanceof NbtContents)) {
               throw new IllegalArgumentException("Don't know how to serialize " + componentcontents + " as a Component");
            }

            NbtContents nbtcontents = (NbtContents)componentcontents;
            jsonobject.addProperty("nbt", nbtcontents.getNbtPath());
            jsonobject.addProperty("interpret", nbtcontents.isInterpreting());
            this.serializeSeparator(p_130708_, jsonobject, nbtcontents.getSeparator());
            DataSource datasource = nbtcontents.getDataSource();
            if (datasource instanceof BlockDataSource) {
               BlockDataSource blockdatasource = (BlockDataSource)datasource;
               jsonobject.addProperty("block", blockdatasource.posPattern());
            } else if (datasource instanceof EntityDataSource) {
               EntityDataSource entitydatasource = (EntityDataSource)datasource;
               jsonobject.addProperty("entity", entitydatasource.selectorPattern());
            } else {
               if (!(datasource instanceof StorageDataSource)) {
                  throw new IllegalArgumentException("Don't know how to serialize " + componentcontents + " as a Component");
               }

               StorageDataSource storagedatasource = (StorageDataSource)datasource;
               jsonobject.addProperty("storage", storagedatasource.id().toString());
            }
         }

         return jsonobject;
      }

      private void serializeSeparator(JsonSerializationContext p_178412_, JsonObject p_178413_, Optional<Component> p_178414_) {
         p_178414_.ifPresent((p_178410_) -> {
            p_178413_.add("separator", this.serialize(p_178410_, p_178410_.getClass(), p_178412_));
         });
      }

      public static String toJson(Component p_130704_) {
         return GSON.toJson(p_130704_);
      }

      public static String toStableJson(Component p_237123_) {
         return GsonHelper.toStableString(toJsonTree(p_237123_));
      }

      public static JsonElement toJsonTree(Component p_130717_) {
         return GSON.toJsonTree(p_130717_);
      }

      @Nullable
      public static MutableComponent fromJson(String p_130702_) {
         return GsonHelper.fromNullableJson(GSON, p_130702_, MutableComponent.class, false);
      }

      @Nullable
      public static MutableComponent fromJson(JsonElement p_130692_) {
         return GSON.fromJson(p_130692_, MutableComponent.class);
      }

      @Nullable
      public static MutableComponent fromJsonLenient(String p_130715_) {
         return GsonHelper.fromNullableJson(GSON, p_130715_, MutableComponent.class, true);
      }

      public static MutableComponent fromJson(com.mojang.brigadier.StringReader p_130700_) {
         try {
            JsonReader jsonreader = new JsonReader(new StringReader(p_130700_.getRemaining()));
            jsonreader.setLenient(false);
            MutableComponent mutablecomponent = GSON.getAdapter(MutableComponent.class).read(jsonreader);
            p_130700_.setCursor(p_130700_.getCursor() + getPos(jsonreader));
            return mutablecomponent;
         } catch (StackOverflowError | IOException ioexception) {
            throw new JsonParseException(ioexception);
         }
      }

      private static int getPos(JsonReader p_130698_) {
         try {
            return JSON_READER_POS.getInt(p_130698_) - JSON_READER_LINESTART.getInt(p_130698_) + 1;
         } catch (IllegalAccessException illegalaccessexception) {
            throw new IllegalStateException("Couldn't read position of JsonReader", illegalaccessexception);
         }
      }
   }
}