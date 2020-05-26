(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports, require('@fullcalendar/core')) :
        typeof define === 'function' && define.amd ? define(['exports', '@fullcalendar/core'], factory) :
            (global = global || self, factory(global.FullCalendarBulma = {}, global.FullCalendar));
}(this, function (exports, core) { 'use strict';

    /*! *****************************************************************************
    Copyright (c) Microsoft Corporation. All rights reserved.
    Licensed under the Apache License, Version 2.0 (the "License"); you may not use
    this file except in compliance with the License. You may obtain a copy of the
    License at http://www.apache.org/licenses/LICENSE-2.0

    THIS CODE IS PROVIDED ON AN *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION ANY IMPLIED
    WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A PARTICULAR PURPOSE,
    MERCHANTABLITY OR NON-INFRINGEMENT.

    See the Apache Version 2.0 License for specific language governing permissions
    and limitations under the License.
    ***************************************************************************** */
    /* global Reflect, Promise */

    var extendStatics = function(d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };

    function __extends(d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    }

    var BulmaCalendarTheme = /** @class */ (function (_super) {
        __extends(BulmaCalendarTheme, _super);
        function BulmaCalendarTheme() {
            return _super !== null && _super.apply(this, arguments) || this;
        }
        return BulmaCalendarTheme;
    }(core.Theme));
    BulmaCalendarTheme.prototype.classes = {
        widget: 'fc-bulma',

        tableGrid: 'table is-bordered',
        tableList: 'table',
        tableListHeading: 'is-selected',

        buttonGroup: 'fc-bulma-button-group', // Can't use the bulma one cause it uses more divs
        button: 'button is-primary',
        buttonActive: 'is-active',

        popover: 'card card-primary',
        popoverHeader: 'card-header',
        popoverContent: 'card-content',
        // day grid
        headerRow: 'table is-bordered',
        // list view
        listView: 'card card-primary'
    };
    BulmaCalendarTheme.prototype.baseIconClass = 'fas';
    BulmaCalendarTheme.prototype.iconClasses = {
        close: 'fa-times',
        prev: 'fa-chevron-left',
        next: 'fa-chevron-right',
        prevYear: 'fa-angle-double-left',
        nextYear: 'fa-angle-double-right'
    };
    BulmaCalendarTheme.prototype.rtlIconClasses = {
        prev: 'fa-chevron-right',
        next: 'fa-chevron-left',
        prevYear: 'fa-angle-double-right',
        nextYear: 'fa-angle-double-left'
    }
    BulmaCalendarTheme.prototype.iconOverrideOption = 'bulmaFontAwesome';
    BulmaCalendarTheme.prototype.iconOverrideCustomButtonOption = 'bulmaFontAwesome';
    BulmaCalendarTheme.prototype.iconOverridePrefix = 'fa-';
    var main = core.createPlugin({
        themeClasses: {
            bulma: BulmaCalendarTheme
        }
    });

    exports.BulmaCalendarTheme = BulmaCalendarTheme;
    exports.default = main;

    Object.defineProperty(exports, '__esModule', { value: true });

}));