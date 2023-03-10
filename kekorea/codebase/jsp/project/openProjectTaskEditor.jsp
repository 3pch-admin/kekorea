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
			var url = "/Windchill/plm/project/onAfterTaskMoveAction";
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
			var url = "/Windchill/plm/project/onAfterTaskResizeAction";
			
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
			var url = "/Windchill/plm/project/onAfterLinkDeleteAction";
			var params = new Object();
			params.oid = oid;
			params.toid = "<%=oid%>";
			
			$(document).ajaxCallServer(url, params, function(data) {
				gantt.render();
			})
		}	
	
		function onDeleteTask(oid, parent) {
			var url = "/Windchill/plm/project/onDeleteTaskAction";
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
			var url = "/Windchill/plm/project/onAfterLinkUpdateAction";
			
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
			var url = "/Windchill/plm/project/onBeforeLinkAddAction";
			
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
			var url = "/Windchill/plm/project/onMoveTaskAction";

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
		
		// ??????
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
		// ?????????
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
	
		//?????????
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
				if(parentId.indexOf("Project") > -1) {
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
			var url = "/Windchill/plm/project/onSaveAction";

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
					t.taskType = "??????";
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
			var url = "/Windchill/plm/project/onSaveTaskAction";

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
				var oid = data.oid; // ??????..
				var id = data.id // ??????
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
			var url = "/Windchill/plm/project/onSaveTemplateTaskAction";
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
				key : "??????",
				label : "??????"
			},{
				key : "??????",
				label : "??????"
			}, {
				key : "??????",
				label : "??????"
			}, {
				key : "??????",
				label : "??????"
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
				label : "??????",
				width : 60,
				max_width : 60,
				template : gantt.getWBSCode
			}, {
				name : "text",
				label : "????????? ???",
				tree : true,
				min_width : 150,
				max_width : 300,
				width : 200,
				resize : true,
				editor : textEditor
			}, {
				name : "start_date",
				label : "?????? ?????????",
				align : "center",
				width : 100,
				max_width : 100,
				min_width : 100
			}, {
				name : "end_date",
				label : "?????? ?????????",
				align : "center",
				width : 100,
				max_width : 100,
				min_width : 100
			}, {
				name : "taskType",
				label : "????????????",
				align : "center",
				width : 100,
				max_width : 100,
				min_width : 100,
				editor : taskTypeEditor
			}, {
				name : "allocate",
				label : "?????????",
				align : "center",
				width : 60,
				max_width : 60,
				resize : false,
				editor : allocateEditor
			}, {
				name : "duration",
				label : "??????(???)",
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
			// ?????? ????????? ??????????????? ????????? ??????
			gantt.config.order_branch = false;
			// ?????? ????????? ????????? ?????? ????????? ????????? ??????
			gantt.config.order_branch_free = false;
			// ?????? ?????? ?????? ??????
			gantt.config.scale_height = 40;
			// ????????????
			gantt.locale = {
				date : {
					month_full : [ "1???", "2???", "3???", "4???", "5???", "6???", "7???", "8???", "9???", "10???", "11???", "12???" ],
					month_short : [ "1???", "2???", "3???", "4???", "5???", "6???", "7???", "8???", "9???", "10???", "11???", "12???" ],
					day_full : [ "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????" ],
					day_short : [ "???", "???", "???", "???", "???", "???", "???" ]
				},
				labels : {
					new_task : "??? ?????????",
					icon_save : "??????",
					icon_cancel : "??????",
					icon_details : "Details",
					icon_edit : "??????",
					icon_delete : "??????",
					confirm_closing : "",//Your changes will be lost, are you sure ?
					confirm_deleting : "???????????? ?????? ???????????????????",

					message_ok : "??????",
					message_cancel : "??????",

					section_description : "??????",
					section_time : "??????",

					confirm_link_deleting : "??? ?????? ????????? ?????? ??????????",
					link_from : "From",
					link_to : "To",
					link : "????????? ??????<br>",
					link_start : "?????? ?????????",
					link_end : "?????? ?????????",

					minutes : "???",
					hours : "??????",
					days : "???",
					weeks : "???",
					months : "???",
					years : "???"
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
			// ?????? ?????? ?????? - ???
			gantt.config.scale_unit = "week";
			gantt.config.date_scale = "%Y??? %F %d???";
			gantt.config.step = 1; // ????????????...
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
					// ???????????? ???????????? ?????? ????????????.
				})

				var length = gantt.getSelectedTasks().length;
				if (length == 0) {
					// 					gantt.alert("????????? ???????????? ???????????????.");
					return false;
				}

				gantt.confirm({
					text : "????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.<br>?????? ???????????????????",
					ok : "??????",
					cancel : "??????",
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
						linkTitle = "?????? : ";
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

				linkTitle += " " + gantt.getTask(link.source).text + " -> ?????? : " + gantt.getTask(link.target).text;

				modal = gantt.modalbox({
					title: linkTitle,
					text: "<div>" +
							"<label>????????? ?????? <input type='number' class='lag-input' /></label>" +
						"</div>",
					buttons: [
						{label:"??????", css:"link-save-btn", value:"save"},
						{label:"??????", css:"link-cancel-btn", value:"cancel"},
						{label:"??????", css:"link-delete-btn", value:"delete"}
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

			// ????????? ?????? ????????? ?????? ??????
			gantt.templates.rightside_text = function(start, end, task) {
				return task.text;
			}

			gantt.attachEvent("onAfterLinkUpdate", function(id,item){
				onAfterLinkUpdate(id, item.lag);
			});
			
			// ????????? - ????????? ??????
			gantt.attachEvent("onBeforeLinkAdd", function(id, link) {
				var linkType = link.type;
				// 				link.lag = 1;
				var sourceTask = gantt.getTask(link.source);
				var targetTask = gantt.getTask(link.target);

				var sourceChild = gantt.hasChild(sourceTask.id);
				var targetChild = gantt.hasChild(targetTask.id);

				var targets = targetTask.$target[0];
				if (targets != undefined) {
					gantt.alert("????????? ?????????????????? ????????????.");
					return false;
				}

				if (sourceChild == true) {
					gantt.alert(sourceTask.text + " ???????????? ?????? ???????????? ???????????? ????????? ????????? ?????? ??? ??? ????????????.");
					return false;
				}

				if (targetChild == true) {
					gantt.alert(targetTask.text + " ???????????? ?????? ???????????? ???????????? ????????? ????????? ?????? ??? ??? ????????????.");
					return false;
				}

				if (linkType != 0) {
					gantt.alert("????????? ????????? ????????? ?????? ???????????????.");
					return false;
				}

				var target = link.target;
				var source = link.source;
				onBeforeLinkAdd(source, target, id);
				return true;
			})
			

// 			gantt.attachEvent("onAfterTaskMove", function(id, parent, tindex) {
// 				var task = gantt.getTask(id);
// 				var parent = task.parent;
// 				var sort = task.sort;
// 				var childrens = gantt.getChildren(parent);
// 				var parentTask = gantt.getTask(parent);
// 				var prevId = gantt.getPrev(id);
// 				var depth = task.$level;
// 				onMoveTask(childrens, parent, task.id, prevId, depth);
// 			})

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

			// ????????? ?????? ??????
			gantt.templates.task_time = function(start, end, task) {
				var convert = gantt.date.date_to_str("%Y-%m-%d???");
				return convert(start) + " ~ " + convert(end);
			}

			// ????????? ?????? ?????????
			gantt.templates.task_text = function(start, end, task) {
				return task.duration + "???";
			};

			gantt.locale.labels.section_taskType = "????????????";

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
					key : "????????????",
					label : "????????????"
				}, {
					key : "????????????",
					label : "????????????"
				} ]
			}, {
				name : "time",
				map_to : "auto",
				type : "duration",
				// 			readonly : true,
				time_format : [ "%Y", "%m", "%d" ],
			// 				readonly : true
			} ]

			// ????????? ?????? ??????
			gantt.templates.task_time = function(start, end, task) {
				var convert = gantt.date.date_to_str("%Y-%m-%d???");
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
					// 					gantt.alert("?????? ????????? ????????? ????????? ????????? ?????????.");
					gantt.undo();
					return false;
				}

				if (hasChild == true) {
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

			// ??????
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

				if(p != undefined) {
					if ("task" == p.type) {
						var poid = p.id;
						var depth = p.$level;
						onSaveTask(text, description, start_date, end_date, du, poid, depth, id, taskType);
					}
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
					gantt.alert("?????? ???????????? ???????????? ?????? ???????????? ?????? ??? ??? ????????????.");
					return false;
				}

				if (taskType == "task") {
					gantt.confirm({
						text : "???????????? ?????? ???????????????????",
						ok : "??????",
						cancel : "??????",
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

			// ?????? ??????
			gantt.config.show_progress = false;
			gantt.config.xml_date = "%Y-%m-%d %H:%i:%s";
			gantt.init("gantt_here");

			gantt.parse(gantts);
		})
	</script>
</td>