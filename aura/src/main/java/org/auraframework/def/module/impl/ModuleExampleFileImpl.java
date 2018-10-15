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
package org.auraframework.def.module.impl;

import org.auraframework.def.module.ModuleExampleFile;

public class ModuleExampleFileImpl implements ModuleExampleFile {


	private static final long serialVersionUID = -2091010591408069905L;
	private String name;
	private String contents;

	public  ModuleExampleFileImpl(String name, String contents) {
		this.name = name;
		this.contents = contents;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getContent() {
		return contents;
	}

}
