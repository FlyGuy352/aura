({
    testStorageServiceNotDefined: function(cmp) {
        var testUtils = cmp.get("v.testUtils");
        testUtils.assertUndefined($A.storageService);
    },
    
    testPlatformExposedAPIs: function(cmp, event, helper) {
        var testUtils = cmp.get("v.testUtils");
        var exposedAPIs = event.getParam("arguments").exposedAPIs;
        for (var i = 0; i < exposedAPIs.length; i++) {
            var api = exposedAPIs[i];
            testUtils.assertDefined($A[api], "Expected " + api + " to be exposed on SecureAura");
        }
    },

    testGetRootReturnsSecureComponent: function(cmp) {
        var testUtils = cmp.get("v.testUtils");
        testUtils.assertStartsWith("SecureComponent:", $A.getRoot().toString(), "Expected $A.getRoot() to return"
                + " a SecureComponent");
    },
    
    testGetCallback: function(cmp) {
        var testUtils = cmp.get("v.testUtils");
        var that = cmp;
        var func = $A.getCallback(function(){
            testUtils.assertStartsWith("SecureWindow", this.toString(), "Expected SecureWindow as context to $A.getCallback");
            testUtils.assertStartsWith("SecureWindow", window.toString(), "Expected SecureWindow for window in $A.getCallback");
            testUtils.assertStartsWith("SecureDocument", document.toString(), "Expected SecureDocument as document in $A.getCallback");
            that.set("v.testComplete", true);
        });
        testUtils.assertTrue(func.toString().indexOf("SecureFunction") === -1, "$A.getCallback should not return a SecureFunction");
        window.setTimeout(func, 0);
    },
    
    testGetGVP: function(cmp) {
        var testUtils = cmp.get("v.testUtils");
        var browserGvp = $A.get("$Browser");
        // check that a couple things on $Browser are there to verify it's the correct object
        testUtils.assertDefined(browserGvp["formFactor"]);
        testUtils.assertDefined(browserGvp["isPhone"]);
    },
    
    testGetDifferentNamespaceComponentReturnsSecureComponentRef: function(cmp) {
        var testUtils = cmp.get("v.testUtils");
        var globalId = cmp.find("textCmp").getGlobalId();
        var secureComponentRef = $A.getComponent(globalId);
        testUtils.assertStartsWith("SecureComponentRef:", secureComponentRef.toString(), "Expected $A.getComponent on a component"
                + " from another namespace to be a SecureComponentRef");
    },
    
    testGetSameNamespaceComponentReturnsSecureComponent: function(cmp) {
        var testUtils = cmp.get("v.testUtils");
        var globalId = cmp.find("lockerCmp").getGlobalId();
        var secureComponent = $A.getComponent(globalId);
        testUtils.assertStartsWith("SecureComponent:", secureComponent.toString(), "Expected $A.getComponent on a component"
                + " from the same namespace to be a SecureComponent");
    },

    testUtilExposedOnSecureAura: function(cmp) {
        var testUtils = cmp.get("v.testUtils");
        // Verify 1 exposed API and 1 API not exposed in Locker
        testUtils.assertDefined($A.util);
        testUtils.assertDefined($A.util.hasClass);
        testUtils.assertUndefined($A.util.isIOSWebView);
    },
    
    testUtilHasClassAPI: function(cmp) {
        var testUtils = cmp.get("v.testUtils");
        var element = cmp.find("lockerCmp").getElement();
        $A.util.addClass(element, "testClass");
        testUtils.assertTrue($A.util.hasClass(element, "testClass"));
    },

    testDynamicallyCreatedComponentDifferentNamespaceIsSecureComponentRef: function(cmp) {
        var testUtils = cmp.get("v.testUtils");
        $A.createComponents([
             ["aura:text",{value:'FirstText'}],
             ["aura:text",{value:'SecondText'}], 
             ["aura:text",{value:'ThirdText'}]],
             function(components, status, statusMessagesList){
                 testUtils.assertEquals("SUCCESS", status, "$A.createComponents call did not return SUCCESS");
                 testUtils.assertEquals(3, components.length, "Did not receive expected number of components from $A.createComponents");
                 for (var i = 0; i < components.length; i++) {
                     var newCmp = components[i];
                     testUtils.assertStartsWith("SecureComponentRef:", newCmp.toString(), "Created components (via $A.createComponents) should be of type SecureComponentRef");
                 }
                 cmp.set("v.testComplete", true);
             }
        );
    },

    testDynamicallyCreatedComponentSameNamespaceIsSecureComponent: function(cmp) {
        var testUtils = cmp.get("v.testUtils");
        $A.createComponent("lockerTest:secureWindowTest", {},
                function(newCmp, status, statusMessagesList){
                    testUtils.assertEquals("SUCCESS", status, "$A.createComponent call did not return SUCCESS");
                    testUtils.assertStartsWith("SecureComponent:", newCmp.toString(), "Created component (via $A.createComponent) should be of type SecureComponent");
                    cmp.set("v.testComplete", true);
                }
        );
    },

    testDynamicallyCreatedAuraHtmlComponentIsSecureComponent: function(cmp) {
        var testUtils = cmp.get("v.testUtils"); 
        $A.createComponent("aura:html", {"tag":"div"},
            function(newCmp, status) {
                testUtils.assertEquals("SUCCESS", status, "$A.createComponent call did not return SUCCESS");
                testUtils.assertStartsWith("SecureComponent:", newCmp.toString(), "Created aura:html component (via $A.createComponent) should be of type SecureComponent");
                cmp.set("v.testComplete", true);
            }
        );
    },

    testGetReferenceOnDynamicLabel: function(cmp) {
        var testUtils = cmp.get("v.testUtils");
        // use string concatenation so framework does not parse the source and pre-fetch the label
        var labelRef = $A.getReference("$Label.test." + "task_mode_today");
        cmp.set("v.labelAttr", labelRef);
        // framework will make a network request for the label so need to wait
        testUtils.addWaitForWithFailureMessage(
                true,
                function() {
                    // wait for placeholder to be replaced
                    return cmp.get("v.labelAttr") !== "[test.task_mode_today]";
                },
                "Label reference never replaced original value",
                function() {
                    var labelText = document.getElementById("labelOutput").textContent;
                    // if server does not have label we get back 'does not exist' message
                    testUtils.assertTrue(labelText === "Today" || labelText === "$Label.test.task_mode_today does not exist.");
                }
        );
    }
})