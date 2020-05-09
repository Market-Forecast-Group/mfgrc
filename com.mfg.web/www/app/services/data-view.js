/*jslint bitwise:true*/
var MFGDataView = {

	getPriceRecord: function (dataview, index) {
		'use strict';
		var startByte = index * 24;
		return {
			physicalTime: dataview.getInt32(startByte + 4), // simulate Int64 with Int32
			priceRaw: dataview.getInt32(startByte + 8),
			time: dataview.getInt32(startByte + 16), // simulate Int64 with Int32
			volume: dataview.getInt32(startByte + 20)
		};
	},

	getPriceRecordCount: function (bytesCount) {
		'use strict';
		return (bytesCount / 24)  | 0;
	},

	getPivotRecord: function (dataview, index) {
		'use strict';
		var startByte = index * 57;
		return {
			pivotPrice: dataview.getFloat64(startByte),
			pivotTime: dataview.getInt32(startByte + 12), // simulate Int64 with Int32
			confirmPrice: dataview.getFloat64(startByte + 16),
			confirmTime: dataview.getInt32(startByte + 28), // simulate Int64 with Int32
			timeInterval: dataview.getInt32(startByte + 36), // simulate Int64 with Int32
			isUp: dataview.getInt8(startByte + 40) === 1,
			pivotPhysicalTime: dataview.getInt32(startByte + 45), // simulate Int64 with Int32
			confirmPhysicalTime: dataview.getInt32(startByte + 53) // simulate Int64 with Int32
		};
	},

	getPivotRecordCount: function (bytesCount) {
		'use strict';
		return (bytesCount / 57)  | 0;
	},

	getBandsRecord: function (dataview, index) {
		'use strict';
		var startByte = index * 64;
		return {
			time: dataview.getInt32(startByte + 4), // simulate Int64 with Int32
			topPrice: dataview.getFloat64(startByte + 8),
			centerPrice: dataview.getFloat64(startByte + 16),
			bottomPrice: dataview.getFloat64(startByte + 24),
			physicalTime: dataview.getInt32(startByte + 36), // simulate Int64 with Int32
			topRaw: dataview.getFloat64(startByte + 40),
			centerRaw: dataview.getFloat64(startByte + 48),
			bottomRaw: dataview.getFloat64(startByte + 56)
		};
	},

	getBandsRecordCount: function (bytesCount) {
		'use strict';
		return (bytesCount / 64)  | 0;
	}
};
