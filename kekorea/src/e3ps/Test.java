package e3ps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;

import e3ps.common.util.CommonUtils;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.QueryResult;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.util.WTProperties;
import wt.viewmarkup.DerivedImage;
import wt.viewmarkup.ViewMarkUpHelper;
import wt.viewmarkup.Viewable;
import wt.viewmarkup.WTMarkUp;

public class Test {

	public static void main(String[] args) throws Exception {

		String oid = "wt.viewmarkup.WTMarkUp:1495121";
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

		System.exit(0);
	}
}
