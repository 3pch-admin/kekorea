package e3ps.common.content.service;

import wt.services.ServiceFactory;

public class CommonContentHelper {

	public static final CommonContentHelper manager = new CommonContentHelper();
	public static final CommonContentService service = ServiceFactory.getService(CommonContentService.class);

	public String getIcon(String ext) throws Exception {
		String path = "/Windchill/extcore/images/fileicon/";
		String icon = "file_generic.gif";

		if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
			icon = "file_jpg.gif";
		} else if (ext.equalsIgnoreCase("gif")) {
			icon = "file_gif.gif";
		} else if (ext.equalsIgnoreCase("png")) {
			icon = "file_png.gif";
		} else if (ext.equalsIgnoreCase("tiff")) {
			icon = "file_tiff.gif";
		} else if (ext.equalsIgnoreCase("pdf")) {
			icon = "file_pdf.gif";
		} else if (ext.equalsIgnoreCase("bmp")) {
			icon = "file_bmp.gif";
		} else if (ext.equalsIgnoreCase("xlsx") || ext.equalsIgnoreCase("xls")) {
			icon = "file_excel.gif";
		}
		return path + icon;
	}
}
