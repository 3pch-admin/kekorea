/**
 * WINDOW POPUP Version: 1.0.1 URL: https://github.com/urwebs/jquery.window.popup Description: Window popup for remote url Author: Jey (https://urwebs.net) Copyright: Copyright 2018 Urwebs License:
 * MIT License
 */
(function($){

    var pluginName = 'windowpopup';

    var defaults = {
        href        : "https://www.jqueryscript.net/",
        title       : "Title",
        width       : 640,
        height      : 640,
        top         : 0,
        left        : 0,
        status      : "no",
        fullscreen  : "no",
        channelmode : "no",
        location    : "no",
        menubar     : "no",
        toolbar     : "no",
        resizable   : "yes",
        scrollbars  : "no",
        alignCenter : true
    };

    function Plugin(element, options){

        this.element = element;
        this.settings = $.extend(true, {}, defaults, options);
        this.init();

    }

    Plugin.prototype = {
        init : function(){

            var settings = this.settings;

            $(this.element).on({
                click : function(event){
                    event.preventDefault();

                    // get data attributes
                    var $element        = $(this);
                    var set_href        = ($element[0].href) ? $element[0].href : settings.href;
                    var set_title       = ($element.data('title') === undefined) ? settings.title : $element.data('title');
                    var set_width       = ($element.data('width') === undefined) ? settings.width : $element.data('width');
                    var set_height      = ($element.data('height') === undefined) ? settings.height : $element.data('height');
                    var set_top         = ($element.data('top') === undefined) ? settings.top : $element.data('top');
                    var set_left        = ($element.data('left') === undefined) ? settings.left : $element.data('left');
                    var set_status      = ($element.data('status') === undefined) ? settings.status : $element.data('status');
                    var set_fullscreen  = ($element.data('fullscreen') === undefined) ? settings.fullscreen : $element.data('fullscreen');
                    var set_channelmode = ($element.data('channelmode') === undefined) ? settings.channelmode : $element.data('channelmode');
                    var set_location    = ($element.data('location') === undefined) ? settings.location : $element.data('location');
                    var set_menubar     = ($element.data('menubar') === undefined) ? settings.menubar : $element.data('menubar');
                    var set_toolbar     = ($element.data('toolbar') === undefined) ? settings.toolbar : $element.data('toolbar');
                    var set_resizable   = ($element.data('resizable') === undefined) ? settings.resizable : $element.data('resizable');
                    var set_scrollbars  = ($element.data('scrollbars') === undefined) ? settings.scrollbars : $element.data('scrollbars');
                    var set_aligncenter = ($element.data('aligncenter') === undefined) ? settings.alignCenter : $element.data('aligncenter');

                    // reinit
                    var reinit_top      = set_top;
                    var reinit_left     = set_left;
                    var reinit_title    = set_title.replace(/\s/gi, ''); // Remove Trim

                    // aligncenter
                    if ( set_aligncenter ) {
                        reinit_top = Math.round( (window.screen.height/2) - (set_height/2) + set_top );
                        reinit_left = Math.round( (window.screen.width/2) - (set_width/2) + set_left );
                    }

                    // popup
                    popup = window.open(''+ set_href +'',''+ reinit_title +'','width='+ set_width +',height='+ set_height +',top='+ reinit_top +',left='+ reinit_left +',status='+ set_status +',fullscreen='+ set_fullscreen +', channelmode='+ set_channelmode +', location='+ set_location +', menubar='+ set_menubar +', toolbar='+ set_toolbar +', resizable='+ set_resizable +', scrollbars='+ set_scrollbars +'');

                    return false;

                }
            });

        }
    };

    $.fn[pluginName] = function(settings){
        return this.each(function(){
            if( !$.data(this, pluginName) ){
                $.data(this, pluginName, new Plugin(this, settings));
            }
        });
    };

}(jQuery));
