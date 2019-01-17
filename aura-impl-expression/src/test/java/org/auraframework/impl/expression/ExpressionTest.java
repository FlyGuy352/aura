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
package org.auraframework.impl.expression;

import static org.auraframework.impl.expression.functions.BooleanFunctions.AND;
import static org.auraframework.impl.expression.functions.BooleanFunctions.NOT;
import static org.auraframework.impl.expression.functions.BooleanFunctions.OR;
import static org.auraframework.impl.expression.functions.MathFunctions.SUBTRACT;
import static org.auraframework.impl.expression.functions.MultiFunctions.ADD;

import java.math.BigDecimal;

import org.auraframework.expression.Expression;
import org.auraframework.expression.ExpressionType;
import org.auraframework.expression.PropertyReference;
import org.auraframework.instance.ValueProvider;
import org.auraframework.service.ContextService;
import org.auraframework.service.DefinitionService;
import org.auraframework.system.Location;
import org.auraframework.util.test.util.UnitTestCase;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;

/**
 * Tests of expression evaluation
 * 
 * @hierarchy Aura.Runtime.Expression.Server.Evaluation
 * @userStory a07B0000000EdAC
 */
public class ExpressionTest extends UnitTestCase {

    private static final Location l = new Location("test", -1);
    private static final PropertyReference i314 = new PropertyReferenceImpl("i314", l);
    private static final PropertyReference i235325 = new PropertyReferenceImpl("i235325", l);
    private static final PropertyReference bTrue = new PropertyReferenceImpl("bTrue", l);
    private static final PropertyReference bFalse = new PropertyReferenceImpl("bFalse", l);
    
    @Mock
    ContextService contextService;
    
    @Mock
    DefinitionService definitionService;

    private static ValueProvider values = new ValueProvider() {
        @Override
        public Object getValue(PropertyReference key) {
            if (key == i314) {
                return Integer.valueOf(314);
            } else if (key == i235325) {
                return Integer.valueOf(235325);
            } else if (key == bTrue) {
                return Boolean.TRUE;
            } else if (key == bFalse) {
                return Boolean.FALSE;
            }
            return null;
        }
    };

    @Test
    public void testNumberExpression() throws Exception {
        Expression e = new FunctionCallImpl(ADD, ImmutableList.<Expression> of(i314, i235325), l);
        Object o = e.evaluate(values);
        assertEquals(314 + 235325, o);

        // (i314 + i235325) - (i314 + i314)
        e = new FunctionCallImpl(SUBTRACT, ImmutableList.<Expression> of(e,
                new FunctionCallImpl(ADD, ImmutableList.<Expression> of(i314, i314), l)), l);
        o = e.evaluate(values);
        assertEquals((314.0 + 235325) - (314 + 314), o);

        e = new FunctionCallImpl(SUBTRACT, ImmutableList.<Expression> of(e, new LiteralImpl(new BigDecimal(17), l)), l);
        o = e.evaluate(values);
        assertEquals(((314.0 + 235325) - (314 + 314)) - 17, o);
    }

    @Test
    public void testBooleanComplex() throws Exception {
        Expression e;
        Object o;

        // true && (false || !true)
        e = new FunctionCallImpl(AND, ImmutableList.<Expression> of(
                bTrue,
                new FunctionCallImpl(OR, ImmutableList.<Expression> of(new LiteralImpl(false, l), new FunctionCallImpl(
                        NOT, ImmutableList.<Expression> of(new LiteralImpl(true, l)), l)), l)), l);
        o = e.evaluate(values);
        assertEquals("Expected boolean expression to be false", Boolean.FALSE, o);
    }

    @Test
    public void testLiteralNull() throws Exception {
        verifyEvaluateResult("null", ExpressionType.LITERAL, null, null);
    }

    @Test
    public void testPropertyEvaluatesToNull() throws Exception {
        ValueProvider vp = new ValueProvider() {
            @Override
            public Object getValue(PropertyReference key) {
                return null;
            }
        };
        verifyEvaluateResult("nullprop", ExpressionType.PROPERTY, vp, null);
        verifyEvaluateResult("nullarray[0]", ExpressionType.PROPERTY, vp, null);
        verifyEvaluateResult("nothing.here == null", ExpressionType.FUNCTION, vp, true);
        verifyEvaluateResult("nothing.here != null", ExpressionType.FUNCTION, vp, false);
    }

    @Test
    public void testPropertyIsNotNull() throws Exception {
        ValueProvider vp = new ValueProvider() {
            @Override
            public Object getValue(PropertyReference key) {
                return "null? no!";
            }
        };
        verifyEvaluateResult("array[66]", ExpressionType.PROPERTY, vp, "null? no!");
        verifyEvaluateResult("something.here == null", ExpressionType.FUNCTION, vp, false);
        verifyEvaluateResult("something.here != null", ExpressionType.FUNCTION, vp, true);
    }

    @Test
    public void testFunctionWithNullOperands() throws Exception {
        verifyEvaluateResult("true && null", ExpressionType.FUNCTION, null, null);
        verifyEvaluateResult("null + 1", ExpressionType.FUNCTION, null, 1);
        verifyEvaluateResult("'null' == null", ExpressionType.FUNCTION, null, false);
    }

    // currently throws NullPointerException
    // @TestLabels(IgnoreFailureReason.IN_DEV)
    // public void testPropertyWithNoValueProvider() throws Exception {
    // verifyEvaluateException("undefined", "??????????");
    // }

    // currently throws IndexOutOfBoundsException, catch during parse?
    // @TestLabels(IgnoreFailureReason.IN_DEV)
    // public void testFunctionMissingOperands() throws Exception {
    // verifyEvaluateException("add()", "??????????");
    // }

    @Test
    public void testFunctionMismatchedOperands() throws Exception {
        // Note the 3.0 on this
        verifyEvaluateResult("3 + ' little piggies'", ExpressionType.FUNCTION, null, "3 little piggies");

        verifyEvaluateResult("'5' + 6", ExpressionType.FUNCTION, null, "56");
        verifyEvaluateResult("'2' == 2", ExpressionType.FUNCTION, null, false);
    }

    @Test
    public void testFunctionEvaluatesToNaN() throws Exception {
        verifyEvaluateResult("0 / 0", ExpressionType.FUNCTION, null, Double.NaN);
    }

    @Test
    public void testFunctionEvaluatesToInfinity() throws Exception {
        verifyEvaluateResult("-2 / -0.0", ExpressionType.FUNCTION, null, Double.POSITIVE_INFINITY);
        verifyEvaluateResult("-5 / 0", ExpressionType.FUNCTION, null, Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testMultilineFunction() throws Exception {
        verifyEvaluateResult("5 +\r\n1\r\n!=\r\n'null'", ExpressionType.FUNCTION, null, true);
    }

    private void verifyEvaluateResult(String expression, ExpressionType type, ValueProvider vp, Object result)
            throws Exception {
        Expression e = new AuraExpressionBuilder(new ExpressionFunctions(contextService, definitionService)).buildExpression(expression, null);
        assertEquals("Unexpected expression type when parsing <" + expression + ">", type, e.getExpressionType());
        assertEquals("Unexpected evaluation of <" + expression + ">", result, e.evaluate(vp));
        Mockito.verifyZeroInteractions(contextService, definitionService);
    }

    // private void verifyEvaluateException(String expression, String
    // messageStartsWith) throws Exception {
    // Expression e = buildExpression(expression);
    // try {
    // Object result = e.evaluate(null);
    // fail("No Exception thrown for <" + expression + ">. Instead, got: " +
    // result);
    // } catch (Exception ex) {
    // if (ex.getMessage() != null &&
    // ex.getMessage().startsWith(messageStartsWith)) return;
    // failNotEquals("Unexpected exception for <" + expression + "> ",
    // messageStartsWith, ex);
    // }
    // }
}
