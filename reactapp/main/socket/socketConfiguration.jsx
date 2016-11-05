import { WS_ROOT_URL } from './../ConfigurationPaths';
import * as ChatActions from './socketActionGenerators';
import * as ActionTypes from './socketActionTypes';
import WSInstance from './WS';
import { fetchNewChatMessages, requestContacts } from './requestNewData';

export const socketConfiguration = (store) => {
    const socketConfig =  {
        ws: null,
        URL: WS_ROOT_URL + '/chat',
        wsDipatcher: (msg) => {
            console.log('msg', msg);
            const parsedMsg = JSON.parse(msg);
            return store.dispatch(ChatActions.receiveMessage(parsedMsg.payload, parsedMsg.socketMessageType));
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
            setTimeout( () => {
                fetchNewChatMessages(socketConfig.ws);
                requestContacts(socketConfig.ws);
            }, 1000);
        }
    };
    return socketConfig;
};