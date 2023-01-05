<%@page import="java.util.Calendar"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.project.Template"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = (String) request.getAttribute("oid");
	String gantt = (String) request.getAttribute("gantt");
	ReferenceFactory rf = new ReferenceFactory();
	Template template = (Template) rf.getReference(oid).getObject();
	Calendar start = Calendar.getInstance();
	start.setTime(template.getPlanStartDate());
	int start_year = start.get(Calendar.YEAR);
	int start_month = start.get(Calendar.MONTH);
	int start_day = start.get(Calendar.DATE);
%>
<td valign="top">
	<div id="gantt_here" style="height: 100%;"></div> <script type="text/javascript">
		var modal;
		var editLinkId;
		function saveLink(){
			var link = gantt.getLink(editLinkId);
	
			var lagValue = modal.querySelector(".lag-input").value;
			if(!isNaN(parseInt(lagValue, 10))){
				link.lag = parseInt(lagValue, 10);
			}
	
			gantt.updateLink(link.id);
			if(gantt.autoSchedule){
				gantt.autoSchedule(link.source);
			}
// 			endPopup();
		}
		
		function deleteLink(){
			gantt.deleteLink(editLinkId);
			endPopup()
		}
	
		function onAfterTaskMove(oid, start_date, end_date) {
			var url = "/Windchill/plm/template/onAfterTaskMoveAction";
			var params = new Object();
			params.oid = oid;
			params.toid = "<%=oid%>";
			params.start_date = start_date;
			params.end_date = end_date;
			
			$(document).ajaxCallServer(url, params, function(data) {
				gantt.autoSchedule();
				gantt.render();
			})
		}
	
		function onAfterTaskResize(oid, start_date, end_date, duration) {
			var url = "/Windchill/plm/template/onAfterTaskResizeAction";
			
			var params = new Object();
			params.toid = "<%=oid%>";
			params.oid = oid;
			params.start_date = start_date;
			params.end_date = end_date;
			params.duration = duration;
			console.log(params);
			$(document).ajaxCallServer(url, params, function(data) {
				gantt.render();
			})
		}

	
		function onAfterLinkDelete(oid) {
			var url = "/Windchill/plm/template/onAfterLinkDeleteAction";
			var params = new Object();
			params.oid = oid;
			params.toid = "<%=oid%>";
			
			$(document).ajaxCallServer(url, params, function(data) {
				gantt.render();
			})
		}	
	
		function onDeleteTask(oid, parent) {
			var url = "/Windchill/plm/template/onDeleteTaskAction";
			var params = new Object();
			params.toid = "<%=oid%>";
			params.oid = oid;
			params.parent = parent;
			
			$(document).ajaxCallServer(url, params, function(data) {
				var parent = data.parent;
				if(gantt.isTaskExists(parent)) {
					var p = gantt.getTask(parent);
					var hasChild = gantt.hasChild(p.id);
					
					if(hasChild == undefined) {
						p.type = "task";
						gantt.refreshTask(p.id);
					}
				}
				gantt.render();
			})
		}
	
		function onAfterLinkUpdate(oid, lag) {
			var url = "/Windchill/plm/template/onAfterLinkUpdateAction";
			
			var params = new Object();
			params.oid = oid;
			params.toid = "<%=oid%>";
			params.lag = lag;
			console.log(params);
			$(document).ajaxCallServer(url, params, function(data) {
				gantt.render();
			})
		}
		
		function onBeforeLinkAdd(source, target, id) {
			var url = "/Windchill/plm/template/onBeforeLinkAddAction";
			
			var params = new Object();
			params.toid = "<%=oid%>";
			params.source = source;
			params.id = id;
			params.target = target;
			
			$(document).ajaxCallServer(url, params, function(data) {
				gantt.changeLinkId(id, data.linkId);
			})
		}	
		
		function onMoveTask(childrens, parent, oid, prevId, depth) {
			var url = "/Windchill/plm/template/onMoveTaskAction";

			var params = new Object();
			params.childrens = childrens;
			params.parent = parent;
			params.oid = oid;
			params.prevId = prevId;
			params.depth = depth;
			params.toid = "<%=oid%>";
			
			$(document).ajaxCallServer(url, params, function(data) {
				gantt.render();
			})
		}	
		
		// 위로
		function moveUp() {
			gantt.eachSelectedTask(function(task_id) {
				taskId = task_id;
				var task = gantt.getTask(taskId);
	
				if(task.type == "placeholder") {
					gantt.unselectTask(taskId);
					return false;
				}
				
				if(task.type == "project" && task.parent == 0) {
					gantt.unselectTask(taskId);
					return false;
				}
			})
			
			var tasks = gantt.getSelectedTasks();
			if(tasks.length == 0) {
				return false;
			}
			
			if(tasks.length > 1) {
				for(var i=0; i<tasks.length; i++) {
					gantt.unselectTask(gantt.getTask(tasks[i]).id);
				}
				return false;
			}
			
			var ids = gantt.getSelectedId();
			var task = gantt.getTask(ids);
			var parentId = task.parent;
			
			var parent = gantt.getTask(ids).parent;
			var idx = gantt.getTaskIndex(ids);
			if((idx - 1) == -1) {
				return false;
			}
			gantt.moveTask(ids, (idx - 1), parent);
			gantt.focus();
		}
		// 아래로
		function moveDown() {
			
			gantt.eachSelectedTask(function(task_id) {
				taskId = task_id;
				var task = gantt.getTask(taskId);
		
				if(task.type == "placeholder") {
					gantt.unselectTask(taskId);
				}
				
				if(task.type == "project" && task.parent == 0) {
					gantt.unselectTask(taskId);
				}
			})
			
			var tasks = gantt.getSelectedTasks();
			if(tasks.length == 0) {
				return false;
			}
			
			if(tasks.length > 1) {
				for(var i=0; i<tasks.length; i++) {
					gantt.unselectTask(gantt.getTask(tasks[i]).id);
				}
				return false;
			}
			
			var ids = gantt.getSelectedId();
			var task = gantt.getTask(ids);
			var parent = gantt.getTask(ids).parent;
			var idx = gantt.getTaskIndex(ids);
			gantt.moveTask(ids, (idx + 1), parent);
			gantt.focus();
		}
	
		//오른쪽
		function moveRight() {
			gantt.eachSelectedTask(function(task_id) {
				taskId = task_id;
				var task = gantt.getTask(taskId);
				if(task.type == "placeholder") {
					gantt.unselectTask(taskId);
				}
				
				if(task.type == "project" && task.parent == 0) {
					gantt.unselectTask(taskId);
				}
			})
			
		    gantt.eachSelectedTask(function(task_id) {
		    	taskId = task_id;
				var prevId = gantt.getPrevSibling(taskId);
				var task = gantt.getTask(taskId);
				var parent = gantt.getTask(task.parent);
				if(gantt.isTaskExists(prevId)) {
					var idx = gantt.getTask(taskId).$index;
					var prev = gantt.getTask(prevId);
					var hasChild = gantt.hasChild(prev.id);
					if(hasChild == undefined) {
						prev.type = "project";
						gantt.refreshTask(prev.id);
					}
					gantt.moveTask(taskId, idx, prevId);
					gantt.focus();
				}
		    });
		}
	
		function moveLeft() {
			gantt.eachSelectedTask(function(task_id) {
				taskId = task_id;
				var task = gantt.getTask(taskId);
				if(task.type == "placeholder") {
					gantt.unselectTask(taskId);
				}
				
				if(task.type == "project" && task.parent == 0) {
					gantt.unselectTask(taskId);
				}
			})
	
			var array = [];
			if(true) {
				gantt.eachSelectedTask(function(task_id) {
					array.push(task_id);
				});
			}
			for(var i=array.length - 1; i>=0; i--) {
				taskId = array[i];
				var task = gantt.getTask(taskId);
	
				var parentId = gantt.getParent(task.id);
				var parentTask;
				if(parentId.indexOf("Template") > -1) {
					break;
				}
				
				parentTask = gantt.getTask(parentId);
				var idx = gantt.getTaskIndex(task.parent);
				gantt.moveTask(taskId, (idx + 1), parentTask.parent);
				gantt.focus();
				break;
			}
		}

	
		function onSave(id, col, newValue) {
			var url = "/Windchill/plm/template/onSaveAction";

			var params = new Object();
			params.oid = id;
			params.col = col;
			params.toid = "<%=oid%>";
			params.newValue = newValue;

			$(document).ajaxCallServer(url, params, function(data) {
				var oid = data.oid;
				var id = data.id;
				if(id != undefined) {
					id = Number(id);
					gantt.changeTaskId(id+1, oid);
					var t = gantt.getTask(oid);
					t.taskType = "일반";
					t.allocate = "0";
					var prevId = gantt.getPrevSibling(t.id);
					var idx = gantt.getTask(t.id).$index;
					gantt.moveTask(t.id, idx, prevId);
					var p = gantt.getTask(prevId).$open = true;
					gantt.updateTask(prevId);
					gantt.render();	
				}
			})
		}
		
		function onSaveTask(text, description, start_date, end_date, du, poid, depth, id, taskType) {
			var url = "/Windchill/plm/template/onSaveTaskAction";

			var params = new Object();
			params.text = text;
			params.id = id;
			params.description = description;
			params.start_date = start_date;
			params.end_date = end_date;
			params.duration = du;
			params.poid = poid;
			params.depth = depth;
			params.taskType = taskType;
			params.toid = "<%=oid%>";

			$(document).ajaxCallServer(url, params, function(data) {
				var oid = data.oid; // 신규..
				var id = data.id // 원래
				var poid = data.poid;
				if(id != undefined) {
					id = Number(id);
					gantt.changeTaskId(id, oid);
					var t = gantt.getTask(oid);
					var parent = gantt.getTask(poid);
					var hasChild = gantt.hasChild(parent.id);
					if(hasChild == true) {
						parent.type = "project";
						gantt.refreshTask(parent.id);
					}
					t.type = "task";
					gantt.updateTask(oid);
					gantt.autoSchedule();
					gantt.render();
				}
			})
		}
		
		function onSaveTemplateTask(oid, text, description, start_date, end_date, du, taskType, poid, childrens) {
			var url = "/Windchill/plm/template/onSaveTemplateTaskAction";
			var params = new Object();
			params.text = text;
			params.oid = oid;
			params.description = description;
			params.start_date = start_date;
			params.end_date = end_date;
			params.duration = du;
			params.taskType = taskType;
			params.poid = poid;
			params.childrens = childrens;
			params.toid = "<%=oid%>";

			$(document).ajaxCallServer(url, params, function(data) {
				gantt.autoSchedule();
				gantt.render();
			})
		}
	</script> <script type="text/javascript">
		$(document).ready(function() {

			var dialogs = $(document).setOpen();
			var box = $(document).setNonOpen();

			var gantts = <%=gantt%>;

			gantt.serverList("taskType", [ {
				key : "일반",
				label : "일반"
			},{
				key : "공통",
				label : "공통"
			}, {
				key : "기계",
				label : "기계"
			}, {
				key : "전기",
				label : "전기"
			}, {
				key : "SW",
				label : "SW"
			} ]);

			// inline editor
			var duEditor = {
				type : "text",
				map_to : "duration"
			};
			
			var allocateEditor = {
				type : "text",
				map_to : "allocate"
			};

			var textEditor = {
				type : "text",
				map_to : "text"
			};

			var taskTypeEditor = {
				type : "select",
				map_to : "taskType",
				options : gantt.serverList("taskType")
			};

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
				editor : textEditor
			}, {
				name : "start_date",
				label : "계획 시작일",
				align : "center",
				width : 100,
				max_width : 100,
				min_width : 100
			}, {
				name : "end_date",
				label : "계획 종료일",
				align : "center",
				width : 100,
				max_width : 100,
				min_width : 100
			}, {
				name : "taskType",
				label : "종료타입",
				align : "center",
				width : 100,
				max_width : 100,
				min_width : 100,
				editor : taskTypeEditor
			}, {
				name : "allocate",
				label : "할당율",
				align : "center",
				width : 60,
				max_width : 60,
				resize : false,
				editor : allocateEditor
			}, {
				name : "duration",
				label : "기간(일)",
				align : "center",
				width : 60,
				max_width : 60,
				resize : false,
				editor : duEditor
// 			}, {
// 				name : "add",
// 				width : 44,
// 				resize : false,
// 				hide : false
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

			gantt.config.placeholder_task = true;
			gantt.config.auto_scheduling = true;
			gantt.config.auto_scheduling_strict = true;
			gantt.config.duration_unit = "day";
			gantt.config.drag_move = true;
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

			// add shortcut

			gantt.addShortcut("shift+up", function(e) {
				moveUp();
			}, "taskCell");

			gantt.addShortcut("shift+down", function(e) {
				moveDown();
			}, "taskCell");

			gantt.addShortcut("shift+right", function(e) {
				moveRight();
			}, "taskCell");

			gantt.addShortcut("shift+left", function(e) {
				moveLeft();
			}, "taskCell");

			gantt.addShortcut("delete", function(e) {
				gantt.eachSelectedTask(function(task_id) {
					taskId = task_id;
					var task = gantt.getTask(taskId);
					if (task.type == "placeholder") {
						gantt.unselectTask(taskId);
					}

					if (task.type == "project") {
						gantt.unselectTask(taskId);
					}
					// 태스크가 아닌것은 일단 제외한다.
				})

				var length = gantt.getSelectedTasks().length;
				if (length == 0) {
					// 					gantt.alert("삭제할 태스크를 선택하세요.");
					return false;
				}

				gantt.confirm({
					text : "선택된 태스크와 관련된 모든 내용이 삭제 되어집니다.<br>삭제 하시겠습니까?",
					ok : "확인",
					cancel : "취소",
					callback : function(result) {
						if (result) {
							gantt.eachSelectedTask(function(task_id) {
								if (gantt.isTaskExists(task_id)) {
									gantt.deleteTask(task_id);
								}
							});
						} else {
							return false;
						}
					}
				})
			}, "taskCell");

			gantt.attachEvent("onLinkDblClick", function(id,e){
				if(gantt.config.readonly) {
					return false;
				}
				editLinkId = id;
				var link = gantt.getLink(id);
				var linkTitle;
				switch(link.type){
					case gantt.config.links.finish_to_start:
						linkTitle = "선행 : ";
						break;
					case gantt.config.links.finish_to_finish:
						linkTitle = "FF";
						break;
					case gantt.config.links.start_to_start:
						linkTitle = "SS";
						break;
					case gantt.config.links.start_to_finish:
						linkTitle = "SF";
						break;
				}

				linkTitle += " " + gantt.getTask(link.source).text + " -> 후행 : " + gantt.getTask(link.target).text;

				modal = gantt.modalbox({
					title: linkTitle,
					text: "<div>" +
							"<label>선후행 기간 <input type='number' class='lag-input' /></label>" +
						"</div>",
					buttons: [
						{label:"저장", css:"link-save-btn", value:"save"},
						{label:"취소", css:"link-cancel-btn", value:"cancel"},
						{label:"삭제", css:"link-delete-btn", value:"delete"}
					],
					width: "460px",
					type: "popup-css-class-here",
					callback: function(result){
						switch(result){
							case "save":
								saveLink();
								break;
							case "cancel":
								cancelEditLink();
								break;
							case "delete":
								deleteLink();
								break;
						}
					}
				});
				modal.querySelector(".lag-input").value = link.lag || 0;
				return false;
			});
			
			gantt.templates.timeline_cell_class = function(item, date) {
				if (date.getDay() == 0 || date.getDay() == 6) {
					return "weekend";
				}
			};

			// 템플릿 기간 오른쪽 날짜 표기
			gantt.templates.rightside_text = function(start, end, task) {
				return task.text;
			}

			gantt.attachEvent("onAfterLinkUpdate", function(id,item){
				onAfterLinkUpdate(id, item.lag);
			});
			
			// 이벤트 - 선후행 설정
			gantt.attachEvent("onBeforeLinkAdd", function(id, link) {
				var linkType = link.type;
				// 				link.lag = 1;
				var sourceTask = gantt.getTask(link.source);
				var targetTask = gantt.getTask(link.target);

				var sourceChild = gantt.hasChild(sourceTask.id);
				var targetChild = gantt.hasChild(targetTask.id);

				var targets = targetTask.$target[0];
				if (targets != undefined) {
					gantt.alert("설정된 선후행관계가 있습니다.");
					return false;
				}

				if (sourceChild == true) {
					gantt.alert(sourceTask.text + " 태스크에 하위 태스크가 존재하여 선후행 관계를 설정 할 수 없습니다.");
					return false;
				}

				if (targetChild == true) {
					gantt.alert(targetTask.text + " 태스크에 하위 태스크가 존재하여 선후행 관계를 설정 할 수 없습니다.");
					return false;
				}

				if (linkType != 0) {
					gantt.alert("잘못된 선후행 관계를 설정 하였습니다.");
					return false;
				}

				var target = link.target;
				var source = link.source;
				onBeforeLinkAdd(source, target, id);
				return true;
			})
			

			gantt.attachEvent("onAfterTaskMove", function(id, parent, tindex) {
				var task = gantt.getTask(id);
				var parent = task.parent;
				var sort = task.sort;
				var childrens = gantt.getChildren(parent);
				var parentTask = gantt.getTask(parent);
				var prevId = gantt.getPrev(id);
				var depth = task.$level;
				onMoveTask(childrens, parent, task.id, prevId, depth);
			})

			gantt.attachEvent("onAfterTaskDelete", function(id, item) {
				var p = item.parent;
				onDeleteTask(id, p);
				gantt.focus();
			})

			gantt.attachEvent("onBeforeGanttRender", function() {
				var range = gantt.getSubtaskDates();
				if (range.start_date && range.end_date) {
					gantt.config.start_date = gantt.calculateEndDate(range.start_date, -8, "day");
					gantt.config.end_date = gantt.calculateEndDate(range.end_date, 18, "day");
				}
			})

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
				return task.duration + "일";
			};

			gantt.locale.labels.section_taskType = "종료타입";

			gantt.config.lightbox.project_sections = [ {
				name : "description",
				height : 100,
				map_to : "text",
				type : "textarea",
				focus : true
			} ]

			gantt.config.lightbox.sections = [ {
				name : "description",
				height : 100,
				map_to : "text",
				type : "textarea",
				focus : true
			}, {
				name : "taskType",
				height : 28,
				map_to : "taskType",
				type : "select",
				options : [ {
					key : "단순종료",
					label : "단순종료"
				}, {
					key : "결재종료",
					label : "결재종료"
				} ]
			}, {
				name : "time",
				map_to : "auto",
				type : "duration",
				// 			readonly : true,
				time_format : [ "%Y", "%m", "%d" ],
			// 				readonly : true
			} ]

			// 편집창 헤더 타임
			gantt.templates.task_time = function(start, end, task) {
				var convert = gantt.date.date_to_str("%Y-%m-%d일");
				return convert(start) + " ~ " + convert(end);
			}
			
			
			gantt.attachEvent("onBeforeTaskDrag", function(id, mode, e){
				return true;
			});
			
			gantt.attachEvent("onAfterTaskDrag", function(id, mode, e) {
				var task = gantt.getTask(id);
				var convert = gantt.date.date_to_str("%Y-%m-%d");
				var start_date = convert(task.start_date);
				var end_date = convert(task.end_date);
				var duration = task.duration;
				var hasChild = gantt.hasChild(id);
				var leftLimit = new Date("<%=start_year%>", "<%=start_month%>", "<%=start_day%>");

				if (+task.start_date < +leftLimit) {
					// 					gantt.alert("계획 시작일 일정은 수정이 불가능 합니다.");
					gantt.undo();
					return false;
				}

				if (hasChild == true) {
					// 					gantt.alert("하위 태스크의 일정을 수정하여 주세요.");
					gantt.undo();
					return false;
				}

				if ("resize" == mode) {
					onAfterTaskResize(id, start_date, end_date, duration);
					gantt.render();
					return true;
				} else if ("move" == mode) {
					onAfterTaskMove(id, start_date, end_date);
					return true;
				}
				return true;
			})

			gantt.attachEvent("onAfterLinkDelete", function(id, item) {
				var target = item.target;
				var source = item.source;

				var targetTask = gantt.getTask(target);
				var sourceTask = gantt.getTask(source);

				gantt.refreshTask(targetTask.id);
				gantt.refreshTask(sourceTask.id);
				onAfterLinkDelete(id);
			})

			gantt.ext.inlineEditors.attachEvent("onBeforeEditStart", function(state) {
				var id = state.id;
				var type = gantt.getTask(id).type;
				if (type == "project") {
					return false;
				}
				return true;
			});

			gantt.ext.inlineEditors.attachEvent("onBeforeSave", function(state) {
				var col = state.columnName;
				var newValue = state.newValue;
				if (col == "duration" && newValue == 0) {
					return false;
				}
				return true;
			});

			// add event
			gantt.ext.inlineEditors.attachEvent("onSave", function(state) {
				var col = state.columnName;
				var id = state.id;
				var newValue = state.newValue;
				onSave(id, col, newValue);
			});

			// 저장
			gantt.attachEvent("onLightboxSave", function(id, task, is_new) {
				var convert = gantt.date.date_to_str("%Y-%m-%d");
				var id = task.id;
				var text = task.text;
				var parent = task.parent;
				var p = gantt.getTask(parent);

				var time = gantt.getLightboxSection("time");
				var du;
				if (time != null) {
					du = time.getValue()["duration"];
				}
				var start_date = convert(task.start_date);
				var end_date = convert(task.end_date);
				var taskType = task.taskType;

				var description = gantt.getLightboxSection("description").getValue();
				console.log(p);
				// 				if (p == "project" == p.type) {
				// 				if)
				// 					var poid = p.id;
				// 					var childrens = gantt.getChildren(parent);
				// 					onSaveTemplateTask(id, text, description, start_date, end_date, du, taskType, poid, childrens);
				// 				}

				if ("task" == p.type) {
					var poid = p.id;
					var depth = p.$level;
					onSaveTask(text, description, start_date, end_date, du, poid, depth, id, taskType);
				}
				return true;
			})

			gantt.attachEvent("onLightboxDelete", function(id) {
				var hasChild = gantt.hasChild(id);
				var taskType = gantt.getTask(id).type;
				if (taskType == "placeholder") {
					gantt.hideLightbox();
					return false;
				}

				if (hasChild == true) {
					gantt.alert("하위 태스크가 존재하여 해당 태스크를 삭제 할 수 없습니다.");
					return false;
				}

				if (taskType == "task") {
					gantt.confirm({
						text : "태스크를 삭제 하시겠습니까?",
						ok : "확인",
						cancel : "취소",
						callback : function(result) {
							if (result) {
								gantt.deleteTask(id);
								gantt.hideLightbox();
							} else {
								return false;
							}
						}
					})
				}
			})

			// 날짜 포맷
			gantt.config.show_progress = false;
			gantt.config.xml_date = "%Y-%m-%d %H:%i:%s";
			gantt.init("gantt_here");

			gantt.parse(gantts);
		})
	</script>
</td>