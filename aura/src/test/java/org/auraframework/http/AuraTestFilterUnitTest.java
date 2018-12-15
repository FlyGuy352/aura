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
package org.auraframework.http;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.auraframework.adapter.ConfigAdapter;
import org.auraframework.adapter.ServletUtilAdapter;
import org.auraframework.def.ApplicationDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.DefDescriptor.DefType;
import org.auraframework.def.TestCaseDef;
import org.auraframework.def.TestSuiteDef;
import org.auraframework.service.ContextService;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.AuraContext;
import org.auraframework.system.AuraContext.Format;
import org.auraframework.system.AuraContext.Mode;
import org.auraframework.test.TestContext;
import org.auraframework.test.TestContextAdapter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

/**
 * Unit tests for AuraTestFilter
 */
public class AuraTestFilterUnitTest {
    abstract class SimpleTestRequestDispatcher implements RequestDispatcher {
        @Override
        public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        }
    }
    
    @Mock
    private TestContextAdapter testContextAdapter;
    
    @Mock
    private TestContext testContext;
    
    @Mock
    private ConfigAdapter configAdapter;
    
    @Mock
    private DefinitionService definitionService;
    
    @Mock
    private ContextService contextService;
    
    @Mock
    private ServletUtilAdapter servletUtilAdapter;
    
    @Mock
    private AuraContext context;
    
    @Mock
    private DefDescriptor<?> targetDescriptor;
    
    @Mock
    private DefDescriptor<?> testSuiteDescriptor;
    
    @Mock
    private TestSuiteDef testSuiteDef;
    
    @Mock
    private TestCaseDef testCaseDef;
    
    @Mock
    private ServletContext requestServletContext;
    
    @Mock
    private ServletContext testServletContext;
    
    @Mock
    private RequestDispatcher dispatcher;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testDoFilterInjectsTestScriptAtEndByDefault() throws Exception {
        AuraTestFilter filter = new AuraTestFilter();
        filter.setTestContextAdapter(testContextAdapter);
        filter.setConfigAdapter(configAdapter);
        filter.setDefinitionService(definitionService);
        filter.setContextService(contextService);
        filter.setServletUtilAdapter(servletUtilAdapter);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter writer = new StringWriter();
        doReturn(new PrintWriter(writer)).when(response).getWriter();
        
        doReturn("testContextKey").when(request).getParameter("aura.test");
        doReturn(testContext).when(testContextAdapter).getTestContext("testContextKey");
        
        doReturn(TRUE).when(contextService).isEstablished();
        doReturn(context).when(contextService).getCurrentContext();
        doReturn(Format.HTML).when(context).getFormat();
        doReturn(Mode.AUTOJSTEST).when(context).getMode();

        doReturn("GET").when(request).getMethod();
        doReturn("").when(request).getContextPath();
        doReturn("/namespace/name.app").when(request).getRequestURI();
        doReturn("someTest").when(request).getParameter("aura.jstestrun");
        
        doReturn(targetDescriptor).when(definitionService).getDefDescriptor("namespace:name", ApplicationDef.class);
        doReturn("namespace").when(targetDescriptor).getNamespace();
        doReturn("name").when(targetDescriptor).getName();
        doReturn(DefType.APPLICATION).when(targetDescriptor).getDefType();
        doReturn(targetDescriptor).when(testSuiteDescriptor).getBundle();
        doReturn(testSuiteDescriptor).when(definitionService).getDefDescriptor(
                        DefDescriptor.JAVASCRIPT_PREFIX + "://namespace.name",
                        TestSuiteDef.class, targetDescriptor);
        doReturn(testSuiteDef).when(definitionService).getDefinition(testSuiteDescriptor);
        doReturn(Lists.newArrayList(testCaseDef)).when(testSuiteDef).getTestCaseDefs();
        doReturn("someTest").when(testCaseDef).getName();
        
        doReturn(requestServletContext).when(request).getServletContext();
        doReturn(testServletContext).when(requestServletContext).getContext(Matchers.anyString());
        
        doReturn(dispatcher).when(testServletContext).getRequestDispatcher(Matchers.startsWith("/aura?"));
        doReturn(dispatcher).when(request).getRequestDispatcher(Matchers.startsWith("/aura?"));
        
        String renderedTargetComponent = "RENDEREDTARGETCOMPONENT";
        SimpleTestRequestDispatcher simpleReqDispatcher = new SimpleTestRequestDispatcher() {
            @Override
            public void forward(ServletRequest req, ServletResponse res) throws IOException {
                res.getWriter().write(renderedTargetComponent);
            }
        };
        doReturn(simpleReqDispatcher).when(testServletContext).getRequestDispatcher(Matchers.startsWith("/aura?"));
        doReturn(simpleReqDispatcher).when(request).getRequestDispatcher(Matchers.startsWith("/aura?"));

        FilterChain chain = (req, res) -> {};
        filter.doFilter(request, response, chain);

        String responseString = writer.toString();
        assertThat(responseString, startsWith(renderedTargetComponent));
        responseString = responseString.substring(renderedTargetComponent.length());
        assertThat(responseString, containsString("<script src='/aura?aura.tag=namespace%3Aname&aura.deftype=APPLICATION&aura.mode=AUTOJSTEST&aura.format=JS&aura.access=AUTHENTICATED&aura.jstestrun=someTest"));
    }

    @Test
    public void testDoFilterHandlesRedirectionWithJstestrun() throws Exception {
        doRedirectionTest("https://host:80/namespace/name.app?attr=val&aura.jstestrun=willbeoverwritten",
                "https://host:80/namespace/name.app?attr=val&aura.jstestrun=someTest");
    }
    
    @Test
    public void testDoFilterHandlesRedirectionWithAnchor() throws Exception {
        doRedirectionTest("/x/y.app?aura.jstestrun=jst#anchor", "/x/y.app?aura.jstestrun=someTest#anchor");
    }
    @Test
    public void testDoFilterHandlesRedirectionWithOnlyJstestrun() throws Exception {
        doRedirectionTest("/x/y.app?aura.jstestrun=_NONE", "/x/y.app?aura.jstestrun=someTest");
    }
    
    @Test
    public void testDoFilterHandlesRedirectionWithEmptyJstestrun() throws Exception {
        doRedirectionTest("/x/y.app?aura.jstestrun=&attr=val", "/x/y.app?aura.jstestrun=someTest&attr=val");
    }
    
    @Test
    public void testDoFilterHandlesRedirectionWithEmptyJstestrunAtEnd() throws Exception {
        doRedirectionTest("/x/y.app?aura.jstestrun=", "/x/y.app?aura.jstestrun=someTest");
    }
    
    @Test
    public void testDoFilterHandlesRedirectionWithoutJstestrun() throws Exception {
        doRedirectionTest("/x/y.app?attr=val", "/x/y.app?attr=val&aura.jstestrun=someTest");
    }
    
    @Test
    public void testDoFilterHandlesRedirectionWithoutAnyParams() throws Exception {
        doRedirectionTest("/x/y.app", "/x/y.app?aura.jstestrun=someTest");
    }

    private void doRedirectionTest(String redirectionUrl, String expectedUrl) throws Exception {
        AuraTestFilter filter = new AuraTestFilter();
        filter.setTestContextAdapter(testContextAdapter);
        filter.setConfigAdapter(configAdapter);
        filter.setDefinitionService(definitionService);
        filter.setContextService(contextService);
        filter.setServletUtilAdapter(servletUtilAdapter);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter writer = new StringWriter();
        doReturn(new PrintWriter(writer)).when(response).getWriter();
        
        doReturn("testContextKey").when(request).getParameter("aura.test");
        doReturn(testContext).when(testContextAdapter).getTestContext("testContextKey");
        
        doReturn(TRUE).when(contextService).isEstablished();
        doReturn(context).when(contextService).getCurrentContext();
        doReturn(Format.HTML).when(context).getFormat();
        doReturn(Mode.AUTOJSTEST).when(context).getMode();

        doReturn("GET").when(request).getMethod();
        doReturn("").when(request).getContextPath();
        doReturn("/namespace/name.app").when(request).getRequestURI();
        doReturn("someTest").when(request).getParameter("aura.jstestrun");
        
        doReturn(targetDescriptor).when(definitionService).getDefDescriptor("namespace:name", ApplicationDef.class);
        doReturn("namespace").when(targetDescriptor).getNamespace();
        doReturn("name").when(targetDescriptor).getName();
        doReturn("namespace:name").when(targetDescriptor).getDescriptorName();
        doReturn(DefType.APPLICATION).when(targetDescriptor).getDefType();
        doReturn(testSuiteDescriptor).when(definitionService).getDefDescriptor(
                        DefDescriptor.JAVASCRIPT_PREFIX + "://namespace.name",
                        TestSuiteDef.class, targetDescriptor);
        doReturn(testSuiteDef).when(definitionService).getDefinition(testSuiteDescriptor);
        doReturn(Lists.newArrayList(testCaseDef)).when(testSuiteDef).getTestCaseDefs();
        doReturn("someTest").when(testCaseDef).getName();
        
        doReturn(requestServletContext).when(request).getServletContext();
        doReturn(testServletContext).when(requestServletContext).getContext(Matchers.anyString());

        SimpleTestRequestDispatcher simpleReqDispatcher = new SimpleTestRequestDispatcher() {
            @Override
            public void forward(ServletRequest req, ServletResponse res) throws IOException {
                ((HttpServletResponse)res).sendRedirect(redirectionUrl);
            }
        };
        doReturn(simpleReqDispatcher).when(testServletContext).getRequestDispatcher(Matchers.startsWith("/aura?"));
        doReturn(simpleReqDispatcher).when(request).getRequestDispatcher(Matchers.startsWith("/aura?"));

        FilterChain chain = (req, res) -> {};
        filter.doFilter(request, response, chain);
        
        verify(response, times(1)).sendRedirect(expectedUrl);
    }
}
