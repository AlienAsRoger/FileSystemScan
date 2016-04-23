package com.example.roger.fileexplorer.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.roger.fileexplorer.db.DbScheme.*;


/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 22.04.2016
 * Time: 12:42
 */
public class DbDataProvider extends ContentProvider {

	private static final ConcurrentHashMap<String, Integer> uriMap;
	private static final ConcurrentHashMap<String, Integer> uriIdsMap;

	public static final String SLASH = "/";
	public static final String NUMBER = "#";
	public static final String SLASH_NUMBER = SLASH + NUMBER;

	public static final String EQUALS = " = ";

	private static final int OPEN_DB_RETRY_DELAY = 2000;

	static {
		uriMap = new ConcurrentHashMap<String, Integer>(Tables.values().length);
		uriIdsMap = new ConcurrentHashMap<String, Integer>(Tables.values().length);

		for (int i = 0; i < Tables.values().length; i++) {
			String table = Tables.values()[i].name();
			uriMap.put(table, i);
			uriIdsMap.put(table + SLASH_NUMBER, i);
		}

	}

	public static final String VND_ANDROID_CURSOR_DIR = "vnd.android.cursor.dir/";

	public static final String VND_ANDROID_CURSOR_ITEM = "vnd.android.cursor.item/";

	private DatabaseHelper dbHelper;

	@Override
	public boolean onCreate() {
		Context context = getContext();
		dbHelper = new DatabaseHelper(context);
		try {
			appDataBase = dbHelper.getWritableDatabase();
		} catch (SQLiteException e) {    // retry opening db after some time
			try {
				e.printStackTrace();
				Log.e("SQL",e.toString());
				Thread.sleep(OPEN_DB_RETRY_DELAY);
			} catch (InterruptedException e1) {
			}
			try {
				appDataBase = dbHelper.getWritableDatabase();
			} catch (SQLiteException dbOpenException) {    // retry didn't help
				e.printStackTrace();
				Log.e("SQL",e.toString());
				// show user a popup dialog that will tell him to reinstall the app
				return false;
			}
		}

		return (appDataBase != null);
	}

	@Override
	public String getType(Uri uri) {
		Tables table = findTableByUri(uri);
		if (table != null) {
			return VND_ANDROID_CURSOR_DIR + PROVIDER_NAME;
		} else {
			table = findTableIdByUri(uri);
			if (table != null) {
				return VND_ANDROID_CURSOR_ITEM + PROVIDER_NAME;
			}
		}
		throw new IllegalArgumentException("Unsupported URI: " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
						String sortOrder) {

		if (appDataBase == null || getContext() == null) {
			return null;
		}
		SQLiteQueryBuilder sqlBuilder = null;
		boolean found = false;
		Tables table = findTableByUri(uri);

		if (table != null) {
			sqlBuilder = new SQLiteQueryBuilder();
			sqlBuilder.setTables(table.name());
			found = true;
		} else {
			table = findTableIdByUri(uri);
			if (table != null) {
				sqlBuilder = new SQLiteQueryBuilder();
				sqlBuilder.setTables(table.name());
				sqlBuilder.appendWhere(_ID + EQUALS + uri.getPathSegments().get(1));
				found = true;
			}
		}


		if (found) {
			Cursor c = sqlBuilder.query(appDataBase, projection, selection, selectionArgs,
					null, null, sortOrder);
			c.setNotificationUri(getContext().getContentResolver(), uri);
			return c;

		}
		throw new IllegalArgumentException("Unsupported URI: " + uri);
	}


	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		if (appDataBase == null || getContext() == null) {
			return 0;
		}
		boolean found = false;
		Tables table = findTableByUri(uri);

		if (table != null) {
			count = appDataBase.update(table.name(), values, selection,
					selectionArgs);
			found = true;

		} else {
			table = findTableIdByUri(uri);
			if (table != null) {
				count = appDataBase.update(table.name(), values, _ID + EQUALS
								+ uri.getPathSegments().get(1)
								+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
						selectionArgs
				);
				found = true;
			}
		}

		if (found) {
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}

		throw new IllegalArgumentException("Unknown URI " + uri);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (appDataBase == null || getContext() == null) {
			return null;
		}
		// uri not found by default
		Boolean uriFound = false;

		// serializing values for debug
		StringBuilder valuesStr = new StringBuilder();
		boolean firstKey = true;
		for (Map.Entry<String, Object> entry : values.valueSet()) {
			if (firstKey) {
				firstKey = false;
				valuesStr.append("{");
			} else {
				valuesStr.append(", ");
			}
			valuesStr.append("[key=\"").append(entry.getKey()).append("\", value=\"")
					.append(entry.getValue()).append("\"]");
		}
		valuesStr.append("}");

		Tables table = findTableByUri(uri);
		if (table != null) {
			uriFound = true;
		} else {
			table = findTableIdByUri(uri);
			if (table != null) {
				uriFound = true;
			}
		}

		if (uriFound) {

			// inserting values
			try {
				long rowID = appDataBase.insertOrThrow(table.name(), "", values);

				// ---if added successfully---
				if (rowID > 0) {
					Uri _uri = ContentUris.withAppendedId(getUriForTable(table), rowID);
					getContext().getContentResolver().notifyChange(_uri, null);
					return _uri;
				}
			} catch (SQLException e) {    // adding failed

			}
		}

		return null;
	}

	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		boolean found = false;

		if (appDataBase == null || getContext() == null) {
			return 0;
		}
		Tables table = findTableByUri(uri);
		if (table != null) {
			count = appDataBase.delete(table.name(), selection, selectionArgs);
			found = true;
		} else {
			table = findTableIdByUri(uri);
			if (table != null) {
				String id = uri.getPathSegments().get(1);
				count = appDataBase.delete(table.name(), _ID + EQUALS + id
								+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
						selectionArgs
				);
				found = true;
			}
		}

		if (found) {
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}

		throw new IllegalArgumentException("Unknown URI " + uri);
	}

	/**
	 * Retrieve version of DB to sync data, and exclude null data request from
	 * DB
	 *
	 * @return DATABASE_VERSION integer value
	 */
	public static int getDbVersion() {
		return DATABASE_VERSION;
	}

	private SQLiteDatabase appDataBase;

	public SQLiteDatabase getDbHandle() {
		return appDataBase;
	}

	public DatabaseHelper getDbHelper() {
		return dbHelper;
	}

	// private static class DatabaseHelper extends SQLiteOpenHelper {
	public static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, getDbVersion());
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Init static tables first
			DbScheme dbScheme = new DbScheme();
			dbScheme.createTables();
			dbScheme.executeDbScript(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// new AppData(context).clearAppData(); // clear all values, to
			// avoid class cast exceptions

			// if old version already has info about daily pending moves, we can't just drop all tables and recreate them
			// because they can contain info about moves, not sent to server and we will lost this data
			// we need to copy old tables with data into temporary tables, than create new tables, than copy old data
			// into new tables with new structure
			DbScheme dbScheme = new DbScheme();
			dbScheme.upgradeDb(oldVersion, newVersion);
			dbScheme.executeDbScript(db);
		}

		public void recreateTables(SQLiteDatabase db) {
			DbScheme dbScheme = new DbScheme();
			dbScheme.dropTables();
			dbScheme.createTables();
			dbScheme.executeDbScript(db);
		}
	}

	private Tables findTableByUri(Uri uri) {
		Tables result = null;
		String name = getTableNameFromUri(uri);
		if (uriMap.containsKey(name)) {
			result = Tables.values()[uriMap.get(name)];
		}
		return result;
	}

	private Tables findTableIdByUri(Uri uri) {
		Tables result = null;
		String name = getTableNameFromUri(uri);
		if (uriIdsMap.containsKey(name)) {
			result = Tables.values()[uriIdsMap.get(name)];
		}
		return result;
	}

	private String getTableNameFromUri(Uri uri) {
		String result = null;
		final List<String> pathSegments = uri.getPathSegments();
		int size = pathSegments.size();

		if (size > 0) {

			boolean hasId;
			try {
				ContentUris.parseId(uri);
				hasId = true;
			} catch (NumberFormatException e) {
				hasId = false;
			}

			if (hasId) {
				result = pathSegments.get(size - 2) + SLASH_NUMBER;
			} else {
				result = uri.getLastPathSegment();
			}
		}

		return result;
	}
}