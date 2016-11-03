export const UPDATE_MESSAGES = "UPDATE_MESSAGES";

export const startGettingMessagesFromLocalStorage = () => {

    //const chatMessages = [
    //    {
    //        id: 1,
    //        from: 'shafique',
    //        to: 'shafiqueksg',
    //        text: 'one',
    //        time: 1
    //    },
    //    {
    //        id: 2,
    //        from: 'shafique',
    //        to: 'shafiqueksg',
    //        text: 'two',
    //        time: 2
    //    },{
    //        id: 3,
    //        from: 'shafiqueksg',
    //        to: 'shafique',
    //        text: 'three',
    //        time: 3
    //    }
    //];
    //localStorage.setItem('chatMessages', JSON.stringify(chatMessages));

    const chatMessageFromLocalStorage = JSON.parse(localStorage.getItem('chatMessages'));

    return (dispatch, getState) => {
        dispatch(updateMessages(chatMessageFromLocalStorage));
    }
};

export const updateMessages = (messages) => {
    return {
        type: UPDATE_MESSAGES,
        messages
    };
};