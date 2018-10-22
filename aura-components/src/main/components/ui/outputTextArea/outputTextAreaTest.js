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
    /**
     * Verify the elements used to display a textArea.
     * Changing the element type would affect styling of third party apps.
     */
    testHtmlElementOfOutputTextArea:{
        attributes:{value : 'Salesforce, ....some literature about the company.'},
        test:function(cmp){
            var span = cmp.find('span');
            //Make sure a span tag is used for outputTextArea. Failure might mean breaking styling of third party app
            $A.test.assertEquals('SPAN', span.getElement().tagName, "OutputTextArea is expected to use a span tag to display value.");
            $A.test.assertEquals('Salesforce, ....some literature about the company.', $A.test.getText(span.getElement()));
        }
    },
    /**
     * verify that empty string value will end up as empty span.
     */
    testEmptyStringAsValue:{
        attributes:{value : ''},
        test:function(cmp){
            var span = cmp.find('span');
            $A.test.assertNotNull(span);
            $A.test.assertEquals('', $A.test.getText(span.getElement()));
        }
    },

    testHtmlElementWithLink:{
        attributes:{value : 'Salesforce.com, ....some literature about the company.'},
        test:function(cmp){
            var span = cmp.find('span');
            //Make sure a span tag is used for outputTextArea. Failure might mean breaking styling of third party app
            $A.test.assertEquals('SPAN', span.getElement().tagName, "OutputTextArea is expected to use a span tag to display value.");
            this.assertLinksPresent(cmp, "href=\"http://Salesforce.com\"");
        }
    },

    testHtmlElementWithTagEscaped:{
        attributes:{value : '<span>Salesforce</span>, ....some literature about the company.'},
        test:function(cmp){
            var span = cmp.find('span');
            //Make sure a span tag is used for outputTextArea. Failure might mean breaking styling of third party app
            $A.test.assertEquals('SPAN', span.getElement().tagName, "OutputTextArea is expected to use a span tag to display value.");
            this.assertTextNotPresent(cmp, "<span>Salesforce</span>");
            this.assertLinksPresent(cmp, "&lt;span&gt;Salesforce&lt;/span&gt;");
        }
    },

    testUrlEndingWithQuotation:{
        attributes:{value : '\"https://www.salesforce.com\" ....some literature about the company.'},
        test:function(cmp){
            var span = cmp.find('span');
            //Make sure a span tag is used for outputTextArea. Failure might mean breaking styling of third party app
            $A.test.assertEquals('SPAN', span.getElement().tagName, "OutputTextArea is expected to use a span tag to display value.");
            this.assertTextNotPresent(cmp, "<span>Salesforce</span>");
            this.assertLinksPresent(cmp, "href=\"https://www.salesforce.com\"");
        }
    },

    testUrlEndingWithEscapedQuotation:{
        attributes:{value : '&quot;https://www.salesforce.com&quot; ....some literature about the company.'},
        test:function(cmp){
            var span = cmp.find('span');
            //Make sure a span tag is used for outputTextArea. Failure might mean breaking styling of third party app
            $A.test.assertEquals('SPAN', span.getElement().tagName, "OutputTextArea is expected to use a span tag to display value.");
            this.assertTextNotPresent(cmp, "<span>Salesforce</span>");
            this.assertLinksPresent(cmp, "href=\"https://www.salesforce.com\"");
        }
    },

    assertLinksPresent: function(cmp, hrefText) {
        $A.test.addWaitForWithFailureMessage(true,
            function() {
                var htmlValue = cmp.find("span").getElement().innerHTML;
                return $A.test.contains(htmlValue, hrefText);
            }, "couldn't find " + hrefText + " in: "  + cmp.find("span").getElement().innerHTML);
    },

    assertTextNotPresent: function(cmp, text) {
    $A.test.addWaitForWithFailureMessage(true,
        function() {
            var htmlValue = cmp.find("span").getElement().innerHTML;
            return !$A.test.contains(htmlValue, text);
        }, "Found " + text + " in: "  + cmp.find("span").getElement().innerHTML);
}
/*eslint-disable semi*/
})
/*eslint-enable semi*/
