/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.functional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.common.TestTypes.BagOfPrimitives;
import com.google.gson.common.TestTypes.ClassWithObjects;

import junit.framework.TestCase;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Functional tests for the different cases for serializing (or ignoring) null fields and object.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class NullObjectAndFieldTest extends TestCase {
  private GsonBuilder gsonBuilder;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    gsonBuilder = new GsonBuilder().serializeNulls();
  }

  public void testTopLevelNullObjectSerialization() {
    Gson gson = gsonBuilder.create();
    String actual = gson.toJson(null);
    assertEquals("null", actual);

    actual = gson.toJson(null, String.class);
    assertEquals("null", actual);
  }

  public void testTopLevelNullObjectDeserialization() throws Exception {
    Gson gson = gsonBuilder.create();
    String actual = gson.fromJson("null", String.class);
    assertNull(actual);
  }

  public void testExplicitSerializationOfNulls() {
    Gson gson = gsonBuilder.create();
    ClassWithObjects target = new ClassWithObjects(null);
    String actual = gson.toJson(target);
    String expected = "{\"bag\":null}";
    assertEquals(expected, actual);
  }

  public void testExplicitDeserializationOfNulls() throws Exception {
    Gson gson = gsonBuilder.create();
    ClassWithObjects target = gson.fromJson("{\"bag\":null}", ClassWithObjects.class);
    assertNull(target.bag);
  }
  
  public void testExplicitSerializationOfNullArrayMembers() {
    Gson gson = gsonBuilder.create();
    ClassWithMembers target = new ClassWithMembers();
    String json = gson.toJson(target);
    assertTrue(json.contains("\"array\":null"));
  }
  
  /** 
   * Added to verify http://code.google.com/p/google-gson/issues/detail?id=68
   */
  public void testNullWrappedPrimitiveMemberSerialization() {
    Gson gson = gsonBuilder.serializeNulls().create();
    ClassWithNullWrappedPrimitive target = new ClassWithNullWrappedPrimitive();
    String json = gson.toJson(target);
    assertTrue(json.contains("\"value\":null"));
  }
  
  /** 
   * Added to verify http://code.google.com/p/google-gson/issues/detail?id=68
   */
  public void testNullWrappedPrimitiveMemberDeserialization() {
    Gson gson = gsonBuilder.create();
    String json = "{'value':null}";
    ClassWithNullWrappedPrimitive target = gson.fromJson(json, ClassWithNullWrappedPrimitive.class);
    assertNull(target.value);
  }
  
  public void testExplicitSerializationOfNullCollectionMembers() {
    Gson gson = gsonBuilder.create();
    ClassWithMembers target = new ClassWithMembers();
    String json = gson.toJson(target);
    assertTrue(json.contains("\"col\":null"));
  }
  
  public void testExplicitSerializationOfNullStringMembers() {
    Gson gson = gsonBuilder.create();
    ClassWithMembers target = new ClassWithMembers();
    String json = gson.toJson(target);
    assertTrue(json.contains("\"str\":null"));
  }

  public void testCustomSerializationOfNulls() {
    gsonBuilder.registerTypeAdapter(ClassWithObjects.class, new ClassWithObjectsSerializer());
    Gson gson = gsonBuilder.create();
    ClassWithObjects target = new ClassWithObjects(new BagOfPrimitives());
    String actual = gson.toJson(target);
    String expected = "{\"bag\":null}";
    assertEquals(expected, actual);
  }
  
  public void testPrintPrintingObjectWithNulls() throws Exception {
    gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();
    String result = gson.toJson(new ClassWithMembers());
    assertEquals("{}\n", result);

    gson = gsonBuilder.serializeNulls().create();
    result = gson.toJson(new ClassWithMembers());
    assertTrue(result.contains("\"str\":null"));
  }
  
  public void testPrintPrintingArraysWithNulls() throws Exception {
    gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();
    String result = gson.toJson(new String[] { "1", null, "3" });
    assertEquals("[\"1\",null,\"3\"]\n", result);

    gson = gsonBuilder.serializeNulls().create();
    result = gson.toJson(new String[] { "1", null, "3" });
    assertEquals("[\"1\",null,\"3\"]\n", result);
  }

  private static class ClassWithNullWrappedPrimitive {
    private Long value;
  }

  private static class ClassWithMembers {
    String str;
    int[] array;
    Collection<String> col;
  }
  
  private static class ClassWithObjectsSerializer implements JsonSerializer<ClassWithObjects> {
    public JsonElement serialize(ClassWithObjects src, Type typeOfSrc,
        JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.add("bag", new JsonNull());
      return obj;
    }
  }
}
