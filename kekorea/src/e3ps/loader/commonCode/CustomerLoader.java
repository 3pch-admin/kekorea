package e3ps.loader.commonCode;

import java.io.File;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.common.util.StringUtils;
import e3ps.loader.service.LoaderHelper;

public class CustomerLoader {

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.out.println("엑셀 파일 추가.");
			System.exit(0);
		}

		CustomerLoader loader = new CustomerLoader();
		loader.load();
		System.out.println("거래처 로더 종료!!");
		System.exit(0);
	}

	private void load() {
		try {
			LoaderHelper.service.loaderCustomer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}