/*jslint bitwise: true */
/*global window, console */
(function() {
	"use strict";

	/**
	 * Simple but fast chart to display line graphs.
	 */
	function Chart(aCanvas) {
		this.canvas = aCanvas;

		this.dsPainterList = [];

		this.xLower = 0;
		this.xUpper = 100;
		this.yLower = 0;
		this.yUpper = 100;
		this.xMargin = 80;
		this.yMargin = 20;
		this.xTick = 1;
		this.yTick = 1;

		this.init = function() {
			this.updateCanvasSize();
			this.ctx = this.canvas.getContext("2d");
			this.gridLinesStyle = "rgb(49, 49, 49)";
			this.gridLabelsStyle = "#fff";
		};

		this.addDataset = function(ds, painter) {
			this.dsPainterList.push([ ds, painter ]);
		};

		this.render = function() {
			var g2, w, h, i, pair, ds, painter, x, y, x1, y1, x0, y0, bestTick, screenY, screenX, str;

			g2 = this.ctx;
			w = this.width();
			h = this.height();

			// background

			g2.fillStyle = "#000";
			g2.fillRect(0, 0, w, h);

			// config global styles
			g2.globalAlpha = 1;

			// margins
			x = this.xMargin;
			y = this.height() - this.yMargin;
			g2.save();
			g2.strokeStyle = this.gridLinesStyle;
			g2.beginPath();
			g2.moveTo(x, 0);
			g2.lineTo(x, y);
			g2.lineTo(this.width(), y);
			g2.stroke();
			g2.restore();

			// grid lines

			screenY = this.getScreenYSpace(this.yTick);
			if (screenY > 50) { // 50 is the min size of a tick in the screen
				bestTick = this.yTick;
			} else {
				bestTick = (this.yTick * Math.ceil(50 / screenY)) | 0;
			}

			if (bestTick <= 0) {
				console.error("WARNING: the computed best y-tick is " + bestTick + " but we replace it by 1");
				alert("WARNING: the computed best y-tick is " + bestTick + " but we replace it by 1");
				alert("range " + this.xLower + " " + this.yUpper);
				bestTick = 1;
			}

			y0 = Math.floor(this.yLower / bestTick) * bestTick;
			x = this.xMargin;

			// horiz lines

			y = y0;
			g2.save();
			g2.lineWidth = 1;
			g2.setLineDash([ 5 ]);
			g2.beginPath();

			while (true) {
				y1 = this.getScreenY(y);

				if (y1 <= 0) {
					break;
				}
				if (y1 < this.height() - this.yMargin) {
					g2.moveTo(x, y1);
					g2.lineTo(this.width(), y1);
					g2.strokeStyle = this.gridLinesStyle;
					g2.stroke();
				}

				y += bestTick;
			}
			g2.restore();

			// horiz labels

			y = y0;
			g2.save();
			g2.lineWidth = 1;
			g2.font = "12px monospace";
			g2.beginPath();

			while (true) {
				y1 = this.getScreenY(y);
				if (y1 < 0) {
					break;
				}

				if (y1 < this.height() - this.yMargin) {
					str = String(y); // TODO: this should be replaced by a
					// formatter function

					x1 = x - 5 - g2.measureText(str).width;
					if (x1 < 0) {
						x1 = 5;
					}
					g2.strokeStyle = this.gridLabelsStyle;
					g2.strokeText(str, x1, y1);
				}
				y += bestTick;
			}
			g2.restore();

			// vert lines

			screenX = this.getScreenXSpace(this.xTick);
			if (screenX > 50) { // 50 is the min size of a tick in the screen
				bestTick = this.xTick;
			} else {
				bestTick = (this.xTick * Math.ceil(50 / screenX)) | 0;
			}

			if (bestTick <= 0) {
				console.error("WARNING: the computed best y-tick is " + bestTick + " but we replace it by 1");
				alert("WARNING: the computed best y-tick is " + bestTick + " but we replace it by 1");
				alert("range " + this.xLower + " " + this.yUpper);
				bestTick = 1;
			}

			x0 = Math.floor(this.xLower / bestTick) * bestTick;
			y = this.height() - this.yMargin;

			x = x0;
			g2.save();
			g2.lineWidth = 1;
			g2.setLineDash([ 5 ]);
			g2.beginPath();

			while (true) {
				x1 = this.getScreenX(x);

				if (x1 > this.width()) {
					break;
				}

				if (x1 > this.xMargin) {
					g2.moveTo(x1, 0);
					g2.lineTo(x1, y);
					g2.strokeStyle = this.gridLinesStyle;
					g2.stroke();
				}

				x += bestTick;
			}
			g2.restore();

			// vert labels

			x = x0;
			// TODO: get the font height from the context
			y = this.height() - this.yMargin + 15;
			g2.save();
			g2.lineWidth = 1;
			g2.font = "12px monospace";
			g2.beginPath();

			while (true) {
				x1 = this.getScreenX(x);
				if (x1 > this.width()) {
					break;
				}

				if (x1 > this.xMargin) {
					str = String(x); // TODO: this should be replaced by a
					// formatter function

					x1 -= g2.measureText(str).width / 2;
					if (x1 < 0) {
						x1 = 5;
					}
					g2.strokeStyle = this.gridLabelsStyle;
					g2.strokeText(str, x1, y);
				}
				x += bestTick;
			}
			g2.restore();

			// datasets
			g2.save();

			g2.rect(this.xMargin, 0, this.width() - this.xMargin, this.height() - this.yMargin);
			g2.clip();

			for (i = 0; i < this.dsPainterList.length; i += 1) {
				pair = this.dsPainterList[i];
				ds = pair[0];
				painter = pair[1];
				painter.paint(this, g2, ds);
			}

			g2.restore();
		};

		this.resetRange = function(xLower, xUpper, yLower, yUpper) {
			this.xLower = xLower;
			this.xUpper = xUpper;
			this.yLower = yLower;
			this.yUpper = yUpper;
		};

		this.setXRange = function(lower, upper) {
			if (lower >= upper) {
				throw "Error xlower " + lower + " , xupper " + upper; 
			}
			this.xLower = lower;
			this.xUpper = upper;
		};

		this.setYRange = function(lower, upper) {
			if (lower >= upper) {
				throw "Error ylower " + lower + " , yupper " + upper; 
			}
			this.yLower = lower;
			this.yUpper = upper;
		};

		this.getXLength = function() {
			return this.xUpper - this.xLower;
		};

		this.getYLength = function() {
			return this.yUpper - this.yLower;
		};

		this.getXMiddle = function() {
			return (this.xLower + this.xUpper) / 2;
		};

		this.getYMiddle = function() {
			return (this.yLower + this.yUpper) / 2;
		};

		this.getScreenXSpace = function(x) {
			return x / (this.xUpper - this.xLower) * (this.width() - this.xMargin);
		};

		this.getScreenX = function(x) {
			return this.xMargin + (x - this.xLower) / (this.xUpper - this.xLower) * (this.width() - this.xMargin);
		};

		this.getScreenYSpace = function(y) {
			return y / (this.yUpper - this.yLower) * (this.height() - this.yMargin);
		};

		this.getScreenY = function(y) {
			var h;
			h = this.height() - this.yMargin;
			return h - (y - this.yLower) / (this.yUpper - this.yLower) * h;
		};

		this.updateCanvasSize = function() {
			this.canvas.width = this.width();
			this.canvas.height = this.height();
		};

		this.width = function() {
			return this.canvas.clientWidth;
		};

		this.height = function() {
			return this.canvas.clientHeight;
		};

		this.init();
	}

	// ///////////////////////
	// DATASET CLASSES
	// //////////////////////

	function SimpleDataset(arr, aXkey, aYkey) {
		this.arr = arr;
		this.xkey = aXkey || "x";
		this.ykey = aYkey || "y";

		this.data = function() {
			return this.arr;
		};

		this.getSeriesCount = function() {
			return 1;
		};

		this.getItemsCount = function(series) {
			return this.data().length;
		};

		this.getItemX = function(series, item) {
			return this.data()[item][this.xkey];
		};

		this.getItemY = function(series, item) {
			return this.data()[item][this.ykey];
		};
	}

	/**
	 * A dataset to get the items of an array. Only "len" items are get starting
	 * from position "start".
	 * 
	 * @param array
	 *            The array with the data
	 * @param xkey
	 *            The key of the X value.
	 * @param ykey
	 *            The key of the Y value.
	 * @param start
	 *            The start position.
	 * @param len
	 *            The number of items.
	 */
	function ArrayRangeDataset(array, xkey, ykey, aStart, length) {
		this.array = array;
		this.xkey = xkey;
		this.ykey = ykey;
		this.start = aStart || 0;
		this.len = length || array.length;

		if (aStart + length > this.array.length) {
			this.len = this.array.length - this.start;
		}

		this.getSeriesCount = function() {
			return 1;
		};

		this.getItemsCount = function(series) {
			return this.len;
		};

		this.getItemX = function(series, item) {
			return this.array[this.start + item][this.xkey];
		};

		this.getItemY = function(series, item) {
			return this.array[this.start + item][this.ykey];
		};

		this.getItem = function(series, item) {
			return this.array[this.start + item];
		};
	}

	function WrapperDataset(ds) {
		this.ds = ds;

		this.getSeriesCount = function() {
			return ds.getSeriesCount();
		};

		this.getItemsCount = function(series) {
			return this.ds.getItemsCount(series);
		};

		this.getItemX = function(series, item) {
			return this.ds.getItemX(series, item);
		};

		this.getItemY = function(series, item) {
			return this.ds.getItemY(series, item);
		};
	}

	/**
	 * Dataset to get only a few number of items ("max") from a given dataset,
	 * like in the MDB.sparse_select() method.
	 * 
	 * @param ds
	 *            Dataset with all available items.
	 * @param max
	 *            The max number of items to gives.
	 */
	function CompressedDataset(ds, limit) {
		this.ds = ds;
		this.max = limit || 500;

		this.getSeriesCount = function() {
			return this.ds.getSeriesCount();
		};

		this.getItemsCount = function(series) {
			var len = this.ds.getItemsCount(series);
			return len > this.max ? this.max : len;
		};

		this.getItemIndex = function(series, item) {
			var len;
			len = this.ds.getItemsCount(series);
			if (len > this.max) {
				return (item / this.max * len) | 0;
			}
			return item;
		};

		this.getItemX = function(series, item) {
			return this.ds.getItemX(series, this.getItemIndex(series, item));
		};

		this.getItemY = function(series, item) {
			return this.ds.getItemY(series, this.getItemIndex(series, item));
		};
	}

	/**
	 * A dataset with no items.
	 */
	function EmptyDataset() {
		this.getSeriesCount = function() {
			return 0;
		};

		this.getItemsCount = function() {
			return 0;
		};
	}

	/*
	 * Constant of an empty dataset.
	 */
	var EMPTY_DATASET = new EmptyDataset();

	// ////////////////////////
	// PAINTER CLASSES
	// ////////////////////////

	function LinePainter(lineStyle) {
		this.lineStyle = lineStyle || "#fff";

		this.paint = function(conv, ctx, ds) {
			var i, j, x, y, len;

			ctx.save();

			ctx.strokeStyle = this.lineStyle;

			for (i = 0; i < ds.getSeriesCount(); i += 1) {
				ctx.beginPath();
				len = ds.getItemsCount(i);
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

	// export classes and constants

	window.LineChart = {
		Chart : Chart,
		SimpleDataset : SimpleDataset,
		CompressedDataset : CompressedDataset,
		EmptyDataset : EmptyDataset,
		WrapperDataset : WrapperDataset,
		ArrayRangeDataset : ArrayRangeDataset,
		EMPTY_DATASET : EMPTY_DATASET,
		LinePainter : LinePainter
	};
}());