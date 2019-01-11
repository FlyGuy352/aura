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

import org.apache.commons.lang3.StringUtils;
import org.auraframework.adapter.ConfigAdapter;
import org.auraframework.adapter.DefinitionParserAdapter;
import org.auraframework.def.LocatorDef;
import org.auraframework.def.RootDefinition;
import org.auraframework.impl.root.locator.LocatorDefImpl;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.TextSource;
import org.auraframework.throwable.quickfix.QuickFixException;

import com.google.common.collect.ImmutableSet;

public class LocatorDefHandler<P extends RootDefinition> extends ParentedTagHandler<LocatorDef, P> {

    public static final String TAG = "aura:locator";

    private static final String ATTRIBUTE_TARGET = "target";
    private static final String ATTRIBUTE_DESCRIPTION = "description";
    private static final String ATTRIBUTE_ALIAS = "alias";
    private static final String ATTRIBUTE_ISPRIMITIVE = "isPrimitive";
    private static final String ANY_TARGET_SELECTOR = "*";
    private static final Set<String> ALLOWED_ATTRIBUTES = ImmutableSet.of(ATTRIBUTE_TARGET, ATTRIBUTE_DESCRIPTION, ATTRIBUTE_ALIAS, ATTRIBUTE_ISPRIMITIVE);

    private final LocatorDefImpl.Builder builder = new LocatorDefImpl.Builder();

    public LocatorDefHandler(XMLStreamReader xmlReader, TextSource<?> source, DefinitionService definitionService,
                             boolean isInInternalNamespace, ConfigAdapter configAdapter,
                             DefinitionParserAdapter definitionParserAdapter, ContainerTagHandler<P> parentHandler) {
        super(xmlReader, source, definitionService, isInInternalNamespace, configAdapter, definitionParserAdapter, parentHandler);
    }

    @Override
    public Set<String> getAllowedAttributes() {
        return LocatorDefHandler.ALLOWED_ATTRIBUTES;
    }

    @Override
    protected void handleChildTag() throws XMLStreamException, QuickFixException {
        String tag = getTagName();
        if (LocatorContextDefHandler.TAG.equals(tag)) {
            // to resolve expressions in locator context definitions, we need to pass in the component as the parent
            builder.addLocatorContext(new LocatorContextDefHandler<>(xmlReader, source,
                    definitionService, isInInternalNamespace, configAdapter, definitionParserAdapter, this.getParentHandler()).getElement());
        } else {
            error("Found unexpected tag inside aura:locator. %s", tag);
        }
    }

    @Override
    protected void handleChildText() throws XMLStreamException, QuickFixException {
        String text = xmlReader.getText();
        if (!StringUtils.isBlank(text)) {
            error("No literal text allowed in " + TAG);
        }
    }

    @Override
    protected void readAttributes() throws QuickFixException {

        String description = getAttributeValue(ATTRIBUTE_DESCRIPTION);
        String target = getAttributeValue(ATTRIBUTE_TARGET);
        String alias = getAttributeValue(ATTRIBUTE_ALIAS);
        String isPrimitive = getAttributeValue(ATTRIBUTE_ISPRIMITIVE);

        if (StringUtils.isBlank(target)) {
            error("The attribute '%s' is required on '<%s>'.", ATTRIBUTE_TARGET, TAG);
        }

        if (StringUtils.isBlank(description)) {
            error("The attribute '%s' is required on '<%s>'.", ATTRIBUTE_DESCRIPTION, TAG);
        }
        
        builder.setParentDescriptor(getParentHandler().getDefDescriptor());
        builder.setLocation(getLocation());
        builder.setDescription(getAttributeValue(ATTRIBUTE_DESCRIPTION));
        builder.setTarget(target);
        
        if (!StringUtils.isBlank(alias)) {
        	builder.setAlias(alias);
        } else if (target.equals(ANY_TARGET_SELECTOR)) {
            error (String.format("You must specify an alias when using the any target selector '%s' on '<%s>'", ANY_TARGET_SELECTOR, TAG));
        }
        
        if (!StringUtils.isBlank(isPrimitive)) {
            builder.setIsPrimitive(Boolean.valueOf(isPrimitive));
        }
    }

    @Override
    public String getHandledTag() {
        return LocatorDefHandler.TAG;
    }

    @Override
    protected void finishDefinition() throws QuickFixException {
    }

    @Override
    protected LocatorDef createDefinition() throws QuickFixException {
        return builder.build();
    }
}
