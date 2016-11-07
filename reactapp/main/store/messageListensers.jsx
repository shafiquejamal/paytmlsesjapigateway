import uniq from 'underscore';

import { addContact } from '../socket/socketActionGenerators';
import { RECEIVE_MESSAGE } from '../socket/socketActionTypes';
import { UPDATE_MESSAGES } from '../chat/chatMessagesActionGenerators.jsx';


export const updateLocalStorage = (messages) => {
    const chatMessageFromLocalStorageUnparsed = localStorage.getItem('chatMessages');

    let messagesToStoreInLocalStorage = [];
    if (chatMessageFromLocalStorageUnparsed) {
        const chatMessageFromLocalStorage = JSON.parse(chatMessageFromLocalStorageUnparsed);
        messagesToStoreInLocalStorage = uniq([...chatMessageFromLocalStorage, ...messages], message => message.id);
    } else {
        messagesToStoreInLocalStorage = uniq(messages, message => message.id);
    }
    localStorage.setItem('chatMessages', JSON.stringify(messagesToStoreInLocalStorage));

};

export const messageListener = (store) => {
    const lastAction = store.getState().lastAction;
    switch (lastAction.type) {
        case RECEIVE_MESSAGE:
            updateLocalStorage([lastAction.payload]);
            return store.dispatch(addContact(lastAction.payload.from));
            break;
        case UPDATE_MESSAGES:
            if (lastAction && lastAction.payload) {
                const newMessages = lastAction.payload;
                updateLocalStorage(newMessages);
            }
            return;
            break;
        default:
            return;

    }
};