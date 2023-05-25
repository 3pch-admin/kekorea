package e3ps.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.ptc.wvs.client.beans.PublishConfigSpec;
import com.ptc.wvs.common.ui.Publisher;
import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;
import com.ptc.wvs.server.util.Util;

import net.sf.json.JSONArray;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.util.WTProperties;
import wt.viewmarkup.ViewMarkUpHelper;
import wt.viewmarkup.Viewable;
import wt.viewmarkup.WTMarkUp;

public class ThumnailUtils {

	private ThumnailUtils() {

	}

	public static String creoViewURL(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();
		return creoViewURL(holder);
	}

	public static Representation getRepresentation(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();
		return getRepresentation(holder);
	}

	public static Representation getRepresentation(ContentHolder holder) throws Exception {
		Representable representable = PublishUtils.findRepresentable(holder);

		Representation representation = PublishUtils.getRepresentation(representable, true, null, false);

		return representation;
	}

	public static Vector<WTMarkUp> getMarkUpList(String oid) throws Exception {
		Vector<WTMarkUp> list = new Vector<WTMarkUp>();

		Representation representation = getRepresentation(oid);
		if (representation != null) {
			QueryResult qr = ViewMarkUpHelper.service.getMarkUps((Viewable) representation);
			while (qr.hasMoreElements()) {
				WTMarkUp markUp = (WTMarkUp) qr.nextElement();
				list.add(markUp);
			}
		}
		return list;
	}

	public static Vector<WTMarkUp> getMarkUpList(ContentHolder holder) throws Exception {
		Vector<WTMarkUp> list = new Vector<WTMarkUp>();

		Representation representation = getRepresentation(holder);
		if (representation != null) {
			QueryResult qr = ViewMarkUpHelper.service.getMarkUps((Viewable) representation);
			while (qr.hasMoreElements()) {
				WTMarkUp markUp = (WTMarkUp) qr.nextElement();
				list.add(markUp);
			}
		}
		return list;
	}

	/**
	 * 크레오 뷰 URL
	 */
	public static String creoViewURL(ContentHolder holder) throws Exception {
		StringBuffer creoView = new StringBuffer();
		Representable representable = PublishUtils.findRepresentable(holder);

		Representation representation = PublishUtils.getRepresentation(representable, true, null, false);

		if (representation != null) {
			String codebase = WTProperties.getLocalProperties().getProperty("wt.server.codebase");
			String jsp = "/wtcore/jsp/wvs/edrview.jsp";
			String url = Util.SandR(Util.SandR(PublishUtils.getPreferedViewURL(representation, true), "%27", "%5C%27"),
					"%22", "%5C%22");
			String objref = Util.SandR(PublishUtils.getRefFromObject(representation), ":", "%3A");

			creoView.append(codebase).append(jsp);
			creoView.append("?url=").append(url);
			creoView.append("&objref=").append(objref);
		}
		return creoView.toString();
	}

	public static String getMarkUpCreoViewUrl(String oid, WTMarkUp markUp) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();
		return getMarkUpCreoViewUrl(holder, markUp);
	}

	/**
	 * 마크업 크레오 뷰 URL
	 */
	public static String getMarkUpCreoViewUrl(ContentHolder holder, WTMarkUp markUp) throws Exception {
		StringBuffer creoView = new StringBuffer();

		Representable representable = PublishUtils.findRepresentable(holder);

		Representation representation = PublishUtils.getRepresentation(representable, true, null, false);

		if (representation != null) {
			String codebase = WTProperties.getLocalProperties().getProperty("wt.server.codebase");
			String jsp = "/wtcore/jsp/wvs/edrview.jsp";
			String url = Util.SandR(Util.SandR(PublishUtils.getPreferedViewURL(representation, true), "%27", "%5C%27"),
					"%22", "%5C%22");

			String objref = PublishUtils.getRefFromObject(representation);
			String mkid = "OR:" + markUp.getPersistInfo().getObjectIdentifier().getStringValue();
			String mkname = markUp.getName();
			String doctype = "model";
			String docname = "Model";

			creoView.append(codebase).append(jsp);
			creoView.append("?url=").append(url);
			creoView.append("&objref=").append(objref);
			creoView.append("&mkid=").append(mkid);
			creoView.append("&mkname=").append(mkname);
			creoView.append("&doctype=").append(doctype);
			creoView.append("&docname=").append(docname);
		}
		return creoView.toString();
	}

	/**
	 * 뷰어 가져오기 (도면 또는 부품)
	 */
	public static String[] getThumnail(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		Persistable per = (Persistable) rf.getReference(oid).getObject();
		String[] data = new String[4];

		if (per != null) {
			Representable thum = (Representable) per;

			String thumnail = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(thum),
					ContentRoleType.THUMBNAIL);
			String thumnail_mini = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(thum),
					ContentRoleType.THUMBNAIL_SMALL);

			if (thumnail_mini == null) {
				thumnail_mini = "/Windchill/extcore/images/productview_publish_24.png";
			}

			if (thumnail == null) {
				thumnail = "/Windchill/extcore/images/productview_openin_250.png";
			}

			Representation representation = PublishUtils.getRepresentation(thum, true, null, false);
			String copyTag = "";
			if (representation != null) {
				copyTag = PublishUtils.getRefFromObject(representation);
			}

			data[0] = thumnail;
			data[1] = thumnail_mini;
			data[2] = copyTag;
			data[3] = oid;
		}

		return data;
	}

	/**
	 * 뷰어 생성
	 */
	public static boolean doPublisher(String oid) throws Exception {
		Publisher publisher = new Publisher();
		PublishConfigSpec pcs = new PublishConfigSpec();
		boolean viewableLink = true;
		boolean forceRepublish = false;
		boolean isPublished = publisher.doPublish(viewableLink, forceRepublish, oid,
				pcs.getEPMActiveNavigationCriteria(false, null), pcs.getPartActiveNavigationCriteria(null), true, null,
				null, pcs.getStructureType(), null, 1);

		return isPublished;
	}

	/**
	 * 마크업 데이터 AUI 그리드용
	 */
	public static JSONArray markUpData(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();

		ContentHolder holder = (ContentHolder) CommonUtils.getObject(oid);

		Vector<WTMarkUp> data = getMarkUpList(oid);
		for (WTMarkUp markUp : data) {
			Map<String, String> map = new HashMap<>();
			String moid = markUp.getPersistInfo().getObjectIdentifier().getStringValue();
			String thum = markUpThumnail(moid);
			String creoViewURL = getMarkUpCreoViewUrl(holder, markUp);
			map.put("creoViewURL", creoViewURL);
			map.put("name", markUp.getName());
			map.put("thumnail", thum);
			map.put("creator", markUp.getOwnership().getOwner().getFullName());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(markUp.getCreateTimestamp(), 16));
			map.put("description", markUp.getDescription());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 마크업 썸네일
	 */
	public static String markUpThumnail(String oid) throws Exception {
		WTMarkUp markUp = (WTMarkUp) CommonUtils.getObject(oid);
		QueryResult result = ContentHelper.service.getContentsByRole(markUp, ContentRoleType.THUMBNAIL);
		String hostName = WTProperties.getLocalProperties().getProperty("wt.rmi.server.hostname");
		StringBuffer sb = new StringBuffer();
		if (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			sb.append("http://");
			sb.append(hostName);
			sb.append("/Windchill/servlet/WindchillAuthGW/wt.content.ContentHttp/viewContent/");
			sb.append(data.getFileName());
			sb.append("?u8&HttpOperationItem=");
			sb.append(data.getPersistInfo().getObjectIdentifier().getStringValue());
			sb.append("&ofn=" + data.getFileName() + "&ContentHolder=");
			sb.append(oid);
			sb.append("&forceDownload=true");
		}
		return sb.toString();
	}
}
