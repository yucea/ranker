var gulp = require('gulp'),
    less = require('gulp-less'),
    //csscomb = require('gulp-csscomb'),
    cssnano = require('gulp-cssnano'),
    rename = require('gulp-rename'),

    cssnanoOptions = {
      zindex: false,
      colormin: false
    },

    cleanCSSOptions = {
      advanced: false
    };

gulp.task('compile-less', function () {
  return gulp.src([
        '../Source_LESS_FILES/*.less', 
        '../Source_LESS_FILES/smartadmin-skin/smartadmin-skins.less',
        '!../Source_LESS_FILES/custom.less',
        '!../Source_LESS_FILES/overrides.less',
        '!../Source_LESS_FILES/variables.less'
        ])
    /* remove any dir and paths */
    .pipe(less())
    .pipe(rename({dirname: ''}))
    //.pipe(csscomb())
    .pipe(gulp.dest('../Source_UNMINIFIED_CSS/'));
});

gulp.task('cssnano', ['compile-less'], function () {
  return gulp.src(['../Source_UNMINIFIED_CSS/' + '*.css', '!' + '../Source_UNMINIFIED_CSS/' + '*.min.css'])
    /* rename with '.min' suffix */
    .pipe(rename({suffix: '.min'}))
    /* minify CSS with options */
    .pipe(cssnano(cssnanoOptions))
    /* write minified CSS */
    .pipe(gulp.dest('css'))
});

// run and watch file changes
gulp.task('watch', function() {
    // watch `.scss` changes
    gulp.watch(['./src/**/*.scss'], ['compile-less','cssnano'])
    .on('change', function(event) {
      console.log('File ' + event.path + ' was ' + event.type + ', running tasks...');
    });
});

// task registery
gulp.task('default', ['compile-less', 'cssnano', 'watch']);