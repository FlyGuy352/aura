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
({
    init: function (cmp, evt, helper) {
        var modelItemsKey, items;
        
        // Use for loading logic. 
        cmp._hasDataProvider = cmp._dataProviders && cmp._dataProviders.length > 0;

        // Attempt to extract the initial set of items. 
        if (cmp._hasDataProvider) {
            modelItemsKey = cmp._dataProviders[0].get('v.modelItemsKey');	

            if (modelItemsKey) {
                items = cmp.get("v.dataProvider.0").get("m." + modelItemsKey);
                cmp.set('v.items', items, true);
            }
        }
        
        helper.initializeCaches(cmp);
        helper.initializeNewColumns(cmp);
        helper.updateColumnAttributes(cmp);
    },

    handleItemsChange: function (cmp, evt, helper) {		
        if (!cmp.isRendered()) {
            helper.updateColumnAttributes(cmp);
            return;
        }
        
        helper.handleItemsChange(cmp, evt.getParams());
        helper.updateColumnAttributes(cmp);
    },
    
    handleColumnsChange: function(cmp, evt, helper) {
        helper.initializeNewColumns(cmp);
    },

    handleColumnSortChange: function (cmp, evt) {
        if (evt) {
            cmp.getSuper().set('v.sortBy', evt);
        }
    },

    handleClick: function (cmp, evt, helper) {
        if (evt.target) {
            var name = $A.util.getDataAttribute(evt.target, 'action-name'); 

            if (name) {
                helper.handleAction(cmp, {
                    name 		: name,
                    index   	: $A.util.getDataAttribute(evt.target, 'item-index'),
                    value 		: $A.util.getDataAttribute(evt.target, 'action-value'),
                    globalId 	: $A.util.getDataAttribute(evt.target, 'action-global-id')
                });
            }
        }
    },
    
    handleUpdateRowAttrs: function(cmp, evt, helper) {
        var params = evt.getParams();
        var rowData = cmp.find("tableRow")[params.index];

        if (params.className && params.classOp) {
            helper.updateRowClass(cmp, rowData, params);
        }
        
        if (params.attributes) {
            helper.updateValueProvider(cmp, rowData, params.attributes);
        }
    }
})// eslint-disable-line semi