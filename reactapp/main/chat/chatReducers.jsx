import { RECEIVE_MESSAGE, DISCONNECT, CONNECT } from './chatActionTypes';

import Message from './message';

const initialState = {
    conversation: [],
    status: false
};

const messageReducer = (state = initialState, action) => {
    switch (action.type) {
        case RECEIVE_MESSAGE:
            return {
                ...state,
                conversation: [ ...state.conversation, new Message(action.message)]
            };

        case CONNECT:
            return {
                ...state,
                status: true
            };

        case DISCONNECT:
            return {
                ...state,
                status: false
            };

        default:
            return state;
    }

};

export default messageReducer;