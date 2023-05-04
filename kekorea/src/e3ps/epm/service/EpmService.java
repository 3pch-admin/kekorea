package e3ps.epm.service;

import java.util.Map;

import wt.epm.EPMDocument;
import wt.method.RemoteInterface;

@RemoteInterface
public interface EpmService {
	/**
	 * 도면 결재
	 */
	public abstract void register(Map<String, Object> params) throws Exception;

	/**
	 * 오토캐드 PDF 변환
	 */
	public abstract void convertAutoCADToPDF(EPMDocument epm) throws Exception;

}
