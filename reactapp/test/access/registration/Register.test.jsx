var React = require('react');
var ReactDOM = require('react-dom');
var expect = require('expect');
var $ = require('jQuery');
var TestUtils = require('react-addons-test-utils');
import { checkAvailable } from '../../../main/access/ApiCalls';

import { Register } from '../../../main/access/registration/Register';

describe('Register', () => {

  it('should exist', () => {
    expect(Register).toExist();
  });

});
