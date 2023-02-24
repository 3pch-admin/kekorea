package e3ps.common.util;

import java.util.ArrayList;
import java.util.Map;

import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.admin.spec.service.OptionsHelper;
import e3ps.admin.spec.service.SpecHelper;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.project.service.ProjectHelper;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.util.FileUtil;

public class AUIGridUtils {

	private AUIGridUtils() {

	}

	public static ArrayList<Map<String, Object>> remoter(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		String target = (String) params.get("target");

		// 프로젝트 리모터
		if (target.contains("project")) {
			list = ProjectHelper.manager.remoter(params);
		} else if (target.contains("spec")) {
//			list = SpecHelper.manager.remoter(params);
		} else if (target.contains("code")) {
			list = CommonCodeHelper.manager.remoter(params);
		} else if (target.contains("options")) {
			list = OptionsHelper.manager.remoter(params);
		}

		return list;
	}

	public static String primaryTemplate(ContentHolder holder) throws Exception {
		String template = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			String icon = CommonContentHelper.manager.getIconPath(ext);
			template += "<a><img src=" + icon + "></a>&nbsp;";
		}
		return template;
	}

	public static String secondaryTemplate(ContentHolder holder) throws Exception {
		String template = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			String icon = CommonContentHelper.manager.getIconPath(ext);
			template += "<a><img src=" + icon + "></a>&nbsp;";
		}
		return template;
	}

	public static String getThumnailSmall(EPMDocument epm) throws Exception {
		String thumnail_mini = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm),
				ContentRoleType.THUMBNAIL_SMALL);

		if (thumnail_mini == null) {
			thumnail_mini = "/Windchill/jsp/images/productview_publish_24.png";
		}
		return thumnail_mini;
	}
}
