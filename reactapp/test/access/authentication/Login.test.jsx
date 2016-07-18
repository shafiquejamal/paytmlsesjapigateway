var React = require('react');
var ReactDOM = require('react-dom');
var expect = require('expect');
var $ = require('jQuery');
var TestUtils = require('react-addons-test-utils');

import { Login } from '../../../main/access/authentication/Login';

describe('Login', () => {

  it('should exist', () => {
    expect(Login).toExist();
  });

});
