package e3ps.event;

import wt.epm.EPMDocument;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteInterface;

@RemoteInterface
public interface EventService {

	/**
	 * 상태값 변경시 체크
	 */
	public abstract void detectTask(LifeCycleManaged lcm) throws Exception;

	/**
	 * AutoCAD PDF 파일 변환
	 */
	public abstract void convertAutoCADToPDF(EPMDocument epm) throws Exception;
}
