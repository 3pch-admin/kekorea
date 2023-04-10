package e3ps.doc.meeting.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
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
import net.sf.json.JSONObject;
import wt.doc.WTDocumentMaster;
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
	public static final String LOCATION = "/Default/프로젝트/회의록";

	/**
	 * 회의록 템플릿 조회
	 */
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

	/**
	 * 회의록 조회
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Meeting.class, true);

		QuerySpecUtils.toOrderBy(query, idx, Meeting.class, Meeting.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		JSONArray list = new JSONArray();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Meeting meeting = (Meeting) obj[0];

			JSONObject node = new JSONObject();
			node.put("oid", meeting.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", meeting.getName());
			QueryResult group = PersistenceHelper.manager.navigate(meeting, "project", MeetingProjectLink.class, false);
			int isNode = 1;
			JSONArray children = new JSONArray();
			while (group.hasMoreElements()) {
				MeetingProjectLink link = (MeetingProjectLink) group.nextElement();
				MeetingDTO dto = new MeetingDTO(link);
				if (isNode == 1) {
					node.put("poid", dto.getPoid());
					node.put("loid", link.getPersistInfo().getObjectIdentifier().getStringValue());
					node.put("projectType_name", dto.getProjectType_name());
					node.put("customer_name", dto.getCustomer_name());
					node.put("install_name", dto.getInstall_name());
					node.put("mak_name", dto.getMak_name());
					node.put("detail_name", dto.getDetail_name());
					node.put("kekNumber", dto.getKekNumber());
					node.put("keNumber", dto.getKeNumber());
					node.put("userId", dto.getUserId());
					node.put("description", dto.getDescription());
					node.put("state", dto.getState());
					node.put("model", dto.getModel());
					node.put("pdate_txt", dto.getPdate_txt());
					node.put("creator", dto.getCreator());
					node.put("creatorId", meeting.getOwnership().getOwner().getName());
					node.put("createdDate_txt", dto.getCreatedDate_txt());
				} else {
					JSONObject data = new JSONObject();
					data.put("name", dto.getName());
					data.put("oid", dto.getOid());
					data.put("loid", link.getPersistInfo().getObjectIdentifier().getStringValue());
					data.put("poid", dto.getPoid());
					data.put("projectType_name", dto.getProjectType_name());
					data.put("customer_name", dto.getCustomer_name());
					data.put("install_name", dto.getInstall_name());
					data.put("mak_name", dto.getMak_name());
					data.put("detail_name", dto.getDetail_name());
					data.put("kekNumber", dto.getKekNumber());
					data.put("keNumber", dto.getKeNumber());
					data.put("userId", dto.getUserId());
					data.put("description", dto.getDescription());
					data.put("state", dto.getState());
					data.put("model", dto.getModel());
					data.put("pdate_txt", dto.getPdate_txt());
					data.put("creator", dto.getCreator());
					data.put("creatorId", meeting.getOwnership().getOwner().getName());
					data.put("createdDate_txt", dto.getCreatedDate_txt());
					children.add(data);
				}
				isNode++;
			}
			node.put("children", children);
			list.add(node);
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
	
//회의록 템플릿 내용 가져오기 ajax
	public ArrayList<Map<String, String>> getContents(String oid) throws Exception {
		System.out.println("oid=" + oid);
		ArrayList<Map<String, String>> list = new ArrayList<>();
		MeetingTemplate meetingTemplate = (MeetingTemplate) CommonUtils.getObject(oid);
		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(CommonCode.class, true);
		int idx = query.appendClassList(Meeting.class, false);
		QuerySpecUtils.toEqualsAnd(query, idx, Meeting.class, "tinyReference.key.id",
				meetingTemplate.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, Meeting.class, Meeting.NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		System.out.println(query);
		Map<String, String> empty = new HashMap<>();
		empty.put("value", "");
		empty.put("name", "선택");
		list.add(empty);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MeetingTemplate mtt = (MeetingTemplate) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("value", mtt.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", mtt.getName());
			list.add(map);
		}
		return list;
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

	public String getNextNumber() throws Exception {

		Calendar ca = Calendar.getInstance();
		int day = ca.get(Calendar.DATE);
		int month = ca.get(Calendar.MONTH) + 1;
		int year = ca.get(Calendar.YEAR);
		DecimalFormat df = new DecimalFormat("00");
		String number = "MEETING-" + df.format(year) + df.format(month) + df.format(day) + "-";

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocumentMaster.class, true);

		QuerySpecUtils.toLikeRightAnd(query, idx, WTDocumentMaster.class, WTDocumentMaster.NUMBER, number);
		QuerySpecUtils.toOrderBy(query, idx, WTDocumentMaster.class, WTDocumentMaster.NUMBER, true);

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocumentMaster document = (WTDocumentMaster) obj[0];

			String s = document.getNumber().substring(document.getNumber().lastIndexOf("-") + 1);

			int ss = Integer.parseInt(s) + 1;
			DecimalFormat d = new DecimalFormat("000");
			number += d.format(ss);
		} else {
			number += "001";
		}
		return number;
	}

}
