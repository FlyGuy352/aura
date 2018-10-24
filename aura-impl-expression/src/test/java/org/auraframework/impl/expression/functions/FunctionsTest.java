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
package org.auraframework.impl.expression.functions;

import static org.auraframework.impl.expression.functions.BooleanFunctions.AND;
import static org.auraframework.impl.expression.functions.BooleanFunctions.NOT;
import static org.auraframework.impl.expression.functions.BooleanFunctions.OR;
import static org.auraframework.impl.expression.functions.BooleanFunctions.TERNARY;
import static org.auraframework.impl.expression.functions.MathFunctions.ABSOLUTE;
import static org.auraframework.impl.expression.functions.MathFunctions.DIVIDE;
import static org.auraframework.impl.expression.functions.MathFunctions.MODULUS;
import static org.auraframework.impl.expression.functions.MathFunctions.MULTIPLY;
import static org.auraframework.impl.expression.functions.MathFunctions.NEGATE;
import static org.auraframework.impl.expression.functions.MathFunctions.SUBTRACT;
import static org.auraframework.impl.expression.functions.MultiFunctions.ADD;
import static org.auraframework.impl.expression.functions.MultiFunctions.EQUALS;
import static org.auraframework.impl.expression.functions.MultiFunctions.GREATER_THAN;
import static org.auraframework.impl.expression.functions.MultiFunctions.GREATER_THAN_OR_EQUAL;
import static org.auraframework.impl.expression.functions.MultiFunctions.LESS_THAN;
import static org.auraframework.impl.expression.functions.MultiFunctions.LESS_THAN_OR_EQUAL;
import static org.auraframework.impl.expression.functions.MultiFunctions.NOTEQUALS;
import static org.auraframework.impl.expression.functions.UtilFunctions.EMPTY;
import static org.auraframework.impl.expression.functions.UtilFunctions.FORMAT;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.auraframework.util.test.util.UnitTestCase;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Basic tests of functions
 *
 */
public class FunctionsTest extends UnitTestCase {
    private Object evaluate(Function f, Object... args) {
        return f.evaluate(Lists.newArrayList(args));
    }

    /* ADD
     * we try to make sure ADD() on java side give us the same output as add() on JS side
     * tests on js side are in expressionTest/functions.cmp
     * here every test function (more or less) is a equivalent to a test cmp (expressionTest:test) in function.cmp.
     * 'skip' in the comment means we cannot test the same thing on the other side
     * 'diff' means js side give us different output
     * just fyi: m.integer = 411; v.integer=7; v.double=3.1; v.doubleString="2.1"; v.string="Component"; v.list=[1,2,3]
              v.emptyString"="";  v.Infinity=Infinity; v.NegativeInfinity=-Infinity; v.NaN=NaN; v.object={};

    Note: Add has two keys : 'add' and 'concat', they are the same, that's why we have test like this on JS side
    <expressionTest:test expression="{!concat(4.1,v.integer)}" exprText="concat(4.1,v.integer)" expected="11.1"/>
    I don't see people using concat, why we have two anyway
   */
    public void testAddNoArgument() throws Exception {
    	assertEquals(null, evaluate(ADD));
    }

    public void testAddOneArgument() throws Exception {
    	assertEquals(10, evaluate(ADD, 10));
    }

    //<expressionTest:test expression="{!m.date + 5}" exprText="m.date + 5" expected="'2004-09-23T16:30:00.000Z5'"/>
    //diff : on JS side we actually resolve the date, here we just output [object Object]
    @Test
    public void testAddDateAndInt() throws Exception {
    	Date d = new Date(1095957000000L);
    	assertEquals("[object Object]5", evaluate(ADD, d, 5));
    }

    //<expressionTest:test expression="{!m.date + '8'}" exprText="m.date + '8'" expected="'2004-09-23T16:30:00.000Z8'"/>
    //diff : on JS side we actually resolve the date, here we just output [object Object]
    @Test
    public void testAddDateAndString() throws Exception {
    	Date d = new Date(1095957000000L);
    	assertEquals("[object Object]8", evaluate(ADD, d, "8"));
    }

    //<expressionTest:test expression="{!3146431.43266 + 937.1652}" exprText="3146431.43266 + 937.1652" expected="3147368.59786"/>
    @Test
    public void testAddTwoDoubles() throws Exception {
        assertEquals(3146431.43266 + 937.1652, evaluate(ADD, 3146431.43266, 937.1652));
    }

    //<expressionTest:test expression="{!'a' + 'x'}" exprText="'a' + 'x'" expected="'ax'"/>
    //<expressionTest:test expression="{!'3' + '3'}" exprText="'3' + '3'" expected="'33'"/>
    //<expressionTest:test expression="{!m.emptyString + '3'}" exprText="m.emptyString + '3'" expected="'3'"/>
    @Test
    public void testAddTwoStrings() throws Exception {
        assertEquals("12", evaluate(ADD, "1", "2"));
    }

    //<expressionTest:test expression="{!add(m.integer, 2.0)}" exprText="add(m.integer, 2.0)" expected="413"/>
    @Test
    public void testAddIntAndDouble() throws Exception {
        assertEquals(314 + 3146431.43266, evaluate(ADD, 314, 3146431.43266));
    }

    //<expressionTest:test expression="{!0 + 0}" exprText="0 + 0" expected="0"/>
    @Test
    public void testAddTwoInts() throws Exception {
        assertEquals(235639, evaluate(ADD, 314, 235325));
    }

    //<expressionTest:test expression="{!1 + v.NaN}" exprText="1 + v.NaN" expected="NaN"/>
    @Test
    public void testAddIntAndNaN() throws Exception {
        assertEquals(Double.NaN, evaluate(ADD, 314, Double.NaN));
    }

    //skip: we don't support Number.MAX_VALUE in markup
    @Test
    public void testAddOverflow() throws Exception {
        assertEquals(Double.MAX_VALUE, evaluate(ADD, Double.MAX_VALUE, 2.0));
    }

    //<expressionTest:test expression="{!'a' + v.double}" exprText="'a' + v.double" expected="'a3.1'"/>
    @Test
    public void testAddStringAndDouble() throws Exception {
        assertEquals("0937.1652", evaluate(ADD, "0", 937.1652));
    }

    //<expressionTest:test expression="{!0 + 'x'}" exprText="0 + 'x'" expected="'0x'"/>
    @Test
    public void testAddZeroAndString() throws Exception {
        assertEquals("01", evaluate(ADD, 0, "1"));
    }

    //<expressionTest:test expression="{!3 + ''}" exprText="3 + ''" expected="'3'"/>
    @Test
    public void testAddIntAndEmptyString() throws Exception {
        assertEquals("314", evaluate(ADD, 314, ""));
    }

    //<expressionTest:test expression="{!'' + 3}" exprText="3 + ''" expected="'3'"/>
    @Test
    public void testAddEmptyStringAndInt() throws Exception {
        assertEquals("314", evaluate(ADD, "", 314));
    }

    //<expressionTest:test expression="{!v.Infinity + 2}" exprText="v.Infinity + 2" expected="Infinity"/>
    @Test
    public void testAddInfinityAndInt() throws Exception {
        assertEquals(Double.POSITIVE_INFINITY, evaluate(ADD, Double.POSITIVE_INFINITY, 235325));
        assertEquals(Double.POSITIVE_INFINITY, evaluate(ADD, Float.POSITIVE_INFINITY, 235325));
    }

   //<expressionTest:test expression="{!v.Infinity + v.NegativeInfinity}" exprText="v.Infinity + v.NegativeInfinity" expected="NaN"/>
   @Test
    public void testAddInfinityAndNegativeInfinity() throws Exception {
        assertEquals(Double.NaN, evaluate(ADD, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
        assertEquals(Double.NaN, evaluate(ADD, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY));
    }

    //<expressionTest:test expression="{!v.Infinity + 'AndBeyond'}" exprText="v.Infinity + 'AndBeyond'" expected="'InfinityAndBeyond'"/>
    @Test
    public void testAddInfinityAndString() throws Exception {
        assertEquals("InfinityAndBeyond", evaluate(ADD, Double.POSITIVE_INFINITY, "AndBeyond"));
    }

    //<expressionTest:test expression="{!'To' + v.NegativeInfinity}" exprText="'To' + v.NegativeInfinity" expected="'To-Infinity'"/>
    @Test
    public void testAddStringAndNegativeInfinity() throws Exception {
        assertEquals("Random-Infinity", evaluate(ADD, "Random", Double.NEGATIVE_INFINITY));
    }


    //<expressionTest:test expression="{!'100' + v.NaN}" exprText="'100' + v.NaN" expected="'100NaN'"/>
    @Test
    public void testAddStringAndNaN() throws Exception {
        assertEquals("1NaN", evaluate(ADD, "1", Double.NaN));
    }

    //<expressionTest:test expression="{!v.nullObj + 1}" exprText="v.nullObj + 1" expected="1"/>
    @Test
    public void testAddNullAndInt() throws Exception {
        assertEquals(1, evaluate(ADD, null, 1));
    }

    //diff: <expressionTest:test expression="{!v.nullObj + 'b'}" exprText="v.nullObj + 'b'" expected="'b'"/>
    @Test
    public void testAddNullAndString() throws Exception {
        assertEquals("nullb", evaluate(ADD, null, "b"));
    }

    //diff: <expressionTest:test expression="{!'b' + !v.nullObj}" exprText="'b' + v.nullObj" expected="'b'"/>
    @Test
    public void testAddStringAndNull() throws Exception {
        assertEquals("cnull", evaluate(ADD, "c", null));
    }

    //<expressionTest:test expression="{!v.nullObj + 2.5}" exprText="v.nullObj + 2.5" expected="2.5"/>
    @Test
    public void testAddNullAndDouble() throws Exception {
        assertEquals(2.5, evaluate(ADD, null, 2.5));
    }

    //diff: <expressionTest:test expression="{!v.nullObj + v.nullObj}" exprText="v.nullObj + v.nullObj" expected="''"/>
    @Test
    public void testAddTwoNulls() throws Exception {
        assertEquals(0, evaluate(ADD, null, null));
    }

    //diff: <expressionTest:test expression="{!'' + (-0.0)}" exprText="'' + (-0.0)" expected="'0'"/>
    @Test
    public void testAddStringAndNegativeZero() throws Exception {
    	assertEquals("-0", evaluate(ADD, "", -0.0));
    }

    //<expressionTest:test expression="{!v.nullList + 'a'}" exprText="v.nullObj + 'a'" expected="'a'"/>
    @Test
    public void testAddListNullAndString() throws Exception {
        List<Object> nullList = Lists.newArrayList();
        nullList.add(null);
        assertEquals("a", evaluate(ADD, nullList, "a"));
    }

    //<expressionTest:test expression="{!v.list + 'a'}" exprText="v.list + 'a'" expected="'1,2,3a'"/>
    @Test
    public void testAddList123AndString() throws Exception {
        assertEquals("1,2,3a", evaluate(ADD, Lists.newArrayList(1, 2, 3), "a"));
    }

    //<expressionTest:test expression="{!v.listWithNull + ''}" exprText="v.listWithNull + ''" expected="',a'"/>
    @Test
    public void testAddListNullStringAndEmptyString() throws Exception {
        List<Object> list = Lists.newArrayList();
        list.add(null);
        list.add("a");
        assertEquals(",a", evaluate(ADD, list, ""));
    }

    //diff: <expressionTest:test expression="{!v.listWithList + ''}" exprText="v.listWithList + ''" expected="'a,,b,c'"/>
    @Test
    public void testAddNestedListNullStringAndEmptyString() throws Exception {
        List<Object> list = Lists.newArrayList();
        List<Object> nested = Lists.newArrayList();
        list.add("a");
        list.add(nested);
        nested.add("b");
        nested.add("c");
        assertEquals("a,b,c", evaluate(ADD, list, ""));
    }

    //diff: <expressionTest:test expression="{!v.listWithNested4Layers + ''}" exprText="v.listWithNested4Layers + ''" expected="'6,7,4,5,2,3,0,1,b'"/>
    @Test
    public void testAddTooDeep() throws Exception {
        List<Object> list = Lists.newArrayList();
        List<Object> nested = Lists.newArrayList();
        List<Object> nested2 = Lists.newArrayList();
        List<Object> nested3 = Lists.newArrayList();
        List<Object> nested4 = Lists.newArrayList();
        list.add("a");
        list.add(nested);
        nested.add(nested2);
        nested2.add(nested3);
        nested3.add(nested4);
        nested4.add("d");
        nested.add("b");
        nested.add("c");
        assertEquals("a,Too Deep,b,c", evaluate(ADD, list, ""));
    }

    //diff: <expressionTest:test expression="{!v.listWithLoop + ''}" exprText="v.listWithLoop + ''" expected="'0,1,'"/>
    @Test
    public void testAddLoop() throws Exception {
        List<Object> list = Lists.newArrayList();
        List<Object> nested = Lists.newArrayList();
        list.add("a");
        list.add(nested);
        nested.add(list);
        assertEquals("a,a,Too Deep", evaluate(ADD, list, ""));
    }

    //<expressionTest:test expression="{!v.map + ''}" exprText="v.map + ''" expected="'[object Object]'"/>
    @Test
    public void testAddMapAndEmptyString() throws Exception {
        Map<Object,Object> map = Maps.newHashMap();
        map.put("a", null);
        map.put("b", "c");
        assertEquals("[object Object]", evaluate(ADD, map, ""));
    }

    /*
     * EQUALS
     */
    //<expressionTest:test expression="{!2 == 2.0}" exprText="2 == 2.0" expected="true"/>
    @Test
    public void testEqualsSameIntAndDouble() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(EQUALS, 2, 2.0));
    }

    //<expressionTest:test expression="{!2 == '2'}" exprText="2 == '2'" expected="false"/>
    @Test
    public void testEqualsSameIntAndString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EQUALS, 2, "2"));
    }

    //<expressionTest:test expression="{!'bum' == 'bum'}" exprText="'bum' == 'bum'" expected="true"/>
    @Test
    public void testEqualsSameString() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(EQUALS, "bum", "bum"));
    }

    //<expressionTest:test expression="{!'Bum' == 'bum'}" exprText="'Bum' == 'bum'" expected="false"/>
    @Test
    public void testEqualsStringsDifferentCapitalization() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EQUALS, "Bum", "bum"));
    }

    //<expressionTest:test expression="{!1 == 3}" exprText="1 == 3" expected="false"/>
    @Test
    public void testEqualsDifferentInts() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EQUALS, 1, 3));
    }

    //<expressionTest:test expression="{!true == false}" exprText="true == false" expected="false"/>
    @Test
    public void testEqualsDifferentBooleans() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EQUALS, Boolean.TRUE, Boolean.FALSE));
    }

    //<expressionTest:test expression="{!false eq false}" exprText="false eq false" expected="true"/>
    //<expressionTest:test expression="{!equals(false, false)}" exprText="equals(false, false)" expected="true"/>
    @Test
    public void testEqualsSameBooleans() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(EQUALS, Boolean.FALSE, Boolean.FALSE));
    }

    //<expressionTest:test expression="{!'' == false}" exprText="'' == false" expected="false"/>
    @Test
    public void testEqualsEmptyStringAndFalse() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EQUALS, "", Boolean.FALSE));
    }

    //<expressionTest:test expression="{!v.Infinity == v.Infinity}" exprText="v.Infinity == v.Infinity" expected="true"/>
    @Test
    public void testEqualsPositiveInfinity() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(EQUALS, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        assertEquals(Boolean.TRUE, evaluate(EQUALS, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    }

    //<expressionTest:test expression="{!v.NegativeInfinity == v.NegativeInfinity}" exprText="v.NegativeInfinity == v.NegativeInfinity" expected="true"/>
    @Test
    public void testEqualsNegativeInfinity() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(EQUALS, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
        assertEquals(Boolean.TRUE, evaluate(EQUALS, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY));
    }

    //<expressionTest:test expression="{!v.Infinity == v.NegativeInfinity}" exprText="v.Infinity == v.NegativeInfinity" expected="false"/>
    @Test
    public void testEqualsPositiveAndNegativeInfinity() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EQUALS, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
        assertEquals(Boolean.FALSE, evaluate(EQUALS, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY));
    }

    //skip: JS only has one infinity, for both float and double
    //<expressionTest:test expression="{!m.infinityFloat == m.infinity}" exprText="m.infinityFloat == m.infinity" expected="true"/>
    @Test
    public void testEqualsDoubleInfinityAndFloatInfinity() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(EQUALS, Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    }

    //<expressionTest:test expression="{!m.naN == v.NaN}" exprText="m.naN == v.NaN" expected="false"/>
    //<expressionTest:test expression="{!m.naN == m.naN}" exprText="m.naN == m.naN" expected="false"/>
    @Test
    public void testEqualsNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EQUALS, Double.NaN, Double.NaN));
    }

    //<expressionTest:test expression="{!v.nullObj == true}" exprText="v.nullObj == true" expected="false"/>
    @Test
    public void testEqualsNullAndBooleanTrue() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EQUALS, null, Boolean.TRUE));
    }

    //<expressionTest:test expression="{!v.nullObj == false}" exprText="v.nullObj == false" expected="false"/>
    @Test
    public void testEqualsNullAndBooleanFalse() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EQUALS, null, Boolean.FALSE));
    }

    //<expressionTest:test expression="{!v.nullObj == ''}" exprText="v.nullObj == ''" expected="false"/>
    @Test
    public void testEqualsNullAndEmptyString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EQUALS, null, ""));
    }

    //<expressionTest:test expression="{!v.nullObj == 0}" exprText="v.nullObj == 0" expected="false"/>
    @Test
    public void testEqualsNullAndZero() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EQUALS, null, 0));
    }

    //<expressionTest:test expression="{!v.nullObj == null}" exprText="v.nullObj == null" expected="true"/>
    @Test
    public void testEqualsNullAndNull() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(EQUALS, null, null));
    }

    //skip: integer and string from model only apply to JS side
    //<expressionTest:test expression="{!m.integer == 411}" exprText="m.integer == 411" expected="true"/>
    //<expressionTest:test expression="{!m.integerString == 511}" exprText="m.integerString == 511" expected="false"/>
    //<expressionTest:test expression="{!m.integerString == '511'}" exprText="m.integerString == '511'" expected="true"/>

    //skip: we don't parse Date here on Java side.
    //<expressionTest:test expression="{!m.date == '2004-09-23T16:30:00.000Z'}" exprText="m.date == '2004-09-23T16:30:00.000Z'" expected="true"/>

    //skip: Java will error out "/ by zero" if we do 1/0
    //<expressionTest:test expression="{!(1/0) == (2/0)}" exprText="(1/0) == (2/0)" expected="true"/>

    /*
     *  NOTEQUALS
     */

    //<expressionTest:test expression="{!true != false}" exprText="true != false" expected="true"/>
    @Test
    public void testNotEqualsDifferentBooleans() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(NOTEQUALS, Boolean.FALSE, Boolean.TRUE));
    }

    //skip: value from model only apply to JS side
    //<expressionTest:test expression="{!notequals(false, m.booleanTrue)}" exprText="notequals(false, m.booleanTrue)" expected="true"/>
    //<expressionTest:test expression="{!m.booleanFalse ne false}" exprText="m.booleanFalse ne false" expected="false"/>

    //<expressionTest:test expression="{!flase  != false}" exprText="false != false" expected="false"/>
    @Test
    public void testNotEqualsSameBoolean() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(NOTEQUALS, Boolean.FALSE, Boolean.FALSE));
    }

    //<expressionTest:test expression="{!0 != '0'}" exprText="0 != '0'" expected="true"/>
    @Test
    public void testNotEqualsZeroAndStringZero() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(NOTEQUALS, 0, "0"));
    }

    //<expressionTest:test expression="{!0 != false}" exprText="0 != false" expected="true"/>
    @Test
    public void testNotEqualsZeroAndBoolean() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(NOTEQUALS, 0, Boolean.FALSE));
    }

    //<expressionTest:test expression="{!v.NaN != v.NaN}" exprText="v.NaN != v.NaN" expected="true"/>
    @Test
    public void testNotEqualsTwoNaNs() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(NOTEQUALS, Double.NaN, Double.NaN));
    }

    //<expressionTest:test expression="{!v.nullObj != v.nullObj}" exprText="v.nullObj != v.nullObj" expected="false"/>
    @Test
    public void testNotEqualsTwoNulls() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(NOTEQUALS, null, null));
    }

    //<expressionTest:test expression="{!v.nullObj != false}" exprText="v.nullObj != false" expected="true"/>
    public void tesNotEqualstNullFalse() throws Exception {
    	assertEquals(Boolean.TRUE, evaluate(NOTEQUALS, null, false));
    }

    /*
     * TERNARY
     */
    //<expressionTest:test expression="{!if(true, 'yes')}" exprText="if(true, 'yes')" expected="'yes'"/>
    @Test
    public void testTernaryTwoParameterTrue() throws Exception {
        assertEquals("1", evaluate(TERNARY, Boolean.TRUE, "1"));
    }

    //diff: <expressionTest:test expression="{!if(false, 'yes')}" exprText="if(false, 'yes')" expected="''"/>
    @Test
    public void testTernaryTwoParameterFalse() throws Exception {
        assertEquals(null, evaluate(TERNARY, Boolean.FALSE, "1"));
    }

    //<expressionTest:test expression="{!true ? 'yes' : 'no'}" exprText="true ? 'yes' : 'no'" expected="'yes'"/>
    @Test
    public void testTernaryTrueReturnString() throws Exception {
        assertEquals("1", evaluate(TERNARY, Boolean.TRUE, "1", "2"));
    }

    //<expressionTest:test expression="{!false ? 'yes' : 'no'}" exprText="false ? 'yes' : 'no'" expected="'no'"/>
    @Test
    public void testTernaryFalseReturnString() throws Exception {
        assertEquals("2", evaluate(TERNARY, Boolean.FALSE, "1", "2"));
    }

    //<expressionTest:test expression="{!true ? v.nullObj : 'no'}" exprText="true ? null : 'no'" expected="null"/>
    @Test
    public void testTernaryTrueReturnNull() throws Exception {
        assertEquals(null, evaluate(TERNARY, Boolean.TRUE, null, "2"));
    }

    //<expressionTest:test expression="{!false ? 'yes' : v.nullObj}" exprText="false ? 'yes' : null" expected="null"/>
    @Test
    public void testTernaryFalseReturnNull() throws Exception {
        assertEquals(null, evaluate(TERNARY, Boolean.FALSE, "1", null));
    }

    //<expressionTest:test expression="{!v.nullObj ? 'yes' : 'no'}" exprText="null ? 'yes' : null" expected="'no'"/>
    @Test
    public void testTernaryNull() throws Exception {
        assertEquals("2", evaluate(TERNARY, null, "1", "2"));
    }

    //<expressionTest:test expression="{!'true' ? 'yes' : 'no'}" exprText="'true' ? 'yes' : 'no'" expected="'yes'"/>
    @Test
    public void testTernaryStringTrue() throws Exception {
        assertEquals("1", evaluate(TERNARY, "true", "1", "2"));
    }

    // <expressionTest:test expression="{!0 ? 'yes' : 'no'}" exprText="0 ? 'yes' : 'no'" expected="'no'"/>
    @Test
    public void testTernaryZero() throws Exception {
        assertEquals("2", evaluate(TERNARY, 0, "1", "2"));
    }

    //<expressionTest:test expression="{!2.1 ? 'yes' : 'no'}" exprText="2.1 ? 'yes' : 'no'" expected="'yes'"/>
    @Test
    public void testTernaryDouble() throws Exception {
        assertEquals("1", evaluate(TERNARY, 3146431.43266, "1", "2"));
    }

    //<expressionTest:test expression="{!'0' ? 'yes' : 'no'}" exprText="'0' ? 'yes' : 'no'" expected="'yes'"/>
    @Test
    public void testTernaryStringZero() throws Exception {
        assertEquals("1", evaluate(TERNARY, "0", "1", "2"));
    }

    //<expressionTest:test expression="{!'false' ? 'yes' : 'no'}" exprText="'false' ? 'yes' : 'no'" expected="'yes'"/>
    @Test
    public void testTernaryStringFalse() throws Exception {
        assertEquals("1", evaluate(TERNARY, "false", "1", "2"));
    }

    //<expressionTest:test expression="{!'' ? 'yes' : 'no'}" exprText="'' ? 'yes' : 'no'" expected="'no'"/>
    @Test
    public void testTernaryEmptyString() throws Exception {
        assertEquals("2", evaluate(TERNARY, "", "1", "2"));
    }

    //<expressionTest:test expression="{!v.NaN ? 'yes' : 'no'}" exprText="v.NaN ? 'yes' : 'no'" expected="'no'"/>
    @Test
    public void testTernaryNaN() throws Exception {
        assertEquals("2", evaluate(TERNARY, Double.NaN, "1", "2"));
    }

    // Skip
    // <expressionTest:test expression="{!if(true, 'yes', 'no')}" exprText="if(true, 'yes', 'no')" expected="'yes'"/>
    // <expressionTest:test expression="{!if(false, 'yes', 'no')}" exprText="if(false, 'yes', 'no')" expected="'no'"/>


    // SUBTRACT

    @Test
    public void testSubtractNoArgument() throws Exception {
        assertEquals(null, evaluate(SUBTRACT));
    }

    @Test
    public void testSubtractOneArgument() throws Exception {
        assertEquals(10, evaluate(SUBTRACT, 10));
    }

    @Test
    public void testSubtractDoubleAndNegativeDouble() throws Exception {
        assertEquals(937.1652 - -8426.6, evaluate(SUBTRACT, 937.1652, -8426.6));
    }

    @Test
    public void testSubtractPositiveInfinity() throws Exception {
        assertEquals(Double.NaN, evaluate(SUBTRACT, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        assertEquals(Double.NaN, evaluate(SUBTRACT, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    }

    @Test
    public void testSubtractIntAndStringInt() throws Exception {
        assertEquals(0.0, evaluate(SUBTRACT, 1, "1"));
    }

    @Test
    public void testSubtractIntAndDouble() throws Exception {
        assertEquals(0.0, evaluate(SUBTRACT, 2, 2.0));
    }

    @Test
    public void testSubtractInfinityAndInt() throws Exception {
        assertEquals(Double.POSITIVE_INFINITY, evaluate(SUBTRACT, Double.POSITIVE_INFINITY, 2));
    }

    @Test
    public void testSubtractIntAndInfinity() throws Exception {
        assertEquals(Double.NEGATIVE_INFINITY, evaluate(SUBTRACT, 3, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testSubtractIntAndNaN() throws Exception {
        assertEquals(Double.NaN, evaluate(SUBTRACT, 3, Double.NaN));
    }

    @Test
    public void testSubtractIntAndString() throws Exception {
        assertEquals(Double.NaN, evaluate(SUBTRACT, 3, "5c"));
    }

    @Test
    public void testSubtractIntAndEmptyString() throws Exception {
        assertEquals(3.0, evaluate(SUBTRACT, 3, ""));
    }

    @Test
    public void testSubtractStringAndInt() throws Exception {
        assertEquals(Double.NaN, evaluate(SUBTRACT, "5c", 3));
    }

    @Test
    public void testSubtractEmptyStringAndInt() throws Exception {
        assertEquals(-3.0, evaluate(SUBTRACT, "", 3));
    }

    @Test
    public void testSubtractTwoEmptyStrings() throws Exception {
        assertEquals(0.0, evaluate(SUBTRACT, "", ""));
    }

    @Test
    public void testSubtractStringIntAndInt() throws Exception {
        assertEquals(3.0, evaluate(SUBTRACT, "4", 1));
    }

    @Test
    public void testSubtractTwoStringInts() throws Exception {
        assertEquals(-2.0, evaluate(SUBTRACT, "3", "5"));
    }

    @Test
    public void testSubtractIntAndNull() throws Exception {
        assertEquals(2.0, evaluate(SUBTRACT, 2, null));
    }

    @Test
    public void testSubtractNullAndDouble() throws Exception {
        assertEquals(-3.1, evaluate(SUBTRACT, null, 3.1));
    }

    @Test
    public void testSubtractTwoNulls() throws Exception {
        assertEquals(0.0, evaluate(SUBTRACT, null, null));
    }

    // MULTIPLY

    @Test
    public void testMultiplyNoArgument() throws Exception {
        assertEquals(null, evaluate(MULTIPLY));
    }

    @Test
    public void testMultiplyOneArgument() throws Exception {
        assertEquals(10, evaluate(MULTIPLY, 10));
    }

    @Test
    public void testMultiplyIntAndDouble() throws Exception {
        assertEquals(1.1, evaluate(MULTIPLY, 1, 1.1));
    }

    @Test
    public void testMultiplyZeroAndInt() throws Exception {
        assertEquals(0.0, evaluate(MULTIPLY, 0, 3));
    }

    @Test
    public void testMultiplyNegativeIntAndNegativeDouble() throws Exception {
        assertEquals(0.2, evaluate(MULTIPLY, -2, -0.1));
    }

    @Test
    public void testMultiplyToGetToInfinity() throws Exception {
        assertEquals(Double.POSITIVE_INFINITY, evaluate(MULTIPLY, 1e200, 1e200));
    }

    @Test
    public void testMultiplyToGetToNegativeInfinity() throws Exception {
        assertEquals(Double.NEGATIVE_INFINITY, evaluate(MULTIPLY, -1e200, 1e200));
    }

    @Test
    public void testMultiplyPositiveInfinity() throws Exception {
        assertEquals(Double.POSITIVE_INFINITY, evaluate(MULTIPLY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testMultiplyZeroAndInfinity() throws Exception {
        assertEquals(Double.NaN, evaluate(MULTIPLY, 0, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testMultiplyIntAndNaN() throws Exception {
        assertEquals(Double.NaN, evaluate(MULTIPLY, 1, Double.NaN));
    }

    @Test
    public void testMultiplyIntAndString() throws Exception {
        assertEquals(Double.NaN, evaluate(MULTIPLY, 5, "5o"));
    }

    @Test
    public void testMultiplyStringAndInt() throws Exception {
        assertEquals(Double.NaN, evaluate(MULTIPLY, "5o", 9));
    }

    @Test
    public void testMultiplyTwoStrings() throws Exception {
        assertEquals(Double.NaN, evaluate(MULTIPLY, "5o", "5o"));
    }

    @Test
    public void testMultiplyIntAndStringDouble() throws Exception {
        assertEquals(2.2, evaluate(MULTIPLY, 2, "1.1"));
    }

    @Test
    public void testMultiplyStringIntAndStringDouble() throws Exception {
        assertEquals(21.7, evaluate(MULTIPLY, "7", "3.1"));
    }

    @Test
    public void testMultiplyIntAndNull() throws Exception {
        assertEquals(0.0, evaluate(MULTIPLY, 3, null));
    }

    @Test
    public void testMultiplyNullAndNegativeDouble() throws Exception {
        assertEquals(-0.0, evaluate(MULTIPLY, null, -0.1));
    }

    @Test
    public void testMultiplyTwoNulls() throws Exception {
        assertEquals(0.0, evaluate(MULTIPLY, null, null));
    }

    // DIVIDE

    @Test
    public void testDivideNoArgument() throws Exception {
        assertEquals(null, evaluate(DIVIDE));
    }

    @Test
    public void testDivideOneArgument() throws Exception {
        assertEquals(10, evaluate(DIVIDE, 10));
    }

    @Test
    public void testDivideDoubleAndNegativeDouble() throws Exception {
        assertEquals(3146431.43266 / -8426.6, evaluate(DIVIDE, 3146431.43266, -8426.6));
    }

    @Test
    public void testDivideTwoInts() throws Exception {
        assertEquals(1.5, evaluate(DIVIDE, 3, 2));
    }

    @Test
    public void testDivideTwoZeros() throws Exception {
        assertEquals(Double.NaN, evaluate(DIVIDE, 0, 0));
    }

    @Test
    public void testDivideIntAndZero() throws Exception {
        assertEquals(Double.POSITIVE_INFINITY, evaluate(DIVIDE, 5, 0));
    }

    @Test
    public void testDivideNegativeIntAndZero() throws Exception {
        assertEquals(Double.NEGATIVE_INFINITY, evaluate(DIVIDE, -5, 0));
    }

    @Test
    public void testDivideTwoInfinity() throws Exception {
        assertEquals(Double.NaN, evaluate(DIVIDE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testDivideIntAndNaN() throws Exception {
        assertEquals(Double.NaN, evaluate(DIVIDE, 1, Double.NaN));
    }

    @Test
    public void testDivideStringAndInt() throws Exception {
        assertEquals(Double.NaN, evaluate(DIVIDE, "5o", 3));
    }

    @Test
    public void testDivideIntAndString() throws Exception {
        assertEquals(Double.NaN, evaluate(DIVIDE, 3, "5o"));
    }

    @Test
    public void testDivideTwoStringDoubles() throws Exception {
        assertEquals(5.0, evaluate(DIVIDE, "5.5", "1.1"));
    }

    @Test
    public void testDivideIntByNegativeZeroString() throws Exception {
        assertEquals(Double.NEGATIVE_INFINITY, evaluate(DIVIDE, 1, "-0"));
    }

    @Test
    public void testDivideIntAndInfinity() throws Exception {
        assertEquals(-0.0, evaluate(DIVIDE, 5, Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testDivideIntAndNull() throws Exception {
        assertEquals(Double.POSITIVE_INFINITY, evaluate(DIVIDE, 3, null));
    }

    @Test
    public void testDivideNullAndInt() throws Exception {
        assertEquals(0.0, evaluate(DIVIDE, null, 3));
    }

    @Test
    public void testDivideTwoNulls() throws Exception {
        assertEquals(Double.NaN, evaluate(DIVIDE, null, null));
    }

    // MODULUS

    @Test
    public void testModulusNoArgument() throws Exception {
        assertEquals(null, evaluate(MODULUS));
    }

    @Test
    public void testModulusOneArgument() throws Exception {
        assertEquals(10, evaluate(MODULUS, 10));
    }

    @Test
    public void testModulusDoubleAndNegativeDouble() throws Exception {
        assertEquals(3146431.43266 % -8426.6, evaluate(MODULUS, 3146431.43266, -8426.6));
    }

    @Test
    public void testModulusIntAndZero() throws Exception {
        assertEquals(Double.NaN, evaluate(MODULUS, 3, 0));
    }

    @Test
    public void testModulusZeroAndInt() throws Exception {
        assertEquals(0.0, evaluate(MODULUS, 0, 3));
    }

    @Test
    public void testModulusTwoZeros() throws Exception {
        assertEquals(Double.NaN, evaluate(MODULUS, 0, 0));
    }

    @Test
    public void testModulusIntAndInfinity() throws Exception {
        assertEquals(3.0, evaluate(MODULUS, 3, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testModulusInfinityAndInt() throws Exception {
        assertEquals(Double.NaN, evaluate(MODULUS, Double.POSITIVE_INFINITY, 3));
    }

    @Test
    public void testModulusIntAndNaN() throws Exception {
        assertEquals(Double.NaN, evaluate(MODULUS, 1, Double.NaN));
    }

    @Test
    public void testModulusIntAndString() throws Exception {
        assertEquals(Double.NaN, evaluate(MODULUS, 3, "5o"));
    }

    @Test
    public void testModulusTwoStrings() throws Exception {
        assertEquals(3.0, evaluate(MODULUS, "23", "4"));
    }

    @Test
    public void testModulusIntAndNull() throws Exception {
        assertEquals(Double.NaN, evaluate(MODULUS, 3, null));
    }

    @Test
    public void testModulusNullAndInt() throws Exception {
        assertEquals(0.0, evaluate(MODULUS, null, 3));
    }

    @Test
    public void testModulusTwoNulls() throws Exception {
        assertEquals(Double.NaN, evaluate(MODULUS, null, null));
    }

    // ABSOLUTE

    @Test
    public void testAbsoluteNoArgument() throws Exception {
        assertEquals(null, evaluate(ABSOLUTE));
    }

    @Test
    public void testAbsoluteValueDouble() throws Exception {
        assertEquals(Math.abs(3146431.43266), evaluate(ABSOLUTE, 3146431.43266));
    }

    @Test
    public void testAbsoluteValueNegativeDouble() throws Exception {
        assertEquals(Math.abs(-8426.6), evaluate(ABSOLUTE, -8426.6));
    }

    @Test
    public void testAbsoluteValueNegativeInfinity() throws Exception {
        assertEquals(Double.POSITIVE_INFINITY, evaluate(ABSOLUTE, Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testAbsoluteValueNaN() throws Exception {
        assertEquals(Double.NaN, evaluate(ABSOLUTE, Double.NaN));
    }

    @Test
    public void testAbsoluteValueNegativeIntString() throws Exception {
        assertEquals(5.0, evaluate(ABSOLUTE, "-5"));
    }

    @Test
    public void testAbsoluteValueString() throws Exception {
        assertEquals(Double.NaN, evaluate(ABSOLUTE, "-5o"));
    }

    @Test
    public void testAbsoluteValueEmptyString() throws Exception {
        assertEquals(0.0, evaluate(ABSOLUTE, ""));
    }

    @Test
    public void testAbsoluteValueNull() throws Exception {
        assertEquals(0.0, evaluate(ABSOLUTE, (Object) null));
    }

    // NEGATE

    @Test
    public void testNegateNoArgument() throws Exception {
        assertEquals(null, evaluate(NEGATE));
    }

    @Test
    public void testNegatePositiveDouble() throws Exception {
        assertEquals(-3146431.43266, evaluate(NEGATE, 3146431.43266));
    }

    @Test
    public void testNegateNegativeDouble() throws Exception {
        assertEquals(8426.6, evaluate(NEGATE, -8426.6));
    }

    @Test
    public void testNegateInfinity() throws Exception {
        assertEquals(Double.NEGATIVE_INFINITY, evaluate(NEGATE, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testNegateNaN() throws Exception {
        assertEquals(Double.NaN, evaluate(NEGATE, Double.NaN));
    }

    @Test
    public void testNegateString() throws Exception {
        assertEquals(Double.NaN, evaluate(NEGATE, "5o"));
    }

    @Test
    public void testNegateStringInt() throws Exception {
        assertEquals(-5.0, evaluate(NEGATE, "5"));
    }

    @Test
    public void testNegateStringEmptyString() throws Exception {
        assertEquals(-0.0, evaluate(NEGATE, ""));
    }

    @Test
    public void testNegateStringNull() throws Exception {
        assertEquals(-0.0, evaluate(NEGATE, (Object) null));
    }

    // GREATER_THAN

    @Test
    public void testGreaterThanNoArgument() throws Exception {
        assertEquals(null, evaluate(GREATER_THAN));
    }

    @Test
    public void testGreaterThanOneArgument() throws Exception {
        assertEquals(10, evaluate(GREATER_THAN, 10));
    }

    @Test
    public void testGreaterThanTwoDoubles() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN, 3146431.43266, 937.1652));
    }

    @Test
    public void testGreaterThanSameDouble() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, 3146431.43266, 3146431.43266));
    }

    @Test
    public void testGreaterThanNegativeDoubleAndDouble() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, -8426.6, 937.1652));
    }

    @Test
    public void testGreaterThanInfinity() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testGreaterThanPositiveInfinityAndNegativeInfinity() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testGreaterThanZeroAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, 0, Double.NaN));
    }

    @Test
    public void testGreaterThanInfinityAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, Double.POSITIVE_INFINITY, Double.NaN));
    }

    @Test
    public void testGreaterThanNaNAndZero() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, Double.NaN, 0));
    }

    @Test
    public void testGreaterThanNaNAndInfinity() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, Double.NaN, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testGreaterThanIntAndString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, 9000, "5o"));
    }

    @Test
    public void testGreaterThanStringAndInt() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, "5o", 4));
    }

    @Test
    public void testGreaterThanTwoStrings() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN, "5o", "4o"));
    }

    @Test
    public void testGreaterThanTwoStringInts() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN, "5", "3.9"));
    }

    @Test
    public void testGreaterThanTwoStringsDifferentCapitalization() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, "5A", "5a"));
    }

    @Test
    public void testGreaterThanZeroAndEmptyString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, 0, ""));
    }

    @Test
    public void testGreaterThanStringAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, "zz", Double.NaN));
    }

    @Test
    public void testGreaterThanNaNAndString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, Double.NaN, "5o"));
    }

    @Test
    public void testGreaterThanBooleanTrueAndBooleanFalse() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN, Boolean.TRUE, Boolean.FALSE));
    }

    @Test
    public void testGreaterThanBooleanTrueAndZero() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN, Boolean.TRUE, 0));
    }

    @Test
    public void testGreaterThanBooleanTrueAndInt() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, Boolean.TRUE, 1));
    }

    @Test
    public void testGreaterThanIntAndNull() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN, 1, null));
    }

    @Test
    public void testGreaterThanNullAndZero() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, null, 0));
    }

    @Test
    public void testGreaterThanTwoNulls() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN, null, null));
    }

    // GREATER_THAN_OR_EQUAL

    @Test
    public void testGreaterThanOrEqualTwoDoubles() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, 3146431.43266, 937.1652));
    }

    @Test
    public void testGreaterThanOrEqualSameDouble() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, 937.1652, 937.1652));
    }

    @Test
    public void testGreaterThanOrEqualNegativeDoubleAndPositiveDouble() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN_OR_EQUAL, -8426.6, 937.1652));
    }

    @Test
    public void testGreaterThanOrEqualInfinity() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testGreaterThanOrEqualPositiveInfintyAndNegativeInfinity() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testGreaterThanOrEqualZeroAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN_OR_EQUAL, 0, Double.NaN));
    }

    @Test
    public void testGreaterThanOrEqualInfinityAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN_OR_EQUAL, Double.POSITIVE_INFINITY, Double.NaN));
    }

    @Test
    public void testGreaterThanOrEqualNaNAndZero() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN_OR_EQUAL, Double.NaN, 0));
    }

    @Test
    public void testGreaterThanOrEqualNaNAndInfinity() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN_OR_EQUAL, Double.NaN, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testGreaterThanOrEqualIntAndString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN_OR_EQUAL, 9000, "5o"));
    }

    @Test
    public void testGreaterThanOrEqualStringAndInt() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN_OR_EQUAL, "5o", 4));
    }

    @Test
    public void testGreaterThanOrEqualTwoStrings() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, "5o", "4o"));
    }

    @Test
    public void testGreaterThanOrEqualStringIntAndStringDouble() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, "5", "3.9"));
    }

    @Test
    public void testGreaterThanOrEqualTwoStringsDifferentCapitalization() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN_OR_EQUAL, "5A", "5a"));
    }

    @Test
    public void testGreaterThanOrEqualZeroAndEmptyString() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, 0, ""));
    }

    @Test
    public void testGreaterThanOrEqualStringAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN_OR_EQUAL, "zz", Double.NaN));
    }

    @Test
    public void testGreaterThanOrEqualNaNAndString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(GREATER_THAN_OR_EQUAL, Double.NaN, "5o"));
    }

    @Test
    public void testGreaterThanOrEqualBooleanTrueAndBooleanFalse() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, Boolean.TRUE, Boolean.FALSE));
    }

    @Test
    public void testGreaterThanOrEqualBooleanTrueAndZero() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, Boolean.TRUE, 0));
    }

    @Test
    public void testGreaterThanOrEqualBooleanTrueAndInt() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, Boolean.TRUE, 1));
    }

    @Test
    public void testGreaterThanOrEqualIntAndNull() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, 1, null));
    }

    @Test
    public void testGreaterThanOrEqualNullAndZero() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, null, 0));
    }

    @Test
    public void testGreaterThanOrEqualNullAndNull() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(GREATER_THAN_OR_EQUAL, null, null));
    }

    // LESS_THAN

    @Test
    public void testLessThanNoArgument() throws Exception {
        assertEquals(null, evaluate(LESS_THAN));
    }

    @Test
    public void testLessThanOneArgument() throws Exception {
        assertEquals(10, evaluate(LESS_THAN, 10));
    }

    @Test
    public void testLessThanTwoDoubles() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, 3146431.43266, 937.1652));
    }

    @Test
    public void testLessThanSameDouble() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, -8426.6, -8426.6));
    }

    @Test
    public void testLessThanNegativeDoubleAndPositiveDouble() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(LESS_THAN, -8426.6, 937.1652));
    }

    @Test
    public void testLessThanInfinity() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testLessThanPositiveInfinityAndNegativeInfinity() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testLessThanZeroAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, 0, Double.NaN));
    }

    @Test
    public void testLessThanInfinityAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, Double.POSITIVE_INFINITY, Double.NaN));
    }

    @Test
    public void testLessThanNaNAndZero() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, Double.NaN, 0));
    }

    @Test
    public void testLessThanNaNAndInfinity() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, Double.NaN, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testLessThanIntAndString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, 9000, "5o"));
    }

    @Test
    public void testLessThanStringAndInt() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, "5o", 4));
    }

    @Test
    public void testLessThanTwoStrings() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, "5o", "4o"));
    }

    @Test
    public void testLessThanStringIntAndStringDouble() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, "5", "3.9"));
    }

    @Test
    public void testLessThanTwoStringsDifferentCapitalization() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(LESS_THAN, "5A", "5a"));
    }

    @Test
    public void testLessThanZeroAndEmptyString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, 0, ""));
    }

    @Test
    public void testLessThanStringAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, "zz", Double.NaN));
    }

    @Test
    public void testLessThanNaNAndString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, Double.NaN, "5o"));
    }

    @Test
    public void testLessThanBooleanTrueAndBooleanFalse() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, Boolean.TRUE, Boolean.FALSE));
    }

    @Test
    public void testLessThanBooleanTrueAndZero() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, Boolean.TRUE, 0));
    }

    @Test
    public void testLessThanBooleanTrueAndInt() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, Boolean.TRUE, 1));
    }

    @Test
    public void testLessThanZeroAndNull() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, 0, null));
    }

    @Test
    public void testLessThanNullAndInt() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(LESS_THAN, null, 1));
    }

    @Test
    public void testLessThanTwoNulls() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN, null, null));
    }

    // LESS_THAN_OR_EQUAL

    @Test
    public void testLessThanOrEqualNoArgument() throws Exception {
        assertEquals(null, evaluate(LESS_THAN_OR_EQUAL));
    }

    @Test
    public void testLessThanOrEqualOneArgument() throws Exception {
        assertEquals(10, evaluate(LESS_THAN_OR_EQUAL, 10));
    }

    @Test
    public void testLessThanOrEqualTwoDoubles() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, 3146431.43266, 937.1652));
    }

    @Test
    public void testLessThanOrEqualSameDouble() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(LESS_THAN_OR_EQUAL, -8426.6, -8426.6));
    }

    @Test
    public void testLessThanOrEqualNegativeDoubleAndPositiveDouble() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(LESS_THAN_OR_EQUAL, -8426.6, 937.1652));
    }

    @Test
    public void testLessThanOrEqualInfinity() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(LESS_THAN_OR_EQUAL, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testLessThanOrEqualPositiveInfinityAndNegativeInfinity() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testLessThanOrEqualZeroAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, 0, Double.NaN));
    }

    @Test
    public void testLessThanOrEqualInfinityAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, Double.POSITIVE_INFINITY, Double.NaN));
    }

    @Test
    public void testLessThanOrEqualNaNAndZero() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, Double.NaN, 0));
    }

    @Test
    public void testLessThanOrEqualNaNAndInfinity() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, Double.NaN, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testLessThanOrEqualIntAndString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, 9000, "5o"));
    }

    @Test
    public void testLessThanOrEqualStringAndInt() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, "5o", 4));
    }

    @Test
    public void testLessThanOrEqualTwoStrings() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, "5o", "4o"));
    }

    @Test
    public void testLessThanOrEqualStringIntAndStringDouble() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, "5", "3.9"));
    }

    @Test
    public void testLessThanOrEqualTwoStringsDifferentCapitalization() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(LESS_THAN_OR_EQUAL, "5A", "5a"));
    }

    @Test
    public void testLessThanOrEqualZeroAndEmptyString() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(LESS_THAN_OR_EQUAL, 0, ""));
    }

    @Test
    public void testLessThanOrEqualStringAndNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, "zz", Double.NaN));
    }

    @Test
    public void testLessThanOrEqualNaNAndString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, Double.NaN, "5o"));
    }

    @Test
    public void testLessThanOrEqualBooleanTrueAndBooleanFalse() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, Boolean.TRUE, Boolean.FALSE));
    }

    @Test
    public void testLessThanOrEqualBooleanTrueAndZero() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, Boolean.TRUE, 0));
    }

    @Test
    public void testLessThanOrEqualBooleanTrueAndInt() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(LESS_THAN_OR_EQUAL, Boolean.TRUE, 1));
    }

    @Test
    public void testLessThanOrEqualIntAndNull() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(LESS_THAN_OR_EQUAL, 1, null));
    }

    @Test
    public void testLessThanOrEqualNullAndZero() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(LESS_THAN_OR_EQUAL, null, 0));
    }

    @Test
    public void testLessThanOrEqualTwoNulls() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(LESS_THAN_OR_EQUAL, null, null));
    }

    // AND

    @Test
    public void testAndNoArgument() throws Exception {
        assertEquals(null, evaluate(AND));
    }

    @Test
    public void testAndSingleBooleanFalse() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(AND, Boolean.FALSE));
    }

    @Test
    public void testAndSingleBooleanTrue() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(AND, Boolean.TRUE));
    }

    @Test
    public void testAndBooleanTrueAndBooleanFalse() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(AND, Boolean.TRUE, Boolean.FALSE));
    }

    @Test
    public void testAndTwoBooleanTrue() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(AND, Boolean.TRUE, Boolean.TRUE));
    }

    @Test
    public void testAndBooleanTrueAndNull() throws Exception {
        assertEquals(null, evaluate(AND, Boolean.TRUE, null));
    }

    @Test
    public void testAndNullAndBooleanTrue() throws Exception {
        assertEquals(null, evaluate(AND, null, Boolean.TRUE));
    }

    @Test
    public void testAndTwoNulls() throws Exception {
        assertEquals(null, evaluate(AND, null, null));
    }

    @Test
    public void testAndTwoInts() throws Exception {
        assertEquals(235325, evaluate(AND, 314, 235325));
    }

    @Test
    public void testAndZeroAndInt() throws Exception {
        assertEquals(0, evaluate(AND, 0, 314));
    }

    @Test
    public void testAndStringZeroAndInt() throws Exception {
        assertEquals(314, evaluate(AND, "0", 314));
    }

    @Test
    public void testAndStringFalseAndInt() throws Exception {
        assertEquals(314, evaluate(AND, "false", 314));
    }

    @Test
    public void testAndEmptyStringAndInt() throws Exception {
        assertEquals("", evaluate(AND, "", 314));
    }

    @Test
    public void testAndNaNAndInt() throws Exception {
        assertEquals(Double.NaN, evaluate(AND, Double.NaN, 314));
    }

    @Test
    public void testAndIntAndEmptyString() throws Exception {
        assertEquals("", evaluate(AND, 314, ""));
    }

    // OR

    @Test
    public void testOrNoArgument() throws Exception {
        assertEquals(null, evaluate(OR));
    }

    @Test
    public void testOrSingleBooleanFalse() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(OR, Boolean.FALSE));
    }

    @Test
    public void testOrSingleBooleanTrue() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(OR, Boolean.TRUE));
    }

    @Test
    public void testOrBooleanTrueAndBooleanFalse() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(OR, Boolean.TRUE, Boolean.FALSE));
    }

    @Test
    public void testOrTwoBooleanFalse() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(OR, Boolean.FALSE, Boolean.FALSE));
    }

    @Test
    public void testOrBooleanFalseAndBooleanTrue() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(OR, Boolean.FALSE, Boolean.TRUE));
    }

    @Test
    public void testOrBooleanFalseAndNull() throws Exception {
        assertEquals(null, evaluate(OR, Boolean.FALSE, null));
    }

    @Test
    public void testOrTwoNulls() throws Exception {
        assertEquals(null, evaluate(OR, null, null));
    }

    @Test
    public void testOrNullAndBooleanTrue() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(OR, null, Boolean.TRUE));
    }

    @Test
    public void testOrZeroAndInt() throws Exception {
        assertEquals(314, evaluate(OR, 0, 314));
    }

    @Test
    public void testOrTwoInts() throws Exception {
        assertEquals(314, evaluate(OR, 314, 235325));
    }

    @Test
    public void testOrStringZeroAndInt() throws Exception {
        assertEquals("0", evaluate(OR, "0", 314));
    }

    @Test
    public void testOrStringFalseAndInt() throws Exception {
        assertEquals("false", evaluate(OR, "false", 314));
    }

    @Test
    public void testOrEmptyStringAndInt() throws Exception {
        assertEquals(314, evaluate(OR, "", 314));
    }

    @Test
    public void testOrNaNAndString() throws Exception {
        assertEquals("Random", evaluate(OR, Double.NaN, "Random"));
    }

    // NOT

    @Test
    public void testNotNoArgument() throws Exception {
        assertEquals(null, evaluate(NOT));
    }

    @Test
    public void testNotBooleanTrue() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(NOT, Boolean.TRUE));
    }

    @Test
    public void testNotBooleanFalse() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(NOT, Boolean.FALSE));
    }

    @Test
    public void testNotEmptyString() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(NOT, ""));
    }

    @Test
    public void testNotString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(NOT, "Random"));
    }

    @Test
    public void testNotStringFalse() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(NOT, "false"));
    }

    @Test
    public void testNotStringZero() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(NOT, "0"));
    }

    @Test
    public void testNotNull() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(NOT, (Object) null));
    }

    @Test
    public void testNotObject() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(NOT, new Object()));
    }

    @Test
    public void testNotDoubleZero() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(NOT, 0.0));
    }

    @Test
    public void testNotDouble() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(NOT, 1.0));
    }

    @Test
    public void testNotNaN() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(NOT, Double.NaN));
        assertEquals(Boolean.TRUE, evaluate(NOT, Float.NaN));
    }

    // EMPTY

    @Test
    public void testIsEmptyNoArgument() throws Exception {
        assertEquals(null, evaluate(EMPTY));
    }

    @Test
    public void testIsEmptyNull() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(EMPTY, (Object) null));
    }

    @Test
    public void testIsEmptyBooleanTrue() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EMPTY, Boolean.TRUE));
    }

    @Test
    public void testIsEmptyBooleanFalse() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EMPTY, Boolean.FALSE));
    }

    @Test
    public void testIsEmptyZero() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EMPTY, 0));
    }

    @Test
    public void testIsEmptyDouble() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EMPTY, 0.0));
    }

    @Test
    public void testIsEmptyNaN() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EMPTY, Double.NaN));
        assertEquals(Boolean.FALSE, evaluate(EMPTY, Float.NaN));
    }

    @Test
    public void testIsEmptyWithEmptyString() throws Exception {
        assertEquals(Boolean.TRUE, evaluate(EMPTY, ""));
    }

    @Test
    public void testIsEmptyWithString() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EMPTY, "Random"));
    }

    @Test
    public void testIsEmptyWithEmptyList() throws Exception {
        List<Object> list = Lists.newArrayList();

        assertEquals(Boolean.TRUE, evaluate(EMPTY, list));
    }

    @Test
    public void testIsEmptyWithList() throws Exception {
        List<Object> list = Lists.newArrayList();
        list.add("a");
        list.add("b");

        assertEquals(Boolean.FALSE, evaluate(EMPTY, list));
    }

    @Test
    public void testIsEmptyObject() throws Exception {
        assertEquals(Boolean.FALSE, evaluate(EMPTY, new Object()));
    }

    // FORMAT: template type
    /* we try to make sure FORMAT() on java side give us the same output as format() on js side
    tests on js side are in expressionTest/functions.cmp
    here every test function (more or less) is a equivalent to a test cmp (expressionTest:test) in function.cmp.
    'skip' in the comment means we cannot test the same thing on the other side
    'diff' means js side give us different output
    just fyi: v.label1 = "Hello {0}", v.label2 = "Hello {0} and {1}"
    */

    //<expressionTest:test expression="{!format()}" exprText="format()" expected="''"/>
    @Test
    public void testFormatNoArguments() throws Exception {
        assertEquals("", evaluate(FORMAT));
    }

    //<expressionTest:test expression="{!format(null)}" exprText="format(null)" expected="''"/>
    @Test
    public void testFormatNull() throws Exception {
        assertEquals("", evaluate(FORMAT, (Object) null));
    }

    //skip: java doesn't have 'undefined'
    //<expressionTest:test expression="{!format(undefined)}" exprText="format(undefined)" expected="''"/>

    //<expressionTest:test expression="{!format(true)}" exprText="format(true)" expected="'true'"/>
    @Test
    public void testFormatBooleanTrue() throws Exception {
        assertEquals("true", evaluate(FORMAT, Boolean.TRUE));
    }

    //<expressionTest:test expression="{!format(false)}" exprText="format(false)" expected="'false'"/>
    @Test
    public void testFormatBooleanFalse() throws Exception {
        assertEquals("false", evaluate(FORMAT, Boolean.FALSE));
    }

    //<expressionTest:test expression="{!format(0)}" exprText="format(0)" expected="'0'"/>
    @Test
    public void testFormatZero() throws Exception {
        assertEquals("0", evaluate(FORMAT, 0));
    }

    //<expressionTest:test expression="{!format(0.0)}" exprText="format(0.0)" expected="'0'"/>
    //<expressionTest:test expression="{!format(123)}" exprText="format(123)" expected="'123'"/>
    //<expressionTest:test expression="{!format(123.4)}" exprText="format(123.4)" expected="'123.4'"/>
    @Test
    public void testFormatDouble() throws Exception {
        assertEquals("0", evaluate(FORMAT, 0.0));
    }

    //<expressionTest:test expression="{!format(NaN)}" exprText="format(NaN)" expected="''"/>
    @Test
    public void testFormatNaN() throws Exception {
        assertEquals("NaN", evaluate(FORMAT, Double.NaN));
        assertEquals("NaN", evaluate(FORMAT, Float.NaN));
    }

    //<expressionTest:test expression="{!format('')}" exprText="format('')" expected="''"/>
    @Test
    public void testFormatWithEmptyString() throws Exception {
        assertEquals("", evaluate(FORMAT, ""));
    }

    //<expressionTest:test expression="{!format('abc')}" exprText="format('abc')" expected="'abc'"/>
    //<expressionTest:test expression="{!format(v.label0)}" exprText="format(v.label0)" expected="'Hello'"/>
    @Test
    public void testFormatWithString() throws Exception {
        assertEquals("Random", evaluate(FORMAT, "Random"));
    }

    //skip: cannot have format([1,2,3...])
    @Test
    public void testFormatWithEmptyList() throws Exception {
        assertEquals("", evaluate(FORMAT, Lists.newArrayList()));
    }

    //skip: cannot have format([1,2,3...])
    @Test
    public void testFormatWithList() throws Exception {
        List<Object> list = Lists.newArrayList();
        list.add("a");
        list.add("b");

        assertEquals("a,b", evaluate(FORMAT, list));
    }

    //skip: cannot have format({key: value})
    @Test
    public void testFormatObject() throws Exception {
        assertEquals("[object Object]", evaluate(FORMAT, new Object()));
    }

    // FORMAT: argument type

    //<expressionTest:test expression="{!format(v.label2, null, undefined)}" exprText="format(v.label2, null, undefined" expected="'Hello  and '"/>
    @Test
	public void testFormatArgNull() throws Exception {
	    assertEquals("X", evaluate(FORMAT, "X{0}", (Object) null));
	}

	//<expressionTest:test expression="{!format(v.label2, true, false)}" exprText="format(v.label2, v.true, false)" expected="'Hello true and false'"/>
    @Test
    public void testFormatArgBoolean() throws Exception {
        assertEquals("XtrueYfalse", evaluate(FORMAT, "X{0}Y{1}", Boolean.TRUE, Boolean.FALSE));
    }

    //<expressionTest:test expression="{!format(v.label2, 0, 0.0)}" exprText="format(v.label2, 0, 0.0)" expected="'Hello 0 and 0'"/>
    //<expressionTest:test expression="{!format(v.label2, 123, 123.4)}" exprText="format(v.label2, 123, 123.4)" expected="'Hello 123 and 123.4'"/>
    @Test
    public void testFormatArgZero() throws Exception {
        assertEquals("X0Y0", evaluate(FORMAT, "X{0}Y{1}", 0, 0.0));
    }

    //<expressionTest:test expression="{!format(v.label1, NaN)}" exprText="format(v.label1, NaN)" expected="'Hello '"/>
    @Test
    public void testFormatArgNaN() throws Exception {
        assertEquals("XNaNYNaN", evaluate(FORMAT, "X{0}Y{1}", Double.NaN, Float.NaN));
    }

    //<expressionTest:test expression="{!format(v.label2, m.stringEmpty, m.string)}" exprText="format(v.label2, m.stringEmpty, m.string)" expected="'Hello  and Model'"/>
    //<expressionTest:test expression="{!format(v.label1, v.string)}" exprText="format(v.label1, v.string)" expected="'Hello Component'"/>
    @Test
    public void testFormatArgString() throws Exception {
        assertEquals("XYRandom", evaluate(FORMAT, "X{0}Y{1}", "", "Random"));
    }

    //<expressionTest:test expression="{!format(v.label2, m.emptyList, m.stringList)}" exprText="format(v.label2, m.emptyList, m.stringList)" expected="'Hello  and one,two,three'"/>
    @Test
    public void testFormatArgList() throws Exception {
        List<Object> list = Lists.newArrayList();
        list.add("a");
        list.add("b");

        assertEquals("XYa,b", evaluate(FORMAT, "X{0}Y{1}", Lists.newArrayList(), list));
    }

    //diff: <expressionTest:test expression="{!format(v.label2, m.objectNull, m.object)}" exprText="format(v.label2, m.objectNull, m.object)" expected="'Hello  and '"/>
    @Test
    public void testFormatArgObject() throws Exception {
        assertEquals("X[object Object]Y", evaluate(FORMAT, "X{0}Y", new Object()));
    }

    //<expressionTest:test expression="{!format(v.label1, v.string, v.integer)}" exprText="format(v.label1, v.string, v.integer)" expected="'Hello Component'"/>
    @Test
    public void testFormatMoreArgThanExpect() throws Exception {
    	assertEquals("X0Y", evaluate(FORMAT, "X{0}Y", 0, 1, 2));
    }

    //<expressionTest:test expression="{!format(v.label1)}" exprText="format(v.label1)" expected="'Hello {0}'"/>
    //<expressionTest:test expression="{!format(v.label2)}" exprText="format(v.label2)" expected="'Hello {0} and {1}'"/>
    @Test
    public void testFormatLessArgThanExpect() throws Exception {
    	assertEquals("X{0}Y", evaluate(FORMAT, "X{0}Y"));
    }

}
