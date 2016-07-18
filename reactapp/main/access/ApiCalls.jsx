import axios from 'axios';

export const ROOT_URL = 'http://localhost:9000/';
export const USERNAME_CHECK_ENDPOINT = 'username';
export const EMAIL_CHECK_ENDPOINT = 'email';

export const checkAvailable = function(endpoint, value) {
    return axios.get(`${ROOT_URL}${endpoint}/${value}`).then(
      (response) => {
        return response.data.status;
      },
      (response) => {
        console.log('failure', response);
      }
    )
}
