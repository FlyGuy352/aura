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

import static org.auraframework.impl.root.parser.handler.RootTagHandler.ATTRIBUTE_DESCRIPTION;

import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.auraframework.adapter.ConfigAdapter;
import org.auraframework.adapter.DefinitionParserAdapter;
import org.auraframework.def.TokensDef;
import org.auraframework.def.TokensImportDef;
import org.auraframework.impl.css.token.TokensImportDefImpl;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.TextSource;
import org.auraframework.throwable.quickfix.QuickFixException;

import com.google.common.collect.ImmutableSet;

public class TokensImportDefHandler extends ParentedTagHandler<TokensImportDef, TokensDef> {
    protected static final String TAG = "aura:import";
    private static final String ATTRIBUTE_NAME = "name";
    private static final Set<String> ALLOWED_ATTRIBUTES = ImmutableSet.of(ATTRIBUTE_NAME, ATTRIBUTE_DESCRIPTION);
    private final TokensImportDefImpl.Builder builder = new TokensImportDefImpl.Builder();

    public TokensImportDefHandler(XMLStreamReader xmlReader, TextSource<?> source, DefinitionService definitionService,
                                  boolean isInInternalNamespace, ConfigAdapter configAdapter,
                                  DefinitionParserAdapter definitionParserAdapter, TokensDefHandler parentHandler) {
        super(xmlReader, source, definitionService, isInInternalNamespace, configAdapter, definitionParserAdapter, parentHandler);
        this.builder.setLocation(getLocation());
        this.builder.setAccess(getAccess(isInInternalNamespace));
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
    protected void readAttributes() {
        String name = getAttributeValue(ATTRIBUTE_NAME);
        if (StringUtils.isBlank(name)) {
            error("Missing required attribute 'name' on ", TAG);
        }
        builder.setImportDescriptor(definitionService.getDefDescriptor(name, TokensDef.class));
        builder.setDescription(getAttributeValue(ATTRIBUTE_DESCRIPTION));
    }

    @Override
    protected void handleChildTag() throws XMLStreamException, QuickFixException {
        error("No children allowed for %s tag", TAG);
    }

    @Override
    protected void handleChildText() throws XMLStreamException, QuickFixException {
        if (!StringUtils.isBlank(xmlReader.getText())) {
            error("No literal text allowed in %s tag", TAG);
        }
    }

    @Override
    protected void finishDefinition() throws QuickFixException {
    }

    @Override
    protected TokensImportDef createDefinition() throws QuickFixException {
        return builder.build();
    }
}
