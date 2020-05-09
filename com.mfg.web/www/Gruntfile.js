module.exports = function(grunt) {
	grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    bower_concat: {
	  all: {
		dest: 'build/_bower.js',
		cssDest: 'build/_bower.css',
		exclude: [
		  // 'datatables-plugins',
		  // 'html5-boilerplate'
		],
		dependencies: {
		  // 'underscore': 'jquery',
		  // 'backbone': 'underscore',
		  // 'jquery-mousewheel': 'jquery'
		},
		bowerOptions: {
		  relative: false
		}
	  }
	}
  });
  
  grunt.loadNpmTasks('grunt-bower-concat');
  
};