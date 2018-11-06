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
package org.auraframework.util.javascript.builder;

import org.auraframework.util.javascript.directive.JavascriptGeneratorMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EnvJavascriptBuilder extends JavascriptBuilder {


    public EnvJavascriptBuilder() {
        super(null);
    }

    @Override
    public List<JavascriptResource> build(JavascriptGeneratorMode mode, boolean isCompat, String inputContent, String outputFileName) throws IOException {
        String output = null;
        if (mode != JavascriptGeneratorMode.DOC && mode.isTestingMode()) {
            output = "typeof process === 'undefined' ? (process = { env: { NODE_ENV: 'test' } }) : process.env ? process.env.NODE_ENV = 'test' : process.env = { NODE_ENV: 'test' } ";
        }
        return Arrays.asList(new JavascriptResource(null, output, null));
    }
}
