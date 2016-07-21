var React = require('react');
var ReactDOM = require('react-dom');
var expect = require('expect');
var $ = require('jQuery');
var TestUtils = require('react-addons-test-utils');

import { ManageAccount } from '../../main/user/ManageAccount';

describe('ManageAccount', () => {

  it('should exist', () => {
    expect(ManageAccount).toExist();
  });

});
