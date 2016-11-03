import { RECEIVE_MESSAGE, DISCONNECT, CONNECT } from './chatActionTypes';
import { UPDATE_MESSAGES } from './chatMessagesActionGenerators';

import Message from './message';

const initialState = {
    conversation: [],
    status: false
};

const messageReducer = (state = initialState, action) => {
    switch (action.type) {
        case UPDATE_MESSAGES:
            return {
                ...state,
                conversation: [ ...state.conversation.concat(action.messages.map(message => new Message(message))) ]
            };
            break;
        case RECEIVE_MESSAGE:
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