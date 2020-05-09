/*jslint vars: true, devel: true bitwise: true */
/*global angular, WebSocket */
(function () {
    'use strict';

    // Declare app level module which depends on views, and components
    var mod;

    mod = angular.module('mfgApp', [
        'ngRoute',
        'ngResource',
        'ngWebsocket',
        'mfgApp.services',
        'mfgApp.symbols',
        'mfgApp.chart',
        'mfgApp.menu',
        'mfgApp.version'
    ]);

    mod.config(['$routeProvider', '$resourceProvider', function ($routeProvider, $resourceProvider) {
        //TODO: maybe not!!!
        $routeProvider.otherwise({
            redirectTo: '/symbols'
        });

        $resourceProvider.defaults.stripTrailingSlashes = false;
    }]);


    mod.run(['$_ui', function ($_ui) {
        $_ui.start();
    }]);

}());