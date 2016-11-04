import { RECEIVE_MESSAGE, DISCONNECT, CONNECT } from './../socket/socketActionTypes';
import { UPDATE_MESSAGES_APPLICATION_LOAD, UPDATE_MESSAGES} from './chatMessagesActionGenerators';

import Message from './message';

const initialState = {
    conversation: [],
    status: false
};

const updateLocalStorage = (messages) => {
    const chatMessageFromLocalStorageUnparsed = localStorage.getItem('chatMessages');
    if (chatMessageFromLocalStorageUnparsed) {
        const chatMessageFromLocalStorage = JSON.parse(chatMessageFromLocalStorageUnparsed);
        const messagesToStoreInLocalStorage = [...chatMessageFromLocalStorage, ...messages];
        localStorage.setItem('chatMessages', JSON.stringify(messagesToStoreInLocalStorage));
    } else {
        localStorage.setItem('chatMessages', JSON.stringify(messages));
    }

};


const messageReducer = (state = initialState, action) => {
    switch (action.type) {
        case UPDATE_MESSAGES:
            let messagesToAdd = [];
            if (action.message) {
                const newMessages = action.message;
                updateLocalStorage(newMessages);
                messagesToAdd = newMessages.map(message => new Message(message));
            }
            return {
                ...state,
                conversation: [ ...state.conversation.concat(messagesToAdd) ]
            };
            break;
        case UPDATE_MESSAGES_APPLICATION_LOAD:
            let messagesToAddApplicationLoad = [];
            if ( Object.prototype.toString.call(action.messages) === '[object Array]' ) {
                messagesToAddApplicationLoad = action.messages.map(message => new Message(message));
            }
            return {
                ...state,
                conversation: [ ...state.conversation.concat(messagesToAddApplicationLoad) ]
            };
            break;
        case RECEIVE_MESSAGE:
            updateLocalStorage([action.message]);
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