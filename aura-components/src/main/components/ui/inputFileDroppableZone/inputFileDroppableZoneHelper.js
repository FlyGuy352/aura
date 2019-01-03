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
    setElementOverStyleClass : function (cmp) {
        this.setDropZoneClassNames(cmp,['drag-over',cmp.get('v.classOver')]);
    },
    removeElementOverStyleClass : function (cmp) {
        this.setDropZoneClassNames(cmp);
    },
    setDropZoneClassNames : function (cmp, obj) {
        var classnames = this.lib.classnames;
        var classAttr  = cmp.get('v.class');
        var classes = classnames.ObjectToString('droppable-zone', obj, classAttr);
        cmp.set('v.dropZoneClassList', classes);
    },
    thereAreFiles : function (dragEvent) {
        return dragEvent.dataTransfer.files.length > 0;
    },
    filesAreValid : function (cmp, event) {
        return this.meetsMultipleConditions(cmp,event) &&
               this.meetsAcceptAndSizeConditions(cmp, event);
    },
    meetsMultipleConditions : function (cmp, event) {
        var multiple = cmp.get('v.multiple');
        return multiple ? multiple : event.dataTransfer.files.length === 1;
    },
    meetsAcceptAndSizeConditions : function (cmp, event) {
        var ContentType = this.ct.contentType;
        var accept = cmp.get('v.accept');
        var size   = Number(cmp.get('v.maxSizeAllowed')) || Infinity;
        var myContentType = new ContentType(accept);
        return this._getFileArr(event).every(function processFile(file) {
            return myContentType.accept(file) && this._fileMeetsSize(file,size);
        }.bind(this));
    },
    _getFileArr : function (dragEvent) {
        return Object.keys(dragEvent.dataTransfer.files).map(function fileMapper(index) {
            return dragEvent.dataTransfer.files[index];
        });
    },
    _fileMeetsSize : function (file, size) {
        return file.size <= size;
    },
    fireDropEvent : function (cmp, event) {
        cmp.getEvent('change').setParams({
            files : event.dataTransfer.files
        }).fire();
    }
});