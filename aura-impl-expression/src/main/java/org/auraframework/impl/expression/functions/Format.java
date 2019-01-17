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
package org.auraframework.impl.expression.functions;

import java.text.MessageFormat;
import java.util.List;

/**
 * Format is meant to match format() in Util.js, except
 * that we safeguard against missing, undefined, or null
 * format string.
 *
 * Since expressions are exposed to the UI, we try to do
 * the most sensible thing, and prevent the display of nulls
 * and undefined like we do with the ADD function.
 */
public class Format extends BaseMultiFunction {

    private static final long serialVersionUID = -7261120970634674388L;
    
    private static final Format INSTANCE = new Format();
    
    /**
     * @return An instance of {@link Format}
     */
    public static final Format getInstance() {
        return INSTANCE;
    }
    
    private Format() {
        // Make sure there is only 1 instance
    }

    @Override
    public Object evaluate(List<Object> args) {
        int size = args.size();
        if (size == 0) {
            return "";
        }

        Object a0 = args.get(0);
        if (a0 == null) {
            return "";
        }

        String formatString = JavascriptHelpers.stringify(a0);
        if (size == 1) {
            return formatString;
        }

        Object[] formatArguments = new Object[size - 1];
        for (int index = 1; index < size; index++) {
            Object ai = args.get(index);
            formatArguments[index - 1] = (ai == null) ? "" : JavascriptHelpers.stringify(ai);
        }

        return MessageFormat.format(formatString, formatArguments);
    }

    @Override
    public String getJsFunction() {
        return "fn.format";
    }

    @Override
    public String[] getKeys() {
        return new String[] { "format" };
    }
}