package e3ps.common.util;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ptc.wvs.client.beans.PublishConfigSpec;
import com.ptc.wvs.common.ui.Publisher;
import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;
import com.ptc.wvs.server.util.Util;

import e3ps.part.service.PartHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
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

			System.out.println("ob=" + objref);

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

	public static String[] getMarkUp(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		WTMarkUp markUp = (WTMarkUp) rf.getReference(oid).getObject();
		return getMarkUp(markUp);
	}

	public static String[] getMarkUp(WTMarkUp markUp) {
		String[] data = new String[4];
		String oid = markUp.getPersistInfo().getObjectIdentifier().getStringValue();
		if (markUp != null) {

			String thumnail = FileHelper.getViewContentURLForType(markUp, ContentRoleType.THUMBNAIL);
			data[0] = thumnail;
			data[1] = oid;
		}
		return data;
	}

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
				thumnail_mini = "/Windchill/jsp/images/productview_publish_24.png";
			}

			Representation representation = PublishUtils.getRepresentation(thum, true, null, false);
			String copyTag = "";
			if (thum != null) {
				copyTag = PublishUtils.getRefFromObject(representation);
			}

			data[0] = thumnail;
			data[1] = thumnail_mini;
			data[2] = copyTag;
			data[3] = oid;
		}

		return data;
	}

	public static boolean doPublisher(Map<String, Object> param) throws Exception {
		Publisher publisher = new Publisher();
		PublishConfigSpec pcs = new PublishConfigSpec();

		boolean viewableLink = true;
		boolean forceRepublish = false;
		String publishoid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		Persistable per = (Persistable) rf.getReference(publishoid).getObject();
		if (per instanceof WTPart) {
			WTPart part = (WTPart) per;
			EPMDocument e = PartHelper.manager.getEPMDocument(part);
			if (e == null) {

			}
		}

		pcs.getEPMActiveNavigationCriteria(false, null);

		boolean isPublished = publisher.doPublish(viewableLink, forceRepublish, publishoid,
				pcs.getEPMActiveNavigationCriteria(false, null), pcs.getPartActiveNavigationCriteria(null), true, null,
				null, pcs.getStructureType(), null, 1);

		return isPublished;
	}

	public static boolean deletePublisher(Map<String, Object> param) throws Exception {
		boolean isSuccess = false;
		ReferenceFactory rf = new ReferenceFactory();
		String oid = (String) param.get("oid");
		ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();

		Representation representation = PublishUtils.getRepresentation(holder);

		if (representation != null) {
			PersistenceHelper.manager.delete(representation);
			isSuccess = true;
		}
		return isSuccess;
	}

	public static boolean doPublisherMulti(Map<String, Object> param) throws Exception {
		List<String> list = (List<String>) param.get("list");
		Publisher publisher = new Publisher();
		PublishConfigSpec pcs = new PublishConfigSpec();
		boolean isPublished = false;
		ReferenceFactory rf = new ReferenceFactory();
		for (String publishoid : list) {

			Persistable per = (Persistable) rf.getReference(publishoid).getObject();
			if (per instanceof WTPart) {
				WTPart part = (WTPart) per;
				EPMDocument e = PartHelper.manager.getEPMDocument(part);
				if (e == null) {
//					continue;
				}
			}

			boolean viewableLink = true;
			boolean forceRepublish = false;

			pcs.getEPMActiveNavigationCriteria(false, null);

			isPublished = publisher.doPublish(viewableLink, forceRepublish, publishoid,
					pcs.getEPMActiveNavigationCriteria(false, null), pcs.getPartActiveNavigationCriteria(null), true,
					null, null, pcs.getStructureType(), null, 1);
		}
		System.out.println(isPublished);
		return isPublished;
	}
}
