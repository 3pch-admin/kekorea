package e3ps.project.task.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.project.task.Task;
import e3ps.project.task.service.TaskHelper;

@Controller
@RequestMapping(value = "/task/**")
public class TaskController extends BaseController {

	@Description(value = "태스크 진행율 수정 페이지")
	@GetMapping(value = "/editProgress")
	public ModelAndView editProgress(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Task task = (Task) CommonUtils.getObject(oid);
		model.addObject("oid", oid);
		model.addObject("task", task);
		model.setViewName("popup:/project/task/task-edit-progress");
		return model;
	}

	@Description(value = "태스크 진행율 수정")
	@ResponseBody
	@PostMapping(value = "/editProgress")
	public Map<String, Object> editUser(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			TaskHelper.service.editProgress(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
