import { RECEIVE_MESSAGE, CONNECT, DISCONNECT, POST_OBJECT } from './chatActionTypes';

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

export function receiveMessage(message) {
    return {
        type: RECEIVE_MESSAGE,
        message: JSON.parse(message)
    }
}

export const postChatMessage = function(text, recipient) {
    return (dispatch, getState) => {
       dispatch(postObject({text, recipient, messageType: 'toServerChat'}));
    };
}

export function postObject(obj) {
    return {
        type: POST_OBJECT,
        obj
    }
}