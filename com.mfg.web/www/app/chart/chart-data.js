/*globals DataView, MFGDataView, console */
function ChartData() {
	'use strict';

	/**
	 * The number of scales.
	 */
	this.numberOfScales = 0;

	/**
	 * The array of prices. A price is an object with the keys: - physicalTime -
	 * priceRaw - volume
	 */
	this.pricesMap = [];

	/**
	 * The pivots container. It is a map like [dataLayer][scale] = [list of
	 * pivots]. The pivot is formed by the fields: - pivotPrice - pivotTime -
	 * confirmPrice - confirmTime - timeInterval - isUp - pivotPhysicalTime -
	 * confirmPhysicalTime
	 */
	this.pivotsMap = [];

	/**
	 * The bands map, like [dataLayer][scale] = [list of bands]. A band object
	 * has the fields: - time - topPrice - centerPrice - bottomPrice -
	 * physicalTime - topRaw - centerRaw - bottomRaw
	 */
	this.bandsMap = [];

	// ////////////////////////////
	// PRICES
	// ////////////////////////////

	this.hasData = function() {
		return this.pricesMap.length > 0;
	};

	this.hasIndicatorData = function() {
		return this.numberOfScales > 0;
	};

	this.addPrice = function(price, dataLayer) {
		var prices;
		prices = this.pricesMap[dataLayer];
		prices.push(price);
	};

	this.addPivot = function(pivot, dataLayer, scale) {
		var arr = this.pivotsMap[dataLayer][scale];
		arr.push(pivot);
	};

	this.addBand = function(band, dataLayer, scale) {
		var arr = this.bandsMap[dataLayer][scale];
		arr.push(band);
	};

	// ////////////////////////////
	// UTILS
	// ////////////////////////////

	this.readBuffer = function(buffer) {
		var dataview, i, len, rec, type, dataLayer, scale, name;

		dataview = new DataView(buffer);

		// the first byte refers to the chart object, like price, pivot, etc...
		// the second byte refers to the data layer
		// the third byte refers, in case of indicator objects, to the scale
		type = dataview.getInt8(0);
		dataLayer = dataview.getInt8(1);
		scale = dataview.getInt8(2);
		// the data start after at byte 3
		dataview = new DataView(buffer, 3);
		rec = null;
		switch (type) {
		case 0:
			// prices received
			len = MFGDataView.getPriceRecordCount(dataview.byteLength);
			for (i = 0; i < len; i += 1) {
				rec = MFGDataView.getPriceRecord(dataview, i);
				this.addPrice(rec, dataLayer);
			}
			name = 'prices ' + dataLayer;
			break;
		case 1:
			// pivots received
			len = MFGDataView.getPivotRecordCount(dataview.byteLength);
			for (i = 0; i < len; i += 1) {
				rec = MFGDataView.getPivotRecord(dataview, i);
				this.addPivot(rec, dataLayer, scale);
			}
			name = 'pivots ' + dataLayer + '-' + scale;
			break;
		case 2:
			// bands received
			len = MFGDataView.getBandsRecordCount(dataview.byteLength);
			for (i = 0; i < len; i += 1) {
				rec = MFGDataView.getBandsRecord(dataview, i);
				this.addBand(rec, dataLayer, scale);
			}
			name = 'bands ' + dataLayer + '-' + scale;
			break;
		default:
			name = '<unknown type>';
			break;
		}
		// console.log('received ' + name);
		return name;
	};

	/**
	 * Prepend the given chart data.
	 * 
	 * @param data
	 *            The data to prepend.
	 */
	this.prepend = function(data) {
		var i, j;
		// prepend prices
		for (i = 0; i < this.pricesMap.length; i += 1) {
			this.pricesMap[i] = data.pricesMap[i].concat(this.pricesMap[i]);
			for (j = 0; j < this.numberOfScales; j += 1) {
				// prepend pivots
				this.pivotsMap[i][j] = data.pivotsMap[i][j].concat(this.pivotsMap[i][j]);
				// prepend bands
				this.bandsMap[i][j] = data.bandsMap[i][j].concat(this.bandsMap[i][j]);
			}
		}
	};

	/**
	 * Reset the data of this model with the given data description. If the
	 * description is not provieded then this assumes the chart is empty.
	 * 
	 * @param A
	 *            description of the data (optional) - layers - scales
	 */
	this.reset = function(desc) {
		var i, j, pivots, bands;

		this.numberOfScales = 0;
		this.pricesMap = [];
		this.pivotsMap = [];
		this.bandsMap = [];

		if (desc) {
			this.numberOfScales = desc.scales;

			for (i = 0; i < desc.layers; i += 1) {
				this.pricesMap.push([]);
				pivots = [];
				bands = [];
				for (j = 0; j <= desc.scales; j += 1) {
					pivots.push([]);
					bands.push([]);
				}
				this.pivotsMap.push(pivots);
				this.bandsMap.push(bands);
			}
		}
	};
}