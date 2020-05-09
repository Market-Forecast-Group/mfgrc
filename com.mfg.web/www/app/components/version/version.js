'use strict';

angular.module('mfgApp.version', [
  'mfgApp.version.interpolate-filter',
  'mfgApp.version.version-directive'
])

.value('version', '0.1');
