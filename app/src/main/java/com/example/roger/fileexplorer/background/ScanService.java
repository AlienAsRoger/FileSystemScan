package com.example.roger.fileexplorer.background;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import com.example.roger.fileexplorer.interfaces.ListenerInterface;
import com.example.roger.fileexplorer.ui.MainActivity;
import com.example.roger.fileexplorer.utils.FileInfo;

import java.io.File;
import java.util.List;

/**
 * Scanning should run in the background so it is available the next time the app is opened
 */
public class ScanService extends Service implements ListenerInterface {

	private ServiceBinder serviceBinder = new ServiceBinder();
	private ListenerInterface listenerInterface;

	public ScanService() {
	}

	public class ServiceBinder extends Binder {

		public ScanService getService() {
			return ScanService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return serviceBinder;
	}

	public void setUpdateInterface(ListenerInterface listenerInterface) {
		this.listenerInterface = listenerInterface;
	}

	public void scanDirectory(){
		File externalStorageDirectory = Environment.getExternalStorageDirectory();
		if (externalStorageDirectory != null) {
	        scanDirectory(externalStorageDirectory.getAbsolutePath(), MainActivity.START_FILE_SIZE);
		} else{
			// TODO show friendly reminder
		}
	}
	public void scanDirectory(String path, long fileSize){
		new GetFileSizeTask(this, fileSize).execute(path);
	}

	@Override
	public void showProgress(boolean show) {
		if (listenerInterface != null) {
			listenerInterface.showProgress(show);
		}
	}

	@Override
	public void onUpdate(List<FileInfo> list) {
//		if (listenerInterface != null) { // TODO fix sort order
//			listenerInterface.onUpdate(list);
//		}

		// save to DB and then load in sorted order
		new SaveFilesTask(list, getContentResolver(), new SaveListener()).execute();
	}

	private class SaveListener implements ListenerInterface {

		@Override
		public void showProgress(boolean show) {
			if (listenerInterface != null) {
				listenerInterface.showProgress(show);
			}
		}

		@Override
		public void onUpdate(List<FileInfo> list) {
			new LoadFilesTask(getContentResolver(), new LoadListener());

			if (listenerInterface != null && list != null) {
				listenerInterface.onUpdate(list);
			}
		}
	}

	private class LoadListener implements ListenerInterface {

		@Override
		public void showProgress(boolean show) {
			if (listenerInterface != null) {
				listenerInterface.showProgress(show);
			}
		}

		@Override
		public void onUpdate(List<FileInfo> list) {

			if (listenerInterface != null) {
				listenerInterface.onUpdate(list);
			}
		}
	}


}
