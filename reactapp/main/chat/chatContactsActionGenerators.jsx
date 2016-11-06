export const UPDATE_CONTACTS = 'UPDATE_CONTACTS';
export const SELECT_CONTACT = 'SELECT_CONTACT';

export const getContactsApplicationLoad = () => {
    const contactsFromLocalStorage = localStorage.getItem('chatContacts');
    const chatContacts = contactsFromLocalStorage ? JSON.parse(contactsFromLocalStorage) : [];
    return (dispatch, getState) => {
        dispatch(updateContacts(chatContacts));
    };
};

export const updateContacts = (payload) => {
    return {
        type: UPDATE_CONTACTS,
        payload
    };
};

export const selectContact = (contact) => {
    return {
        type: SELECT_CONTACT,
        activeContact: contact
    }
};