var React = require('react');
var ReactDOM = require('react-dom');
var expect = require('expect');
var $ = require('jQuery');
var TestUtils = require('react-addons-test-utils');

import { ChangePassword } from '../../main/user/ChangePassword';

describe('ManageAccount', () => {

  it('should exist', () => {
    expect(ChangePassword).toExist();
  });

});
