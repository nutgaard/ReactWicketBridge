var gulp = require('gulp');
var browserify = require('browserify');
var watchify = require('watchify');
var reactify = require('reactify');
var notify = require('gulp-notify');
var source = require('vinyl-source-stream');

var config = require('./buildConfig.json');


function buildJS(isDev) {
    console.log('Building ', isDev);

    var props = watchify.args;
    props.entries = ['./index.js'];
    props.debug = isDev;
    props.standalone = 'Component';

    var bundler = isDev ? watchify(browserify(props)) : browserify(props);
    bundler.transform(reactify);

    function rebundle() {
        var stream = bundler.bundle();
        return stream
            .on('error', notify.onError({
                title: 'Compile Error',
                message: '<%= error.message %>'
            }))
            .pipe(source('index.js'))
            .pipe(gulp.dest('./dist'));
    }

    bundler.on('update', function () {
        var start = new Date();
        console.log('Rebundling');
        rebundle();
        console.log('Rebundled in ' + (new Date() - start) + 'ms');
    });

    return rebundle();
}


gulp.task('default', buildJS.bind(this, false));
gulp.task('dev', buildJS.bind(this, true));