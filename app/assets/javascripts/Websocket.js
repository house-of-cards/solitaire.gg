define(["Config"], function (cfg) {
  "use strict";

  function Websocket(url, onConnect, onMessage) {
    this.url = url;
    this.connected = false;

    var me = this;
    var ws = new WebSocket(this.url);
    ws.onopen = function() {
      me.connected = true;
      onConnect(me.url);
    };
    ws.onmessage = function(event) {
      var json = JSON.parse(event.data);
      onMessage(json.c, json.v);
    };
    ws.onclose = function() {
      me.connected = false;
      cfg.log.info("Websocket connection to [" + me.url + "] closed.");
    };
    ws.onerror = function() {
      me.connected = false;
      cfg.log.error("Received error from websocket connection to [" + me.url + "].");
    };
    this.connection = ws;
  }

  Websocket.prototype.connect = function() {
    var msg = { "c": c, "v": v };
    var s = null;
    if(cfg.debug) {
      s = JSON.stringify(msg, undefined, 2);
    } else {
      s = JSON.stringify(msg);
    }
    this.connection.send(s);
  };

  Websocket.prototype.send = function(c, v) {
    var msg = { "c": c, "v": v };
    var s = null;
    if(cfg.debug) {
      s = JSON.stringify(msg, undefined, 2);
    } else {
      s = JSON.stringify(msg);
    }
    cfg.log.debug("Sending [" + c + "] message: " + s);
    this.connection.send(s);
  };

  return Websocket;
});
