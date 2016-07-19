import React from 'react';
import { connect } from 'react-redux';

export const RegistrationSuccess = React.createClass({
  render() {
    return (
      <div>Registration succeeded - click here to log in.</div>
    );
  }
});

export default connect()(RegistrationSuccess);
