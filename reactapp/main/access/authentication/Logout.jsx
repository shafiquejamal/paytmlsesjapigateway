import React from 'react';
import * as Redux from 'react-redux';
import { startLoggingOutUser } from './authenticationActionGenerators';

export const Logout = React.createClass({
  componentWillMount() {
    const { dispatch } = this.props;
    dispatch(startLoggingOutUser());
  },
  render() {
    return (
      <div className="container">
        <p>You are now logged out.</p>
      </div>
    );
  }
});

export default Redux.connect((state) => { return state; })(Logout)
