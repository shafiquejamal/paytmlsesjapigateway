import React from 'react';
import { Route, IndexRoute } from 'react-router';

import Template from './main/Template';
import LoginOrRegister from './main/LoginOrRegister';
import Example1 from './main/Example1';
import Example2 from './main/Example2';
import Register from './main/access/registration/Register';
import Login from './main/access/authentication/login';
import { checkAvailable } from './main/access/ApiCalls';

export const REGISTER_LINK = "/register";
export const REGISTER_TEXT = "Register";
export const LOGIN_LINK = "/login";
export const LOGIN_TEXT = "Login";

const RegisterWrapper = React.createClass({
  render: function() {
    return (
      <Register checkAvailable={checkAvailable} />
    );
  }
});

export default (
<Route path="/" component={Template}>
    <IndexRoute component={LoginOrRegister} />
    <Route path="example1" component={Example1} />
    <Route path="example2" component={Example2} />
    <Route path={REGISTER_LINK} component={RegisterWrapper} />
    <Route path={LOGIN_LINK} component={Login} />
</Route>
);
