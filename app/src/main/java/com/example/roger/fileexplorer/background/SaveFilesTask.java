package com.example.roger.fileexplorer.background;

import android.content.ContentResolver;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import com.example.roger.fileexplorer.db.DbDataManager;
import com.example.roger.fileexplorer.interfaces.ListenerInterface;
import com.example.roger.fileexplorer.utils.FileInfo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 22.04.2016
 * Time: 14:27
 */
public class SaveFilesTask  extends AsyncTask<Void, Void, Integer> {

	private List<FileInfo> list;
	private ContentResolver contentResolver;
	@Nullable
	private ListenerInterface saveListener;

	SaveFilesTask(List<FileInfo> list, ContentResolver contentResolver, ListenerInterface saveListener) {
		this.list = list;
		this.contentResolver = contentResolver;
		this.saveListener = saveListener;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		DbDataManager.saveFileInfo(contentResolver, list);

		return null;
	}

	@Override
	protected void onPostExecute(Integer integer) {
		super.onPostExecute(integer);

		if (saveListener != null) {
			saveListener.onUpdate(null);
		}
	}
}
