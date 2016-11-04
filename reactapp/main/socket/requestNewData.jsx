export const fetchNewChatMessages = (socketConfig) => {

    const chatMessageFromLocalStorage = JSON.parse(localStorage.getItem('chatMessages'));
    let fetch = () => {};
    if (Object.prototype.toString.call(chatMessageFromLocalStorage) === '[object Array]') {
        const latestDateTimeMillis =
            chatMessageFromLocalStorage.sort(function(a,b) {return (a.time > b.time) ? -1 :  1;}).map( message => message.time)[0];
        fetch = () => {
            socketConfig.ws.postObject({
                messageType: 'toServerRequestMessages',
                'afterDateTimeInMillis': latestDateTimeMillis});
        };
    } else {
        fetch = () => {
            socketConfig.ws.postObject({messageType: 'toServerRequestMessages'});
        };
    }
    fetch();

};