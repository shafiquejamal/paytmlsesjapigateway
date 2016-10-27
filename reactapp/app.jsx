import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { Router, hashHistory } from 'react-router';
import promise from 'redux-promise';
import WSInstance from './main/chat/ChatWS';
import * as ChatActions from './main/chat/chatActionGenerators';
import * as ActionTypes from './main/chat/chatActionTypes';
import { WS_ROOT_URL } from './main/ConfigurationPaths';

import routes from './routes';
import { LOGIN_USER } from './main/access/authentication/authenticationActionGenerators'

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

require('style!css!sass!applicationStyles');

ReactDOM.render(
    <Provider store={store}>
        <Router history={hashHistory} routes={routes} />
    </Provider>,
    document.getElementById('app')
);

const URL = WS_ROOT_URL + '/chat';

const sock = {
    ws: null,
    URL,
    wsDipatcher: (msg) => {
        return store.dispatch(ChatActions.receiveMessage(msg));
    },
    wsListener: () => {
        const lastAction = store.getState().lastAction;

        switch (lastAction.type) {
            case ActionTypes.POST_MESSAGE:
                return sock.ws.postMessage(lastAction.text);

            case ActionTypes.CONNECT:
                return sock.startWS();

            case ActionTypes.DISCONNECT:
                return sock.stopWS();

            default:
                return;
        }
    },
    stopWS: () => {
        sock.ws.close();
        sock.ws = null
    },
    startWS: () => {
        if(!!sock.ws) sock.ws.close();
        sock.ws = new WSInstance(sock.URL, sock.wsDipatcher)
    }
};

store.subscribe(() => sock.wsListener());