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
    init: function(cmp, event, helper) {
        helper.initialize(cmp);
    },
    createPanel: function (cmp, event, helper) {
        helper = cmp.getConcreteComponent().helper;
        helper.createPanel(cmp, event.getParams());
    },
    destroyPanel: function (cmp, event, helper) {
        helper = cmp.getConcreteComponent().helper;
        helper.destroyPanel(cmp, event.getParams());
    },
    handleNotify: function (cmp, event, helper) {
        var action = event.getParam('action'),
            intf   = event.getParam('typeOf');

        helper = cmp.getConcreteComponent().helper;

        if (action === 'destroyPanel' && intf === 'ui:destroyPanel') {
            event.stopPropagation();
            // W-5194732 panelManage2 may receive ui:notify which should send to panel.
            // Skip, If config doesn't provide panelInstance.
            var config = event.getParam('payload');
            if (config && config.panelInstance != null) {
                helper.destroyPanel(cmp, config);
            }            
        } else if (action === 'beforeShow' && intf === 'ui:panel') {
            helper.beforeShow(cmp, event.getParam('payload'));
        } else {
            helper.broadcastNotify(cmp, event.getSource(), event.getParams());
        }
    },
    getActivePanel: function (cmp, event, helper) {
        var callback = event.getParam('callback');
        if (callback) {
            helper.getActivePanel(cmp, callback);
        }
    },

    stackPanel: function(cmp, event, helper) {
        var callback = event.getParam('callback');
        if (callback) {
            helper.stackElement(callback);
        }
    },
    
    registerPanels: function (cmp, event, helper) {
        helper.registerPanels(cmp, event.getParams());
    }
})// eslint-disable-line semi