package e3ps;

import java.io.File;
import java.io.FileOutputStream;

import com.aspose.pdf.Document;
import com.aspose.pdf.devices.PngDevice;
import com.aspose.pdf.devices.Resolution;

import e3ps.common.aspose.AsposeUtils;
import e3ps.project.Project;
import wt.query.QuerySpec;

public class Test {

	public static void main(String[] args) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);
		query.setDistinct(true);
		query.setAdvancedQueryEnabled(true);
		System.out.println(query);
		System.out.println("종료,,,:");
		System.exit(0);
	}
}