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
package org.auraframework.docs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.auraframework.annotations.Annotations.ServiceComponentModelInstance;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.DefDescriptor.DefType;
import org.auraframework.def.DescriptorFilter;
import org.auraframework.def.DocumentationDef;
import org.auraframework.ds.servicecomponent.ModelInstance;
import org.auraframework.pojo.Example;
import org.auraframework.service.ContextService;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.Annotations.AuraEnabled;

@ServiceComponentModelInstance
public class ExamplesModel implements ModelInstance {
    List<Map<String, String>> examples = new ArrayList<>();
    String message;

    public ExamplesModel(ContextService contextService, DefinitionService definitionService) throws Exception {

        String name = (String) contextService.getCurrentContext().getCurrentComponent().getAttributes()
                .getValue("name");

        if (name != null && !name.isEmpty()) {
            Set<DefDescriptor<?>> descriptors = definitionService.find(new DescriptorFilter("markup://" + name,
                    DefType.DOCUMENTATION.name()));
            if (descriptors.size() > 0) {
                for (DefDescriptor<?> descriptor : descriptors) {

                    DefType type = descriptor.getDefType();
                    switch (type) {
                    case DOCUMENTATION:
                        Map<String, String> m;

                        try {
                            DocumentationDef docDef = (DocumentationDef) definitionService.getDefinition(descriptor);

                            Collection<Example> exampleDescs = docDef.getExamples();

                            for (Example example : exampleDescs) {
                                m = new TreeMap<>();

                                m.put("label", example.getLabel());
                                m.put("description", example.getDescription());
                                m.put("ref", example.getRef());

                                examples.add(m);
                            }
                        } catch (Exception e) {
                            // only display errors in loading DocDefs in dev mode
                            if (contextService.getCurrentContext().isDevMode()) {
                                m = new TreeMap<>();
                                m.put("error", e.toString());
                                m.put("descriptor", descriptor.getDescriptorName());
                                examples.add(m);
                            }
                        }

                        break;
                    default: // not including other types in scan
                    }
                }

                if (examples.size() == 0) {
                    message = "This component or application has no examples.";
                }
            } else {
                message = "Example not available";
            }
        }
    }

    @AuraEnabled
    public List<Map<String, String>> getExamples() {
        return examples;
    }

    @AuraEnabled
    public String getMessage() {
        return message;
    }

}
