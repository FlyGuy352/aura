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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.auraframework.adapter.ConfigAdapter;
import org.auraframework.adapter.DefinitionParserAdapter;
import org.auraframework.adapter.ExpressionBuilder;
import org.auraframework.def.ComponentDef;
import org.auraframework.def.ComponentDefRef;
import org.auraframework.def.Definition;
import org.auraframework.def.DefinitionReference;
import org.auraframework.impl.root.AttributeDefRefImpl;
import org.auraframework.impl.root.component.ComponentDefRefImpl;
import org.auraframework.impl.root.component.ComponentDefRefImpl.Builder;
import org.auraframework.impl.root.component.DefRefDelegate;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.TextSource;
import org.auraframework.throwable.quickfix.QuickFixException;

/**
 * Handles all references to other components. Note that while the reference to the other component is created here, it
 * is not validated until the
 * {@link ComponentDefRefImpl#validateReferences(org.auraframework.validation.ReferenceValidationContext)} method is
 * called by loading registry.
 */
public class ComponentDefRefHandler<P extends Definition> extends BaseDefRefHandler<ComponentDefRef, P, ComponentDef, ComponentDefRefImpl.Builder> {

    public ComponentDefRefHandler(XMLStreamReader xmlReader, TextSource<?> source, DefinitionService definitionService,
                                  boolean isInInternalNamespace, ConfigAdapter configAdapter,
                                  DefinitionParserAdapter definitionParserAdapter, ExpressionBuilder expressionBuilder,
                                  ContainerTagHandler<P> parentHandler) {
        super(xmlReader, source, definitionService, isInInternalNamespace, configAdapter, definitionParserAdapter, expressionBuilder, parentHandler);
    }

    @Override
    protected Builder createBuilder() {
        Builder builder = new ComponentDefRefImpl.Builder();
        builder.setDescriptor(definitionService.getDefDescriptor(getTagName(), ComponentDef.class));
        return builder;
    }

    /**
     * Expects either Set tags or ComponentDefRefs
     */
    @Override
    protected void handleChildTag() throws XMLStreamException, QuickFixException {

        String tag = getTagName();
        if (AttributeDefRefHandler.TAG.equalsIgnoreCase(tag)) {
            AttributeDefRefImpl attributeDefRef = new AttributeDefRefHandler<>(xmlReader, source, definitionService,
                    isInInternalNamespace, configAdapter, definitionParserAdapter, expressionBuilder, getParentHandler())
                    .getElement();
            builder.setAttribute(attributeDefRef.getDescriptor(), attributeDefRef);
        } else {
            ComponentDefRef componentDefRef = getDefRefHandler(getParentHandler()).getElement();
            if (componentDefRef.isFlavorable() || componentDefRef.hasFlavorableChild()) {
                builder.setHasFlavorableChild(true);
            }
            DefinitionReference defRef = new DefRefDelegate(componentDefRef);
            body.add(defRef);
        }
    }

    @Override
    public String getHandledTag() {
        return "Component Reference";
    }
}
