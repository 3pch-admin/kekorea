package e3ps.controller;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import e3ps.admin.PasswordSetting;
import e3ps.admin.service.AdminHelper;
import e3ps.approval.ApprovalLine;
import e3ps.approval.ApprovalMaster;
import e3ps.approval.Notice;
import e3ps.approval.beans.ApprovalMasterViewData;
import e3ps.approval.service.ApprovalHelper;
import e3ps.approval.service.NoticeHelper;
import e3ps.common.excel.ExcelHelper;
import e3ps.common.mail.MailUtils;
import e3ps.common.util.ColumnParseUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.FolderUtils;
import e3ps.common.util.StringUtils;
import e3ps.common.util.ThumnailUtils;
import e3ps.doc.service.DocumentHelper;
import e3ps.epm.service.EpmHelper;
import e3ps.org.People;
import e3ps.org.service.OrgHelper;
import e3ps.part.service.PartHelper;
import e3ps.project.service.ProjectHelper;
import net.sf.json.JSONArray;
import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.CollationKeyFactory;
import wt.util.SortedEnumeration;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.viewmarkup.WTMarkUp;

@Controller
public class CommonController extends BaseController {

	private static final Logger logger = LogR.getLogger(CommonController.class.getName());

	@RequestMapping(value = "/appendData")
	@ResponseBody
	public Map<String, Object> appendData(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		String sessionid = (String) params.get("sessionid");
		int start = (int) params.get("start");
		int end = (int) params.get("end");
		try {
			logger.info("Call CommonController appendData Method !!");
			PagingQueryResult qr = PagingSessionHelper.fetchPagingSession(start, end, Long.parseLong(sessionid));
			ArrayList list = ColumnParseUtils.parse(qr);
			logger.info("=" + list.size());
			result.put("list", list);
			result.put("result", SUCCESS);
			logger.info(result);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description("주석세트 페이지")
	@RequestMapping(value = "/common/alertPassword")
	public ModelAndView alertPassword(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/common/alertPassword");
		return model;
	}

	@Description("주석세트 페이지")
	@RequestMapping(value = "/common/viewMarkup")
	public ModelAndView viewMarkup(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		String popup = (String) param.get("popup");
		boolean isPopup = Boolean.parseBoolean(popup);
		ContentHolder holder = null;
		ReferenceFactory rf = new ReferenceFactory();
		Vector<WTMarkUp> list = new Vector<WTMarkUp>();
		try {
			holder = (ContentHolder) rf.getReference(oid).getObject();
			list = ThumnailUtils.getMarkUpList(holder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("list", list);
		if (isPopup) {
			model.setViewName("popup:/common/viewMarkup");
		} else {
			model.setViewName("default:/common/viewMarkup");
		}
		return model;
	}

	@Description("CreoView URL")
	@RequestMapping(value = "/common/getCreoViewURL")
	@ResponseBody
	public Map<String, Object> getCreoViewURL(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		String oid = (String) param.get("oid");
		try {

			ReferenceFactory rf = new ReferenceFactory();
			ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();
			String creoView = ThumnailUtils.creoViewURL(holder);

			System.out.println("===" + creoView);

			map = new HashMap<String, Object>();
			if (!StringUtils.isNull(creoView)) {
				map.put("creoView", creoView);
				map.put("result", SUCCESS);
			} else {
				map.put("msg", "CreoView 파일이 생성 되지 않은 데이터입니다.");
				map.put("result", FAIL);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("새 이름으로 저장")
	@RequestMapping(value = "/common/saveAsObjectAction")
	@ResponseBody
	public Map<String, Object> saveAsObjectAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		List<String> oidArray = (List<String>) param.get("list");

		System.out.println("o=" + oidArray);
		try {
			if (oidArray.toString().contains("wt.part.WTPart")) {
				System.out.println("시작...");
				map = PartHelper.service.saveAsPartAction(param);
			} else if (oidArray.contains("wt.epm.EPMDocument")) {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("메인 페이지")
	@RequestMapping(value = "/common/main")
	public ModelAndView main(HttpServletRequest request) throws Exception {

		PasswordSetting ps = AdminHelper.manager.getPasswordSetting();
		Calendar ca3 = Calendar.getInstance();
		Timestamp tt = DateUtils.getCurrentTimestamp();
		ca3.setTimeInMillis(tt.getTime());
		ca3.add(Calendar.DAY_OF_MONTH, -5);

		int day = ca3.getTime().getDate();
		int month = ca3.getTime().getMonth() + 1;

		boolean isSix = false;
		boolean isThree = false;

		if (6 == ps.getReset()) {

			if (month == 6 || month == 12) {
				if (day == 25) {
					isSix = true;
				}
			}

		} else if (3 == ps.getReset()) {
			if (month == 3 || month == 6 || month == 9 || month == 12) {
				if (day == 25) {
					isThree = true;
				}
			}
		}

		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		boolean isKEK = false;
		if ("kek1".equals(sessionUser.getName())) {
			isKEK = true;
		}

//		Map<String, Object> param = new HashMap<String, Object>();
		ModelAndView model = new ModelAndView();
		PagingQueryResult nResult = NoticeHelper.manager.getMainNoticeList();
		PagingQueryResult pResult = ProjectHelper.manager.getMainMyProjectList();
//		Map<String, Object> pList = ProjectHelper.manager.findMyProject(param);
		PagingQueryResult aResult = ApprovalHelper.manager.findAgreeAndApprovalList();

		// model.addObject("gap", gap);
//		model.addObject("aList", (ArrayList<ApprovalColumnData>) aList.get("list"));
//		model.addObject("pList", (ArrayList<ProjectColumnData>) pList.get("list"));
//		model.addObject("list", list);
		model.addObject("nResult", nResult);
		model.addObject("pResult", pResult);
		model.addObject("aResult", aResult);
		model.addObject("isSix", isSix);
		model.addObject("isThree", isThree);
		if (!isKEK) {
			model.setViewName("default:/common/main");
		} else {
			model.setViewName("default:/epm/listViewer");
		}
		return model;
	}

	@Description("멀티 뷰어 생성")
	@RequestMapping(value = "/common/doPublisherMulti")
	@ResponseBody
	public Map<String, Object> doPublisherMulti(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			boolean isPublished = ThumnailUtils.doPublisherMulti(param);
			if (!isPublished) {
				map.put("result", SUCCESS);
				map.put("msg", "뷰어 파일이 생성 되어집니다.\n잠시 후에 확인 해주세요.");
			} else {
				map.put("result", FAIL);
				map.put("msg", "뷰어 파일 생성 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("뷰어 생성")
	@RequestMapping(value = "/common/doPublisher")
	@ResponseBody
	public Map<String, Object> doPublisher(@RequestParam Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		System.out.println("para2qm=" + param);
		try {
			boolean isPublished = ThumnailUtils.doPublisher(param);
			if (!isPublished) {
				map.put("result", SUCCESS);
				map.put("msg", "뷰어 파일이 생성 되어집니다.\n잠시 후에 확인 해주세요.");
			} else {
				map.put("result", FAIL);
				map.put("msg", "뷰어 파일 생성 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("뷰어 삭제")
	@RequestMapping(value = "/common/deletePublisher")
	@ResponseBody
	public Map<String, Object> deletePublisher(@RequestParam Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			boolean isSuccess = ThumnailUtils.deletePublisher(param);
			if (isSuccess) {
				map.put("result", SUCCESS);
				map.put("msg", "뷰어 파일이 삭제 되어집니다.\n잠시 후에 확인 해주세요.");
			} else {
				map.put("result", FAIL);
				map.put("msg", "뷰어 파일 생성 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("메뉴얼 페이지")
	@RequestMapping(value = "/common/manual")
	public ModelAndView manual(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String path = WTProperties.getLocalProperties().getProperty("wt.home");
		path = path += File.separator + "codebase" + File.separator + "jsp" + File.separator + "common" + File.separator
				+ "manual";
		Vector<File> list = ContentUtils.getFileLists(path, param);
		model.addObject("list", list);
		model.setViewName("default:/common/manual");
		return model;
	}

	@Description("셋업 파일 페이지")
	@RequestMapping(value = "/common/setupFiles")
	public ModelAndView setupFiles(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String path = WTProperties.getLocalProperties().getProperty("wt.home");
		path = path += File.separator + "codebase" + File.separator + "jsp" + File.separator + "common" + File.separator
				+ "setupFiles";
		Vector<File> list = ContentUtils.getFileLists(path, param);
		model.addObject("list", list);
		model.setViewName("default:/common/setupFiles");
		return model;
	}

	// end

	@Description("상태값 변경")
	@RequestMapping(value = "/common/setStateObjAction")
	@ResponseBody
	public Map<String, Object> setStateObjAction(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = CommonUtils.setStateObjAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("문서 삭제")
	@RequestMapping(value = "/common/deleteObject")
	@ResponseBody
	public Map<String, Object> deleteObject(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = null;
		try {
			String items = (String) param.get("items");
			String[] oids = items.split(",");

			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < oids.length; i++) {
				String oid = oids[i];
				list.add(oid);
			}

			param.put("list", list);

			if (oids[0].contains("e3ps.admin.Code")) {
				map = AdminHelper.service.deleteCodeAction(param);
			} else if (oids[0].contains("e3ps.admin.LoginHistory")) {
				map = AdminHelper.service.deteleLoginHistory(param);
			} else if (oids[0].contains("wt.part.WTPart")) {
				map = PartHelper.service.deletePartAction(param);
			} else if (oids[0].contains("wt.epm.EPMDocument")) {
			} else if (oids[0].contains("wt.doc.WTDocument")) {
				map = DocumentHelper.service.deleteDocumentAction(param);
			} else if (oids[0].contains("e3ps.approval.Notice")) {
				map = NoticeHelper.service.deleteNoticeAction(param);
			} else if (oids[0].contains("e3ps.echange.ECN")) {
				// map = ECNHelper.service.
			} else if (oids[0].contains("e3ps.echange.STN")) {

			} else if (oids[0].contains("e3ps.approval.ApprovalLine")) {
				map = ApprovalHelper.service.deleteLines(param);
			} else if (oids[0].contains("e3ps.approval.ApprovalMaster")) {
				map = ApprovalHelper.service.deleteLines(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("새 이름으로 저장 페이지")
	@RequestMapping(value = "/common/saveAsObject")
	public ModelAndView saveAsObject(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String items = (String) param.get("items");

		String[] oids = items.split(",");
		ArrayList<RevisionControlled> list = new ArrayList<RevisionControlled>();
		ReferenceFactory rf = new ReferenceFactory();
		for (int i = 0; i < oids.length; i++) {
			RevisionControlled rc = (RevisionControlled) rf.getReference(oids[i]).getObject();

			if (rc instanceof WTPart) {
				WTPart part = (WTPart) rc;
				String type = part.getPartType().toString();
				if (!type.equals("separable")) {
					continue;
				}
			} else if (rc instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) rc;
				String type = epm.getDocType().toString();
				if (!type.equals("separable")) {
					continue;
				}
			}
			list.add(rc);
		}

		model.addObject("list", list);
		model.setViewName("popup:/common/saveAsObject");
		return model;
	}

	@Description("폴더 가져오기")
	@RequestMapping(value = "/common/getFolder")
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
	@RequestMapping(value = "/common/openFolder")
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

	@Description("개인 테이블 컬럼 사이즈 저장")
	@RequestMapping(value = "/common/saveUserTableStyle")
	@ResponseBody
	public Map<String, Object> saveUserTableStyle(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.service.saveUserTableStyle(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("개인 테이블 리스트 사이즈 저장")
	@RequestMapping(value = "/common/saveUserPaging")
	@ResponseBody
	public Map<String, Object> saveUserPaging(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.service.saveUserPaging(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("개인 테이블 컬럼 저장")
	@RequestMapping(value = "/common/saveUserTableSet")
	@ResponseBody
	public Map<String, Object> saveUserTableSet(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.service.saveUserTableSet(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("개인 테이블 컬럼 순서")
	@RequestMapping(value = "/common/saveUserTableIndexs")
	@ResponseBody
	public Map<String, Object> saveUserTableIndexs(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.service.saveUserTableIndexs(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/common/qna")
	public ModelAndView qna(@RequestParam Map<String, Object> param) {
		ModelAndView model = new ModelAndView();
		model.setViewName("default:/common/qna");
		return model;
	}

	@RequestMapping(value = "/common/errorPage")
	public ModelAndView errorPage(@RequestParam Map<String, Object> param) {
		ModelAndView model = new ModelAndView();
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));

		if (isPopup) {
			model.setViewName("popup:/common/errorPage");
		} else {
			model.setViewName("default:/common/errorPage");
		}
		return model;
	}

	@RequestMapping(value = "/common/infoVersion")
	public ModelAndView infoVersion(@RequestParam Map<String, Object> param) {
		ModelAndView model = new ModelAndView();
		String sort = (String) param.get("sort");
		if (StringUtils.isNull(sort)) {
			sort = "2";
		}
		String popup = (String) param.get("popup");
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		QueryResult result = null;
		Persistable per = null;
		SortedEnumeration se = null;
		try {
			per = (Persistable) rf.getReference(oid).getObject();

			if (per instanceof RevisionControlled) {
				RevisionControlled rc = (RevisionControlled) per;
				result = VersionControlHelper.service.allIterationsOf(rc.getMaster());
				CollationKeyFactory factory = new CollationKeyFactory();
				// if == 2 desending
				// if != 2 asceding

				se = new SortedEnumeration(result.getEnumeration(), factory, Integer.parseInt(sort));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("sort", sort);
		model.addObject("se", se);
		model.addObject("popup", popup);
		model.setViewName("popup:/common/infoVersion");
		return model;
	}

	@Description("엑셀출력")
	@RequestMapping(value = "/common/exportExcel")
	@ResponseBody
	public Map<String, Object> exportExcel(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		String url = (String) param.get("url");
		try {

			File excel = ExcelHelper.manager.printExcel(param);

			map.put("result", SUCCESS);
			map.put("url", "/Windchill/jsp/temp/pdm/excelForm/" + excel.getName());
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "엑셀출력에 실패하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", url);
			map.put("result", FAIL);
		}
		return map;
	}

	@Description("개정")
	@RequestMapping(value = "/common/reviseObject")
	@ResponseBody
	public Map<String, Object> reviseObject(@RequestParam Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		Versioned versioned = null;
		String oid = (String) param.get("oid");
		String preUrl = (String) param.get("preUrl");
		String url = null;
		try {
			if (StringUtils.isNull(preUrl) || "undefined".equals(preUrl)) {
				url = CommonUtils.getURL(oid);
			} else {
				url = preUrl;
			}

			versioned = (Versioned) rf.getReference(oid).getObject();
			versioned = (Versioned) VersionControlHelper.service.newVersion(versioned);
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			VersionControlHelper.setNote(versioned, "사용자 " + user.getFullName() + "에 의해서 개정됨");
			versioned = (Versioned) PersistenceHelper.manager.save(versioned);

			map.put("msg", "개정 되었습니다.");
			map.put("url", url);
			map.put("result", SUCCESS);
		} catch (Exception e) {
			map.put("msg", "개정에 실패하였습니다.\n시스템 관리자에게 문의하세요");
//			map.put("url", ERROR_PAGE_URL);
			map.put("result", FAIL);
			e.printStackTrace();
		}
		return map;
	}

	@Description("정보 보기 페이지 리다이렉트")
	@RequestMapping(value = "/common/viewObject")
	public ModelAndView viewObject(@RequestParam Map<String, Object> param) {
		ModelAndView model = new ModelAndView();
		String url = "";
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		Persistable per = null;

		try {
			per = (Persistable) rf.getReference(oid).getObject();

			// 객체별 View...
			if (per instanceof WTPart) {
				url = "/Windchill/plm/part/viewPart?oid=" + oid + "&popup=true";
			} else if (per instanceof EPMDocument) {
				url = "/Windchill/plm/epm/viewEpm?oid=" + oid + "&popup=true";
			} else if (per instanceof WTDocument) {
				url = "/Windchill/plm/document/viewDocument?oid=" + oid + "&popup=true";
			} else if (per instanceof Notice) {
				url = "/Windchill/plm/approval/viewNotice?oid=" + oid + "&popup=true";
			} else if (per instanceof ApprovalLine) {
				url = "/Windchill/plm/approval/infoApproval?oid=" + oid + "&popup=true";
			} else if (per instanceof People) {
				url = "/Windchill/plm/org/viewUser?oid=" + oid + "&popup=true";
			} else if (per instanceof ApprovalMaster) {
				url = "/Windchill/plm/approval/infoMasterApproval?oid=" + oid + "&popup=true";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		RedirectView rv = new RedirectView(url);
		model.setView(rv);
		return model;
	}

	@Description("메일 전송")
	@RequestMapping(value = "/common/sendMailAction")
	@ResponseBody
	public Map<String, Object> createDocumentAction(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = null;
		try {
			map = MailUtils.sendCommonMail(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("메일 보내기 페이지")
	@RequestMapping(value = "/common/sendMail")
	public ModelAndView sendMail(@RequestParam Map<String, Object> param) {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		WTUser user = null;
		ApprovalMaster master = null;
		ApprovalMasterViewData data = null;
		ReferenceFactory rf = new ReferenceFactory();
		try {
			master = (ApprovalMaster) rf.getReference(oid).getObject();
			data = new ApprovalMasterViewData(master);
			user = (WTUser) SessionHelper.manager.getPrincipal();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("data", data);
		model.addObject("user", user);
		model.setViewName("popup:/common/sendMail");
		return model;
	}

	@Description("뷰어 생성")
	@RequestMapping(value = "/common/doPublishView")
	@ResponseBody
	public Map<String, Object> doPublishView(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = null;
		try {
			map = EpmHelper.service.doPublishView(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("뷰어 삭제")
	@RequestMapping(value = "/common/deletePublishView")
	@ResponseBody
	public Map<String, Object> deletePublishView(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = null;
		try {
			map = EpmHelper.service.deletePublishView(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("폴더 트리")
	@RequestMapping(value = "/common/getFolderTree")
	@ResponseBody
	public JSONArray getFolderTree(@RequestParam Map<String, Object> param) {
		JSONArray node = null;
		try {
			node = FolderUtils.getFolderTree(param);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return node;
	}

	@Description("폴더 생성")
	@RequestMapping(value = "/common/createFolderAction")
	@ResponseBody
	public Map<String, Object> createFolderAction(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = null;
		try {
			map = FolderUtils.createFolderAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("폴더 수정")
	@RequestMapping(value = "/common/renameFolderAction")
	@ResponseBody
	public Map<String, Object> renameFolderAction(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = null;
		try {
			map = FolderUtils.renameFolderAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("폴더 삭제")
	@RequestMapping(value = "/common/deleteFolderAction")
	@ResponseBody
	public Map<String, Object> deleteFolderAction(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = null;
		try {
			map = FolderUtils.deleteFolder(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
