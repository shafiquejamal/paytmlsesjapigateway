import md5 from 'blueimp-md5';

export const fetchNewChatMessages = (ws) => {

    const chatMessageFromLocalStorage = JSON.parse(localStorage.getItem('chatMessages'));
    const objectToPost = { messageType: 'toServerRequestMessages'};
    let fetch = () => {};
    if (chatMessageFromLocalStorage && chatMessageFromLocalStorage.length && chatMessageFromLocalStorage.length > 0) {
        const latestDateTimeMillis =
            chatMessageFromLocalStorage.sort(function(a,b) {return (a.time > b.time) ? -1 :  1;}).map( message => message.time - 5 * 86400000)[0];
        fetch = () => {
            ws.postObject({
                ...objectToPost,
                'afterDateTimeInMillis': latestDateTimeMillis});
        };
    } else {
        fetch = () => {
            ws.postObject(objectToPost);
        };
    }
    fetch();

};

export const requestContacts = (ws) => {
    const chatContacts = localStorage.getItem('chatContacts');
    const md5ofContacts = md5(chatContacts);
    ws.postObject({messageType: 'toServerRequestContacts', md5ofContacts});
};
