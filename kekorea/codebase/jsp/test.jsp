<html>
<head>
<script type="text/javascript" src="http://jqueryui.com/latest/jquery-1.3.2.js"></script>
<script>
$(document).ready(function(){
fnAdjustTable();
});

fnAdjustTable=function(){

var colCount=$('#firstTr111>td').length; //get total number of column

var m=0;
var n=0;
var brow='mozilla';
jQuery.each(jQuery.browser, function(i, val) {
if(val==true){

brow=i.toString();
}
});
$('.tableHeader').each(function(i){
if(m<colCount){

if(brow=='mozilla'){
$('#firstTd').css("width",$('.tableFirstCol').innerWidth());//for adjusting first td

$(this).css('width',$('#table_div td:eq('+m+')').innerWidth());//for assigning width to table Header div
}
else if(brow=='msie'){
$('#firstTd').css("width",$('.tableFirstCol').width());

$(this).css('width',$('#table_div td:eq('+m+')').width()-2);//In IE there is difference of 2 px
}
else if(brow=='safari'){
$('#firstTd').css("width",$('.tableFirstCol').width());

$(this).css('width',$('#table_div td:eq('+m+')').width());
}
else{
$('#firstTd').css("width",$('.tableFirstCol').width());

$(this).css('width',$('#table_div td:eq('+m+')').innerWidth());
}
}
m++;
});

$('.tableFirstCol').each(function(i){
if(brow=='mozilla'){
$(this).css('height',$('#table_div td:eq('+colCount*n+')').outerHeight());//for providing height using scrollable table column height
}else if(brow=='msie'){

$(this).css('height',$('#table_div td:eq('+colCount*n+')').innerHeight()-2);
}else{
$(this).css('height',$('#table_div td:eq('+colCount*n+')').height());
}
n++;
});



}

//function to support scrolling of title and first column
fnScroll=function(){

$('#divHeader').scrollLeft($('#table_div').scrollLeft());
$('#firstcol').scrollTop($('#table_div').scrollTop());


}

</script>
</head>
<body>
<table cellspacing="0" cellpadding="0" border="0" >
<tr>
<td id="firstTd">
</td>
<td>
<div id="divHeader" style="overflow:hidden;width:284px;">
<table cellspacing="0" cellpadding="0" border="1" >
<tr>
<td ><div class="tableHeader">Title1</div></td><td><div class="tableHeader">Title2</div></td><td><div class="tableHeader">Title3</div></td><td><div class="tableHeader">Title4</div></td><td><div class="tableHeader">Title5</div></td>
</tr>
</table>
</div>
</td>
</tr>
<tr>
<td valign="top">
<div id="firstcol" style="overflow: hidden;height:80px">
<table width="200px" cellspacing="0" cellpadding="0" border="1" >
<tr>
<td class="tableFirstCol">First Col row1 </td>
</tr>
<tr>
<td class="tableFirstCol">First Col row2</td>
</tr>
<tr>
<td class="tableFirstCol">First Col row3</td>
</tr>
<tr>
<td class="tableFirstCol">First Col row4</td>
</tr>
<tr>
<td class="tableFirstCol">First Col row5</td>
</tr>
<tr>
<td class="tableFirstCol">First Col row6</td>
</tr>
<tr>
<td class="tableFirstCol">First Col row7</td>
</tr>
<tr>
<td class="tableFirstCol">First Col row8</td>
</tr>
<tr>
<td class="tableFirstCol">First Col row9</td>
</tr>
</table>
</div>
</td>
<td valign="top">
<div id="table_div" style="overflow: scroll;width:1000px;height:100px;position:relative" onscroll="fnScroll()" >
<table width="1000px" cellspacing="0" cellpadding="0" border="1" >
<tr id='firstTr'>
<td>Row1Col1</td><td>Row1Col2</td><td>Row1Col3</td><td>Row1Col4</td><td>Row1Col5</td>
</tr>
<tr>
<td>Row2Col1</td><td>Row2Col2</td><td>Row2Col3</td><td>Row2Col4</td><td>Row3Col5</td>
</tr>
<tr>
<td>Row3Col1</td><td>Row3Col2</td><td>Row3Col3</td><td>Row3Col4</td><td>Row3Col5</td>
</tr>
<tr>
<td>Row4Col1</td><td>Row4Col2</td><td>Row4Col3</td><td>Row4Col4</td><td>Row4Col5</td>
</tr>
<tr>
<td>Row5Col1</td><td>Row5Col2</td><td>Row5Col3</td><td>Row5ol4</td><td>Row5Col5</td>
</tr>
<tr>
<td>Row6Col1</td><td>Row6Col2</td><td>Row6Col3</td><td>Row6Col4</td><td>Row6Col5</td>
</tr>
<tr>
<td>Row7Col1</td><td>Row7Col2</td><td>Row7Col3</td><td>Row7Col4</td><td>Row7Col5</td>
</tr>
<tr>
<td>Row8Col1</td><td>Row8Col2</td><td>Row8Col3</td><td>Row8Col4</td><td>Row8Col5</td>
</tr>
<tr>
<td>Row9Col1</td><td>Row9Col2</td><td>Row9Col3</td><td>Row9Col4</td><td>Row9Col5</td>
</tr>
</table>
</div>
</tr>
</table>
</body>
</html>