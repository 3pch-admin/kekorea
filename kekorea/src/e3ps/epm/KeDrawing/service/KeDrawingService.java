package e3ps.epm.KeDrawing.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface KeDrawingService {

	public abstract void create(Map<String, Object> params) throws Exception;

}
