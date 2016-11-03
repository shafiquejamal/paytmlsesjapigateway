import { RECEIVE_MESSAGE, DISCONNECT, CONNECT } from './chatActionTypes';
import { UPDATE_MESSAGES } from './chatMessagesActionGenerators';

import Message from './message';

const initialState = {
    conversation: [],
    status: false
};

const updateLocalStorage = () => {

};


const messageReducer = (state = initialState, action) => {
    switch (action.type) {
        case UPDATE_MESSAGES:
            let messagesToAdd = [];
            if (action.message) {
                const newMessages = action.message.toClientChatMessages;
                const chatMessageFromLocalStorageUnparsed = localStorage.getItem('chatMessages');
                if (chatMessageFromLocalStorageUnparsed) {
                    const chatMessageFromLocalStorage = JSON.parse(chatMessageFromLocalStorageUnparsed);
                    const messagesToStoreInLocalStorage = [...chatMessageFromLocalStorage, ...newMessages];
                    localStorage.setItem('chatMessages', JSON.stringify(messagesToStoreInLocalStorage));
                } else {
                    localStorage.setItem('chatMessages', JSON.stringify(newMessages));
                }
                messagesToAdd = newMessages.map(message => new Message(message));
            }
            return {
                ...state,
                conversation: [ ...state.conversation.concat(messagesToAdd) ]
            };
            break;
        case RECEIVE_MESSAGE:
            const chatMessageFromLocalStorageUnparsed = localStorage.getItem('chatMessages');
            if (chatMessageFromLocalStorageUnparsed) {
                const chatMessageFromLocalStorage = JSON.parse(chatMessageFromLocalStorageUnparsed);
                const messagesToStoreInLocalStorage = [...chatMessageFromLocalStorage, ...[action.message]];
                localStorage.setItem('chatMessages', JSON.stringify(messagesToStoreInLocalStorage));
            } else {
                localStorage.setItem('chatMessages', JSON.stringify(action.message));
            }
            return {
                ...state,
                conversation: [ ...state.conversation, new Message(action.message)]
            };
            break;
        case CONNECT:
            return {
                ...state,
                status: true
            };
            break;
        case DISCONNECT:
            return {
                ...state,
                status: false
            };
            break;
        default:
            return state;
    }

};

export default messageReducer;