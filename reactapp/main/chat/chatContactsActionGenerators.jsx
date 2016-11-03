export const GET_CONTACTS = 'GET_CONTACTS';
export const SELECT_CONTACT = 'SELECT_CONTACT';

export const getContactsApplicationLoad = () => {
    const contactsFromLocalStorage = localStorage.getItem('chatContacts');
    const contacts = contactsFromLocalStorage ? JSON.parse(contactsFromLocalStorage) : [];
    return (dispatch, getState) => {
        dispatch(getContacts(contacts));
    };
};

export const getContacts = (contacts) => {
    return {
        type: GET_CONTACTS,
        contacts
    };
};

export const selectContact = (contact) => {
    return {
        type: SELECT_CONTACT,
        activeContact: contact
    }
};