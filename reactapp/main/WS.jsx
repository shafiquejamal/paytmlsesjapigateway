export default class WS {
    constructor(wsurl, dispatcher) {
        this.websocket = new WebSocket(wsurl + "?token=" + localStorage.getItem('token')); //TODO: Yes I know this is bad, but Websockets do not allow setting custom headers... yet.
        this.dispatcher = dispatcher;
        this.websocket.onmessage = function (event) {
            dispatcher(event.data)
        }
    }

    postMessage(text) {
        this.websocket.send(
            text
        );
    }

    close() {
        this.websocket.close();
    }

}