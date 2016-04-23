package com.example.roger.fileexplorer.background;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import com.example.roger.fileexplorer.interfaces.ListenerInterface;
import com.example.roger.fileexplorer.utils.FileInfo;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 22.04.2016
 * Time: 10:05
 */
public class GetFileSizeTask extends AsyncTask<String, Void, Integer> {


	private static final String TAG = "test";

	@Nullable
	private WeakReference<ListenerInterface> face;
	private List<FileInfo> list;
	private long fileSize;


	public GetFileSizeTask(ListenerInterface face, long fileSize) {
		this.fileSize = fileSize;
		this.face = new WeakReference<ListenerInterface>(face);
		list = new LinkedList<FileInfo>();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		ListenerInterface listener = null;
		if (face != null) {
			listener = face.get();
		}
		if (listener != null) {
			listener.showProgress(true);
		}
	}

	@Override
	protected Integer doInBackground(String... params) {

		File file = new File(params[0]);
		scanDirStructure(file);
		return 0;
	}

	@Override
	protected void onPostExecute(Integer integer) {
		super.onPostExecute(integer);

		if (integer < 0) {
			return;
		}
		ListenerInterface listener = null;
		if (face != null) {
			listener = face.get();
		}
		if (listener != null) {
			listener.showProgress(false);
			listener.onUpdate(list);
		}
	}

	public long scanDirStructure(File f) {
		long size = 0;
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			if (files != null) {
				for (File file : files) {
					size += scanDirStructure(file);
				}
			}
			// update dir
			updateFileSize(f, size, true);
		} else {
			size = f.length();
			// update file
			updateFileSize(f, size, false);
		}
		return size;
	}

	/**
	 *  check if file size is more than 30MB
	 */
	private void updateFileSize(File file, long size, boolean isDir) {
		if (size >= fileSize) {
			list.add(new FileInfo(size, file.getAbsolutePath(), isDir));
		}
	}

}
