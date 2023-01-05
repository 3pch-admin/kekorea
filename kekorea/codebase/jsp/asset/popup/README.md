# README   
Simple jQuery plugin for window popup.   


## DEMO   
[DEMO](demo/index.html)   


## INSTALLATION   
~~~
<script src="jquery.js"></script>
<script src="jquery.window.popup.js"></script>
~~~


## OPTIONS   
| option      | type    | default                   | notes                                     |
|-------------|---------|---------------------------|-------------------------------------------|
| href        | string  | "https://urwebs.net"      | url |
| title       | string  | "WINDOW POPUP"            | 제목 |
| width       | number  | 640                       | 열리는 창의 가로 크기 |
| height      | number  | 640                       | 열리는 창의 세로 크기 |
| top         | number  | 0                         | 열리는 창의 좌표 위쪽 |
| left        | number  | 0                         | 열리는 창의 좌표 왼쪽 |
| status      | boolean | no                        | ( yes / no / 1 / 0 ) 상태 표시바 보이거나 숨기기 |
| fullscreen  | boolean | no                        | ( yes / no / 1 / 0 ) 전체 창 |
| channelmode | boolean | no                        | ( yes / no / 1 / 0 ) 채널모드 F11 키 기능이랑 같음 |
| location    | boolean | no                        | ( yes / no / 1 / 0 ) 주소창 |
| menubar     | boolean | no                        | ( yes / no / 1 / 0 ) 메뉴바 |
| toolbar     | boolean | no                        | ( yes / no / 1 / 0 ) 툴바 |
| resizable   | boolean | no                        | ( yes / no / 1 / 0 ) 창 |
| scrollbars  | boolean | no                        | ( yes / no / 1 / 0 ) 창 스크롤바 |
| alignCenter | boolean | true                      | ( true / false ) 중앙정렬 |



## USAGE   
~~~
$.popupWindow([options]);
~~~

### SIMPLE EXAMPLE   
~~~
$('.window-popup').windowpopup();
~~~

### EXAMPLE WITH OPTIONS   
~~~
$('.window-popup').windowpopup({
    href        : "https://urwebs.net",
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
    resizable   : "no",
    scrollbars  : "no",
    alignCenter : true
});
~~~

### EXAMPLE WITH TAG   
~~~
<a class="window-popup" href="https://naver.com" data-title="Title" data-width="320" data-height="320" data-top="0" data-left="0" data-status="no" data-fullscreen="no" data-channelmode="no" data-location="no" data-menubar="no" data-toolbar="no" data-resizable="no" data-scrollbars="no" datadata-aligncenter="true">EXAMPLE</a>
~~~


### LICENSE   
Released under the MIT License.   



## REFERENCE   
[Web APIs | MDN]<https://developer.mozilla.org/en-US/docs/Web/API/Window/open>   