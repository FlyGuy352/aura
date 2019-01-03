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
package org.auraframework.impl.factory;

import java.util.EnumSet;

import org.auraframework.annotations.Annotations.ServiceComponent;
import org.auraframework.builder.BaseComponentDefBuilder;
import org.auraframework.def.BaseComponentDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.impl.root.parser.handler.RootTagHandler;
import org.auraframework.impl.service.CompilerServiceImpl;
import org.auraframework.system.ApiVersioned;
import org.auraframework.system.BundleSource;
import org.auraframework.system.BundleSourceOption;
import org.auraframework.system.CompileOptions;
import org.auraframework.throwable.quickfix.QuickFixException;

@ServiceComponent
public abstract class BaseComponentDefFactory<T extends BaseComponentDef> extends BundleBaseFactory<T> {
    @Override
    public T getDefinition(DefDescriptor<T> descriptor, BundleSource<T> source) throws QuickFixException {
        return this.getDefinition(descriptor, source, CompilerServiceImpl.DEFAULT_COMPILE_OPTIONS);
    }

    @Override
    public T getDefinition(DefDescriptor<T> descriptor, BundleSource<T> source, CompileOptions compileOptions)
            throws QuickFixException {
        RootTagHandler<T> handler = getDefinitionBuilder(descriptor, source);
        if (handler == null) {
            return null;
        }
        BaseComponentDefBuilder<T> builder = (BaseComponentDefBuilder<T>)handler.getBuilder();
        EnumSet<BundleSourceOption> bundleOptions = compileOptions.getSourceOptions();
        builder.setMinifyEnabled(bundleOptions.contains(BundleSourceOption.Minify));

        if (source instanceof ApiVersioned){
            builder.setAPIVersion(((ApiVersioned)source).getAPIVersion());
        }
        return builder.build();
    }
}
