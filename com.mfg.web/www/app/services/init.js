/*jslint vars: true, devel: true bitwise: true */
/*global angular, MfgService, ChartService, UIService */
(function () {
    'use strict';
    var services = angular.module('mfgApp.services', []);

    services.factory('$_mfg', function () {
        return new MfgService();
    });

    services.factory('$_chart', ['$location', '$_mfg', '$_ui', '$rootScope', function ($location, $_mfg, $_ui, $rootScope) {
        return new ChartService($location, $_mfg, $_ui, $rootScope);
    }]);

    services.factory('$_ui', function () {
        return new UIService();
    });

}());