/*global $, console, angular */
(function () {
    'use strict';

    /**
     * Use this service to handle some aspects of the UI that is difficult with angular, but easy with jquery.
     * Boostrap and third-plugins use jquery, we cannot ignore this fact.
     */
    function UIService() {
        var self = this;

        this.start = function () {
            this.menuItems = $('[show-id]');

            this.menuItems.click(function () {
                var id;
                $('.mfg-main-panel').hide();
                id = $(this).attr('show-id');
                $('#' + id).show();
            });

            $(window).load(function () {
                self.showPane('symbols');
            });
        };

        this.showPane = function (id) {
            $('.mfg-main-panel').hide();
            $('#' + id).show();
        };
    }

    window.UIService = UIService;
}());