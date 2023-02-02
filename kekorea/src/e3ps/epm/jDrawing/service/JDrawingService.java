package e3ps.epm.jDrawing.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface JDrawingService {

	public abstract void create(Map<String, Object> params) throws Exception;

}
