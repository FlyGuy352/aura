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
package org.auraframework.util.validation;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.auraframework.util.javascript.JavascriptProcessingError.Level;
import org.auraframework.util.json.JsonEncoder;
import org.auraframework.util.json.JsonReader;
import org.junit.Test;

public class ValidationErrorTest {

    @Test
    public void testJsonSerialization() {
        ValidationError error = new ValidationError("tool", "/file/name", 11, 3, "message", "evidence",
                Level.Error, "rule");
        String json = JsonEncoder.serialize(error);
        @SuppressWarnings("unchecked")
        ValidationError dError = ValidationError.deserialize((Map<String, ?>) new JsonReader().read(json));
        assertEquals(error.toCommonFormat(), dError.toCommonFormat());
    }

    @Test
    public void testTextSerialization() {
        ValidationError error = new ValidationError("tool", "/file/name", 11, 3, "message", "evidence",
                Level.Error, "rule");
        String text = error.toCommonFormat();
        ValidationError dError = ValidationError.fromCommonFormat(text);
        assertEquals(error.toCommonFormat(), dError.toCommonFormat());
    }
}
