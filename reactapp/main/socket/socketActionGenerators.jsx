import { RECEIVE_MESSAGE, CONNECT, DISCONNECT, POST_OBJECT } from './socketActionTypes';

export function connect() {
    return {
        type: CONNECT
    }
}

export function disconnect() {
    return {
        type: DISCONNECT
    }
}

export function receiveMessage(message, messageType) {
    return {
        type: messageType,
        message: message
    }
}

export const postChatMessage = (text, recipient) => {
    return (dispatch, getState) => {
       dispatch(postObject({text, recipient, messageType: 'toServerChat'}));
    };
};

export function postObject(obj) {
    return {
        type: POST_OBJECT,
        obj
    }
}