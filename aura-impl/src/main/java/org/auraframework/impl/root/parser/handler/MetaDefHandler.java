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
package org.auraframework.impl.root.parser.handler;

import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.auraframework.pojo.Meta;
import org.auraframework.system.TextSource;
import org.auraframework.throwable.quickfix.InvalidAccessValueException;
import org.auraframework.throwable.quickfix.QuickFixException;

import com.google.common.collect.ImmutableSet;

/**
 * Parses <aura:meta> tags
 */
public class MetaDefHandler extends BaseXMLElementHandler {

    public static final String TAG = "aura:meta";

    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final Set<String> ALLOWED_ATTRIBUTES = new ImmutableSet.Builder<String>().add(ATTRIBUTE_NAME,
            ATTRIBUTE_VALUE).addAll(RootTagHandler.ALLOWED_ATTRIBUTES).build();

    private String name;
    private String value;

    public MetaDefHandler(DocumentationDefHandler parentHandler, XMLStreamReader xmlReader, TextSource<?> source) {
        super(xmlReader, source);
    }

    @Override
    public String getHandledTag() {
        return TAG;
    }

    @Override
    public Set<String> getAllowedAttributes() {
        return ALLOWED_ATTRIBUTES;
    }

    @Override
    protected void handleChildTag() throws XMLStreamException,
            QuickFixException {
        // No child. Do nothing.
    }

    @Override
    protected void handleChildText() throws XMLStreamException,
            QuickFixException {
        // No child. Do nothing.
    }

    @Override
    protected void readAttributes() throws InvalidAccessValueException {
        this.name = getAttributeValue(ATTRIBUTE_NAME);
        this.value = getAttributeValue(ATTRIBUTE_VALUE);
    }

    public Meta getElement() throws QuickFixException, XMLStreamException {
        readElement();
        Meta meta = new Meta(this.name, this.value, this.startLocation);
        meta.validate();
        return meta;
    }
}
