/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.auraframework.util.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

/**
 */
public class JsonReaderTest {
    /**
     * This class just uses JsonReader class to parse a JsonString.
     * 
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testReadComplexObject() throws IOException {
        String json = "{\"key1\":[[\"string1\",\"string2\"],true,10,[false,1.5,{\"key2\":[\"string1\",\"string2\"]}]]}";
        Object o = new JsonReader().read(json);
        assertTrue(o instanceof Map);
        Map<String, Object> outerMap = (Map<String, Object>) o;
        List<Object> outerList = (List<Object>) outerMap.get("key1");
        List<Object> item0 = (List<Object>) outerList.get(0);
        assertEquals("string1", item0.get(0));
        assertEquals("string2", item0.get(1));
        assertEquals(true, outerList.get(1));
        assertEquals(new BigDecimal(10), outerList.get(2));
        List<Object> item3 = (List<Object>) outerList.get(3);
        assertEquals(false, item3.get(0));
        assertEquals(new BigDecimal(1.5), item3.get(1));
        Map<String, Object> innerMap = (Map<String, Object>) item3.get(2);
        List<Object> innerList = (List<Object>) innerMap.get("key2");
        assertEquals("string1", innerList.get(0));
        assertEquals("string2", innerList.get(1));
    }

    /**
     * Make sure our escape sequence is yelled about correctly.
     */
    @Test
    public void testReadBadEscape() throws IOException {
        try {
            new JsonReader().read("{\"\\~\":\"b\"}");
            fail("should have caught a bad escape exception");
        } catch (Exception e) {
            assertTrue("Message did not have escape sequence: "+e.getMessage(),
                e.getMessage().contains("Unknown escape sequence : \\~"));
        }
    }

    /**
     * Test case to verify the handling of IOExceptions
     * 
     * @throws Exception
     */
    @Test
    public void testIOException() throws Exception {
        IOException expected = new IOException();
        Exception actual = null;
        Reader newFile = Mockito.mock(FileReader.class);
        Mockito.when(newFile.read()).thenThrow(expected);
        try {
            new JsonReader().read(newFile);
            fail("When the reader fumbles, the JsonReader should have signaled that");
        } catch (JsonStreamReader.JsonParseException e) {
            actual = e;
        } finally {
            newFile.close();
        }
        assertNotNull("IOException should propagate", actual);
        assertSame("IOException should be passed up as cause", expected, actual.getCause());
    }
}
