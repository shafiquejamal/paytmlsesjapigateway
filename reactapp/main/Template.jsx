import React, { Component } from 'react';
import { Link } from 'react-router';
import { REGISTER_LINK, REGISTER_TEXT, LOGIN_LINK, LOGIN_TEXT } from '../routes';

export default class Template extends Component {
    render() {
        console.log('REGISTER_LINK', REGISTER_LINK);
        console.log('LOGIN_LINK', LOGIN_LINK);
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
                        <li className="nav-item">
                            <Link to={REGISTER_LINK} className="nav-link">{REGISTER_TEXT}</Link>
                        </li>
                        <li className="nav-item">
                            <Link to={LOGIN_LINK} className="nav-link">{LOGIN_TEXT}</Link>
                        </li>
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
}
