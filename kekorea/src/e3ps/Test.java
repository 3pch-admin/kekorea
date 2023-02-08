package e3ps;

import com.aspose.pdf.Document;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.aspose.AsposeUtils;
import e3ps.common.util.CommonUtils;
import e3ps.epm.numberRule.NumberRule;
import jxl.write.biff.NumberRecord;
import wt.fc.PersistenceHelper;
import wt.method.RemoteMethodServer;

public class Test {

	public static void main(String[] args) throws Exception {

		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin1");
		
		String oid = "e3ps.admin.commonCode.CommonCode:182753";
		CommonCode c = (CommonCode) CommonUtils.getObject(oid);
		System.out.println(c);
		NumberRule numberRule = NumberRule.newNumberRule();
		numberRule.setDepartment(c);
		numberRule.setBusinessSector(c);
		numberRule.setDocument(c);
		numberRule.setDrawingCompany(c);
		numberRule.setName("123");
		numberRule.setNumber("KABCA00001");
		numberRule.setVersion(1);
		PersistenceHelper.manager.save(numberRule);

		System.out.println("정ㅇ");
	}

}
