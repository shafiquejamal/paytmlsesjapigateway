import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { Router, hashHistory } from 'react-router';
import promise from 'redux-promise';
import WSInstance from './main/socket/WS';
import * as ChatActions from './main/socket/socketActionGenerators';
import * as ActionTypes from './main/socket/socketActionTypes';
import { WS_ROOT_URL } from './main/ConfigurationPaths';
import { socketConfiguration } from './main/socket/socketConfiguration';

import { updateMessagesApplicationLoad } from './main/chat/chatMessagesActionGenerators';
import { getContactsApplicationLoad, updateContacts } from './main/chat/chatContactsActionGenerators.jsx';

import routes from './routes';
import { LOGIN_USER } from './main/access/authentication/authenticationActionGenerators'

import { RECEIVE_MESSAGE } from './main/socket/socketActionTypes';


var store = require('configureStore').configure();

const token = localStorage.getItem('token');
const email = localStorage.getItem('email');
const username = localStorage.getItem('username');
if (token) {
  store.dispatch({
    type: LOGIN_USER,
    email,
    username
  })
}

const messages = JSON.parse(localStorage.getItem('chatMessages'));
if (messages) {
    store.dispatch(updateMessagesApplicationLoad(messages));
}

const testContacts = ['shafique', 'shafiquep', 'shafiqueksg'];
localStorage.setItem('chatContacts', JSON.stringify(testContacts));
const chatContacts = JSON.parse(localStorage.getItem('chatContacts'));
if (chatContacts) {
    store.dispatch(getContactsApplicationLoad(chatContacts));
}

require('style!css!sass!applicationStyles');

ReactDOM.render(
    <Provider store={store}>
        <Router history={hashHistory} routes={routes} />
    </Provider>,
    document.getElementById('app')
);

const sock = socketConfiguration(store);

const messageListener = () => {
    const lastAction = store.getState().lastAction;
    console.log("Message listener", lastAction);
    switch (lastAction.type) {
        case RECEIVE_MESSAGE:
            console.log("calling update contacts:", lastAction);
            return store.dispatch(updateContacts([lastAction.payload.from]));
        default:
            console.log("calling nothing", lastAction.type);
            return;

    }
};


store.subscribe(() => sock.wsListener());
store.subscribe(() => messageListener());