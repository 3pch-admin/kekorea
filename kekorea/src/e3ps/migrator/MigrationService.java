package e3ps.migrator;

import java.util.HashMap;

import wt.method.RemoteInterface;

@RemoteInterface
public interface MigrationService {

	public abstract void projectToMak(HashMap<String, Object> map) throws Exception;
}
