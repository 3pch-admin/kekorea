package e3ps.doc.meeting.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.meeting.MeetingProjectLink;
import e3ps.doc.meeting.MeetingTemplate;
import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.doc.meeting.dto.MeetingTemplateDTO;
import e3ps.project.Project;
import net.sf.json.JSONArray;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class MeetingHelper {

	public static final MeetingHelper manager = new MeetingHelper();
	public static final MeetingService service = ServiceFactory.getService(MeetingService.class);

	// 회의록이 저장되어지는 폴더 위치 상수
	public static final String LOCATION = "/Default/프로젝트/회의로";

	public Map<String, Object> template(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<MeetingTemplateDTO> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(MeetingTemplate.class, true);

		QuerySpecUtils.toBooleanAnd(query, idx, MeetingTemplate.class, MeetingTemplate.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, MeetingTemplate.class, MeetingTemplate.NAME, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MeetingTemplate meetingTemplate = (MeetingTemplate) obj[0];
			MeetingTemplateDTO column = new MeetingTemplateDTO(meetingTemplate);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<MeetingDTO> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Meeting.class, true);
		int idx_link = query.appendClassList(MeetingProjectLink.class, true);
		int idx_p = query.appendClassList(Project.class, true);

		QuerySpecUtils.toInnerJoin(query, Meeting.class, MeetingProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toInnerJoin(query, Project.class, MeetingProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleBObjectRef.key.id", idx_p, idx_link);

		QuerySpecUtils.toOrderBy(query, idx, Meeting.class, Meeting.CREATE_TIMESTAMP, true);
		QuerySpecUtils.toOrderBy(query, idx, Meeting.class, Meeting.NAME, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Meeting meeting = (Meeting) obj[0];
			MeetingProjectLink link = (MeetingProjectLink) obj[1];
			Project project = (Project) obj[2];
			MeetingDTO column = new MeetingDTO(link);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public ArrayList<Map<String, String>> getMeetingTemplateMap() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(MeetingTemplate.class, true);
		QuerySpecUtils.toBooleanAnd(query, idx, MeetingTemplate.class, MeetingTemplate.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, MeetingTemplate.class, MeetingTemplate.NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MeetingTemplate meetingTemplate = (MeetingTemplate) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("oid", meetingTemplate.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", meetingTemplate.getName());
			list.add(map);
		}
		return list;
	}

	public String getContent(String oid) throws Exception {
		MeetingTemplate meetingTemplate = (MeetingTemplate) CommonUtils.getObject(oid);
		return meetingTemplate.getContent();
	}

	public JSONArray jsonArrayAui(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		Meeting meeting = (Meeting) CommonUtils.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Meeting.class, true);
		int idx_link = query.appendClassList(MeetingProjectLink.class, true);
		QuerySpecUtils.toInnerJoin(query, Meeting.class, MeetingProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, MeetingProjectLink.class, "roleAObjectRef.key.id",
				meeting.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MeetingProjectLink link = (MeetingProjectLink) obj[1];
			Project project = link.getProject();
			Map<String, String> map = new HashMap<>();
			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("projectType_name", project.getProjectType() != null ? project.getProjectType().getName() : "");
			map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
			map.put("mak_name", project.getMak() != null ? project.getMak().getName() : "");
			map.put("detail_name", project.getDetail() != null ? project.getDetail().getName() : "");
			map.put("kekNumber", project.getKekNumber());
			map.put("keNumber", project.getKeNumber());
			map.put("description", project.getDescription());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}
}
