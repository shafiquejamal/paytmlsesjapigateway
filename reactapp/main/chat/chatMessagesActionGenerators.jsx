export const UPDATE_MESSAGES = "UPDATE_MESSAGES";

export const startGettingMessagesFromLocalStorage = () => {

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