import React, { Component } from 'react';
import { Link } from 'react-router';

export default class Template extends Component {
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
                    </ul>
                </nav>

                <div className="container">
                    {this.props.children}

                    <hr/>

                        <footer>
                            <p>&copy; Company 2015</p>
                        </footer>
                </div>

            </div>
        );
    }
}
