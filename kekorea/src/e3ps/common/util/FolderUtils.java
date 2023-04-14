package e3ps.common.util;

import java.util.Enumeration;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.clients.folder.FolderTaskLogic;
import wt.folder.Folder;

public class FolderUtils {

	private FolderUtils() {

	}

	/**
	 * 폴더 구조 트리로 가져오기
	 */
	public static JSONArray loadFolderTree(Map<String, String> params) throws Exception {
		String location = params.get("location");
		String container = params.get("container");
		Folder root = null;
		if ("product".equals(container)) {
			root = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
		} else if ("library".equals(container)) {
			root = FolderTaskLogic.getFolder(location, CommonUtils.getLibrary());
		}

		JSONArray list = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("location", root.getFolderPath());
		rootNode.put("name", root.getName());

		JSONArray children = new JSONArray();
		Enumeration result = FolderTaskLogic.getSubFolders(root);
		while (result.hasMoreElements()) {
			Folder child = (Folder) result.nextElement();
			JSONObject node = new JSONObject();
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("location", child.getFolderPath());
			node.put("name", child.getName());
			loadFolderTree(child, node);
			children.add(node);
		}
		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}

	/**
	 * 폴더 구조 트리로 가져오기 재귀함수
	 */
	private static void loadFolderTree(Folder parent, JSONObject parentNode) throws Exception {
		JSONArray children = new JSONArray();
		Enumeration result = FolderTaskLogic.getSubFolders(parent);
		while (result.hasMoreElements()) {
			Folder child = (Folder) result.nextElement();
			JSONObject node = new JSONObject();
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("location", child.getFolderPath());
			node.put("name", child.getName());
			loadFolderTree(child, node);
			children.add(node);
		}
		parentNode.put("children", children);
	}
}
