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

export function receiveMessage(payload, messageType) {
    return {
        type: messageType,
        payload
    }
}

export function postObject(obj) {
    return {
        type: POST_OBJECT,
        obj
    }
}

export const postChatMessage = (text, recipient) => {
    return (dispatch, getState) => {
       dispatch(postObject({text, recipient, messageType: 'toServerChat'}));
    };
};

export const addContact = (usernameOfContactToAdd) => {
    console.log('adding contact', usernameOfContactToAdd);
    return (dispatch, getState) => {
        dispatch(postObject({usernameOfContactToAdd, messageType: 'toServerAddContact'}));
    };
};