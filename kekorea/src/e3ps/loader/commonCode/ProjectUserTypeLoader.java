package e3ps.loader.commonCode;

import e3ps.loader.service.LoaderHelper;

public class ProjectUserTypeLoader {

	public static void main(String[] args) throws Exception {
		ProjectUserTypeLoader loader = new ProjectUserTypeLoader();
		loader.load();
		System.out.println("프로젝트 유저 타입 로더 종료!!");
		System.exit(0);
	}

	private void load() throws Exception {
		LoaderHelper.service.loaderProjectUserType();
	}
}
