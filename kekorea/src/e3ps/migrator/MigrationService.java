package e3ps.migrator;

import wt.method.RemoteInterface;

@RemoteInterface
public interface MigrationService {

	public abstract void projectToMak() throws Exception;
}
