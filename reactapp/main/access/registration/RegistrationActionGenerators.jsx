import axios from 'axios';
import { ROOT_URL } from '../../configuration';

export const USERNAME_CHECK_ENDPOINT = '/username';
export const EMAIL_CHECK_ENDPOINT = '/email';
export const REGISTER_ENDPOINT = '/register'

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
    return axios.post(`${ROOT_URL}${REGISTER_ENDPOINT}`, {email, username, password})
    }
}
