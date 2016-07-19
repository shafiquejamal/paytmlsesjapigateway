var webpack = require('webpack');
var path = require('path');
var envFile = require('node-env-file');

process.env.NODE_ENV = process.env.NODE_ENV || 'development';

try {
    envFile(path.join(__dirname, 'reactconfig/' + process.env.NODE_ENV + '.env'));
} catch (e) {

}

module.exports = {
    entry: [
        'script!jquery/dist/jquery.min.js',
        './reactapp/app.jsx'
    ],
    externals: {
        jquery: 'jQuery'
    },
    plugins: [
        new webpack.ProvidePlugin({
            '$': 'jquery',
            'jQuery': 'jquery'
        })
    ],
    output: {
        path: __dirname,
        filename: './app/assets/javascripts/bundle.js'
    },
    resolve: {
        root: __dirname,
        alias: {
            configureStore: 'reactapp/main/store/configureStore.jsx',
            applicationStyles: 'reactapp/styles/app.scss',
            configuration: 'reactapp/main/configuration.jsx',
            routes: 'reeactapp/routes.jsx'
        },
        extensions: ['', '.js', '.jsx']
    },
    module: {
        loaders: [
            {
                loader: 'babel-loader',
                query: {
                    presets: ['react', 'es2015', 'stage-0']
                },
                test: /\.jsx?$/,
                exclude: /(node_modules|bower_components)/
            }
        ]
    },
    sassLoader: {
        includePaths: [
           // path.resolve(__dirname, './node_modules/foundation-sites/scss')
        ]
    },
    devtool: process.env.NODE_ENV === 'production' ? undefined : 'cheap-module-eval-source-map'
};
