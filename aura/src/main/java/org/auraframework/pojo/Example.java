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
package org.auraframework.pojo;

import java.io.Serializable;

/**
 * aura:description 
 * Holds aura:documentation example
 */
public class Example implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String ref;
    private final String name;
    private final String label;
    private final String description;
    
    public Example(String name, String label, String ref, String description) {
        this.name = name;
        this.label = label;
        this.ref = ref;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }
    
    public String getRef() {
        return ref;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
