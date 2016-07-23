import axios from 'axios';
import { ROOT_URL } from '../../configuration';

const USERNAME_CHECK_ENDPOINT = '/username';
const EMAIL_CHECK_ENDPOINT = '/email';
const REGISTER_ENDPOINT = '/register'
const ACTIVATE_ENDPOINT = '/activate'

export const checkAvailable = (endpoint, value) => {
  return function(dispatch, getState) {
    return axios.get(`${ROOT_URL}${endpoint}/${value}`).then(
      (response) => {
        return response.data.status;
      },
      (response) => {
        console.log('failure', response);
      }
    );
  }
}

export const registerUser = (email, username, password) => {
  return function(dispatch, getState) {
    return axios.post(`${ROOT_URL}${REGISTER_ENDPOINT}`, {email, username, password});
    }
}

export const startActivatingUser = (email, code) => {
  return function(dispatch, getState) {
    return axios.get(`${ROOT_URL}${ACTIVATE_ENDPOINT}?email=${email}&code=${code}`);
  }
}
