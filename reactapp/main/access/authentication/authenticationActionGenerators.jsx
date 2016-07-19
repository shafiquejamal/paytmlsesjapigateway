import axios from 'axios';
import validator from 'validator';

import { ROOT_URL } from '../../configuration';

export const LOGIN_ENDPOINT = '/authenticate';
export const LOGIN_USER = 'LOGIN_USER';
export const LOGOUT_USER = 'LOGOUT_USER';

export const startLoggingInUser = function(emailOrUsername, password) {
  var loginCredentials = {}
  return (dispatch, getState) => {
    if (validator.isEmail(emailOrUsername)) {
      loginCredentials = {
        email: emailOrUsername,
        password: password
      };
    } else {
      loginCredentials = {
        username: emailOrUsername,
        password: password
      };
    };
    return axios.post(`${ROOT_URL}${LOGIN_ENDPOINT}`, loginCredentials).then(
      (response) => {
        if (response.data.token) {
          dispatch(loginUser());
          localStorage.setItem('token',response.data.token);
        }
        return response;
      },
      (response) => {
        return response;
      }
    );
  }
};

export const startLoggingOutUser = function() {
  return (dispatch, getState) => {
    localStorage.removeItem('token');
    dispatch(logoutUser());
    console.log('getState', getState());
  };
};

export const loginUser = () => {
  return {
    type: LOGIN_USER,
  }
};

export const logoutUser = () => {
  return {
    type: LOGOUT_USER,
  }
};
