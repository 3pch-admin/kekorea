<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="/Windchill/jsp/asset/axisj/jquery/jquery-1.12.3.min.js"></script>
<!-- <script type="text/javascript" src="/Windchill/jsp/js/jquery-1.7.js"></script> -->
<script type="text/javascript" src="/Windchill/jsp/js/jquery.cookie.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/jquery-ui/jquery-ui.js"></script>
<script src="//cdn.jsdelivr.net/npm/ui-contextmenu/jquery.ui-contextmenu.min.js"></script>

<!-- jexcel -->
<!-- <script src="https://bossanova.uk/jexcel/v3/jexcel.js"></script> -->
<script src="/Windchill/jsp/asset/jexcel/dist/jexcel.js"></script>
<script src="/Windchill/jsp/asset/jsuites/dist/jsuites.js"></script>
<!-- <script src="https://bossanova.uk/jsuites/v2/jsuites.js"></script> -->

<!-- module js -->
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<script type="text/javascript" src="/Windchill/jsp/js/quick.js"></script>
<script type="text/javascript" src="/Windchill/jsp/js/approval.js"></script>


<script type="text/javascript" src="/Windchill/jsp/asset/sortable/jquery.tablesort.js"></script>

<!-- 결재 -->
<script type="text/javascript" src="/Windchill/jsp/js/approvals.js"></script>
<!-- 문서 -->
<script type="text/javascript" src="/Windchill/jsp/js/documents.js"></script>
<!-- 부품 -->
<script type="text/javascript" src="/Windchill/jsp/js/parts.js"></script>
<!-- 유저 -->
<script type="text/javascript" src="/Windchill/jsp/js/orgs.js"></script>
<!-- 도면 -->
<script type="text/javascript" src="/Windchill/jsp/js/epms.js"></script>
<!-- bom -->
<script type="text/javascript" src="/Windchill/jsp/js/boms.js"></script>
<!-- template -->
<script type="text/javascript" src="/Windchill/jsp/js/templates.js"></script>
<!-- project -->
<script type="text/javascript" src="/Windchill/jsp/js/projects.js"></script>
<!-- partlists -->
<script type="text/javascript" src="/Windchill/jsp/js/partlists.js"></script>
<!-- tree -->
<script type="text/javascript" src="/Windchill/jsp/js/trees.js"></script>
<!-- admins -->
<script type="text/javascript" src="/Windchill/jsp/js/admins.js"></script>
<!-- custom grid -->
<script type="text/javascript" src="/Windchill/jsp/js/grid.js"></script>
<!-- img view js -->
<script type="text/javascript" src="/Windchill/jsp/asset/magnify/dist/jquery.magnify.js"></script>

<!-- axisj js -->
<script type="text/javascript" src="/Windchill/jsp/asset/axisj/dist/AXJ.all.js"></script>

<!-- ax5 -->
<script type="text/javascript" src="/Windchill/jsp/asset/ax5core/dist/ax5core.min.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/ax5ui-mask/dist/ax5mask.min.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/ax5ui-dialog/dist/ax5dialog.js"></script>

<!-- checkbox, radiobox js -->
<script type="text/javascript" src="/Windchill/jsp/asset/radio/dist/jquery.checks.js"></script>

<!-- popup js -->
<!-- <script type="text/javascript" src="/Windchill/jsp/asset/popup/dist/jquery.window.popup.js"></script> -->

<!-- colResizable -->
<!-- <script type="text/javascript" src="/Windchill/jsp/js/colResizable-1.6.js"></script> -->

<!-- table header fixed -->
<script type="text/javascript" src="/Windchill/jsp/asset/fixHeader/tableHeadFixer.js"></script>

<!-- upload -->
<script type="text/javascript" src="/Windchill/jsp/js/upload.js"></script>

<!-- tree -->
<script type="text/javascript" src="/Windchill/jsp/asset/fancytree/src/jquery.fancytree.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/fancytree/src/jquery.fancytree.persist.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/fancytree/src/jquery.fancytree.edit.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/fancytree/src/jquery.fancytree.table.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/fancytree/src/jquery.fancytree.dnd5.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/fancytree/src/jquery.fancytree.gridnav.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/fancytree/src/jquery.fancytree.multi.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/fancytree/src/jquery.fancytree.childcounter.js"></script>

<!-- contextmenu -->
<script type="text/javascript" src="/Windchill/jsp/asset/contextmenu/dist/jquery.contextMenu.js"></script>

<!-- table header -->
<script type="text/javascript" src="/Windchill/jsp/asset/headerdnd/js/dragndrop.table.columns.js"></script>

<!-- table sorter -->
<script type="text/javascript" src="/Windchill/jsp/asset/sorter/js/table-sorter.js"></script>

<!-- creo view -->
<script type="text/javascript" src="/Windchill/jsp/js/pview.js"></script>
<script type="text/javascript" src="/Windchill/jsp/js/pvlaunch.js"></script>

<!-- tables -->
<script type="text/javascript" src="/Windchill/jsp/js/tables.js"></script>

<!-- mouse wheel -->
<script type="text/javascript" src="/Windchill/jsp/asset/mousewheel/jquery.mousewheel.js"></script>

<!-- dhtmlx gantt -->
<script type="text/javascript" src="/Windchill/jsp/asset/gantt/codebase/dhtmlxgantt.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/gantt/codebase/ext/dhtmlxgantt_auto_scheduling.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/gantt/codebase/ext/dhtmlxgantt_keyboard_navigation.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/gantt/codebase/ext/dhtmlxgantt_multiselect.js"></script>
<script type="text/javascript" src="/Windchill/jsp/asset/gantt/codebase/ext/dhtmlxgantt_undo.js"></script>


<!-- favicon -->
<link rel="shortcut icon" href="/Windchill/jsp/images/logo3.gif" type="image/x-icon">

<!-- jexcel -->
<!-- <link rel="stylesheet" href="https://bossanova.uk/jexcel/v3/jexcel.css"> -->
<link rel="stylesheet" href="/Windchill/jsp/asset/jexcel/dist/jexcel.css">
<!-- <link rel="stylesheet" href="https://bossanova.uk/jsuites/v2/jsuites.css"> -->
<link rel="stylesheet" href="/Windchill/jsp/asset/jsuites/dist/jsuites.css">

<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Noto+Sans+KR:300&amp;subset=korean">
<!-- solution css -->
<link rel="stylesheet" href="/Windchill/jsp/css/common.css">
<link rel="stylesheet" href="/Windchill/jsp/css/approval.css">

<!-- axisj css -->
<link rel="stylesheet" href="/Windchill/jsp/asset/axicon/axicon.css">
<link rel="stylesheet" href="/Windchill/jsp/asset/axisj/ui/bulldog/AXJ.min.css">

<!-- ax5 -->
<link rel="stylesheet" href="/Windchill/jsp/asset/ax5ui-mask/dist/ax5mask.css">
<link rel="stylesheet" href="/Windchill/jsp/asset/ax5ui-dialog/dist/ax5dialog.css">

<!-- img view css -->
<link rel="stylesheet" href="/Windchill/jsp/asset/magnify/dist/jquery.magnify.css">

<!-- checkbox, radiobox css -->
<link rel="stylesheet" href="/Windchill/jsp/asset/radio/skins/css/checks.css">

<!-- tree -->
<link rel="stylesheet" href="/Windchill/jsp/asset/fancytree/dist/skin-win8/ui.fancytree.css">

<!-- contextmenu -->
<link rel="stylesheet" href="/Windchill/jsp/asset/contextmenu/dist/jquery.contextMenu.css">

<!-- table header -->
<link rel="stylesheet" href="/Windchill/jsp/asset/headerdnd/css/dragndrop.table.columns.css">

<!-- sorter -->
<!-- <link rel="stylesheet" href="/Windchill/jsp/asset/sorter/css/table-sorter.css"> -->

<!-- dhtmlx gantt -->
<link rel="stylesheet" href="/Windchill/jsp/asset/gantt/codebase/dhtmlxgantt.css">
<link rel="stylesheet" href="/Windchill/jsp/asset/gantt/codebase/skins/dhtmlxgantt_skyblue.css">

<!-- spread sheet -->
<link rel="stylesheet" href="/Windchill/jsp/asset/spreadsheet/codebase/spreadsheet.css">



</head>
<body>
<script type="text/javascript">
mask.open();
$(function() {
	
}).keyup(function(e) {
	if(e.keyCode == 27) {
		mask.close();
		$("#tt").hide();
	}
})
</script>
<div id="tt">
	<img src="/Windchill/jsp/images/loading.gif">
</div>
</body>
</html>