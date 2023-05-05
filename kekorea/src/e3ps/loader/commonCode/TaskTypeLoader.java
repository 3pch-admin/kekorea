package e3ps.loader.commonCode;

import e3ps.loader.service.LoaderHelper;

public class TaskTypeLoader {

	public static void main(String[] args) throws Exception {
		TaskTypeLoader loader = new TaskTypeLoader();
		loader.load();
		System.out.println("태스크 타입 로더 종료!!");
		System.exit(0);
	}

	private void load() throws Exception {
		LoaderHelper.service.loaderTaskType();
	}
}
