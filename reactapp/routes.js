import React from 'react';
import { Route, IndexRoute } from 'react-router';
import Template from './main/Template';
import LoginOrRegister from './main/LoginOrRegister';
import Example1 from './main/Example1';
import Example2 from './main/Example2';

export default (
<Route path="/" component={Template}>
    <IndexRoute component={LoginOrRegister} />
    <Route path="example1" component={Example1} />
    <Route path="example2" component={Example2} />
</Route>
);
