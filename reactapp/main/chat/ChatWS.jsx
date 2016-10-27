export default class ChatWS {
    constructor(wsurl, dispatcher) {
        this.websocket = new WebSocket(wsurl);
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