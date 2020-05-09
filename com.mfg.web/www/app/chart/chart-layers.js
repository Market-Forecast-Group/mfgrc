/*globals LineChart*/
'use strict';

var SCALES_COLOR = [ 'rgb(0,255, 255)', 'rgb(0,255, 255)', 'rgb(255,198, 0)', 'rgb(0,255, 0)', 'rgb(0,0, 255)', 'rgb(255,0, 0)', 'rgb(160,71, 163)',
		'rgb(0,255, 255)', 'rgb(255,198, 0)', 'rgb(0,255, 0)', 'rgb(0,0, 255)', 'rgb(255,0, 0)', 'rgb(160,71, 163)', 'rgb(0,255, 255)', 'rgb(255,198, 0)',
		'rgb(0,255, 0)', 'rgb(0,0, 255)', 'rgb(255,0, 0)', 'rgb(160,71, 163)', 'rgb(0,255, 0)', 'rgb(0,0, 255)', 'rgb(255,0, 0)', 'rgb(160,71, 163)',
		'rgb(0,255,255)', 'rgb(255,198, 0)', 'rgb(0,255, 0)', 'rgb(0,0, 255)', 'rgb(255,0, 0)', 'rgb(160,71, 163)', 'rgb(0,255, 255)', 'rgb(255,198, 0)' ];

/**
 * The Price layer, like in MFG desktop chart.
 */
function PriceLayer(chart) {
	this.chart = chart;
	this.lineChart = chart.lineChart;
	this.dsPrices = new LineChart.CompressedDataset(LineChart.EMPTY_DATASET, 1000);
	this.chart.lineChart.addDataset(this.dsPrices, new LineChart.LinePainter());

	this.update = function() {
		var ds;
		ds = this.chart.model.getPricesDataset(this.lineChart.xLower, this.lineChart.xUpper);
		this.dsPrices.ds = ds;
	};
}

function ZZLayer(chart) {
	this.chart = chart;
	this.lineChart = chart.lineChart;

	/**
	 * This is the ZZ painter. It takes from the dataset, as series, the scale,
	 * and as items, the pivots.
	 */
	function ZZPainter() {
		this.paint = function(conv, ctx, ds) {
			var i, j, x, y, len;

			ctx.save();

			// for each scale
			for (i = 0; i < ds.getSeriesCount(); i += 1) {
				ctx.strokeStyle = SCALES_COLOR[i];
				ctx.beginPath();
				len = ds.getItemsCount(i);
				// for each pivot
				for (j = 0; j < len; j += 1) {
					x = ds.getItemX(i, j);
					y = ds.getItemY(i, j);
					x = conv.getScreenX(x);
					y = conv.getScreenY(y);

					if (j === 0) {
						ctx.moveTo(x, y);
					} else {
						ctx.lineTo(x, y);
					}
				}
				ctx.stroke();
			}

			ctx.restore();
		};
	}

	this.init = function() {
		this.dsPivots = new LineChart.CompressedDataset(LineChart.EMPTY_DATASET, 1000);
		this.chart.lineChart.addDataset(this.dsPivots, new ZZPainter());
	};

	this.update = function() {
		var ds;
		ds = this.chart.model.getPivotsDataset(this.lineChart.xLower, this.lineChart.xUpper);
		this.dsPivots.ds = ds;
	};

	this.init();
}

function BandLayer(chart) {
	this.chart = chart;
	this.lineChart = chart.lineChart;

	/**
	 * This is the bands painter. It takes from the dataset, as series, the
	 * scale, and as items, the bands.
	 */
	function BandPainter() {
		var YKEYS = [ 'topPrice', 'centerPrice', 'bottomPrice' ];
		this.paint = function(conv, ctx, ds) {
			var i, j, k, x, y, len, band;

			ctx.save();

			// for each scale
			for (i = 0; i < ds.getSeriesCount(); i += 1) {
				for (k = 0; k < 3; k += 1) {
					ctx.strokeStyle = SCALES_COLOR[i];
					ctx.beginPath();
					len = ds.getItemsCount(i);
					// for each band
					for (j = 0; j < len; j += 1) {
						// we assume we can get the band item from the
						// dataset
						band = ds.getItem(i, j);
						x = band.time;
						y = band[YKEYS[k]];
						x = conv.getScreenX(x);
						y = conv.getScreenY(y);

						if (j === 0) {
							ctx.moveTo(x, y);
						} else {
							ctx.lineTo(x, y);
						}
					}
					ctx.stroke();
				}
			}

			ctx.restore();
		};
	}

	this.init = function() {
		this.dsBands = new LineChart.CompressedDataset(LineChart.EMPTY_DATASET, 1000);
		// we add a getItem() method to the compressed dataset.
		this.dsBands.getItem = function(series, item) {
			return this.ds.getItem(series, this.getItemIndex(series, item));
		};
		this.chart.lineChart.addDataset(this.dsBands, new BandPainter());
	};

	this.update = function() {
		var ds;
		ds = this.chart.model.getBandsDataset(this.lineChart.xLower, this.lineChart.xUpper);
		this.dsBands.ds = ds;
	};

	this.init();
}