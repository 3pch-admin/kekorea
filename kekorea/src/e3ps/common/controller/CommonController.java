package e3ps.common.controller;

import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.util.FolderUtils;
import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/common/**")
public class CommonController {

	@Description("폴더 가져오기")
	@RequestMapping(value = "/getFolder")
	@ResponseBody
	public JSONArray getFolder(@RequestParam Map<String, Object> param) {
		JSONArray node = null;
		try {
			node = FolderUtils.openFolder(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return node;
	}

	@Description("폴더 선택")
	@RequestMapping(value = "/openFolder")
	public ModelAndView openFolder(@RequestParam Map<String, Object> param) {
		ModelAndView model = new ModelAndView();
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		if (isPopup) {
			model.setViewName("popup:/common/openFolder");
		} else {
			model.setViewName("/jsp/common/openFolder.jsp");
		}
		return model;
	}
}
