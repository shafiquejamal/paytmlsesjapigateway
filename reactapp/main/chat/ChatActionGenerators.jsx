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

export function postObject(obj) {
    return {
        type: POST_OBJECT,
        obj
    }
}