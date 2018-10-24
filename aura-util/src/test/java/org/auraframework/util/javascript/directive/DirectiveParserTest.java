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
package org.auraframework.util.javascript.directive;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;

import org.auraframework.util.test.util.UnitTestCase;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * This class is to test the DirectiveParser class {@link DirectiveParser}.
 * DirectiveParser class parses specified files for directives. Directives are
 * specified with //# as prefix. The list of directives, root node of the file
 * system and javascript generation modes are specified in an
 * DirectiveBasedJavascriptGroup object.
 */
public class DirectiveParserTest extends UnitTestCase {
    /**
     * Try to pass an empty set of directive. Ideally the test should return
     * just the contents of the file after stripping of all the lines with
     * directives (lines starting with //#)
     */
    @Test
    public void testZeroDirectiveTypes() throws Exception {
        File file = getResourceFile("/testdata/javascript/head.js");
        DirectiveBasedJavascriptGroup jg = new DirectiveBasedJavascriptGroup("testDummy", file.getParentFile(),
                file.getName(), new ArrayList<DirectiveType<?>>(), EnumSet.of(JavascriptGeneratorMode.TESTING));
        DirectiveParser dp = new DirectiveParser(jg, jg.getStartFile());
        dp.parseFile();
        goldFileText(dp.generate(JavascriptGeneratorMode.PRODUCTION), ".js");
    }

    /**
     * "end" is a reserved DirectiveType label. This must not be used by other
     * directives.
     */
    @Test
    public void testEndDirectiveTypes() throws Exception {
        File file = getResourceFile("/testdata/javascript/head.js");
        DirectiveBasedJavascriptGroup jg = new DirectiveBasedJavascriptGroup("testDummy", file.getParentFile(),
                file.getName(), ImmutableList.<DirectiveType<?>> of(DirectiveFactory.getEndDirective()), EnumSet.of(
                        JavascriptGeneratorMode.DEVELOPMENT, JavascriptGeneratorMode.TESTING,
                        JavascriptGeneratorMode.PRODUCTION));
        DirectiveParser dp = new DirectiveParser(jg, jg.getStartFile());
        try {
            dp.parseFile();
            fail("Passing an END directive should have thrown an exception");
        } catch (RuntimeException e) {
            assertTrue("The Javascript Processor failed for some unknown reason",
                    e.getMessage().equals("cannot create a directive with the reserved label \"end\""));
        }
    }

    /**
     * Duplicate directive labels must not be accepted
     */
    @Test
    public void testDupDirectiveTypes() throws Exception {
        File file = getResourceFile("/testdata/javascript/head.js");
        DirectiveBasedJavascriptGroup jg = new DirectiveBasedJavascriptGroup("testDummy", file.getParentFile(),
                file.getName(), ImmutableList.<DirectiveType<?>> of(DirectiveFactory.getMockDirective(),
                        DirectiveFactory.getDummyDirectiveType(), DirectiveFactory.getMockDirective()),
                EnumSet.of(JavascriptGeneratorMode.TESTING));
        DirectiveParser dp = new DirectiveParser(jg, jg.getStartFile());
        try {
            dp.parseFile();
            fail("Passing an directives with duplicate labels should have failed");
        } catch (RuntimeException e) {
            assertTrue("The Javascript Processor failed for some unknown reason",
                    e.getMessage().startsWith("Multiple directives registered for label"));
        }
    }

    /*
     * Tests for the parse() method in DirectiveParser
     */
    /**
     * Positive test: Test a multiline directive by gold filing the contents
     * passed to the directive object
     */
    @Test
    public void testMultilineDirective() throws Exception {
        File file = getResourceFile("/testdata/javascript/testMultilineDirective.js");
        DirectiveBasedJavascriptGroup jg = new DirectiveBasedJavascriptGroup("testDummy", file.getParentFile(),
                file.getName(), ImmutableList.<DirectiveType<?>> of(DirectiveFactory.getMultiLineMockDirectiveType()),
                EnumSet.of(JavascriptGeneratorMode.TESTING));
        DirectiveParser dp = new DirectiveParser(jg, jg.getStartFile());
        dp.parseFile();
        LinkedList<Directive> directives = dp.directives;
        Directive multiLine = directives.getFirst();
        assertTrue("Should have created a MultiLineMockDirective after parsing the file", multiLine.getClass()
                .getName().contains("MultiLineMockDirective"));
        // This dummy MultiLineMockDirective is written to throw content when
        // asked to generateOutput
        goldFileText(multiLine.generateOutput(JavascriptGeneratorMode.TESTING), ".js");
    }

    /*
     * Tests for the generate() method in DirectiveParser
     */
    @Test
    public void testCallGenerateBeforeParse() throws Exception {
        File file = getResourceFile("/testdata/javascript/testMultilineDirective.js");
        DirectiveBasedJavascriptGroup jg = new DirectiveBasedJavascriptGroup("testDummy", file.getParentFile(),
                file.getName(), ImmutableList.<DirectiveType<?>> of(DirectiveFactory.getMultiLineMockDirectiveType()),
                EnumSet.of(JavascriptGeneratorMode.TESTING));
        DirectiveParser dp = new DirectiveParser(jg, jg.getStartFile());
        try {
            dp.generate(JavascriptGeneratorMode.TESTING);
            fail("Parser should generate output only after parsing the files");
        } catch (Exception expected) {
            // Should say generate cannot be called before parsing the group
        }
    }  

    /**
     * Gold filing the output generated by different kinds of generation modes
     */
    @Test
    public void testAllKindsOfDirectiveGenerate() throws Exception {
        File file = getResourceFile("/testdata/javascript/testAllKindsOfDirectiveGenerate.js");
        DirectiveBasedJavascriptGroup jg = new DirectiveBasedJavascriptGroup("testDummy", file.getParentFile(),
                file.getName(), ImmutableList.<DirectiveType<?>> of(DirectiveFactory.getMultiLineMockDirectiveType(),
                        DirectiveFactory.getMockDirective(), DirectiveFactory.getDummyDirectiveType()), null);
        DirectiveParser dp = new DirectiveParser(jg, jg.getStartFile());
        dp.parseFile();
        goldFileText(dp.generate(JavascriptGeneratorMode.TESTING), "_test.js");
        goldFileText(dp.generate(JavascriptGeneratorMode.AUTOTESTING), "_auto.js");
        // The content generated in PRODUCTION mode still has comments because
        // the DirectiveParser doesn't really
        // compress the JS files.
        // Compression is handled in DirectivebasedJavascriptGroup
        goldFileText(dp.generate(JavascriptGeneratorMode.PRODUCTION), "_prod.js");
        goldFileText(dp.generate(JavascriptGeneratorMode.DEVELOPMENT), "_dev.js");
    }
    
    @Test
    public void testParser() throws Exception {
        TestGroup g = new TestGroup(getResourceFile("/testdata/directive/testParser.js"));
        DirectiveParser parser = new DirectiveParser(g, g.getStartFile());
        parser.parseFile();
        LinkedList<Directive> directives = parser.directives;
        assertEquals("didn't found the right number of directives", 2, directives.size());
        // directives are in reverse order
        Directive last = directives.get(0);
        Directive first = directives.get(1);
        assertEquals(last.getLine(), "{\"modes\": [\"MOCK2\"]}");
        assertTrue(last.hasOutput(JavascriptGeneratorMode.MOCK2));
        assertFalse(last.hasOutput(JavascriptGeneratorMode.MOCK1));
        assertFalse(last.hasOutput(JavascriptGeneratorMode.PRODUCTION));
        assertEquals(first.getLine(), "{\"modes\": [\"MOCK1\"]}");
        assertTrue(first.hasOutput(JavascriptGeneratorMode.MOCK1));
        assertFalse(first.hasOutput(JavascriptGeneratorMode.MOCK2));
        assertFalse(first.hasOutput(JavascriptGeneratorMode.PRODUCTION));
    }

    /**
     * tests that a directive can begin with // #
     */
    @Test
    public void testSpaceInDirective() throws Exception {
        TestGroup g = new TestGroup(getResourceFile("/testdata/javascript/testSpaces.js"));
        DirectiveParser parser = new DirectiveParser(g, g.getStartFile());
        parser.parseFile();
        LinkedList<Directive> directives = parser.directives;
        assertEquals("didn't found the right number of directives", 3, directives.size());
        // directives are in reverse order
        Directive third = directives.get(0);
        Directive second = directives.get(1);
        Directive first = directives.get(2);
        assertTrue(first.hasOutput(JavascriptGeneratorMode.MOCK2));
        assertFalse(first.hasOutput(JavascriptGeneratorMode.MOCK1));
        assertEquals(first.getLine(), "{\"modes\": [\"MOCK2\"], \"blah\": \"my\"}");
        assertTrue(second.hasOutput(JavascriptGeneratorMode.MOCK1));
        assertFalse(second.hasOutput(JavascriptGeneratorMode.MOCK2));
        assertEquals(second.getLine(), "{\"modes\": [\"MOCK1\"], \"blah\": \"spatula\"}");
        assertTrue(third.isMultiline());
    }
}

