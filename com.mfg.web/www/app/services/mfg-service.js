/*jslint vars: true, devel: true bitwise: true */
/*global angular, WebSocket */
(function () {
    'use strict';

    /**
     * Class to handle signals
     */
    var Signals = function () {

        this.callbacks = {};

        /**
         * Add callback.
         * @param key The namespace of the callback.
         */
        this.add = function (key, fn) {
            var list = this.callbacks[key];
            if (list === undefined) {
                list = [];
                this.callbacks[key] = list;
            }
            this.callbacks[key].push(fn);
        };

        /**
         * Remove all callbacks of the given namespace.
         * @param key The callbacks's namespace.
         */
        this.reset = function (key) {
            delete this.callbacks[key];
        };

        /**
         * Shortcut to reset and add a callback to a namespace.
         * @key The callback namespace.
         */
        this.resetAndAdd = function (key, fn) {
            this.reset(key);
            this.add(key, fn);
        };

        /**
         * Execute all callbacks under the given namesapce.
         * @param key The namespace of the callbacks to execute.
         */
        this.execute = function (key, data) {
            var list, i, fn;
            list = this.callbacks[key];
            if (list !== undefined) {
                for (i = 0; i < list.length; i += 1) {
                    fn = list[i];
                    fn(data);
                }
            }
        };
    };

    var MfgService = function () {
        var SIGNAL_SYMBOLS_STATUS_UPDATE = 'symbolsStatusUpdate';
        var SIGNAL_CONNECTION_STATUS_UPDATE = 'connectionStatusUpdate';

        this.symbolsStatusSignal = new Signals();
        this.connectionStatusSignal = new Signals();

        var self = this;

        this.resetAndAddSymbolStatusChangedListener = function (listener) {
            this.symbolsStatusSignal.resetAndAdd(SIGNAL_SYMBOLS_STATUS_UPDATE, listener);
        };

        this.resetAndAddConnectionStatusChangedListener = function (listener) {
            this.connectionStatusSignal.resetAndAdd(SIGNAL_CONNECTION_STATUS_UPDATE, listener);
        };

        this.isClosed = function () {
            return this.wsCommand === undefined || this.wsCommand.readyState === WebSocket.CLOSED;
        };

        this.isOpen = function () {
            return this.wsCommand !== undefined && this.wsCommand.readyState === WebSocket.OPEN;
        };

        this.connect = function () {
            // the socket of the commands
            this.wsCommand = new WebSocket('ws://' + window.location.host + '/command');
            this.wsCommand.wasConnectedBefore = false;

            this.wsCommand.onopen = function () {
                self.wsCommand.wasConnectedBefore = true;
                console.log('command socket: connected');
                self.connectionStatusSignal.execute(SIGNAL_CONNECTION_STATUS_UPDATE, true);
            };

            this.wsCommand.onmessage = function (msg) {
                console.log('command socket: onmessage');
                var data = JSON.parse(msg.data);

                // this is the entry point of all command messages

                if (data.cmd === 'config-tree') {
                    self.symbolsStatusSignal.execute(SIGNAL_SYMBOLS_STATUS_UPDATE, data);
                }
            };

            this.wsCommand.onclose = function () {
                if (self.wsCommand.wasConnectedBefore) {
                    console.log('command socket: onclose');
                    self.connectionStatusSignal.execute(SIGNAL_CONNECTION_STATUS_UPDATE, false);
                }
                setTimeout(function () {
                    console.log('command socket: try to connect');
                    self.connect();
                }, 5 * 1000);
            };
        };

        this.playConfig = function (id) {
            this.sendPlayStopCmd('play', id);
        };

        this.stopConfig = function (id) {
            this.sendPlayStopCmd('stop', id);
        };

        this.sendPlayStopCmd = function (action, id) {
            var msg = {
                cmd: action + '-config',
                id: id
            };
            this.wsCommand.send(JSON.stringify(msg));
        };

        this.requestSymbolsList = function () {
            if (this.isOpen()) {
                var msg = {
                    cmd: 'config-tree'
                };
                this.wsCommand.send(JSON.stringify(msg));
                console.log('sent symbol list request');
            } else {
                console.log('cannot request symbol list, the socket is not open');
            }
        };

        console.log('start MfgService service');
        this.connect();
    };

    window.MfgService = MfgService;
}());