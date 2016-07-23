# Crauth

## Create. Read. Never Update. Never Delete.

This is a template application that has the following characteristics:
- credentials authentication
- ScalikeJDBC
- ReactJS
- Play Framework
- Postgres

## Requirements

- Java 1.8+
- Postgres 9.3+
- SBT
- Node 6.3.0+

## How to run locally (Mac or Linux)


1. In a terminal tab, clone the repository into a directory and cd into that directory:
```
git clone https://github.com/shafiquejamal/play-authentication.git /path/to/project
cd /path/to/project
```
Set any environment variables as indicated in conf/application.conf.

2. In conf/application.conf, set the db credentials (and any other credentials) to match what you will use on your system. Otherwise, in a terminal tab go to psql:
```
psql
```
and in the psql REPL, create two databases: 'crauth' and 'crauthtest'

3. Run SBT:
```
sbt
```
and wait for the dependencies to be downloaded

4. In another terminal tab, cd into the project directory and run 'npm install':
```
cd /path/to/project
npm install
```
When npm is done installing run
```
npm test
```
then run
```
webpack -w
```
5. After webpack is running with no errors, go back to the SBT terminal tab. In SBT run the following commands:
```
;test ; ~run
```
6. In a browswer, go to http://localhost:9000, and voila!

Changes to the .jsx files, these will automatically be reflected in the running app after reloading the webpage (assuming no errors - check the terminal tab running webpack and the browser javascript console to check for errors).

## References:

Email activation approach:
https://stackoverflow.com/questions/2362259/generating-storing-account-activation-code

React and Play Framework:
http://ticofab.io/react-js-tutorial-with-play_scala_webjars/

Testing the controllers:
http://www.michaelpollmeier.com/2015/09/25/playframework-guice-di

Twitter Bootstrap:
http://bootsnipp.com/snippets/featured/register-page

React JS:
Udemy.com courses by Adrew Mead and Stephen Grider
