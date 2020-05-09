'use strict';

/**
 * Dataset for prices. The X values are the index and the Y values are the abs
 * of the "priceRaw".
 */
function PriceDataset(ds) {
	this.ds = ds;

	this.getSeriesCount = function() {
		return 1;
	};

	this.getItemsCount = function(series) {
		return this.ds.getItemsCount(series);
	};

	this.getItemX = function(series, item) {
		return this.ds.getItemX(series, item);
	};

	this.getItemY = function(series, item) {
		return Math.abs(this.ds.getItemY(series, item));
	};
}

/**
 * This dataset is compound of many sub-datasets, each series is a sub-dataset
 * that represents a scale.
 */
function IndicatorDataset(scalesDatasets) {
	this.scalesDatasets = scalesDatasets;

	this.getSeriesCount = function() {
		return this.scalesDatasets.length;
	};

	this.getItemsCount = function(series) {
		return this.scalesDatasets[series].getItemsCount(0);
	};

	this.getItemX = function(series, item) {
		return this.scalesDatasets[series].getItemX(series, item);
	};

	this.getItemY = function(series, item) {
		return this.scalesDatasets[series].getItemY(series, item);
	};

	this.getItem = function(series, item) {
		return this.scalesDatasets[series].getItem(series, item);
	};
}

/**
 * This chart model is different of the one we use in MFG desktop, this is not
 * only an abstraction of the real data, else here we build the data. When a
 * price, an indicator, or any other chart related data comes from the server,
 * it is kept in memory by this model.
 * 
 * Also, given a range, this model builds the chart datasets.
 */
function ChartModel(data) {

	/**
	 * The current data layer to show
	 */
	this.dataLayer = 0;

	/*
	 * The minimum scale to show. The chart shows all scales from "displayScale"
	 * to "displayScale + 3".
	 */
	this.displayScale = 2;

	this.data = data || new ChartData();

	// ////////////////////////////
	// PRICES
	// ////////////////////////////

	this.hasData = function() {
		return this.data.hasData();
	};

	this.hasIndicatorData = function() {
		return this.data.hasIndicatorData();
	};

	/**
	 * Get the prices from "lowerTime" to "upperTime".
	 */
	this.getPricesDataset = function(lowerTime, upperTime) {
		if (!this.data.hasData()) {
			return LineChart.EMPTY_DATASET;
		}
		return new PriceDataset(this.getDataset(
				this.data.pricesMap[this.dataLayer], 'time', 'priceRaw',
				lowerTime, upperTime));
	};

	this.getPricesLowerTime = function() {
		var prices = this.data.pricesMap[this.dataLayer];

		if (prices.length === 0) {
			return 0;
		}

		return prices[0].time;
	};

	this.getPricesUpperTime = function() {
		var prices = this.data.pricesMap[this.dataLayer];

		if (prices.length === 0) {
			return 0;
		}

		return prices[prices.length - 1].time;
	};

	this.getPricesCount = function() {
		if (this.data.pricesMap.length === 0) {
			return LineChart.EMPTY_DATASET;
		}

		var prices = this.data.pricesMap[this.dataLayer];
		return prices.length;
	};

	// ////////////////////////////
	// PIVOTS
	// ////////////////////////////

	this.getNumberOfScales = function() {
		return this.data.numberOfScales;
	};

	this.getPivotsDataset = function(lowerTime, upperTime) {
		return this.getIndicatorDataset(this.data.pivotsMap, 'pivotTime',
				'pivotPrice', lowerTime, upperTime);
	};

	// ////////////////////////////
	// BANDS
	// ////////////////////////////

	this.getBandsDataset = function(lowerTime, upperTime) {
		var ds = this.getIndicatorDataset(this.data.bandsMap, 'time',
				'centerPrice', lowerTime, upperTime);
		return ds;
	};

	// ////////////////////////////
	// UTILS
	// ////////////////////////////

	this.getIndicatorDataset = function(map, xkey, ykey, lowerTime, upperTime) {
		if (map.length === 0) {
			return LineChart.EMPTY_DATASET;
		}

		var scales, scaleData, i, ds, dsList, minscale;

		dsList = [];

		scales = map[this.dataLayer];
		minscale = Math.min(this.displayScale, this.data.numberOfScales - 2);

		for (i = 0; i < scales.length; i += 1) {
			scaleData = scales[i];
			if (scaleData.length === 0 || i < minscale
					|| i >= this.displayScale + 3) {
				ds = LineChart.EMPTY_DATASET;
			} else {
				ds = this.getDataset(scaleData, xkey, ykey, lowerTime,
						upperTime);
			}
			dsList.push(ds);
		}
		return new IndicatorDataset(dsList);
	};

	this.getDataset = function(array, xkey, ykey, lowerKey, upperKey) {
		if (array.length === 0) {
			return LineChart.EMPTY_DATASET;
		}
		var start, stop;
		start = this.indexOf(array, xkey, lowerKey);
		stop = this.indexOf(array, xkey, upperKey);
		return new LineChart.ArrayRangeDataset(array, xkey, ykey, start, stop
				- start + 2);
	};

	this.indexOf = function(array, key, keyVal) {
		var low, high, mid, midVal;
		low = 0;
		high = array.length - 1;

		while (low <= high) {
			mid = ((low + high) / 2) | 0;
			midVal = array[mid][key];

			if (midVal < keyVal) {
				low = mid + 1;
			} else if (midVal > keyVal) {
				high = mid - 1;
			} else {
				return mid; // key found
			}
		}
		return low === 0 ? 0 : low - 1; // key not found.
	};

	/**
	 * Reset the data of this model with the given data description. If the
	 * description is not provieded then this assumes the chart is empty.
	 * 
	 * @param A
	 *            description of the data (optional) - layers - scales
	 */
	this.reset = function(desc) {
		this.dataLayer = 0;

		if (desc) {
			this.displayScale = desc.scales - 2;
		} else {
			this.displayScale = 2;
		}

		this.data.reset(desc);
	};
}
