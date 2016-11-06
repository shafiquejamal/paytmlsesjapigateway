import { UPDATE_CONTACTS, SELECT_CONTACT } from './chatContactsActionGenerators.jsx';

export const chatContactsReducer = (state = { contacts: [], activeContact: null }, action) => {
    switch (action.type) {
        case UPDATE_CONTACTS:
            if (state.contacts.indexOf(action.payload) == -1) {
                return {
                    ...state,
                    contacts: action.payload
                };
            } else {
                return state;
            }

            break;
        case SELECT_CONTACT:
            return {
                ...state,
                activeContact: action.activeContact
            };
            break;
        default:
            return state;
    }
};