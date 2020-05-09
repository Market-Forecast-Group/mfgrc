/*jslint bitwise: true */
/*global angular, $ */
(function () {
    'use strict';

    var mod = angular.module('mfgApp.menu', []);

    mod.controller('MenuCtrl', ['$scope', '$_chart', function ($scope, $_chart) {
        /*
        
        This is a task layout:
            {
                name: 'Request Historical Data',
                prog: 34
            }
        The "prog" is the percent progress.
        
        */
        $scope.chartTasks = [];

        $scope.tasks = function () {
            return $scope.chartTasks;
        };

        $_chart.tasksChangedHandler = function (tasks) {
            $scope.$apply(function () {
                var menu, expanded, firstTasks;

                firstTasks = $scope.chartTasks.length === 0;
                
                $scope.chartTasks = tasks;

                menu = $('#tasks-menu');
                expanded = menu.attr('aria-expanded') === 'true';

                if (tasks.length === 0) {
                    if (expanded) {
                        menu.dropdown('toggle');
                    }
                } else {
                    if (!expanded && firstTasks) {
                        menu.dropdown('toggle');
                    }
                }
            });
        };
    }]);
}());