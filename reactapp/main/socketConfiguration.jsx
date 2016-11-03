import { WS_ROOT_URL } from './ConfigurationPaths';
import * as ChatActions from './chat/chatActionGenerators';
import * as ActionTypes from './chat/chatActionTypes';
import WSInstance from './WS';

export const socketConfiguration = (store) => {
    const socketConfig =  {
        ws: null,
        URL: WS_ROOT_URL + '/chat',
        wsDipatcher: (msg) => {
            const parsedMsg = JSON.parse(msg);
            return store.dispatch(ChatActions.receiveMessage(parsedMsg, parsedMsg.socketMessageType));
        },
        wsListener: () => {
            const lastAction = store.getState().lastAction;
            switch (lastAction.type) {
                case ActionTypes.POST_OBJECT:
                    return socketConfig.ws.postObject(lastAction.obj);

                case ActionTypes.CONNECT:
                    return socketConfig.startWS();

                case ActionTypes.DISCONNECT:
                    return socketConfig.stopWS();

                default:
                    return;
            }
        },
        stopWS: () => {
            socketConfig.ws.close();
            socketConfig.ws = null
        },
        startWS: () => {
            if (!!socketConfig.ws) socketConfig.ws.close();
            socketConfig.ws = new WSInstance(socketConfig.URL, socketConfig.wsDipatcher);
            const chatMessageFromLocalStorage = JSON.parse(localStorage.getItem('chatMessages'));
            if (Object.prototype.toString.call(chatMessageFromLocalStorage) === '[object Array]') {
                const latestDateTimeMillis = chatMessageFromLocalStorage.sort(function(a,b) {return (a.time > b.time) ? -1 :  1;}).map( message => message.time)[0];
                setTimeout( () => {
                    socketConfig.ws.postObject({
                        messageType: 'toServerRequestMessages',
                        'afterDateTimeInMillis': latestDateTimeMillis});
                }, 500);
            } else {
                setTimeout( () => {
                    socketConfig.ws.postObject({messageType: 'toServerRequestMessages'});
                }, 1000);
            }

        }
    };
    return socketConfig;
};