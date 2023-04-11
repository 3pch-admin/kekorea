package e3ps.loader.commonCode;

import e3ps.loader.service.LoaderHelper;

public class ProjectTypeLoader {

	public static void main(String[] args) throws Exception {
		ProjectTypeLoader loader = new ProjectTypeLoader();
		loader.load();
		System.out.println("프로젝트 타입 로더 종료!!");
		System.exit(0);
	}

	private void load() throws Exception {
		LoaderHelper.service.loaderProjectType();
	}
}