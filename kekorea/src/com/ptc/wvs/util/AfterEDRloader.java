package com.ptc.wvs.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ptc.wvs.common.util.WVSProperties;
import com.ptc.wvs.server.util.FileHelper;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.filter.NavigationCriteria;
import wt.org.WTPrincipal;
import wt.pom.Transaction;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.session.SessionHelper;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

public class AfterEDRloader {

	private static final String CLASS_NAME = AfterEDRloader.class.getName();
	public static String WT_TEMP = getWtTemp();
	private static boolean VERBOSE = getVerboseProperty();

	public static String[] copyToEPM(Representable repable, Persistable object, Representation rep,
			NavigationCriteria docCriteria, NavigationCriteria partCriteria, int structureType) throws WTException {

		String[] ret = { "AfterEDRloader starts: Checking if object is EPMDoc" };

		if (object instanceof EPMDocument) {
			// the session is changed to administrator with
			// SessionHelper.manager.setAdministrator(). The method runs under administrator
			// during each invocation
			// get current user
			WTPrincipal realUser = SessionHelper.manager.getPrincipal();
			if (VERBOSE)
				System.out.println("realUser..." + realUser);

			// set session user to Administrator
			SessionHelper.manager.setAdministrator();
			if (VERBOSE)
				System.out.println("setAdministrator..." + SessionHelper.manager.getPrincipal());

			EPMDocument cadDoc = (EPMDocument) object;

			if (VERBOSE) {
				System.out.println("copyToEPM() START");
				System.out.println("copyToEPM : Param cadDoc : " + object);
			}

			// WVSProperties wvsProperties = new WVSProperties();
			try {
				String p = WVSProperties.getPropertyValue("publish.afterloadermethod.copyToEPM.Filext");
				Boolean downloadToLocal = getDownloadToLocalProperty();
				if (p != null && p.trim().length() > 0) {
					StringTokenizer tok = new StringTokenizer(p.trim(), " ");
					while (tok.hasMoreTokens()) {
						String t = tok.nextToken();
//						  if (t.equals("zip")) t = tok.nextToken();
						deletecopyToEPMContents(cadDoc, t);
						String ret2 = copyVisualizations(cadDoc, rep, t, downloadToLocal);
						if (!ret2.equals("")) {
							ret[0] = ret[0] + "..Renaming: " + ret2;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (VERBOSE)
				System.out.println(".copyToEPM() END");
			// set back to current user
			SessionHelper.manager.setPrincipal(realUser.getName());
		}

		return ret;
	}

	private static String copyVisualizations(EPMDocument epmDoc, Representation rep, String viewableExt,
			Boolean downloadToLocal) throws WTException {

		String ret = "";
		Transaction trx = new Transaction();

		try {
			trx.start();
			ContentHolder ch = null;
			Vector<?> appDatas = null;
			ApplicationData appData = null;

			String fileName = null;
			String sFileName = null;
			ApplicationData appDataNew = null;
			InputStream is = null;

			ch = ContentHelper.service.getContents(rep);
			appDatas = ContentHelper.getContentListAll(ch);

			String directory = null;
			if (downloadToLocal) {
				// get epmDoc info
//				String container = WTContainerHelper.getContainer(epmDoc).getName();
//				String authapp = epmDoc.getAuthoringApplication().toString();
//				String epmdocType = epmDoc.getDocType().toString();

				String localDir = getDownloadToLocalDir2();
				if (localDir != null) {
					SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM");
//					File dir = new File(localDir + File.separator + "Design_deliverables" + File.separator + container + File.separator + authapp + File.separator + epmdocType + "_" + fm.format(Calendar.getInstance().getTime()));
					File dir = new File(localDir + File.separator + fm.format(Calendar.getInstance().getTime()));
					if (dir == null || !dir.exists())
						dir.mkdirs();
					directory = dir.getAbsolutePath();
				} else {
					downloadToLocal = false;
				}
			}

			for (int i = 0; i < appDatas.size(); i++) {
				appData = (ApplicationData) appDatas.get(i);
				fileName = appData.getFileName();

				if (fileName != null && fileName.indexOf("." + viewableExt) != -1) {
					sFileName = getcopyToEPMFileName(epmDoc, fileName, viewableExt);

					if (!sFileName.equals(fileName)) {
						if (downloadToLocal)
							ret = downloadContent(epmDoc, appData, directory, fileName, sFileName);
						appDataNew = copyApplicationData(epmDoc, appData, sFileName);
						is = ContentServerHelper.service.findContentStream(appData);
						ContentServerHelper.service.updateContent(epmDoc, appDataNew, is);
					}
				}
			}

			trx.commit();
			trx = null;

		} catch (PropertyVetoException e) {
			throw new WTException(e);
		} catch (IOException e) {
			throw new WTException(e);
		} finally {
			if (trx != null) {
				trx.rollback();
				trx = null;
			}
		}

		return ret;
	}

	private static ApplicationData copyApplicationData(ContentHolder ch, ApplicationData source, String sFileName)
			throws WTException, WTPropertyVetoException {

		ApplicationData appDataNew = ApplicationData.newApplicationData(ch);
		appDataNew.setFileName(sFileName);
		appDataNew.setFileSize(source.getFileSize());
		appDataNew.setRole(ContentRoleType.SECONDARY);
		appDataNew.setCategory("IMAGE");
		appDataNew = (ApplicationData) PersistenceHelper.manager.save(appDataNew);

		return appDataNew;
	}

	private static boolean deletecopyToEPMContents(ContentHolder ch, String viewableExt)
			throws WTException, PropertyVetoException {

		String sName = ((EPMDocument) ch).getCADName();
		ch = (EPMDocument) ContentHelper.service.getContents(ch);

		/*
		 * Get the list of ContentItem out of the passed ContentHolder This should be
		 * done after a call to ContentHelper.service.getContents( ContentHolder ) Note
		 * this will NOT return the primary content for a FormatContentHolder.
		 */

		ApplicationData appData = null;
		Vector<?> appDatas = null;
		String fileName = null;
		sName = sName.substring(0, sName.indexOf("."));

//		appDatas = ContentHelper.getContentListExcludeRoles(ch, "THUMBNAIL,THUMBNAIL3D,THUMBNAIL_SMALL,CAD_HIDDEN_CONTENT");
		appDatas = ContentHelper.getContentListExcludeRoles(ch, "THUMBNAIL,THUMBNAIL3D,THUMBNAIL_SMALL");

		int num_of_files = appDatas.size();
		for (int i = 0; i < appDatas.size(); i++) {
			appData = (ApplicationData) appDatas.get(i);
			fileName = appData.getFileName();

			if (fileName.indexOf(sName) != -1 && fileName.indexOf("." + viewableExt) != -1) {

				if (VERBOSE)
					System.out.println("Removing..." + fileName);
				ContentServerHelper.service.deleteContent(ch, appData);
			}
		}

		return true;
	}

	private static String getcopyToEPMFileName(ContentHolder ch, String appFileName, String viewableExt)
			throws WTException {

		String sFileName = ((EPMDocument) ch).getCADName();
		String iterationInfo = ((EPMDocument) ch).getIterationDisplayIdentifier().toString();
//		String versionInfo = ((EPMDocument)ch).getVersionDisplayIdentity().toString();

		if (!viewableExt.equals("zip")) {
			sFileName = sFileName.substring(0, sFileName.indexOf(".")) + "_" + iterationInfo + "." + viewableExt; // <CADName>_<iterationInfo>.extension
																													// (ex.
																													// ABCED-FGH-I_A.1.pdf)
//			sFileName = sFileName.substring(0, sFileName.indexOf(".")) + "_" + versionInfo +  "." + viewableExt; // <CADName>_<versionInfo>.extension (ex. ABCED-FGH-I_Rev A.pdf)
		} else {
			sFileName = appFileName;
			sFileName = sFileName.substring(0, sFileName.indexOf(".")) + "_" + iterationInfo + "." + viewableExt; // <CADName>_<iterationInfo>.extension
																													// (ex.
																													// ABCED-FGH-I_A.1.pdf)
//			sFileName = sFileName.substring(0, appFileName.indexOf(".")) + "_" + versionInfo +  "." + viewableExt; // <CADName>_<versionInfo>.extension (ex. ABCED-FGH-I_Rev A.pdf)
		}

		return sFileName;

	}

	public static String downloadContent(EPMDocument epmDoc, ApplicationData appData, String dir, String fromFileName,
			String toFileName) {

		String ret = "";
		try {
			// Download ApplicationData
			File contentFile = FileHelper.getFileContent(epmDoc, appData, dir, null);
			FileUtil.moveFile(dir, fromFileName, toFileName);

			ret = contentFile.toString();
			if (VERBOSE)
				System.out.println("downloadContent..." + ret);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	// Get wvs property
	private static boolean getDownloadToLocalProperty() {
		boolean local = false;
		try {
			String tmp = WVSProperties.getPropertyValue("publish.afterloadermethod.copyToEPM.downloadToLocal");
			if (tmp != null && tmp.equalsIgnoreCase("true")) {
				local = true;
			}
		} catch (Throwable e) {
			System.out.println("Error reading publish.afterloadermethod.copyToEPM.downloadToLocal property");
			e.printStackTrace();
		}

		return local;
	}

	/**
	 * private static String getDownloadToLocalDir() { String localDir = ""; try {
	 * String tmp =
	 * WVSProperties.getPropertyValue("publish.afterloadermethod.copyToEPM.downloadToLocal.dir");
	 * if (tmp == null || tmp.length() == 0) { return localDir; } localDir = tmp;
	 * 
	 * } catch (Throwable e) { System.out.println("Error reading
	 * publish.afterloadermethod.copyToEPM.downloadToLocal property");
	 * e.printStackTrace(); }
	 * 
	 * return localDir; }
	 **/

	private static String getDownloadToLocalDir2() {
		String localDir = WT_TEMP + File.separator + "Afterloader_DownloadDir";
		try {
			String tmp = System.getenv("AFTERLOADERDIR");
			if (VERBOSE)
				System.out.println("AFTERLOADERDIR: " + tmp);
			if (tmp == null || tmp.length() == 0) {
				return localDir;
			}
			localDir = tmp;

		} catch (Throwable e) {
			System.out.println("Error reading publish.afterloadermethod.copyToEPM.downloadToLocal property");
			e.printStackTrace();
		}

		return localDir;
	}

	public static String getWtTemp() {
		String wtHome = null;
		try {
			WTProperties props = WTProperties.getLocalProperties();
			wtHome = props.getProperty("wt.temp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wtHome;
	}

	public static String[] renameAdditionalfiles(Representable repable, Persistable object, Representation rep,
			NavigationCriteria docCriteria, NavigationCriteria partCriteria, int structureType) {
		String[] ret = { "AfterEDRloader starts: Checking if object is EPMDoc" };

		if (object instanceof EPMDocument) {

			Transaction trx = new Transaction();
			try {
				trx.start();

				Representation ch = (Representation) ContentHelper.service.getContents(rep);
				EPMDocument cadDoc = (EPMDocument) object;

				if (VERBOSE)
					System.out.println("renameAdditionalfiles() START");
				if (VERBOSE)
					System.out.println("renameAdditionalfiles : Param cadDoc : " + cadDoc);

				ApplicationData appData = null;
				Vector<?> appDatas = null;
				String fileName = null;
				String sName = null;
				String fileExt = null;
				String postfix = null;
				InputStream is = null;
				String iterationInfo = cadDoc.getIterationDisplayIdentifier().toString();

				appDatas = ContentHelper.getContentList(ch);
				int num_of_files = appDatas.size();

				for (int i = 0; i < appDatas.size(); i++) {
					appData = (ApplicationData) appDatas.get(i);
					String sFileName = cadDoc.getCADName();

					if (appData.getRole() != null && appData.getRole().equals(ContentRoleType.ADDITIONAL_FILES)) {
						fileName = appData.getFileName();
						postfix = fileName.substring(fileName.lastIndexOf("_") + 1);
						fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);

						if (fileExt.equals("zip")) {
							sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + "_" + iterationInfo + "_"
									+ postfix;
						} else {
							sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + "_" + iterationInfo + "."
									+ fileExt;
						}

						appData.setFileName(sFileName);

						is = ContentServerHelper.service.findContentStream(appData);
						ContentServerHelper.service.updateContent(ch, appData, is);
					}
				}
				trx.commit();
				trx = null;

			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WTException e) {
				e.printStackTrace();
			} finally {
				if (trx != null) {
					trx.rollback();
					trx = null;
				}
			}

		}

		return ret;
	}

	// Get Verbose property
	private static boolean getVerboseProperty() {
		boolean VERBOSE = false;
		try {
			String tmp = WVSProperties.getPropertyValue(CLASS_NAME + ".verbose");
			if (tmp != null && tmp.equalsIgnoreCase("true")) {
				VERBOSE = true;
			}
		} catch (Throwable e) {
			System.out.println(CLASS_NAME + ": Error reading properties " + CLASS_NAME + ".verbose");
			e.printStackTrace();
		}

		return VERBOSE;
	}

}