import { LOGIN_USER, LOGOUT_USER } from './authenticationActionGenerators';

export const authenticationReducer = (state = {}, action) => {
  switch (action.type) {
    case LOGIN_USER:
     return {
       ...state,
       authenticated: true,
       email: action.email,
       username: action.username
     };
     break;
    case LOGOUT_USER:
      return {
        ...state,
        authenticated: false,
        email: undefined,
        username: undefined
      };
     break;
    default:
      return state;
  }
};
