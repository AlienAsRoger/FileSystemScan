package com.example.roger.fileexplorer.background;

import android.content.ContentResolver;
import android.os.AsyncTask;
import com.example.roger.fileexplorer.db.DbDataManager;
import com.example.roger.fileexplorer.interfaces.ListenerInterface;
import com.example.roger.fileexplorer.utils.FileInfo;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 22.04.2016
 * Time: 14:28
 */
public class LoadFilesTask extends AsyncTask<Void, Void, Integer> {

	private List<FileInfo> list;
	private ContentResolver contentResolver;
	private WeakReference<ListenerInterface> listenerInterface;

	LoadFilesTask( ContentResolver contentResolver, ListenerInterface listenerInterface) {
		this.contentResolver = contentResolver;
		this.listenerInterface = new WeakReference<ListenerInterface>(listenerInterface);
	}

	@Override
	protected Integer doInBackground(Void... params) {
		list = DbDataManager.getFileInfo(contentResolver);

		return null;
	}

	@Override
	protected void onPostExecute(Integer integer) {
		super.onPostExecute(integer);

		if (listenerInterface != null) {
			ListenerInterface face = this.listenerInterface.get();
			if (face != null) {
				face.onUpdate(list);
			}
		}
	}
}
