/*jslint bitwise:true*/
/*globals LineChart, ChartModel, PriceLayer, ZZLayer, BandLayer, $, console*/
'use strict';

var CHART_ANIMATION_NONE = 0;
var CHART_ANIMATION_ZOOM_OUT = 1;
var CHART_ANIMATION_SCROLL_END = 2;

function MfgChart(element, aModel) {
	this.lineChart = new LineChart.Chart(element);
	this.model = aModel || new ChartModel();
	this.priceLayer = null;
	this.zzLayer = null;
	this.bandLayer = null;
	this.hammer = null;
	this.animation = CHART_ANIMATION_ZOOM_OUT;

	var self = this;

	this.update = function() {
		try {
			this.updateData();
			this.autorange();
			this.render();
		} catch (e) {
			alert(e);
		}
	};

	this.animate = function() {
		switch (this.animation) {
		case CHART_ANIMATION_SCROLL_END:
			this.scrollEnd();
			break;
		case CHART_ANIMATION_ZOOM_OUT:
			this.zoomOutAll();
			break;
		}
	};

	this.updateData = function() {
		this.priceLayer.update();
		this.zzLayer.update();
		this.bandLayer.update();
	};

	this.render = function() {
		if ($(this.canvas()).is(':visible')) {
			this.lineChart.render();
		}
	};

	this.canvas = function() {
		return this.lineChart.canvas;
	};

	this.updateSize = function() {
		var canvas, h;
		canvas = this.canvas();
		// h = ($(window).height() - $(canvas).offset().top - 20);
		// take the whole window height, in this way we can scroll and see
		// wider chart.
		h = $(window).height();
		canvas.style.height = h + 'px';
		canvas.height = h;
		canvas.width = $(canvas).width();
		this.render();
	};

	this.zoomOutAll = function() {
		// zoom out all the prices
		var count = this.model.getPricesCount();
		if (count >= 0) {
			var min = this.model.getPricesLowerTime();
			var max = this.model.getPricesUpperTime();
			var len = max - min;
			if (len === 0) {
				len = this.lineChart.xTick * 10;
				min -= len;
				max += len;
			}
			var blank = 0.1 * len;
			min -= blank;
			max += blank;
			this.lineChart.setXRange(min, max);
		}
	};

	this.scrollStart = function(e) {
		var count = this.model.getPricesCount();
		if (count > 0) {
			var len = this.lineChart.getXLength();
			if (len < 3) {
				len = 3;
			}
			var first = this.model.getPricesLowerTime();
			var blank = 0.1 * len;
			var lower = first - blank;
			var upper = first + len;
			this.lineChart.setXRange(lower, upper);
		}
	};
	
	this.scrollEnd = function(e) {
		if (this.panStartX !== undefined) {
			// we do not want to scroll end when the chart is scrolling manually
			return;
		}

		var count = this.model.getPricesCount();
		if (count > 0) {
			var len = this.lineChart.getXLength();
			if (len < 3) {
				len = 3;
			}
			var last = this.model.getPricesUpperTime();
			var blank = 0.1 * len;
			var upper = last + blank;
			var lower = upper - len;
			this.lineChart.setXRange(lower, upper);
		}
	};

	this.autorange = function() {
		this.autorangeWithDataset(this.priceLayer.dsPrices);
	};

	this.autorangeWithDataset = function(ds) {
		var i, y, min, max, len, count;

		min = Number.MAX_VALUE;
		max = Number.MIN_VALUE;
		count = ds.getItemsCount(0);

		for (i = 0; i < count; i += 1) {
			y = ds.getItemY(0, i);
			min = Math.min(min, y);
			max = Math.max(max, y);
		}

		if (min === Number.MAX_VALUE) {
			min = max = 0;
		}

		len = max - min;

		if (len === 0) {
			len = this.lineChart.yTick * 10;
			min -= len;
			max += len;
		}
		len *= 0.1;
		min -= len;
		max += len;
		this.lineChart.setYRange(min, max);
	};

	this.swapDataLayer = function(dataLayer) {
		this.model.dataLayer = dataLayer;
		this.zoomOutAll();
		this.update();
	};

	this.changeScale = function(dir) {
		var scale;
		scale = this.model.displayScale + dir;
		scale = Math.min(this.model.getNumberOfScales() - 2, Math.max(2, scale));
		this.model.displayScale = scale;

		this.update();

		return {
			start : scale,
			stop : scale + 2
		};
	};

	this.init = function() {
		this.priceLayer = new PriceLayer(this);
		this.zzLayer = new ZZLayer(this);
		this.bandLayer = new BandLayer(this);

		this.hammer = new Hammer(element);

		this.hammer.get('pinch').set({
			enable : true
		});

		this.hammer.on('pinch', function(e) {
			self.pinch(e);
		});

		this.hammer.on('pinchstart', function(e) {
			self.pinchStart(e);
		});

		this.hammer.on('panstart', function(e) {
			self.panStart(e);
		});

		this.hammer.on('panend', function(e) {
			self.panEnd(e);
		});

		this.hammer.on('pan', function(e) {
			self.pan(e);
		});

		$(window).keyup(function(e) {
			var I = 73;
			var O = 79;
			var k = e.which;
			var scale;
			if (k === I) {
				scale = 2;
			} else if (k === O) {
				scale = 0.5;
			} else {
				return;
			}
			self.pinchStart();
			self.pinch({
				scale : scale
			});
		});

		this.zoomOutAll();
		this.autorange();
	};

	this.panStart = function(e) {
		this.panStartX = e.center.x;
		this.panStartXMiddle = this.lineChart.getXMiddle();
	};

	this.panEnd = function(e) {
		delete this.panStartX;
		delete this.panStartXMiddle;
	};

	this.pan = function(e) {
		// this happens from time to time, probably because weird combination of
		// gestures so the "panstart" event is not fired, or the "x" of that
		// event
		// is not defined.
		if (this.panStartX === undefined) {
			return;
		}

		var x = e.center.x;
		var factor = (this.panStartX - x) / this.lineChart.width();
		var xlen = this.lineChart.getXLength();
		var blank = 0.1 * xlen;
		var offset = factor * xlen;
		var lower = this.panStartXMiddle - xlen / 2 + offset;
		var upper = lower + xlen;

		if (factor < 0) {
			var min = this.model.getPricesLowerTime();
			min -= blank;
			if (lower < min) {
				lower = min;
				upper = lower + xlen;
			}
		} else {
			var max = this.model.getPricesUpperTime();
			max += blank;
			if (upper > max) {
				upper = max;
				lower = upper - xlen;
			}
		}

		this.lineChart.setXRange(lower, upper);

		this.update();
	};

	this.pinchStart = function() {
		this.pinchStartLength = this.lineChart.getXLength();
	};

	/**
	 * Pinch event. This zooms the chart.
	 * 
	 * @param scale
	 *            The scale of the pinch, taken from the Hammer pinch event.
	 */
	this.pinch = function(e) {
		var scale = 1 / e.scale;
		var len = (scale * this.pinchStartLength);
		var middle = this.lineChart.getXMiddle();
		var lower = middle - len / 2;
		var upper = middle + len / 2;
		if (scale > 1) {
			var min = this.model.getPricesLowerTime();
			var max = this.model.getPricesUpperTime();
			var blank = 0.1 * this.lineChart.getXLength();

			if (min > lower + blank) {
				lower = min - blank;
			}
			if (max < upper - blank) {
				upper = max - blank;
			}
		}
		this.lineChart.setXRange(lower, upper);
		this.update();
	};

	this.init();
}
