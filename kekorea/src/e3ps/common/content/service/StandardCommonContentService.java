package e3ps.common.content.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import wt.fv.FileFolder;
import wt.fv.FvHelper;
import wt.fv.FvMount;
import wt.fv.FvTransaction;
import wt.fv.StandardFvService;
import wt.fv.StoreStreamListener;
import wt.fv.StoredItem;
import wt.fv.Vault;
import wt.fv.uploadtocache.BackupedFile;
import wt.fv.uploadtocache.CacheDescriptor;
import wt.fv.uploadtocache.CachedContentDescriptor;
import wt.objectstorage.ContentFileWriter;
import wt.objectstorage.ContentManagerFactory;
import wt.objectstorage.ContentStorageManager;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardCommonContentService extends StandardManager implements CommonContentService {

	public static StandardCommonContentService newStandardCommonContentService() throws WTException {
		StandardCommonContentService instance = new StandardCommonContentService();
		instance.initialize();
		return instance;
	}

	@Override
	public CachedContentDescriptor doUpload(CacheDescriptor localCacheDescriptor, File file) throws Exception {
		CachedContentDescriptor ccd = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			long folderId = localCacheDescriptor.getFolderId();
			long fileName = localCacheDescriptor.getFileNames()[0];
			long vaultId = localCacheDescriptor.getVaultId();
			long streamId = localCacheDescriptor.getStreamIds()[0];

			InputStream[] streams = new InputStream[1];
			streams[0] = new FileInputStream(file);
			long[] fileSize = new long[1];
			fileSize[0] = file.length();

			Vault vault = CommonContentHelper.manager.getLocalVault(vaultId);
			saveVault(vault, folderId, fileName, streams[0], fileSize);
			ccd = new CachedContentDescriptor(streamId, folderId, fileSize[0], 0, file.getPath());

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return ccd;
	}

	private void saveVault(Vault vault, long folderId, long fileName, InputStream inputStream, long[] fileSize)
			throws Exception {
		FileFolder folder = null;
		StoreStreamListener listener = new StoreStreamListener();
		FvTransaction trs = new FvTransaction();
		try {
			trs.start();
			trs.addTransactionListener(listener);
			listener.prepareToGetFolder(vault);

			folder = StandardFvService.getActiveFolder(vault);

			listener.informGotFolderOk();
			FvMount mount = StandardFvService.getLocalMount(folder);

			String path = mount.getPath();
			String fn = StoredItem.buildFileName(fileName);
			String mountType = FvHelper.service.getMountType(mount);
			BackupedFile backupFile = new BackupedFile(mountType, path, fn);

			listener.prepareToUpload(folder, backupFile, path, fn);

			ContentStorageManager manager = ContentManagerFactory.getContentManager(mountType);
			ContentFileWriter cfWriter = manager.getContentFileWriter(backupFile.getFirstContentFile());

			cfWriter.storeStream(inputStream, backupFile, fileSize[0], false);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null) {
				trs.rollback();
			}
		}
	}
}
