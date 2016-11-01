import { GET_CONTACTS, SELECT_CONTACT } from './chatContactsActionGenerators.jsx';

export const chatContactsReducer = (state = { contacts: [], activeContact: null }, action) => {
    switch (action.type) {
        case GET_CONTACTS:
            return {
                ...state,
                contacts: action.contacts
            };
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