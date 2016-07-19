import React from 'react';
import { Route, IndexRoute } from 'react-router';

import Template from './main/Template';
import LoginOrRegister from './main/LoginOrRegister';
import Example1 from './main/Example1';
import Example2 from './main/Example2';
import Register from './main/access/registration/Register';
import RegistrationSuccess from './main/access/registration/RegistrationSuccess';
import Login from './main/access/authentication/Login';
import Logout from './main/access/authentication/Logout';

export const REGISTER_LINK = "/register";
export const REGISTER_TEXT = "Register";
export const LOGIN_LINK = "/login";
export const LOGIN_TEXT = "Login";
export const LOGOUT_LINK = "/logout";
export const LOGOUT_TEXT = "Logout";
export const REGISTRATION_SUCCESS_LINK = "/registration-success";

export default (
<Route path="/" component={Template}>
    <IndexRoute component={LoginOrRegister} />
    <Route path="example1" component={Example1} />
    <Route path="example2" component={Example2} />
    <Route path={REGISTER_LINK} component={Register} />
    <Route path={REGISTRATION_SUCCESS_LINK} component={RegistrationSuccess} />
    <Route path={LOGIN_LINK} component={Login} />
    <Route path={LOGOUT_LINK} component={Logout} />
</Route>
);
