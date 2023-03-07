package e3ps;

import java.io.File;

import com.aspose.pdf.Document;

import e3ps.common.aspose.AsposeUtils;

public class Test {

	public static void main(String[] args) throws Exception {

		String s = "HQ-SiO2, HTO, ";

		String[] ss = s.split(",");
		for (String a : ss) {
			System.out.println(a.trim());
		}

	}
}
