/*jslint bitwise: true */
/*global angular */
(function() {
	'use strict';

	var mod = angular.module('mfgApp.chart', []);

	mod.controller('ChartCtrl', [ '$scope', '$_chart', '$timeout', function($scope, $_chart, $timeout) {
		$scope.configName = null;

		$_chart.showConfigInChartHandler = function(node) {
			// use timeout to avoid conflicts with the ng realm
			$timeout(function() {
				$scope.node = node;
				$scope.showChartCanvas = node && node.running;
			}, 0, true);
		};
	} ]);

	mod.controller('ChartPanelCtrl', [ '$scope', '$_chart', '$timeout', function($scope, $_chart, $timeout) {
		$scope.scaleRange = {
			start : 2,
			stop : 4
		};

		$scope.hasData = false;
		$scope.hasIndicatorData = false;
		$scope.node = false;
		$scope.dataLayer = 0;

		$scope.scrollStart = function() {
			console.log('scroll start');
			$_chart.chart.scrollStart();
			console.log("lower = " + $_chart.chart.lineChart.xLower);
			$_chart.chart.update();
		};

		$scope.scrollEnd = function() {
			$_chart.chart.scrollEnd();
			$_chart.chart.update();
		};

		$scope.swapLayer = function(dataLayer) {
			$_chart.chart.swapDataLayer(dataLayer);
			$scope.dataLayer = dataLayer;
			console.log('dataLayer ' + $scope.dataLayer);
		};

		$scope.changeScale = function(dir) {
			$scope.scaleRange = $_chart.chart.changeScale(dir);
		};

		$scope.requestData = function() {
			$_chart.requestData($scope.node.id);
		};

		$_chart.dataChangedHandler = function() {
			// use timeout to avoid conflicts with the ng realm
			$timeout(function() {
				var scale = $_chart.chart.model.displayScale;
				$scope.node = $_chart.node;
				$scope.hasData = $_chart.hasData();
				$scope.hasIndicatorData = $_chart.chart.model.hasIndicatorData();
				$scope.scaleRange = {
					start : scale,
					stop : scale + 2
				};
			}, 0, true);
		};
	} ]);
}());