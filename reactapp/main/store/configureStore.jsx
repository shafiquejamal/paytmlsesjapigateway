import * as redux from 'redux';
import thunk from 'redux-thunk';
import { reducer as formReducer } from 'redux-form';
// import { checkUsernameAvailableReducer } from '../registration/RegistrationReducers';

export var configure = (initialState = {}) => {
    var reducer = redux.combineReducers({
      form: formReducer,
      // isUsernameIsAvailable: checkUsernameAvailableReducer
    });

    // compose composes all of our middleware
    var store = redux.createStore(reducer, initialState, redux.compose(
        redux.applyMiddleware(thunk),
        window.devToolsExtension ? window.devToolsExtension() : f => f
    ));

    return store;
};
