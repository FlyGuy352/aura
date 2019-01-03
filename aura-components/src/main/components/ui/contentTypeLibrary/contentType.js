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

/*eslint-disable */
function contentTypeLibrary () {
    var SEPARATOR = '/';
    var COMA      = ',';
    var VALID_TYPE_LIST = [ 'application'
                          , 'audio'
                          , 'example'
                          , 'image'
                          , 'message'
                          , 'model'
                          , 'multipart'
                          , 'text'
                          , 'video'
                          , '*'
                          ];


    function Factory (contentTypes) {
        return new ContentType(contentTypes);
    }

    // Constructor
    function ContentType (contentTypes) {
        this.setAccepted(contentTypes);
    }

    ContentType.prototype.accept = function (contentType) {
        var type = _isValidContentType(contentType) ? contentType : _isFile(contentType) ? contentType.type : false;
        return type && this._contentTypes.some(function isSubset(contentType) {
                return _isSubset(type, contentType);
            })
    };

    ContentType.prototype.setAccepted = function (contentTypes) {
        if (Array.isArray(contentTypes)) {
            this._contentTypes = _parseValidContentTypes(contentTypes);
            return this;
        }
        if ($A.util.isString(contentTypes)) {
            this._contentTypes = _parseValidContentTypes(contentTypes.split(COMA));
            return this;
        }
        this._contentTypes = [];
        return this;
    };

    function _parseValidContentTypes (contentTypesArr) {
        return contentTypesArr.filter(function filterContentType(contentType) {
            return _isValidContentType(contentType);
        });
    }

    function _isValidContentType (contentType) {
        return $A.util.isString(contentType)
                && _hasTypeAndSubtypeOnly(contentType)
                && _hasValidType(contentType);
    }

    function _hasTypeAndSubtypeOnly (contentType) {
        return contentType.split(SEPARATOR).length === 2;
    }

    function _hasValidType (contentType) {
        var type = contentType.split(SEPARATOR)[0].toLowerCase();
        return VALID_TYPE_LIST.indexOf(type) !== -1;
    }

    function _isSubset (type, superType) {
        type = _splitContentType(type);
        superType = _splitContentType(superType);
        return (type.type === superType.type || superType.type === '*')
                && (type.subType === superType.subType || superType.subType === '*');
    }

    function _isFile (file) {
        return file instanceof File;
    }

    function _splitContentType (contentType) {
        return {
            type    : contentType.split(SEPARATOR)[0],
            subType : contentType.split(SEPARATOR)[1]
        };
    }

    return Factory;
} /*eslint-enable */