import * as redux from 'redux';
import thunk from 'redux-thunk';

export var configure = (initialState = {}) => {
    var reducer = redux.combineReducers({});

    // compose composes all of our middleware
    var store = redux.createStore(reducer, initialState, redux.compose(
        redux.applyMiddleware(thunk),
        window.devToolsExtension ? window.devToolsExtension() : f => f
    ));

    return store;
};
