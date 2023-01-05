package e3ps.common.code.service;

import java.util.Map;

import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface CommonCodeService {

	public abstract Map<String, Object> createCodeAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> modifyCodeAction(Map<String, Object> param) throws WTException;
	
	
}
