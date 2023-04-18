package e3ps;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.db.DBCPManager;
import e3ps.common.util.CommonUtils;
import e3ps.erp.service.ErpHelper;
import e3ps.project.Project;
import net.sf.json.JSONArray;
import wt.org.StandardOrganizationServicesManager;
import wt.workflow.engine.StandardWfEngineService;

public class Test {

	public static void main(String[] args) throws Exception {

		String s = "asd'.prt";

		System.out.println(s.substring(0, s.lastIndexOf(".")));

		System.exit(0);
	}
}
