package e3ps.doc.meeting.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.meeting.MeetingProjectLink;
import e3ps.doc.meeting.MeetingTemplate;
import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.doc.meeting.dto.MeetingTemplateDTO;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.output.Output;
import e3ps.project.output.OutputDocumentLink;
import e3ps.project.output.OutputProjectLink;
import e3ps.project.output.dto.OutputDTO;
import e3ps.project.template.Template;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
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
		Map<String, Object> map = new HashMap<String, Object>();System.out.println("params 안에 뭐 있나 보자아아아아아아아아"+params+"아아아아아아아아아");
		JSONArray list = new JSONArray();
		String description = (String) params.get("description");
		String kekNumber = (String) params.get("kekNumber");
		String keNumber = (String) params.get("keNumber");
		String pdateFrom = (String) params.get("pdateFrom");
		String pdateTo = (String) params.get("pdateTo");
		String userId = (String) params.get("userId");
		String kekState = (String) params.get("kekState");
		String model = (String) params.get("model");
		String customer_name = (String) params.get("customer_name");
		String install_name = (String) params.get("install_name");
		String projectType = (String) params.get("projectType");
		String machineOid = (String) params.get("machineOid");
		String elecOid = (String) params.get("elecOid");
		String softOid = (String) params.get("softOid");
		String mak_name = (String) params.get("mak_name");
		String detail_name = (String) params.get("detail_name");
		String template = (String) params.get("template");
//		String creator = (String) params.get("creator");
		String psize = (String) params.get("psize");
		
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Meeting.class, true);
//		int idx_o = query.appendClassList(Output.class, false);
		int idx_p = query.appendClassList(Project.class, true);
		int idx_mplink = query.appendClassList(MeetingProjectLink.class, false);
//		QuerySpecUtils.toInnerJoin(query, Meeting.class, Output.class, "documentReference.key.id",
//				WTAttributeNameIfc.ID_NAME, idx, idx_o);
//		int idx_p = query.appendClassList(Project.class, true);//이 줄부터 PersistenceHelper까지 아래의 메소드에서 가져온 코드
////		QuerySpecUtils.toCI(query, idx, Meeting.class);
		QuerySpecUtils.toInnerJoin(query, Meeting.class, MeetingProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_mplink);
		QuerySpecUtils.toInnerJoin(query, Project.class, MeetingProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleBObjectRef.key.id", idx_p, idx_mplink);
//		QuerySpecUtils.toEqualsAnd(query, idx_link, MeetingProjectLink.class, "roleAObjectRef.key.id", meeting);
//		QueryResult result = PersistenceHelper.manager.find(query);

//		QuerySpecUtils.toTimeGreaterAndLess(query, idx, Meeting.class, Meeting.CREATE_TIMESTAMP, );
//		QuerySpecUtils.to(query, idx, Meeting.class, Meeting.NAME, n);
//		QuerySpecUtils.toLikeAnd(query, idx, Meeting.class, Meeting.DESCRIPTION, description);
//		QuerySpecUtils.toCreator(query, idx, Project.class, userId);
//		QuerySpecUtils.toTimeGreaterAndLess(query, idx, Meeting.class, Meeting.CREATE_TIMESTAMP , pdateFrom, pdateTo);
		
		
		//output helper에서 가져온 코드==========================================
		if (!StringUtils.isNull(kekNumber) || !StringUtils.isNull(keNumber) || !StringUtils.isNull(description)) {

			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			
			int idx_olink = query.appendClassList(OutputDocumentLink.class, false);
			int idx_plink = query.appendClassList(OutputProjectLink.class, false);
			int idx_o = query.appendClassList(Output.class, false);
//			int idx_p = query.appendClassList(Project.class, true);
			
			query.appendOpenParen();
			
			ClassAttribute roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
			ClassAttribute roleBca = new ClassAttribute(WTDocument.class, WTAttributeNameIfc.ID_NAME);
			SearchCondition sc = new SearchCondition(
					new ClassAttribute(OutputDocumentLink.class, "roleAObjectRef.key.id"), "=", roleAca);
			query.appendWhere(sc, new int[] { idx_olink, idx_o });
			query.appendAnd();
			sc = new SearchCondition(new ClassAttribute(OutputDocumentLink.class, "roleBObjectRef.key.id"), "=",
					roleBca);
			query.appendWhere(sc, new int[] { idx_olink, idx });
			query.appendAnd();
			
			roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
			roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

			sc = new SearchCondition(new ClassAttribute(OutputProjectLink.class, "roleAObjectRef.key.id"), "=",
					roleAca);
			query.appendWhere(sc, new int[] { idx_plink, idx_o });
			query.appendAnd();
			sc = new SearchCondition(new ClassAttribute(OutputProjectLink.class, "roleBObjectRef.key.id"), "=",
					roleBca);
			query.appendWhere(sc, new int[] { idx_plink, idx_p });

			query.appendCloseParen();
			
			QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.KEK_NUMBER, kekNumber);
			QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.KE_NUMBER, keNumber);
			QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.DESCRIPTION, description);
		}
		//project에서 가져온 코드
//		if (!StringUtils.isNull(customer_name)) {
//			CommonCode customerCode = (CommonCode) CommonUtils.getObject(customer_name);
//			QuerySpecUtils.toEqualsAnd(query, idx_p, Project.class, "customerReference.key.id", customerCode);
//		}
//
//		if (!StringUtils.isNull(install_name)) {
//			CommonCode installCode = (CommonCode) CommonUtils.getObject(install_name);
//			QuerySpecUtils.toEqualsAnd(query, idx_p, Project.class, "installReference.key.id", installCode);
//		}
//
//		if (!StringUtils.isNull(projectType)) {
//			CommonCode projectTypeCode = (CommonCode) CommonUtils.getObject(projectType);
//			QuerySpecUtils.toEqualsAnd(query, idx_p, Project.class, "projectTypeReference.key.id", projectTypeCode);
//		}
//
//		if (!StringUtils.isNull(machineOid)) {
//			WTUser machine = (WTUser) CommonUtils.getObject(machineOid);
//			CommonCode machineCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
//			int idx_plink = query.appendClassList(ProjectUserLink.class, false);
//			int idx_u = query.appendClassList(WTUser.class, false);
//
//			QuerySpecUtils.toInnerJoin(query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
//					"roleAObjectRef.key.id", idx, idx_plink);
//			QuerySpecUtils.toInnerJoin(query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
//					"roleBObjectRef.key.id", idx_u, idx_plink);
//			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", machine);
//			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id",
//					machineCode);
//		}
//
//		if (!StringUtils.isNull(elecOid)) {
//			WTUser elec = (WTUser) CommonUtils.getObject(elecOid);
//			CommonCode elecCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
//			int idx_plink = query.appendClassList(ProjectUserLink.class, false);
//			int idx_u = query.appendClassList(WTUser.class, false);
//
//			QuerySpecUtils.toInnerJoin(query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
//					"roleAObjectRef.key.id", idx, idx_plink);
//			QuerySpecUtils.toInnerJoin(query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
//					"roleBObjectRef.key.id", idx_u, idx_plink);
//			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", elec);
//			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id", elecCode);
//		}
//
//		if (!StringUtils.isNull(softOid)) {
//			WTUser soft = (WTUser) CommonUtils.getObject(softOid);
//			CommonCode softCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
//			int idx_plink = query.appendClassList(ProjectUserLink.class, false);
//			int idx_u = query.appendClassList(WTUser.class, false);
//
//			QuerySpecUtils.toInnerJoin(query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
//					"roleAObjectRef.key.id", idx, idx_plink);
//			QuerySpecUtils.toInnerJoin(query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
//					"roleBObjectRef.key.id", idx_u, idx_plink);
//			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", soft);
//			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id", softCode);
//		}
//
//		if (!StringUtils.isNull(mak_name)) {
//			CommonCode makCode = (CommonCode) CommonUtils.getObject(mak_name);
//			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "makReference.key.id", makCode);
//		}
//
//		if (!StringUtils.isNull(detail_name)) {
//			CommonCode detailCode = (CommonCode) CommonUtils.getObject(detail_name);
//			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "detailReference.key.id", detailCode);
//		}
//
//		if (!StringUtils.isNull(template)) {
//			Template t = (Template) CommonUtils.getObject(template);
//			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "templateReference.key.id", t);
//		}
		//=============================================================

//		JSONArray list = new JSONArray();
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			Meeting meeting = (Meeting) obj[0];
//			JSONObject node = new JSONObject();
//			node.put("oid", meeting.getPersistInfo().getObjectIdentifier().getStringValue());
//			node.put("name", meeting.getName());
//			QueryResult group = PersistenceHelper.manager.navigate(meeting, "project", MeetingProjectLink.class, false);
//			int isNode = 1;
//			JSONArray children = new JSONArray();
//			while (group.hasMoreElements()) {
//				MeetingProjectLink link = (MeetingProjectLink) group.nextElement();
//				MeetingDTO dto = new MeetingDTO(link);
//				if (isNode == 1) {
//					node.put("poid", dto.getPoid());
//					node.put("loid", link.getPersistInfo().getObjectIdentifier().getStringValue());
//					node.put("projectType_name", dto.getProjectType_name());
//					node.put("customer_name", dto.getCustomer_name());
//					node.put("install_name", dto.getInstall_name());
//					node.put("mak_name", dto.getMak_name());
//					node.put("detail_name", dto.getDetail_name());
//					node.put("kekNumber", dto.getKekNumber());
//					node.put("keNumber", dto.getKeNumber());
//					node.put("userId", dto.getUserId());
//					node.put("description", dto.getDescription());
//					node.put("state", dto.getState());
//					node.put("model", dto.getModel());
//					node.put("pdate_txt", dto.getPdate_txt());
//					node.put("creator", dto.getCreator());
//					node.put("creatorId", meeting.getOwnership().getOwner().getName());
//					node.put("createdDate_txt", dto.getCreatedDate_txt());
//				} else {
//					JSONObject data = new JSONObject();
//					data.put("name", dto.getName());
//					data.put("oid", dto.getOid());
//					data.put("loid", link.getPersistInfo().getObjectIdentifier().getStringValue());
//					data.put("poid", dto.getPoid());
//					data.put("projectType_name", dto.getProjectType_name());
//					data.put("customer_name", dto.getCustomer_name());
//					data.put("install_name", dto.getInstall_name());
//					data.put("mak_name", dto.getMak_name());
//					data.put("detail_name", dto.getDetail_name());
//					data.put("kekNumber", dto.getKekNumber());
//					data.put("keNumber", dto.getKeNumber());
//					data.put("userId", dto.getUserId());
//					data.put("description", dto.getDescription());
//					data.put("state", dto.getState());
//					data.put("model", dto.getModel());
//					data.put("pdate_txt", dto.getPdate_txt());
//					data.put("creator", dto.getCreator());
//					data.put("creatorId", meeting.getOwnership().getOwner().getName());
//					data.put("createdDate_txt", dto.getCreatedDate_txt());
//					children.add(data);
//				}
//				isNode++;
//			}
//			node.put("children", children);
//			list.add(node);
//		}
		QuerySpecUtils.toOrderBy(query, idx, Meeting.class, Meeting.CREATE_TIMESTAMP, true);
		System.out.println("회의록 쿼리 확인 좀 해보장아ㅏ아아아아아아아앙 :"+query+"아아아아아아");
		pager = new PageQueryUtils(params, query);
		result = pager.find();
		while (result.hasMoreElements()) {int i=0;i++;
			Object[] obj = (Object[]) result.nextElement();
			System.out.println(obj.length+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+i+"@"+result);
			MeetingProjectLink meeting = (MeetingProjectLink) obj[0];
			MeetingDTO dto = new MeetingDTO(meeting);
			list.add(dto);
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

	public JSONArray jsonAuiProject(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		Meeting meeting = (Meeting) CommonUtils.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Meeting.class, true);
		int idx_link = query.appendClassList(MeetingProjectLink.class, true);
		QuerySpecUtils.toInnerJoin(query, Meeting.class, MeetingProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, MeetingProjectLink.class, "roleAObjectRef.key.id", meeting);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MeetingProjectLink link = (MeetingProjectLink) obj[1];
			Project project = link.getProject();
			Map<String, String> map = new HashMap<>();
			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("projectType_name", project.getProjectType() != null ? project.getProjectType().getName() : "");
			map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
			map.put("install_name", project.getInstall() != null ? project.getInstall().getName() : "");
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
