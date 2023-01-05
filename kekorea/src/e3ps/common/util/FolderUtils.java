package e3ps.common.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.clients.folder.FolderTaskLogic;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.folder.SubFolderIdentity;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class FolderUtils implements MessageHelper {

	private FolderUtils() {

	}

	public static JSONArray getFolderTree(Map<String, Object> param) throws Exception {
		String root = (String) param.get("root");
		Folder rootFolder = FolderTaskLogic.getFolder(root, CommonUtils.getContainer());
		JSONArray jsonArray = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("id", rootFolder.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("text", rootFolder.getName());
		rootNode.put("type", "root");
		getSubFolder(rootFolder, rootNode);
		jsonArray.add(rootNode);
		return jsonArray;
	}

	private static void getSubFolder(Folder root, JSONObject rootNode) throws Exception {
		Enumeration children = FolderTaskLogic.getSubFolders(root);
		JSONArray jsonChildren = new JSONArray();
		while (children.hasMoreElements()) {
			Folder child = (Folder) children.nextElement();

			JSONObject node = new JSONObject();
			node.put("type", "childrens");
			node.put("id", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("title", child.getName());
			node.put("expanded", false);
			node.put("location", child.getFolderPath());
			node.put("folder", true);
			getSubFolder(child, node);

			jsonChildren.add(node);
		}
		rootNode.put("children", jsonChildren);
	}

	public static JSONArray openFolder(Map<String, Object> param) throws Exception {
		String root = (String) param.get("root");
		String contenxt = (String) param.get("context");
		Folder rootFolder = null;
		if ("LIBRARY".equals(contenxt)) {
			rootFolder = FolderTaskLogic.getFolder(root, CommonUtils.getLibrary());
		} else if ("PRODUCT".equals(contenxt)) {
			rootFolder = FolderTaskLogic.getFolder(root, CommonUtils.getContainer());
		} else if ("EPLAN".equals(contenxt)) {
			rootFolder = FolderTaskLogic.getFolder(root, CommonUtils.getEPLAN());
		}

		JSONArray jsonArray = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("type", "root");
		rootNode.put("id", rootFolder.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("title", rootFolder.getName());
		rootNode.put("location", rootFolder.getFolderPath());
		rootNode.put("expanded", true);
		rootNode.put("folder", true);
		getSubFolder(rootFolder, rootNode);
		jsonArray.add(rootNode);
		return jsonArray;
	}

	public static ArrayList<Folder> getSubFolders(Folder root, ArrayList<Folder> folders) throws Exception {
		QueryResult result = FolderHelper.service.findSubFolders(root);
		while (result.hasMoreElements()) {
			SubFolder sub = (SubFolder) result.nextElement();
			folders.add(sub);
			getSubFolders(sub, folders);
		}
		return folders;
	}

	public static Map<String, Object> deleteFolder(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		Folder folder = null;
		ReferenceFactory rf = new ReferenceFactory();
		try {
			folder = (Folder) rf.getReference(oid).getObject();

			boolean isExistData = FolderUtils.isExistData(folder);
			// 존재
			if (isExistData) {
				map.put("result", "FAIL");
				map.put("reload", false);
				map.put("msg", "삭제 하려는 폴더에 데이터가 존재 합니다.");
				return map;
			}
			PersistenceHelper.manager.delete(folder);

			map.put("result", SUCCESS);
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "폴더 삭제 중 에러가 발생 하였습니다.\n관리자에게 문의하세요.");
			e.printStackTrace();
		}
		return map;
	}

	private static boolean isExistData(Folder folder) {
		boolean isExistData = false;
		try {
			QueryResult result = FolderHelper.service.findFolderContents(folder);
			if (result.hasMoreElements()) {
				isExistData = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isExistData;
	}

	public static Map<String, Object> createFolderAction(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		String poid = (String) param.get("poid");
		String text = (String) param.get("text");
		String context = (String) param.get("context");
		Folder pfolder = null;
		Folder f = null;
		ReferenceFactory rf = new ReferenceFactory();
		try {

			pfolder = (Folder) rf.getReference(poid).getObject();

			boolean isExist = FolderUtils.isEqualLevel(pfolder, text);
			// 존재
			if (isExist) {
				map.put("result", "FAIL");
				map.put("reload", false);
				map.put("msg", "같은 레벨에 동일한 폴더가 존재합니다.");
				return map;
			}

			if (context.equalsIgnoreCase("PRODUCT")) {
				f = FolderHelper.service.createSubFolder(pfolder.getFolderPath() + "/" + text,
						CommonUtils.getContainer());
			} else if (context.equalsIgnoreCase("LIBRARY")) {
				f = FolderHelper.service.createSubFolder(pfolder.getFolderPath() + "/" + text,
						CommonUtils.getLibrary());
			} else if (context.equalsIgnoreCase("EPLAN")) {
				f = FolderHelper.service.createSubFolder(pfolder.getFolderPath() + "/" + text,
						CommonUtils.getEPLAN());
			}
			map.put("foid", f.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("result", SUCCESS);
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "폴더 생성 중 에러가 발생 하였습니다.\n관리자에게 문의하세요.");
			e.printStackTrace();
		}
		return map;
	}

	public static Map<String, Object> renameFolderAction(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String text = (String) param.get("text");
		String poid = (String) param.get("poid");
		SubFolder f = null;
		Folder pfolder = null;
		ReferenceFactory rf = new ReferenceFactory();
		try {

			f = (SubFolder) rf.getReference(oid).getObject();

			if (!StringUtils.isNull(poid)) {
				pfolder = (Folder) rf.getReference(poid).getObject();
				boolean isExist = FolderUtils.isEqualLevel(pfolder, text);
				// 존재
				if (isExist) {
					map.put("result", "FAIL");
					map.put("reload", false);
					map.put("msg", "같은 레벨에 동일한 폴더가 존재합니다.");
					return map;
				}
			}

			SubFolderIdentity identity = (SubFolderIdentity) f.getIdentificationObject();
			identity.setName(text);
			IdentityHelper.service.changeIdentity(f, identity);

			map.put("result", SUCCESS);
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "폴더 수정 중 에러가 발생 하였습니다.\n관리자에게 문의하세요.");
			e.printStackTrace();
		}
		return map;
	}

	private static boolean isEqualLevel(Folder pfolder, String text) {
		boolean isExist = false;

		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(SubFolder.class, true);

			SearchCondition sc = new SearchCondition(SubFolder.class, "folderingInfo.parentFolder.key.id", "=",
					pfolder.getPersistInfo().getObjectIdentifier().getId());

			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(SubFolder.class, SubFolder.NAME, "=", text);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				isExist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}
}
