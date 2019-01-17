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
import org.auraframework.adapter.ExpressionBuilder;
import org.auraframework.builder.DefBuilder;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.TokenDef;
import org.auraframework.def.TokenDescriptorProviderDef;
import org.auraframework.def.TokenMapProviderDef;
import org.auraframework.def.TokensDef;
import org.auraframework.def.TokensImportDef;
import org.auraframework.expression.PropertyReference;
import org.auraframework.impl.css.token.TokensDefImpl;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.TextSource;
import org.auraframework.throwable.quickfix.InvalidAccessValueException;
import org.auraframework.throwable.quickfix.QuickFixException;

import com.google.common.collect.ImmutableSet;

/**
 * Handler for {@code aura:tokens} tags.
 */
public final class TokensDefHandler extends FileTagHandler<TokensDef> {
    public static final String TAG = "aura:tokens";
    private static final String ATTRIBUTE_EXTENDS = "extends";
    private static final String ATTRIBUTE_PROVIDER = "provider";
    private static final String ATTRIBUTE_MAP_PROVIDER = "mapProvider";
    private static final String ATTRIBUTE_SERIALIZE = "serialize";

    private static final Set<String> ALLOWED_ATTRIBUTES = new ImmutableSet.Builder<String>()
            .add(ATTRIBUTE_ACCESS, ATTRIBUTE_EXTENDS, ATTRIBUTE_SERIALIZE, RootTagHandler.ATTRIBUTE_API_VERSION)
            .addAll(RootTagHandler.ALLOWED_ATTRIBUTES)
            .build();

    private static final Set<String> INTERNAL_ALLOWED_ATTRIBUTES = new ImmutableSet.Builder<String>()
            .add(ATTRIBUTE_PROVIDER, ATTRIBUTE_MAP_PROVIDER)
            .addAll(ALLOWED_ATTRIBUTES)
            .addAll(RootTagHandler.INTERNAL_ALLOWED_ATTRIBUTES)
            .build();

    private final TokensDefImpl.Builder builder = new TokensDefImpl.Builder();

    public TokensDefHandler(XMLStreamReader xmlReader, TextSource<TokensDef> source,
                            DefinitionService definitionService, boolean isInInternalNamespace,
                            ConfigAdapter configAdapter, DefinitionParserAdapter definitionParserAdapter,
                            ExpressionBuilder expressionBuilder, DefDescriptor<TokensDef> defDescriptor) throws QuickFixException {
        super(xmlReader, source, definitionService, isInInternalNamespace, configAdapter, definitionParserAdapter, expressionBuilder, defDescriptor);
        builder.setOwnHash(source.getHash());
        builder.setDescriptor(defDescriptor);
        builder.setLocation(startLocation);
    }

    @Override
    public String getHandledTag() {
        return TAG;
    }

    @Override
    public Set<String> getAllowedAttributes() {
        return isInInternalNamespace() ? INTERNAL_ALLOWED_ATTRIBUTES : ALLOWED_ATTRIBUTES;
    }

    @Override
    public DefBuilder<TokensDef,TokensDef> getBuilder() {
        return builder;
    }

    @Override
    protected void readAttributes() throws QuickFixException {
        super.readAttributes();

        String parent = getAttributeValue(ATTRIBUTE_EXTENDS);
        if (!StringUtils.isBlank(parent)) {
            builder.setExtendsDescriptor(definitionService.getDefDescriptor(parent.trim(), TokensDef.class));
        }

        String provider = getAttributeValue(ATTRIBUTE_PROVIDER);
        if (!StringUtils.isBlank(provider)) {
            builder.setDescriptorProvider(definitionService.getDefDescriptor(provider, TokenDescriptorProviderDef.class));
        }

        String mapProvider = getAttributeValue(ATTRIBUTE_MAP_PROVIDER);
        if (!StringUtils.isBlank(mapProvider)) {
            builder.setMapProvider(definitionService.getDefDescriptor(mapProvider, TokenMapProviderDef.class));
        }

        String serialize = getAttributeValue(ATTRIBUTE_SERIALIZE);
        if (!StringUtils.isBlank(serialize)) {
            builder.setSerialize(Boolean.parseBoolean(serialize));
        }

        try {
            builder.setAccess(readAccessAttribute());
        } catch (InvalidAccessValueException e) {
            builder.setParseError(e);
        }
    }

    @Override
    protected boolean allowPrivateAttribute() {
        return true;
    }

    @Override
    protected void handleChildTag() throws XMLStreamException, QuickFixException {
        String tag = getTagName();

        if (TokenDefHandler.TAG.equalsIgnoreCase(tag)) {
            TokenDef def = new TokenDefHandler(xmlReader, source, definitionService, isInInternalNamespace,
                    configAdapter, definitionParserAdapter, expressionBuilder, this).getElement();
            if (builder.tokens().containsKey(def.getName())) {
                error("Duplicate token %s", def.getName());
            }
            builder.addTokenDef(def);

        } else if (isInInternalNamespace && TokensImportDefHandler.TAG.equalsIgnoreCase(tag)) {
            // imports must come before tokens. This is mainly for simplifying the token lookup implementation,
            // while still matching the most common expected usages of imports vs. declared tokens.
            if (!builder.tokens().isEmpty()) {
                error("tag %s must come before all declared tokens", TokensImportDefHandler.TAG);
            }

            TokensImportDef def = new TokensImportDefHandler(xmlReader, source, definitionService,
                    isInInternalNamespace, configAdapter, definitionParserAdapter, expressionBuilder, this).getElement();
            if (builder.imports().contains(def.getImportDescriptor())) {
                error("Duplicate import %s", def.getName());
            }
            builder.addImport(def.getImportDescriptor());
        } else {
            error("Found unexpected tag %s", tag);
        }
    }

    @Override
    protected void handleChildText() throws XMLStreamException, QuickFixException {
        if (!StringUtils.isBlank(xmlReader.getText())) {
            error("No literal text allowed in TokensDef");
        }
    }

    @Override
    public void addExpressionReferences(Set<PropertyReference> propRefs) {
        builder.addAllExpressionRefs(propRefs);
    }
}
