var React = require('react');
var ReactDOM = require('react-dom');
var expect = require('expect');
var $ = require('jQuery');
var TestUtils = require('react-addons-test-utils');

import { Logout } from '../../../main/access/authentication/Logout';

describe('Logout', () => {

  it('should exist', () => {
    expect(Logout).toExist();
  });

});
