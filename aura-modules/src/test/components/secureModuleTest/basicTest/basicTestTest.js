({
    /**
     * Note that this test file operates in system mode (objects are not Lockerized) so the tests delegate logic and
     * verification to the controller and helper files, which operate in user mode.
     */

    // LockerService not supported on IE
    browsers: ["-IE8", "-IE9", "-IE10", "-IE11"],

    setUp: function (cmp) {
        cmp.set("v.testUtils", $A.test);
    },

    testDollarAuraIsSecure: {
        test: function(cmp) {
            cmp.testDollarAuraIsSecure();
        }
    },

    testDollarAuraNotAccessibleInModules: {
        test: function (cmp) {
            cmp.testDollarAuraNotAccessibleInModules();
        }
    },

    /**
     * Verify that miscellaneous globals like aura, Sfdc, sforce
     */
    testMiscGlobalsNotAccessibleInModules: {
        test: function (cmp) {
            cmp.testMiscGlobalsNotAccessibleInModules();
        }
    },
    testWindowIsSecure: {
        test: function (cmp) {
            cmp.testWindowIsSecure();
        }
    },
    testLWCIsSecure: {
        test: function (cmp) {
            cmp.testLWCIsSecure();
        }
    },

    // Disabled until LWC supports * imports again
    _testEngineIsImmutable: {
        test: function (cmp) {
            cmp.testEngineIsImmutable();
        }
    },

    testOptOutOfLockerUsingMetaData: {
        test: function (cmp) {
            cmp.testOptOutOfLockerUsingMetaData();
        }
    },

    testLightningElementIsImmutable: {
        test: function (cmp) {
            cmp.testLightningElementIsImmutable();
        }
    },

    /**
     * Verify that modules are evaluated in browsers where locker is gracefully degraded
     */
    testSecureModulesInUnsupportedBrowsers: {
        // only run in unsupported browsers where we fallback to non-Locker mode
        browsers: ["IE8", "IE9", "IE10", "IE11"],
        test: function (cmp) {
            cmp.testSecureModulesInUnsupportedBrowsers();
        }
    },

    testCanAccessDocumentBodyFromInternalLib: {
        test: function(cmp) {
            cmp.sanityChecksTester("testCanAccessDocumentBodyFromInternalLib");
        }
    },

    testCanAccessDocumentHeadFromInternalLib: {
        test: function(cmp) {
            cmp.sanityChecksTester("testCanAccessDocumentHeadFromInternalLib");
        }
    },

    testWindowIsSecureInInternalLib: {
        test: function(cmp) {
            cmp.sanityChecksTester("testWindowIsSecureInInternalLib");
        }
    },

    testDollarAuraNotAccessibleInInternalLib: {
        test: function(cmp) {
            cmp.sanityChecksTester("testDollarAuraNotAccessibleInInternalLib");
        }
    },

    testSecureWrappersInRenderer: {
        attributes: {
            testRenderer: true
        },
        test: function(cmp) {
            // Renderer will throw an error on load if anything is not Lockerized as expected, nothing to assert here.
        }
    },

    testDocumentIsSecure: {
        test: function(cmp) {
            cmp.sanityChecksTester("testDocumentIsSecure");
        }
    },

    testDocumentIsSecureInInternalLib: {
        test: function(cmp) {
            cmp.sanityChecksTester("testDocumentIsSecureInInternalLib");
        }
    },

    testAppendDynamicallyCreatedDivToMarkup: {
        test: function(cmp) {
            cmp.sanityChecksTester("testAppendDynamicallyCreatedDivToMarkup");
        }
    },
    testContextInModule: {
        test: function(cmp) {
            cmp.sanityChecksTester("testContextInModule");
        }
    },

    testDefineGetterExploit: {
        // This exploit not covered in IE11
        browsers: ["-IE8", "-IE9", "-IE10", "-IE11"],
        // Remove UnAdaptableTest label when unsafe-eval and unsafe-inline are added back to CSP
        labels: ["UnAdaptableTest"],
        test: function(cmp) {
            cmp.sanityChecksTester("testDefineGetterExploit");
        }
    },

    /**
     * See W-2974202 for original exploit.
     */
    testSetTimeoutNonFunctionParamExploit: {
        test: function(cmp) {
            cmp.sanityChecksTester("testSetTimeoutNonFunctionParamExploit");
        }
    },

    testLocationExposed: {
        test: function(cmp) {
            cmp.sanityChecksTester("testLocationExposed");
        }
    },

    testCtorAnnotation: {
        test: function(cmp) {
            cmp.sanityChecksTester("testCtorAnnotation");
        }
    },

    testSecureElementPrototypeCounterMeasures: {
        test: function(cmp) {
            cmp.sanityChecksTester("testSecureElementPrototypeCounterMeasures");
        }
    },

    testInstanceOf: {
        test: function(cmp) {
            cmp.sanityChecksTester("testInstanceOf");
        }
    }
})
