/*jslint vars: true, devel: true bitwise: true */
/*global angular */
(function() {
	'use strict';

	var mod = angular.module('mfgApp.symbols', [ 'ngRoute' ]).config([ '$routeProvider', function($routeProvider) {
		// TODO: do not configure routing for now
		/*
		 * $routeProvider.when('/symbols', { templateUrl:
		 * 'symbols/symbols.html', controller: 'SymbolsCtrl' });
		 */
	} ]);

	var updateScopeWithSymbolListData = function($scope, $_mfg, $_chart, data) {
		var symbols = [];

		if (data.error) {
			alert(data.error);
		} else {
			data.continuousContracts.forEach(function(symbol) {
				var symbolNode = {
					displayName : symbol.name,
					children : []
				};
				// add the symbol node, it will be used only to display
				// no actions will be attached to it
				symbols.push(symbolNode);

				// first kid is the continuous contract
				symbolNode.children.push({
					id : symbol.id,
					displayName : 'Continuous Contract',
					running : symbol.running,
					enabled : symbol.enabled,
					children : []
				});

				// then add the inputs
				symbol.inputs.forEach(function(input) {
					symbolNode.children.push({
						id : input.id,
						displayName : input.name,
						running : input.running,
						enabled : input.enabled
					});
					input.tradings.forEach(function(trading) {
						// and add the tradings
						symbolNode.children.push({
							id : trading.id,
							displayName : input.name + ' / ' + trading.name,
							running : trading.running,
							enabled : trading.enabled
						});
					});
				});
			});
			$scope.symbols = symbols;

			symbols.forEach(function(symbol) {
				symbol.children.forEach(function(node) {
					if (!node.running && $_chart.currentId === node.id) {
						// clear the chart but also assign the new node info.
						$_chart.clearChart(node);
						return;
					}
				});
			});
		}
	};

	/**
	 * When a configuration is played/stopped, we put a Waiting icon until we
	 * get a response from the server.
	 */
	var updateScopeWithWaitingStatus = function($scope, waitingForId) {
		// put the parent symbol as waiting
		$scope.symbols.forEach(function(symbol) {
			symbol.children.forEach(function(node) {
				if (node.id === waitingForId) {
					symbol.waiting = true;
					return;
				}
			});
		});

		// put as waiting all the children of waiting symbols
		$scope.symbols.forEach(function(symbol) {
			if (symbol.waiting) {
				symbol.children.forEach(function(node) {
					node.waiting = true;
				});
			}
		});
	};

	/**
	 * Put the disconnection status on all symbols. This just put the 'waiting'
	 * field to true.
	 */
	var updateScopWithDisconnectionStatus = function($scope) {
		$scope.symbols.forEach(function(symbol) {
			symbol.children.forEach(function(node) {
				node.waiting = true;
			});
		});
	};

	mod.controller('SymbolsCtrl', [ '$scope', '$_mfg', '$_chart', function($scope, $_mfg, $_chart) {
		// init
		$scope.symbols = [];

		// dom events
		$scope.playNode = function(id) {
			updateScopeWithWaitingStatus($scope, id);
			$_mfg.playConfig(id);
		};

		$scope.stopNode = function(id) {
			updateScopeWithWaitingStatus($scope, id);
			$_mfg.stopConfig(id);
		};

		$scope.showChart = function(node) {
			$_chart.showChart(node);
		};

		// listen to server symbols
		$_mfg.resetAndAddSymbolStatusChangedListener(function(data) {
			console.log('symbols changed on server, apply changes');
			$scope.$apply(function() {
				updateScopeWithSymbolListData($scope, $_mfg, $_chart, data);
			});
		});

		// listen to server connection
		$_mfg.resetAndAddConnectionStatusChangedListener(function(connected) {
			if (connected) {
				console.log('server connected, request symbol list');
				$_mfg.requestSymbolsList();
			} else {
				console.log('server disconnected, update symbol list status icons');
				$scope.$apply(function() {
					updateScopWithDisconnectionStatus($scope);
				});
			}
		});

		// request the symbol list
		$_mfg.requestSymbolsList();
	} ]);
}());