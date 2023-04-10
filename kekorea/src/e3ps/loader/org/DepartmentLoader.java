package e3ps.loader.org;

import e3ps.loader.service.LoaderHelper;

public class DepartmentLoader {

	public static void main(String[] args) throws Exception {
		DepartmentLoader loader = new DepartmentLoader();
		loader.load();
		System.out.println("부서 로더 종료!!");
		System.exit(0);
	}

	private void load() throws Exception {
		LoaderHelper.service.loaderDepartment();
	}
}
