package com.example.roger.fileexplorer.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.roger.fileexplorer.R;
import com.example.roger.fileexplorer.adapters.MyAdapter;
import com.example.roger.fileexplorer.background.ScanService;
import com.example.roger.fileexplorer.db.DbDataManager;
import com.example.roger.fileexplorer.interfaces.ListenerInterface;
import com.example.roger.fileexplorer.utils.FileInfo;
import com.example.roger.fileexplorer.utils.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Build an Android app that scans external storage and shows the largest files and directories.
 * Providing a user interface that would allow a user to see how their storage is being used. The
 * most important thing is that you get some kind of basic app working.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListenerInterface, AdapterView.OnItemClickListener {

	public static final int REQUEST_PERMISSION_READ_STORAGE = 88;
	public static final String ROOT = "root";
	public static final long START_FILE_SIZE = 50000000; // 50 MB
	public static final long MIN_FILE_SIZE = 2000000; // 2 MB


	private View progressBar;
	private boolean serviceBounded;
	private ScanService.ServiceBinder serviceBinder;
	private ListView listView;
	private ServiceConnectionListener serviceConnectionListener;
	private List<String> paths;
	private String currentDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setTitle(ROOT);
		currentDir = ROOT;
		paths = new LinkedList<>();
		paths.add(ROOT);
		widgetsInit();
	}

	@Override
	protected void onResume() {
		super.onResume();

		checkStoragePermission();

		List<FileInfo> fileInfo = DbDataManager.getFileInfo(getContentResolver());
		updateAdapter(fileInfo);

		connectServiceAndStartScan();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (serviceBounded) {
			unbindService(serviceConnectionListener);
		}
	}

	private void checkStoragePermission() {
		// get permission here
		Activity thisActivity = this;
		int permissionCheck = ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.READ_EXTERNAL_STORAGE);

		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

				AlertDialog.Builder builder =
						new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
				builder.setTitle(getString(R.string.dialog_title));
				builder.setMessage(getString(R.string.dialog_text));
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setNegativeButton(android.R.string.cancel, null);
				builder.show();
			} else {
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
						REQUEST_PERMISSION_READ_STORAGE);

				// REQUEST_PERMISSION_READ_STORAGE is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case REQUEST_PERMISSION_READ_STORAGE: {
				connectServiceAndStartScan();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.calcBtn) {
			scanSelectedDir();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FileInfo fileInfo = (FileInfo) parent.getAdapter().getItem(position);
		if (fileInfo.isDir()) {
			currentDir = fileInfo.getPath();
			setTitle(Utils.getFilenameFromPath(currentDir));

			paths.add(currentDir);
			scanSelectedDir();
		}
	}

	@Override
	public void onBackPressed() {
		// go down
		paths.remove(currentDir);
		if (paths.size() > 0) { // if we are not in the root
			currentDir = paths.get(paths.size() - 1);
			setTitle(Utils.getFilenameFromPath(currentDir));

			scanSelectedDir();
		} else {
			super.onBackPressed();
		}

	}

	private void connectServiceAndStartScan() {
		serviceConnectionListener = new ServiceConnectionListener();
		bindService(new Intent(this, ScanService.class), serviceConnectionListener, Activity.BIND_AUTO_CREATE);
	}

	private void scanSelectedDir() {
		if (serviceBounded) {
			ScanService service = serviceBinder.getService();
			if (currentDir.equals(ROOT)) {
				service.scanDirectory();
			} else {
				service.scanDirectory(currentDir, MIN_FILE_SIZE);
			}
		} else if (!isFinishing()) {
			connectServiceAndStartScan();
		}
	}

	private class ServiceConnectionListener implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
			serviceBounded = true;
			serviceBinder = (ScanService.ServiceBinder) iBinder;

			ScanService service = serviceBinder.getService();
			service.setUpdateInterface(MainActivity.this);
			service.scanDirectory();

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			serviceBounded = false;
		}
	}

	@Override
	public void showProgress(boolean show) {
		progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onUpdate(List<FileInfo> list) {
		// get file and dir sizes
		updateAdapter(list);
	}

	private void updateAdapter(List<FileInfo> list) {
		MyAdapter adapter = new MyAdapter(this, R.layout.list_item, list);
		listView.setAdapter(adapter);
	}

	private void widgetsInit() {
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		progressBar = findViewById(R.id.progressBar);
		findViewById(R.id.calcBtn).setOnClickListener(this);
	}

}
