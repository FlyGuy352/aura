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
package org.auraframework.test.instance;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.auraframework.adapter.ConfigAdapter;
import org.auraframework.def.ActionDef;
import org.auraframework.def.ControllerDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.instance.AbstractActionImpl;
import org.auraframework.instance.Action;
import org.auraframework.instance.InstanceStack;
import org.auraframework.system.LoggingContext;
import org.auraframework.throwable.AuraExecutionException;
import org.auraframework.util.json.Json;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AbstractActionImplTest {
    //
    // A class to remove the 'abstract'
    //
    // We do not verify any of the actual functions here, except to use run to change the state.
    //
    private static class MyAction extends AbstractActionImpl<ActionDef> {
        public MyAction(DefDescriptor<ControllerDef> controllerDescriptor, ActionDef actionDef,
                Map<String, Object> paramValues) {
            this(Mockito.mock(ConfigAdapter.class), controllerDescriptor, actionDef, paramValues);
        }

        private MyAction(ConfigAdapter configAdapter, DefDescriptor<ControllerDef> controllerDescriptor,
                ActionDef actionDef, Map<String, Object> paramValues) {
            super(controllerDescriptor,  actionDef, paramValues, configAdapter);
        }

        @Override
        public void run() throws AuraExecutionException {
        }

        @Override
        public Object getReturnValue() {
            return null;
        }

        @Override
        public List<Object> getErrors() {
            return null;
        }

        @Override
        public void serialize(Json json) throws IOException {
        }

        public void setState(State state) {
            this.state = state;
        }
    };

    @Test
    public void testId() {
        ActionDef def = Mockito.mock(ActionDef.class);
        Action test = new MyAction(null, def, null);

        assertThat("id should be initialized to null", test.getId(), nullValue());
        test.setId("a");
        assertThat("setId should work the first time.", test.getId(), equalTo("a"));
        test.setId("b");
        assertThat("setId should work a second time.", test.getId(), equalTo("b"));
        test.setId(null);
        assertThat("setId should work a third time.", test.getId(), nullValue());
    }

    private Action getActionWithId(String id) {
        ActionDef def = Mockito.mock(ActionDef.class);
        Action test = new MyAction(null, def, null);
        test.setId(id);
        return test;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testActions() {
        ActionDef def = Mockito.mock(ActionDef.class);
        Action test = new MyAction(null, def, null);

        List<Action> actions = test.getActions();
        assertThat("Actions should empty", actions, empty());

        List<Action> newActions = Lists.newArrayList(getActionWithId("a"), getActionWithId("b"));
        test.add(newActions);
        actions = test.getActions();
        assertThat("Incorrect actions returned", actions,
            contains(
                hasProperty("id", equalTo("a")),
                hasProperty("id", equalTo("b"))
            )
        );

        newActions = Lists.newArrayList(getActionWithId("c"), getActionWithId("d"));
        test.add(newActions);
        actions = test.getActions();
        assertThat("Incorrect actions returned", actions,
            contains(
                hasProperty("id", equalTo("a")),
                hasProperty("id", equalTo("b")),
                hasProperty("id", equalTo("c")),
                hasProperty("id", equalTo("d"))
            )
        );
    }

    @Test
    public void testState() {
        ActionDef def = Mockito.mock(ActionDef.class);
        MyAction test = new MyAction(null, def, null);

        Assert.assertEquals("state should be initialized to new", Action.State.NEW, test.getState());
        test.setState(Action.State.RUNNING);
        Assert.assertEquals("state should be able to change", Action.State.RUNNING, test.getState());
    }

    @Test
    public void testStorable() {
        ActionDef def = Mockito.mock(ActionDef.class);
        Action test = new MyAction(null, def, null);

        assertThat("isStorable should be initialized to false", test.isStorable(), equalTo(false));
        test.setStorable();
        assertThat("isStorable should change on setStorable", test.isStorable(), equalTo(true));
        test.setStorable();
        assertThat("isStorable should not change on second setStorable", test.isStorable(), equalTo(true));
    }

    @Test
    public void testOfflineAction() {
        ActionDef def = Mockito.mock(ActionDef.class);
        MyAction test = new MyAction(null, def, null);

        Assert.assertEquals("isOfflineAction should be initialized to false", false, test.isOfflineAction());
        test.markOfflineAction();
        Assert.assertEquals("isOfflineAction should change on markOfflineAction", true, test.isOfflineAction());
        Assert.assertEquals("id should change on markOfflineAction", "s", test.getId());
        test.setId("x");
        test.markOfflineAction();
        Assert.assertEquals("isOfflineAction should not change on second markOfflineAction", true, test.isOfflineAction());
        Assert.assertEquals("id should not change on second markOfflineAction", "x", test.getId());
    }

    @Test
    public void testDescriptor() {
        ActionDef def = Mockito.mock(ActionDef.class);
        Action test = new MyAction(null, def, null);
        @SuppressWarnings("unchecked")
        DefDescriptor<ActionDef> expectedDesc = Mockito.mock(DefDescriptor.class);
        Mockito.when(def.getDescriptor()).thenReturn(expectedDesc);

        Assert.assertSame("descriptor should work", expectedDesc, test.getDescriptor());
    }

    @Test
    public void testParams() {
        Map<String, Object> params = Maps.newHashMap();
        ActionDef def = Mockito.mock(ActionDef.class);
        Action test = new MyAction(null, def, params);
        LoggingContext.KeyValueLogger logger = Mockito.mock(LoggingContext.KeyValueLogger.class);

        assertThat("params should be initialized", test.getParams(), sameInstance(params));

        params.put("a", "b");
        test.logParams(logger);
        // logable values of null should avoid calls to the logger.
        Mockito.verifyNoMoreInteractions(logger);

        Mockito.when(def.getLoggableParams()).thenReturn(Lists.newArrayList("a", "b"));
        test.logParams(logger);
        Mockito.verify(logger, Mockito.times(1)).log("a", "b");
        Mockito.verify(logger, Mockito.times(1)).log("b", "null");
        Mockito.verifyNoMoreInteractions(logger);

        test = new MyAction(null, def, null);
        // Rebuild logger to check the logging correctly.
        logger = Mockito.mock(LoggingContext.KeyValueLogger.class);
        Assert.assertEquals("null params results in empty params", 0, test.getParams().size());
        test.logParams(logger);
        Mockito.verify(logger, Mockito.times(1)).log("a", "null");
        Mockito.verify(logger, Mockito.times(1)).log("b", "null");
        Mockito.verifyNoMoreInteractions(logger);
    }

    @Test
    public void testInstanceStack() {
        ActionDef def = Mockito.mock(ActionDef.class);
        Action test = new MyAction(null, def, null);
        InstanceStack iStack = test.getInstanceStack();
        Assert.assertEquals("Instance stack should be initialized without action ID as path", "/*[0]", iStack.getPath());
        Assert.assertEquals("Subsequent calls to getInstanceStack should return same InstanceStack", iStack,
                test.getInstanceStack());
    }

    /**
     * Verify we can set an Id after we get an InstaceStack. Used to threw Exception, now valid.
     */
    @Test
    public void testSetIdWithInstanceStackSet() {
        ActionDef def = Mockito.mock(ActionDef.class);
        Action test = new MyAction(null, def, null);
        test.getInstanceStack();
        test.setId("newId");
    }
}
