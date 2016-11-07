export const UPDATE_MESSAGES = "UPDATE_MESSAGES";

export const updateMessagesApplicationLoad = (payload) => {
    return {
        type: UPDATE_MESSAGES,
        payload
    };
};

