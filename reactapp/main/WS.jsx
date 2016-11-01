import axios from 'axios';
import { ROOT_URL } from './ConfigurationPaths';

export default class WS {
    constructor(wsurl, dispatcher) {

        axios.post(`${ROOT_URL}/single-use-token`,
            { no: 'data'},
            { headers: { authorization: "Bearer " + localStorage.getItem('token') }}
        ).then( (response) => {
                if (response.data.singleUseToken) {
                    localStorage.setItem('singleUseToken', response.data.singleUseToken);
                }
                return response
            }
        );

        this.websocket = new WebSocket(wsurl + "?token=" + localStorage.getItem('singleUseToken'));
        this.dispatcher = dispatcher;
        this.websocket.onmessage = function (event) {
            dispatcher(event.data)
        }
    }

    postMessage(text, recipient) {
        this.websocket.send(
            text + recipient
        );
    }

    close() {
        this.websocket.close();
    }

}