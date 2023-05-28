package e3ps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;

import e3ps.common.mail.MailUtils;
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

		MailUtils.test();

		System.exit(0);
	}
}
