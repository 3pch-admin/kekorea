package e3ps.common.aspose;

import com.aspose.pdf.License;

public class AsposeUtils {

	private static final String licPath = "D:\\ptc\\lic\\Aspose.Pdf.Java.lic";

	private AsposeUtils() {

	}

	public static void setAsposeLic() throws Exception {
		License license = new License();
		license.setLicense(licPath);
	}
}
