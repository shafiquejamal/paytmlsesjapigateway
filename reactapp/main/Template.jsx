import React from 'react';
import { Link } from 'react-router';
import * as Redux from 'react-redux';

import { REGISTER_LINK, REGISTER_TEXT, LOGIN_LINK, LOGIN_TEXT, LOGOUT_LINK, LOGOUT_TEXT } from '../routes';

export const Template = React.createClass({
    renderLinks() {
      const { authenticated } = this.props.auth;
      if (!authenticated) {
        return (
          <div>
            <li className="nav-item">
                <Link to={REGISTER_LINK} className="nav-link pull-right">{REGISTER_TEXT}</Link>
            </li>
            <li className="nav-item">
                <Link to={LOGIN_LINK} className="nav-link pull-right">{LOGIN_TEXT}</Link>
            </li>
          </div>
        );
      } else {
        return (
          <li className="nav-item">
              <Link to={LOGOUT_LINK} className="nav-link pull-right">{LOGOUT_TEXT}</Link>
          </li>
        );
      }
    },
    render() {
        return (
            <div>
                <nav className="navbar navbar-static-top navbar-dark bg-inverse">
                    <a className="navbar-brand" href="#">Project name</a>
                    <ul className="nav navbar-nav">
                      <li className="nav-item active">
                          <a className="nav-link" href="#">Home <span className="sr-only">(current)</span></a>
                      </li>
                      <li className="nav-item">
                          <Link to="/example1" className="nav-link">Example 1</Link>
                      </li>
                      <li className="nav-item">
                          <Link to="/example2" className="nav-link">Example 2</Link>
                      </li>
                      {this.renderLinks()}
                    </ul>

                </nav>

                {this.props.children}

                <div className="container">
                    <hr/>

                        <footer className="col-md-12">
                            <p>&copy; Company 2015</p>
                        </footer>
                </div>

            </div>
        );
    }
});

export default Redux.connect((state) => {return state;})(Template);
