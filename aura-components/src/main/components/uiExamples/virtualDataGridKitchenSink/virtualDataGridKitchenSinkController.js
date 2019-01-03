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
	focusNext : function(cmp, evt, helper) {
		var current = cmp._currentIndex;
		if (!current) {
			current = 1;
		}
		cmp.getElement().querySelectorAll('input')[current].focus();
		cmp._currentIndex = current + 1;
	},
	
	handleGridEvents : function(cmp, evt, helper) {
		if (evt.getParam("action") === 'select') {
			var index = evt.getParam("index") || -1;
			var payload = evt.getParam("payload");
			
			var prefix = payload.value ? 'Selected' : 'Deselected';
			var subject = payload.selectedItem ? payload.selectedItem.subject : "Header"
			
			helper.updateSelectedItems(cmp, prefix, index);
			alert(prefix + ' ' + subject + ' @ ' + index);
		} else {
			alert(evt);
		}
	},

	init : function(cmp, evt, helper) {
		$A.metricsService.enablePlugins();
		helper.generateColumnConfigs(cmp);
	},
	
    spit : function(cmp, evt, helper) {
    	var list = cmp.find("grid").get("v.items");
		cmp.set("v.gridItems", list);
	},
	
	replaceData : function(cmp, evt, helper) {
		$A.metricsService.transactionStart("vDataGridKitchenSink","replace",{context:{}});

		cmp.find("data").set("v.empty", false);
		cmp.find("data").getEvent("provide").fire();
		
		$A.metricsService.transactionEnd("vDataGridKitchenSink","replace",{
			postProcess: function (transaction) {
				return transaction;
			}
		});
	},
	
	emptyData : function(cmp, evt, helper) {
		$A.metricsService.transactionStart("vDataGridKitchenSink","empty",{context:{}});
		cmp.find("data").set("v.empty", true);
		cmp.find("data").getEvent("provide").fire();
		$A.metricsService.transactionEnd("vDataGridKitchenSink","empty",{
			postProcess: function (transaction) {
				return transaction;
			}
		});
	},
	
	switchColumn : function(cmp, evt, helper) {
		var grid = cmp.find("grid"),
			colIndex = 4,
			headers = grid.get("v.headerColumns"),
			columns = grid.get("v.columns"),
			config = (headers[colIndex].get("v.name") === "activityDate") ?
				cmp._columnConfigs.who : cmp._columnConfigs.activityDate;
		
		$A.newCmpAsync(
				this, 
				function setheaderColumns(newCmp) {
					headers[4] = newCmp;
					grid.set("v.headerColumns", headers);
				},
				config.header
		);
		columns[colIndex] = config.column;
		grid.set("v.columns", columns);
	},
	
	onsort : function(cmp, evt, helper) {
		var params = evt.getParams();

		params.callback({
			data : ["hi", "bye"],
			state : "State",
			error : {}
		});
	},

	updateItem: function(cmp, event, helper){
		
		var grid = cmp.find('grid');
		var selectedItems = cmp.get('v.gridSelectedItems');

		if (selectedItems.length === 0){
			alert('Please select a row to update!');
		} else {
			var index, visualIndex;
			
			for (var i=0;i<selectedItems.length;i++){
				index = selectedItems[i];
				visualIndex = index+1;	
				var item = {
	                id           : visualIndex,
	                subject      : 'Changed ' + visualIndex, 
	                activityDate : helper.generateRandomDateString().toString(),
	                who          : {
	                    name : 'Changed ' + visualIndex,
	                    id   : '00' + visualIndex
	                },
	                what: {
	                    name : 'Acme' + visualIndex,
	                    id   : '00' + visualIndex
	                },
	                url: 'https://www.google.com'
				}
			
				grid.updateItem(item,index);
			}
		}
	}
})