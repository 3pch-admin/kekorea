package e3ps.migrator;

import wt.services.ServiceFactory;

public class MigrationHelper {

	public static final MigrationHelper manager = new MigrationHelper();
	public static final MigrationService service = ServiceFactory.getService(MigrationService.class);

	public String[] orgToCode(String mak) throws Exception {
		String[] codes = new String[2];

		if ("B-D-Poly".equals(mak)) {
			codes[0] = "POLY";
			codes[1] = "B-D-POLY";
		} else if ("SRN/AlN/SRO".equals(mak)) {
			codes[0] = "Ad-TiN";
			codes[1] = "Ad-TiN";
		} else if ("SRN/AIN/SRO".equals(mak)) {
			codes[0] = "Al2O3";
			codes[1] = "Al2O3";
		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {

		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		} else if ("".equals(mak)) {
		}
		return codes;
	}
}
