package e3ps.doc.service;

import java.util.Map;

import wt.method.RemoteInterface;
import wt.util.WTException;
import wt.vc.Versioned;

@RemoteInterface
public interface DocumentService {

	/**
	 * 문서 결재
	 */
	public abstract void register(Map<String, Object> params) throws Exception;

}
