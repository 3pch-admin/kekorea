package e3ps.common.util;

import java.util.ArrayList;
import java.util.Map;

import e3ps.project.service.ProjectHelper;

public class AUIGridUtils {

	private AUIGridUtils() {

	}

	public static ArrayList<Map<String, Object>> remoter(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		String term = (String) params.get("term");
		String target = (String) params.get("target");

		// 프로젝트 리모터
		if ("project".equals(target)) {
			list = ProjectHelper.manager.remoter(term);
		}

		return list;
	}
}
