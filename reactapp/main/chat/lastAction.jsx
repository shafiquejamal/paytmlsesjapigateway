import messages from './chatReducers';

function lastAction(state = null, action) {
    return action;
}

export default { messages, lastAction };