var React = require('react');
var ReactDOM = require('react-dom');
var expect = require('expect');
var $ = require('jQuery');
var TestUtils = require('react-addons-test-utils');

import { ResendActivation } from '../../../main/access/registration/ResendActivation';

describe('ResendActivation', () => {

  it('should exist', () => {
    expect(ResendActivation).toExist();
  });

});
