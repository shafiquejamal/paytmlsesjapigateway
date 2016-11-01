import * as redux from 'redux';
import thunk from 'redux-thunk';
import { reducer as formReducer } from 'redux-form';
import { authenticationReducer } from '../access/authentication/authenticationReducers'
import messageReducer  from '../chat/chatReducers';
import { chatContactsReducer } from '../chat/chatContactsReducer';
import lastAction from '../chat/lastAction';

export var configure = (initialState = { }) => {
    var reducer = redux.combineReducers({
      form: formReducer,
      auth: authenticationReducer,
      messages: messageReducer,
      lastAction: lastAction.lastAction,
      contacts: chatContactsReducer
    });

    // compose composes all of our middleware
    return redux.createStore(reducer, initialState, redux.compose(
        redux.applyMiddleware(thunk),
        window.devToolsExtension ? window.devToolsExtension() : f => f
    ));

};
