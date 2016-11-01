export const GET_CONTACTS = 'GET_CONTACTS';
export const SELECT_CONTACT = 'SELECT_CONTACT';

export const getContacts = () => {
    return {
        type: GET_CONTACTS,
        contacts: ['shafique', 'shafiquep', 'shafiqueksg']
    }
};

export const selectContact = (contact) => {
    return {
        type: SELECT_CONTACT,
        activeContact: contact
    }
};