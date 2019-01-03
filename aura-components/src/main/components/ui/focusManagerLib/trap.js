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

 // Code fom:
 // https://github.com/davidtheclark/tabbable
 // https://github.com/davidtheclark/focus-trap

function lib(focusUtil) { //eslint-disable-line no-unused-vars
    var trap;
    var tabbableNodes;
    var previouslyFocused;
    var activeFocusTrap;
    var config;

    function tabbable(el) {
        return focusUtil.getTabbableChildren(el);
    }

    function updateTabbableNodes() {
        tabbableNodes = tabbable(trap);
    }

    function checkClick(e) {
        if (trap.contains(e.target)) {
            return;
        }
        e.preventDefault();
        e.stopImmediatePropagation();
    }

    function checkFocus(e) {
        updateTabbableNodes();
        if (trap.contains(e.target)){
            return;
        }
        tabbableNodes[0].focus();
    }

    function checkKey(e) {
        if (e.key === 'Tab' || e.keyCode === 9) {
            updateTabbableNodes();
            var lastTabbableNode = tabbableNodes[tabbableNodes.length - 1];
            var firstTabbableNode = tabbableNodes[0];
            if (e.shiftKey) {
                if (e.target === firstTabbableNode) {
                    e.preventDefault();
                    lastTabbableNode.focus();
                    return;
                }
                return;
            }
            if (e.target === lastTabbableNode) {
                e.preventDefault();
                firstTabbableNode.focus();
                return;
            }
        }

        if (e.key === 'Escape' || e.key === 'Esc' || e.keyCode === 27) {
            deactivate(); //eslint-disable-line no-use-before-define
        }
    }

    function deactivate() {
        if (!activeFocusTrap) {
            return;
        }
        activeFocusTrap = false;

        document.removeEventListener('focus', checkFocus, true);
        document.removeEventListener('click', checkClick, true);
        document.removeEventListener('touchend', checkClick, true);
        document.removeEventListener('keydown', checkKey, true);

        if (config.onDeactivate) {
            config.onDeactivate();
        }

        if (!config.keyboardOnly) {
            setTimeout(function setPreviouslyFocusedFocus() {
                previouslyFocused.focus();
            }, 0);
        }
    }

    function activate(element, options) {
        // There can be only one focus trap at a time
        if (activeFocusTrap) {
            deactivate();
        }

        activeFocusTrap = true;

        trap = (typeof element === 'string') ? document.querySelector(element) : element;
        config = options || {};
        previouslyFocused = document.activeElement;

        updateTabbableNodes();

        var focusNode = (function focusNodeClojure() {
            var node;
            if (!config.initialFocus) {
                node = tabbableNodes[0];
                if (!node) {
                    throw new Error('You can\'t have a focus-trap without at least one focusable element');
                }
              return node;
            }

            if (typeof config.initialFocus === 'string') {
                node = document.querySelector(config.initialFocus);
            } else {
                node = config.initialFocus;
            }

            if (!node) {
              throw new Error('The `initialFocus` selector you passed referred to no known node');
            }
            return node;
        }());

        focusNode.focus();

        document.addEventListener('keydown', checkKey, true);
        if (config.keyboardOnly !== true) {
            document.addEventListener('focus', checkFocus, true);
            document.addEventListener('click', checkClick, true);
            document.addEventListener('touchend', checkClick, true);
        }
    }

    return {
        activate   : activate,
        deactivate : deactivate
    };
}
