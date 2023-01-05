package e3ps.controller;

import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.project.Task;
import e3ps.project.Template;
import e3ps.project.beans.TaskViewData;
import e3ps.project.beans.TemplateViewData;
import e3ps.project.service.TemplateHelper;
import net.sf.json.JSONArray;
import wt.fc.ReferenceFactory;

@Controller
public class TemplateController extends BaseController {

	@RequestMapping(value = "/template/onSaveTemplateAction")
	@ResponseBody
	public Map<String, Object> onSaveTemplateAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.onSaveTemplate(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("템플릿 추가")
	@RequestMapping(value = "/template/addTemplateAction")
	@ResponseBody
	public Map<String, Object> addDocumentAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.addTemplateAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("템플릿 추가 페이지")
	@RequestMapping(value = "/template/addTemplate")
	public ModelAndView addEpm() throws Exception {
		ModelAndView model = new ModelAndView();

		// EPMDocumentType[] epmTypes = EPMDocumentType.getEPMDocumentTypeSet();
		// model.addObject("epmTypes", epmTypes);
		model.setViewName("popup:/project/template/addTemplate");
		return model;
	}

	@RequestMapping(value = "/template/onAfterTaskMoveAction")
	@ResponseBody
	public Map<String, Object> onAfterTaskMoveAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.onAfterTaskMoveAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/template/onAfterLinkUpdateAction")
	@ResponseBody
	public Map<String, Object> onAfterLinkUpdateAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.onAfterLinkUpdateAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/template/onAfterTaskResizeAction")
	@ResponseBody
	public Map<String, Object> onAfterTaskResizeAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.onAfterTaskResizeAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/template/onAfterLinkDeleteAction")
	@ResponseBody
	public Map<String, Object> onAfterLinkDeleteAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.onAfterLinkDeleteAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("템플릿 태스크 정보 페이지")
	@RequestMapping(value = "/template/viewTemplateTask")
	public ModelAndView viewTemplateTask(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		Task task = null;
		Template template = null;
		TaskViewData data = null;
		TemplateViewData tdata = null;
		ReferenceFactory rf = new ReferenceFactory();
		try {
			task = (Task) rf.getReference(oid).getObject();
			template = task.getTemplate();
			data = new TaskViewData(task);
			tdata = new TemplateViewData(template);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("tdata", tdata);
		model.addObject("data", data);
		if (isPopup) {
			model.setViewName("popup:/project/template/viewTemplateTask");
		} else {
			model.setViewName("default:/project/template/viewTemplateTask");
		}
		return model;
	}

	@RequestMapping(value = "/template/onBeforeLinkAddAction")
	@ResponseBody
	public Map<String, Object> onBeforeLinkAddAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.onBeforeLinkAddAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/template/onMoveTaskAction")
	@ResponseBody
	public Map<String, Object> onMoveTaskAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.onMoveTaskAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/template/onDeleteTaskAction")
	@ResponseBody
	public Map<String, Object> onDeleteTaskAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.onDeleteTaskAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/template/onSaveTaskAction")
	@ResponseBody
	public Map<String, Object> onSaveTaskAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.onSaveTaskAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/template/onSaveAction")
	@ResponseBody
	public Map<String, Object> onSaveAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.onSaveAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(map);
		return map;
	}

	@RequestMapping(value = "/template/onSaveTemplateTaskAction")
	@ResponseBody
	public Map<String, Object> createTemplateTaskAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.onSaveTemplateTaskAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("템플릿 태스크 편집 페이지")
	@RequestMapping(value = "/template/openTemplateTaskEditor")
	public ModelAndView openTemplateTaskEditor(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		if (isPopup) {
			model.setViewName("popup:/project/template/openTemplateTaskEditor");
		} else {
			model.setViewName("default:/project/template/openTemplateTaskEditor");
		}

		String gantt = TemplateHelper.manager.loadGanttTemplate(param);
		model.addObject("gantt", gantt);
		model.addObject("oid", oid);
		return model;
	}

	@Description("템플릿 트리 가져오기")
	@RequestMapping(value = "/template/getTemplateTaskTree")
	@ResponseBody
	public JSONArray getTemplateTaskTree(@RequestParam Map<String, Object> param) {
		JSONArray node = null;
		try {
			node = TemplateHelper.manager.openTemplateTree(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return node;
	}

	@Description("템플릿 정보 페이지")
	@RequestMapping(value = "/template/viewTemplate")
	public ModelAndView viewTemplate(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		Template template = null;
		TemplateViewData data = null;
		ReferenceFactory rf = new ReferenceFactory();
		try {
			template = (Template) rf.getReference(oid).getObject();
			data = new TemplateViewData(template);
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addObject("data", data);
		if (isPopup) {
			model.setViewName("popup:/project/template/viewTemplate");
		} else {
			model.setViewName("default:/project/template/viewTemplate");
		}
		return model;
	}

	@Description("템플릿 조회 페이지")
	@RequestMapping(value = "/template/listTemplate")
	public ModelAndView listTemplate() throws Exception {
		ModelAndView model = new ModelAndView();
		// default 승인됨
		model.setViewName("default:/project/template/listTemplate");
		return model;
	}

	@Description("템플릿 목록 가져오기")
	@ResponseBody
	@RequestMapping(value = "/template/listTemplateAction")
	public Map<String, Object> listTemplateAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.manager.find(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("템플릿 등록 페이지")
	@RequestMapping(value = "/template/createTemplate")
	public ModelAndView createProject() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("default:/project/template/createTemplate");
		return model;
	}

	@Description("템플릿 등록")
	@RequestMapping(value = "/template/createTemplateAction")
	@ResponseBody
	public Map<String, Object> createTemplateAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.createTemplateAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("템플릿 수정")
	@RequestMapping(value = "/template/modifyTemplateAction")
	@ResponseBody
	public Map<String, Object> modifyTemplateAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.modifyTemplateAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("템플릿 삭제")
	@RequestMapping(value = "/template/deleteTemplateAction")
	@ResponseBody
	public Map<String, Object> deleteTemplateAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = TemplateHelper.service.deleteTemplateAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
