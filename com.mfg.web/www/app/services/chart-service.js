/*global angular, $, console, WebSocket, MfgChart, ChartModel, DataView, MFGDataView, ChartData */
/*jslint bitwise:true*/
(function() {
	'use strict';
	/**
	 * A service to deal with the chart stuff.
	 */
	var ChartService = function($location, $_mfg, $_ui, $rootScope) {
		var self = this;

		// fields

		this.currentId = null;
		this.wsHistData = null;
		this.wsRtData = null;

		// handlers

		this.showConfigInChartHandler = null;
		this.dataChangedHandler = null;
		this.tasksChangedHandler = null;

		// set this function to the global scope
		$rootScope.initChart = function() {
			self.initChart();
		};

		this.connect = function() {
			// the socket of the historical data
			this.wsHistData = new WebSocket('ws://' + window.location.host + '/hist-data');
			this.wsHistData.binaryType = 'arraybuffer';
			this.wsHistData.onmessage = function(msg) {
				// for now the chart data is coded in JSON
				var data, jsonData;
				data = msg.data;
				if (typeof (data) === 'string') {
					jsonData = JSON.parse(msg.data);
					switch (jsonData.type) {
					case 'data-desc':
						self.dataDescriptionReceived(jsonData);
						break;
					case 'progress':
						self.historicalDataProgressReceived(jsonData);
						break;

					default:
						break;
					}
				} else {
					// databuffer received
					self.historicalDataReceived(data);
				}
			};

			this.wsRtData = new WebSocket('ws://' + window.location.host + '/rt-data');
			this.wsRtData.binaryType = 'arraybuffer';
			this.wsRtData.onmessage = function(msg) {
				var data;
				data = msg.data;
				self.realTimeDataReceived(data);
			};
		};

		this.isClosed = function() {
			return this.wsHistData === undefined || this.wsHistData.readyState === WebSocket.CLOSED;
		};

		this.isOpen = function() {
			return this.wsHistData !== undefined && this.wsHistData.readyState === WebSocket.OPEN;
		};

		/**
		 * Send a message to the server to start streaming the real-time data of
		 * the given configuration. In the "filter" argument we expect an object
		 * with the information needed to filter the data. This is the structure
		 * of that object: - layer The data layer to request (0, 1, 2) -
		 * indLevel The indicator scale to request (2,...)
		 * 
		 * The chart service then is reported with the requested data with a
		 * $_chart.rtDataReceived(data) call.
		 * 
		 * @param configId
		 *            The id of the configuration to show.
		 * @param filter
		 *            The data filter information.
		 */
		this.requestData = function(configId) {
			var msg, strMsg;

			if (this.isOpen()) {
				msg = {
					'cmd' : 'data',
					'id' : configId
				};
				strMsg = JSON.stringify(msg);
				this.wsHistData.send(strMsg);
				this.wsRtData.send(strMsg);
			}
		};

		this.dataDescriptionReceived = function(desc) {
			this.resetChart(desc);
			this.dataChangedHandler();
			this.progCount = 0;
			this.tasksChangedHandler([ {
				name : 'Receiving...',
				prog : 0
			} ]);
		};

		/**
		 * This method is called when new historical data arrives. The
		 * historical data comes in the form of a DataView.
		 * 
		 * @param dataview
		 *            The DataView of the buffer sent from the server.
		 */
		this.historicalDataReceived = function(buffer) {
			// the node is not running, then ignore the received data
			if (!this.node.running) {
				return;
			}

			var beforeHasData = this.historicalData.hasData();

			this.historicalData.readBuffer(buffer);

			// this is used to init anything in the chart that depends on set
			// the first data to the chart
			if (!beforeHasData) {
				this.dataChangedHandler();
			}
		};

		this.historicalDataProgressReceived = function(msg) {
			var p = msg.prog;
			console.log('progress ' + p);
			if (p === 100) {
				this.allHistoricalDataReceived();
			} else {
				this.tasksChangedHandler([ {
					name : 'Receiving ' + name,
					prog : p
				} ]);
			}
		};

		/**
		 * Called when the historical data was sent complete.
		 */
		this.allHistoricalDataReceived = function() {
			this.tasksChangedHandler([]);

			var temp = this.historicalData;
			this.realTimeData.prepend(temp);

			// reset non-used historical data to release some memory
			this.historicalData.reset();

			this.chart.zoomOutAll();
			this.chart.update();
		};

		/**
		 * Called by the $_mfg service when the chart message arrive.
		 */
		this.realTimeDataReceived = function(buffer) {
			// the node is not running, then ignore the received data
			if (!this.node.running) {
				return;
			}

			this.realTimeData.readBuffer(buffer);
			// this.chart.animate();
			this.chart.update();
		};

		/**
		 * We assume the chart was open for a running configuration.
		 * 
		 * @param id
		 *            The id of the running configuration.
		 */
		this.showChart = function(node) {
			var id = node.id;

			// clear the chart if it is a different configuration or it is not
			// running
			this.resetChart();
			this.showConfigInChartHandler(node);
			this.dataChangedHandler();

			this.currentId = id;

			// for certain reason we should give some time before to update
			// the chart size, maybe the problem is that yet the DOM is not
			// complete updated after the $_ui.showPanel() call.
			setTimeout(function() {
				self.chart.updateSize();
				self.chart.update();
			}, 1);

			$_ui.showPane('chart');

			this.node = node;
		};

		this.clearChart = function(node) {
			this.node = node;
			this.resetChart();
			this.showConfigInChartHandler(this.node);
			this.dataChangedHandler();
			this.chart.update();
		};

		this.initChart = function() {
			var canvas, model;

			this.historicalData = new ChartData();
			this.realTimeData = new ChartData();

			canvas = document.getElementById('chart1');
			model = new ChartModel(this.realTimeData);
			// model = new ChartModel(this.historicalData);

			this.chart = new MfgChart(canvas, model);
			// TODO: just for now, it should be taken from the server
			this.chart.lineChart.yTick = 25;
			this.chart.lineChart.xTick = 1;
			// --
			this.chart.update();
			this.resizeChart();

			// register window's listener to resize the chart
			$(window).resize(function() {
				self.resizeChart();
			});
		};

		this.resetChart = function(desc) {
			this.realTimeData.reset(desc);
			this.historicalData.reset(desc);
			this.chart.model.reset(desc);
		};

		this.hasData = function() {
			return this.chart.model.hasData();
		};

		this.resizeChart = function() {
			this.chart.updateSize();
		};

		this.connect();
	};

	window.ChartService = ChartService;
}());