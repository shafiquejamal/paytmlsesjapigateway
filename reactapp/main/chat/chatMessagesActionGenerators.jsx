export const UPDATE_MESSAGES_APPLICATION_LOAD = "UPDATE_MESSAGES_APPLICATION_LOAD";
export const UPDATE_MESSAGES = "UPDATE_MESSAGES";

export const updateMessagesApplicationLoad = (payload) => {
    return {
        type: UPDATE_MESSAGES_APPLICATION_LOAD,
        payload
    };
};

