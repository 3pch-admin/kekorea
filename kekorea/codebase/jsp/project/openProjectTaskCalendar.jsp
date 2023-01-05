<%@page import="e3ps.project.Project"%>
<%@page import="java.util.Calendar"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.project.Template"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = (String) request.getAttribute("oid");
	String gantt = (String) request.getAttribute("gantt");
	ReferenceFactory rf = new ReferenceFactory();
	Project project = (Project) rf.getReference(oid).getObject();
	Calendar start = Calendar.getInstance();
	start.setTime(project.getPlanStartDate());
	int start_year = start.get(Calendar.YEAR);
	int start_month = start.get(Calendar.MONTH);
	int start_day = start.get(Calendar.DATE);
%>
<style>
html {
 	height: 100% !important;
}
</style>
<td valign="top">
	<div id="gantt_here" style="height: 100%;"></div> <script type="text/javascript">
		
	</script> <script type="text/javascript">
		$(document).ready(function() {

			var dialogs = $(document).setOpen();
			var box = $(document).setNonOpen();

			var gantts =
	<%=gantt%>
		;
			gantt.config.readonly = true;
			gantt.config.columns = [ {
				name : "wbs",
				label : "순서",
				width : 60,
				max_width : 60,
				template : gantt.getWBSCode
			}, {
				name : "text",
				label : "태스크 명",
				tree : true,
				min_width : 150,
				max_width : 300,
				width : 200,
				resize : true,
			}, {
				name : "start_date",
				label : "계획 시작일",
				align : "center",
				width : 80,
				max_width : 80,
				min_width : 80
			}, {
				name : "end_date",
				label : "계획 종료일",
				align : "center",
				width : 80,
				max_width : 80,
				min_width : 80
			}, {
				name : "real_start_date",
				label : "실제 시작일",
				align : "center",
				width : 80,
				max_width : 80,
				min_width : 80
			}, {
				name : "real_end_date",
				label : "실제 종료일",
				align : "center",
				width : 80,
				max_width : 80,
				min_width : 80
			}, {
				name : "taskType",
				label : "종료타입",
				align : "center",
				width : 100,
				max_width : 100,
				min_width : 100,
			}, {
				name : "allocate",
				label : "할당율",
				align : "center",
				width : 50,
				max_width : 50,
				resize : false,
			}, {
				name : "duration",
				label : "기간(일)",
				align : "center",
				width : 50,
				max_width : 50,
				resize : false,
			} ]
			// 부모 태스크 내에서만의 태스크 이동
			gantt.config.order_branch = false;
			// 전체 태스크 내에선 이동 불가능 정렬이 힘듬
			gantt.config.order_branch_free = false;
			// 날짜 표기 부분 높이
			gantt.config.scale_height = 40;
			// 한글작업
			gantt.locale = {
				date : {
					month_full : [ "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월" ],
					month_short : [ "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월" ],
					day_full : [ "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일" ],
					day_short : [ "일", "월", "화", "수", "목", "금", "토" ]
				},
				labels : {
					new_task : "새 태스크",
					icon_save : "저장",
					icon_cancel : "취소",
					icon_details : "Details",
					icon_edit : "편집",
					icon_delete : "삭제",
					confirm_closing : "",//Your changes will be lost, are you sure ?
					confirm_deleting : "태스크를 삭제 하시겠습니까?",

					message_ok : "확인",
					message_cancel : "취소",

					section_description : "설명",
					section_time : "기간",

					confirm_link_deleting : "선 후행 관계를 삭제 합니다?",
					link_from : "From",
					link_to : "To",
					link : "선후행 삭제<br>",
					link_start : "후행 태스크",
					link_end : "선행 태스크",

					minutes : "분",
					hours : "시간",
					days : "일",
					weeks : "주",
					months : "월",
					years : "년"
				}
			};

			gantt.templates.task_class = function(start, end, task) {

				var no_progress = "no_progress_task";

				if (task.no_progress) {
					//return "no_progress_task";
					no_progress = "no_progress_task";
				}

				switch (task.state) {
				case "0":
					return no_progress + " delay";
					break;
				case "1":
					return no_progress + " warning";
					break;
				case "2":
					return no_progress + " progress";
					break;
				case "4":
					return no_progress + " ready";
					break;
				case "5":
					return no_progress + " complete";
					break;
				}
			};

			gantt.templates.grid_folder = function(item) {
				var state = item.state;
				if (state == "0") {
					return "<div class='gantt_tree_icon gantt_state_delay'></div>";
				} else if (state == "1") {
					return "<div class='gantt_tree_icon gantt_state_warning'></div>";
				} else if (state == "2") {
					return "<div class='gantt_tree_icon gantt_state_progress'></div>";
				} else if (state == "4" || state == "6") {
					return "<div class='gantt_tree_icon gantt_state_ready'></div>";
				} else if (state == "5") {
					return "<div class='gantt_tree_icon gantt_state_complete'></div>";
				} else {
					if (item.type != "placeholder") {
						return "<div class='gantt_tree_icon gantt_state_ready'></div>";
					}
				}
			};

			gantt.templates.grid_file = function(item) {
				var state = item.state;
				if (state == "0") {
					return "<div class='gantt_tree_icon gantt_state_delay'></div>";
				} else if (state == "1") {
					return "<div class='gantt_tree_icon gantt_state_warning'></div>";
				} else if (state == "2") {
					return "<div class='gantt_tree_icon gantt_state_progress'></div>";
				} else if (state == "4" || state == "6") {
					return "<div class='gantt_tree_icon gantt_state_ready'></div>";
				} else if (state == "5") {
					return "<div class='gantt_tree_icon gantt_state_complete'></div>";
				} else {
					if (item.type != "placeholder") {
						return "<div class='gantt_tree_icon gantt_state_ready'></div>";
					}
				}
			};

			// 			gantt.config.placeholder_task = true;
			gantt.config.auto_scheduling = true;
			gantt.config.auto_scheduling_strict = true;
			gantt.config.duration_unit = "day";
			gantt.config.drag_move = false;
			gantt.config.drag_resize = true;
			gantt.config.fit_tasks = true;
			gantt.config.show_unscheduled = true;
			// 차트 기본 단위 - 월
			gantt.config.scale_unit = "week";
			gantt.config.date_scale = "%Y년 %F %d일";
			gantt.config.step = 1; // 간격이군...
			gantt.config.min_column_width = 20;
			gantt.config.scale_height = 60;

			gantt.config.keyboard_navigation = true;
			gantt.config.keyboard_navigation_cells = true

			gantt.templates.timeline_cell_class = function(item, date) {
				if (date.getDay() == 0 || date.getDay() == 6) {
					return "weekend";
				}
			};

			// 템플릿 기간 오른쪽 날짜 표기
			gantt.templates.lefttside_text = function(start, end, task) {
				return task.text;
			}

			gantt.config.subscales = [ {
				unit : "day",
				step : 1,
				date : "%D"
			} ]

			// 편집창 헤더 타임
			gantt.templates.task_time = function(start, end, task) {
				var convert = gantt.date.date_to_str("%Y-%m-%d일");
				return convert(start) + " ~ " + convert(end);
			}

			// 편집창 헤더 텍스트
			gantt.templates.task_text = function(start, end, task) {

				if (gantt.config.taskMode && task.progress == 1) {
					return "";
				}

				if (gantt.config.taskMode && task.isTaskUser) {
					return "";
				}

				if (gantt.config.viewMode || gantt.config.resourceMode) {
					return "";
				}

				if (task.state != 4) {
// 					return "<font color='white'>" + task.duration + "일</font>";
				} else {
					return "";
				}
				return "";
			};

			gantt.locale.labels.section_taskType = "종료타입";

			// 편집창 헤더 타임
			gantt.templates.task_time = function(start, end, task) {
				var convert = gantt.date.date_to_str("%Y-%m-%d일");
				return convert(start) + " ~ " + convert(end);
			}

			gantt.templates.progress_text = function(start, end, task) {
				return "<span style='text-align:left;'>" + Math.round(task.progress * 100) + "% </span>";
			};

			// 날짜 포맷
			gantt.config.show_progress = true;
			gantt.config.xml_date = "%Y-%m-%d %H:%i:%s";
			gantt.config.scale_unit = "week";
			gantt.config.date_scale = "%Y년 %F %d일";
			gantt.config.step = 1; // 간격이군...
			gantt.config.min_column_width = 20;
			gantt.config.scale_height = 60;
			gantt.config.subscales = [ {
				unit : "day",
				step : 1,
				date : "%D"
			} ]
			gantt.init("gantt_here");

			gantt.parse(gantts);
		})
	</script>
</td>