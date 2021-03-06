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
package org.auraframework.components.test.java.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.auraframework.annotations.Annotations.ServiceComponent;
import org.auraframework.def.ComponentDef;
import org.auraframework.ds.servicecomponent.Controller;
import org.auraframework.instance.Component;
import org.auraframework.service.InstanceService;
import org.auraframework.system.Annotations.AuraEnabled;
import org.auraframework.system.Annotations.BackgroundAction;
import org.auraframework.system.Annotations.Key;
import org.auraframework.system.Location;
import org.auraframework.throwable.AuraHandledException;
import org.auraframework.throwable.AuraRuntimeException;
import org.auraframework.throwable.GenericEventException;
import org.auraframework.util.date.DateOnly;
import org.springframework.context.annotation.Lazy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@ServiceComponent
public class JavaTestController implements Controller {

    @Inject
    @Lazy
    private InstanceService instanceService;

    @AuraEnabled
    public void noArgs() {
    }

    @AuraEnabled
    public Object getComponents(@Key("token") String token, @Key("input") String input) throws Exception {
        int count = input == null ? 1 : Integer.parseInt(input);
        List<Component> cmps = new LinkedList<>();
        while (count-- > 0) {
            Object val = token + ":java:" + count;
            Map<String, Object> atts = ImmutableMap.of("value", val);
            Component cmp = instanceService.getInstance("auratest:text", ComponentDef.class, atts);
            cmps.add(cmp);
        }
        return cmps.toArray();
    }

    @AuraEnabled
    public String getString(@Key("param") String param) throws Exception {
        return param;
    }

    @AuraEnabled
    public int getInt(@Key("param") int param) throws Exception {
        return param;
    }

    @AuraEnabled
    public String getLoggableString(@Key(value = "param", loggable = true) String param) throws Exception {
        return param;
    }

    @AuraEnabled
    public int getLoggableInt(@Key(value = "param", loggable = true) int param) throws Exception {
        return param;
    }

    @AuraEnabled
    public String getSelectedParamLogging(@Key(value = "strparam", loggable = true) String strparam,
                                          @Key(value = "intparam") int intparam) {
        return strparam;
    }

    @AuraEnabled
    public String getMultiParamLogging(@Key(value = "we", loggable = true) String we,
                                       @Key(value = "two", loggable = true) int two) {
        return we + two;
    }

    @AuraEnabled
    public int getExplicitExcludeLoggable(@Key(value = "param", loggable = false) int param) {
        return param;
    }

    @AuraEnabled
    public String getCustomParamLogging(@Key(value = "param", loggable = true) CustomParamType param) {
        return "Anything";
    }

    public static class CustomParamType {
        @Override
        public String toString() {
            return "CustomParamType_toString";
        }
    }

    @AuraEnabled
    public Map<String,Object> getSelectedObjectParamLogging(@Key(value = "logparam", loggable = true) Map<String,Object> logparam,
                                          @Key(value = "strparam", loggable = true) String strparam,
                                          @Key(value = "otherparam") Map<String,Object> otherparam) {
        return logparam;
    }

    /**
     * Note: these cases are pretty specific to js://test.testActionExceptions
     * 
     * @param exceptionType What type (class) of exception to throw
     * @param cause Cause parameter of Exception. Either a class of type Throwable or String
     */
    @AuraEnabled
    public void throwsThrowable(@Key("type") String exceptionType, @Key("cause") String cause) throws Throwable {
        if (exceptionType.equals("java.lang.Throwable")) {
            throw new Throwable(cause);
        } else if (exceptionType.equals("java.lang.RuntimeException")) {
            throw new RuntimeException(new IllegalAccessException());
        } else if (exceptionType.equals("java.lang.Error")) {
            throw new Error(new RuntimeException());
        } else if (exceptionType.equals("java.lang.reflect.InvocationTargetException")) {
            throw new InvocationTargetException(new IllegalArgumentException());
        } else if (exceptionType.equals("java.lang.IllegalArgumentException")) {
            throw new IllegalArgumentException(cause);
        } else if (exceptionType.equals("java.lang.IllegalAccessException")) {
            throw new IllegalAccessException(cause);
        } else if (exceptionType.equals("java.lang.reflect.InvocationTargetException")) {
            if (cause.equals("java.lang.IllegalArgumentException")) {
                throw new InvocationTargetException(new IllegalArgumentException());
            } else if (cause.equals("aura.throwable.AuraHandledException")) {
                throw new InvocationTargetException(new AuraHandledException(""));
            }
        } else if (exceptionType.equals("aura.throwable.AuraHandledException")) {
            if (cause.equals("java.lang.IllegalArgumentException")) {
                throw new AuraHandledException(new IllegalArgumentException());
            } else {
                throw new AuraHandledException(cause);
            }
        } else {
            throw new RuntimeException();
        }
    }

    @AuraEnabled
    public void throwsCSE(@Key("event") String event, @Key("paramName") String paramName,
                          @Key("paramValue") String paramValue) throws Throwable {
        GenericEventException gee = new GenericEventException(event);
        if (paramName != null) {
            gee.addParam(paramName, paramValue);
        }
        throw gee;
    }

    @AuraEnabled
    public void throwsException(@Key("errorMsg") String errorMsg) throws Exception {
        throw new AuraHandledException(errorMsg);
    }

    private static Map<String, StringBuffer> buffers = Maps.newLinkedHashMap();

    @AuraEnabled
    public String getBuffer() throws Exception {
        String id = UUID.randomUUID().toString();
        buffers.put(id, new StringBuffer());
        return id;
    }

    @AuraEnabled
    public void deleteBuffer(@Key("id") String id) throws Exception {
        buffers.remove(id);
    }

    /**
     * Wait for delayMs milliseconds and then return a auratest:text component whose value is the current buffer
     * contents plus the current append.
     */
    @AuraEnabled
    public Component appendBuffer(@Key("id") String id, @Key("delayMs") BigDecimal delayMs,
                                  @Key("append") String append) throws Exception {
        StringBuffer buffer = buffers.get(id);
        buffer.append(append);
        long delay = delayMs.longValue();
        if (delay > 0) {
            Thread.sleep(delay);
        }
        Map<String, Object> atts = ImmutableMap.of("value", (Object) (buffer + "."));
        return instanceService.getInstance("auratest:text", ComponentDef.class, atts);
    }

    @AuraEnabled
    public Boolean echoCheckbox(@Key("inVar") Boolean inVar) {
        return inVar;
    }

    @AuraEnabled
    public BigDecimal echoCurrency(@Key("inVar") BigDecimal inVar) {
        return inVar;
    }

    @AuraEnabled
    public BigDecimal echoDecimal(@Key("inVar") BigDecimal inVar) {
        return inVar;
    }

    @AuraEnabled
    public DateOnly echoDate(@Key("inVar") DateOnly inVar) {
        return inVar;
    }

    @AuraEnabled
    public Date echoDateTime(@Key("inVar") Date inVar) {
        return inVar;
    }

    @AuraEnabled
    public String echoEmail(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    public long echoNumber(@Key("inVar") long inVar) {
        return inVar;
    }

    @AuraEnabled
    public String echoNumberString(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    public Boolean echoOption(@Key("inVar") Boolean inVar) {
        return inVar;
    }

    @AuraEnabled
    public BigDecimal echoPercent(@Key("inVar") BigDecimal inVar) {
        return inVar;
    }

    @AuraEnabled
    public String echoPhone(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    public String echoPicklist(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    public String echoSearch(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    public String echoSecret(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    public String echoSelect(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    public String echoSelectMulti(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    public Boolean echoSelectOption(@Key("inVar") Boolean inVar) {
        return inVar;
    }

    @AuraEnabled
    public String echoText(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    @BackgroundAction
    public String echoTextBackground(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    public String echoTextArea(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    public String echoUrl(@Key("inVar") String inVar) {
        return inVar;
    }

    @AuraEnabled
    public void throwExceptionNoLineNums() {
        Location loc = new Location("test-filename", 123456789);
        AuraRuntimeException e = new AuraRuntimeException("throwExceptionNoLineNums", loc);
        throw e;
    }

    @AuraEnabled
    public void throwExceptionWithLineNums() {
        Location loc = new Location("test-filename", 4444, 55555, 123456789);
        AuraRuntimeException e = new AuraRuntimeException("throwExceptionNoLineNums", loc);
        throw e;
    }

    @AuraEnabled
    public void dummy() {
    }

    @SuppressWarnings("rawtypes")
    @AuraEnabled
    public List<Map> getList(@Key("start") int start, @Key("limit") int limit) throws Exception {
        List<Map> myList = new ArrayList<>();
        for (int i = start; i < limit; i++) {
            char alphabet = (char) (65 + (i % 26));
            Map<String, String> row = new HashMap<>();
            row.put("index", (i + 1) + "");
            row.put("char", "server " + alphabet);
            myList.add(row);
        }
        return myList;
    }

    @SuppressWarnings("rawtypes")
    @AuraEnabled
    public Map echoMap(@Key("map") Map map) {
        return map;
    }
}

