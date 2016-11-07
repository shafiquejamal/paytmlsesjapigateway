import { uniq } from 'underscore';

import { RECEIVE_MESSAGE, DISCONNECT, CONNECT } from './../socket/socketActionTypes';
import { UPDATE_MESSAGES} from './chatMessagesActionGenerators';

import Message from './message';

const initialState = {
    conversation: [],
    status: false
};


const messageReducer = (state = initialState, action) => {
    switch (action.type) {
        case UPDATE_MESSAGES:
            let messagesToAdd = [];
            if (action.payload) {
                messagesToAdd = action.payload.map(message => new Message(message));
            }
            const uniqueMessages = uniq(state.conversation.concat(messagesToAdd), message => message.message.id);
            return {
                ...state,
                conversation: [ ...uniqueMessages ]
            };
            break;
        case RECEIVE_MESSAGE:
            return {
                ...state,
                conversation: [ ...state.conversation, new Message(action.payload)]
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