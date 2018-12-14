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
package org.auraframework.impl;

import java.util.Map;

import org.antlr.misc.MutableInteger;
import org.auraframework.adapter.ConfigAdapter;
import org.auraframework.adapter.ContextAdapter;
import org.auraframework.annotations.Annotations.ServiceComponent;
import org.auraframework.def.BaseComponentDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.DefDescriptor.DefType;
import org.auraframework.impl.context.AuraContextImpl;
import org.auraframework.instance.GlobalValueProvider;
import org.auraframework.instance.GlobalValueProviderFactory;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.AuraContext;
import org.auraframework.system.AuraContext.Authentication;
import org.auraframework.system.AuraContext.Format;
import org.auraframework.system.AuraContext.Mode;
import org.auraframework.system.RegistrySet;
import org.auraframework.test.TestContextAdapter;
import org.auraframework.throwable.AuraRuntimeException;
import org.auraframework.util.json.JsonSerializationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 */
@ServiceComponent
public class ContextAdapterImpl implements ContextAdapter {

    private final ConfigAdapter configAdapter;
    private final DefinitionService definitionService;
    private final GlobalValueProviderFactory globalValueProviderFactory;
    
    @Autowired(required=false)
    private TestContextAdapter testContextAdapter;
   
    private final static ThreadLocal<AuraContext> currentContext = new ThreadLocal<>();
    
    private final static ThreadLocal<AuraContext> systemContext = new ThreadLocal<>();

    private final static ThreadLocal<MutableInteger> systemDepth = new ThreadLocal<>();

    @Autowired
    @Lazy
    public ContextAdapterImpl(final ConfigAdapter configAdapter, final DefinitionService definitionService, final GlobalValueProviderFactory globalValueProviderFactory) {
        this.configAdapter = configAdapter;
        this.definitionService = definitionService;
        this.globalValueProviderFactory = globalValueProviderFactory;
    }

    @Override
    public AuraContext establish(Mode mode, RegistrySet registries,
                                 Map<DefType, String> defaultPrefixes, Format format, Authentication access,
                                 JsonSerializationContext jsonContext,
                                 Map<String, GlobalValueProvider> globalProviders,
                                 DefDescriptor<? extends BaseComponentDef> appDesc) {
        return establish(mode, registries, format, access, jsonContext, globalProviders, appDesc);
    }
    
    @Override
    public AuraContext establish(Mode mode, RegistrySet registries, Format format, Authentication access,
                                 JsonSerializationContext jsonContext, Map<String, GlobalValueProvider> globalProviders,
                                 DefDescriptor<? extends BaseComponentDef> appDesc) {
        
        final AuraContext context;
        if ((globalProviders == null) || globalProviders.isEmpty()) {
            context = new AuraContextImpl(mode, registries, format, access, jsonContext, configAdapter,
                    definitionService, testContextAdapter, globalValueProviderFactory);
        } else {
            context = new AuraContextImpl(mode, registries, null, format, access, jsonContext,
                    globalProviders, configAdapter, definitionService, testContextAdapter);
        }
        currentContext.set(context);
        context.setApplicationDescriptor(appDesc);
        
        return context;
    }

    @Override
    public AuraContext pushSystemContext() {
        MutableInteger count = systemDepth.get();
        AuraContext context = currentContext.get();
        
        if (count == null) {
            count = new MutableInteger(1);
            systemDepth.set(count);
        } else {
            count.value += 1;
        }
        if (count.value == 1) {
            context.setSystemMode(true);
        }
        return context;
    }

    @Override
    public void popSystemContext() {
        AuraContext context = currentContext.get();
        MutableInteger count = systemDepth.get();

        if (count == null || count.value == 0) {
            throw new AuraRuntimeException("unmatched pop");
        }
        count.value -= 1;
        if (count.value == 0) {
            context.setSystemMode(false);
    }
    }
    
    @Override
    public AuraContext getCurrentContext() {
        return currentContext.get();
    }

    @Override
    public boolean isEstablished() {
        return currentContext.get() != null;
    }

    @Override
    public void release() {
        currentContext.set(null);
        systemContext.set(null);

        MutableInteger count = systemDepth.get();
        if (count != null && count.value != 0) {
            throw new AuraRuntimeException("unmatched push");
        }
    }
}
