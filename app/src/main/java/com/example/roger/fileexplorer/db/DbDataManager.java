package com.example.roger.fileexplorer.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.example.roger.fileexplorer.utils.FileInfo;

import java.util.ArrayList;
import java.util.List;

import static com.example.roger.fileexplorer.db.DbScheme.*;


/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 22.04.2016
 * Time: 12:42
 */
public class DbDataManager {

	private static final String ORDER_BY = "ORDER BY";
	private static final String GROUP_BY = "GROUP BY";
	public static final String ASCEND = " ASC";
	public static final String DESCEND = " DESC";
	//	public static final String SLASH_ = "/";
	public static final String OR_ = " OR ";
	public static final String LIKE_ = " LIKE ?";
	public static final String AND_ = " AND ";
	public static final String MORE_ = " > ";
	public static final String MORE_EQUALS_ARG_ = " >=?";
	public static final String EQUALS_ = "=";
	public static final String EQUALS_ARG_ = "=?";
	public static final String NOT_EQUALS_ = "!=";
	public static final String NOT_EQUALS_ARG_ = "!=?";
	public static final String LIMIT_ = " LIMIT ";
	public static final String LIMIT_1 = _ID + " LIMIT 1";
	public static final String COLLATE_NOCASE = " COLLATE NOCASE";
	public static final String CHESS_GAME_CLASS = "chess";
	public static final int TRUE = 1;
	public static final int FALSE = 0;
	public static final String S_TRUE = "1";
	public static final String S_FALSE = "0";


	public static String SELECTION_PATH = concatArguments(V_PATH);

	public static String concatArguments(String... arguments) {
		StringBuilder selection = new StringBuilder();

		String separator = "";
		for (String argument : arguments) {
			selection.append(separator);
			separator = AND_;
			selection.append(argument);
			selection.append(EQUALS_ARG_);
		}
		return selection.toString();
	}

	public static Cursor query(ContentResolver contentResolver, QueryParams params) {
		return contentResolver.query(params.getUri(), params.getProjection(), params.getSelection(),
				params.getArguments(), params.getOrder());
	}

	public static String[] getArguments(String... values) {
		return values;
	}

	public static void saveFileInfo(ContentResolver contentResolver, List<FileInfo> fileInfoList) {
		Uri uri = getUriForTable(Tables.FILES);

		for (FileInfo dataObj : fileInfoList) {

			final String[] arguments1 = getArguments(String.valueOf(dataObj.getPath()));

			Cursor cursor = contentResolver.query(uri, null, SELECTION_PATH, arguments1, null);

			ContentValues values = new ContentValues();

			values.put(V_NAME, dataObj.getName());
			values.put(V_PATH, dataObj.getPath());
			values.put(V_SIZE, dataObj.getSize());
			values.put(V_IS_DIR, dataObj.isDir() ? 1 : 0);

			updateOrInsertValues(contentResolver, cursor, uri, values);
		}
	}

	public static List<FileInfo> getFileInfo(ContentResolver contentResolver) {
		QueryParams queryParams = new QueryParams();
		queryParams.setUri(DbScheme.getUriForTable(Tables.FILES));
		queryParams.setOrder(DbScheme.V_SIZE + DbDataManager.DESCEND);
		Cursor cursor = query(contentResolver, queryParams);

		List<FileInfo> diagramList = new ArrayList<FileInfo>();
		if (cursor != null && cursor.moveToFirst()) {
			do {
				FileInfo diagram = new FileInfo();
				diagram.setDir(getInt(cursor, V_IS_DIR) == 1);
				diagram.setName(getString(cursor, V_NAME));
				diagram.setPath(getString(cursor, V_PATH));
				diagram.setSize(getLong(cursor, V_SIZE));

				diagramList.add(diagram);
			} while (cursor.moveToNext());
		}
		return diagramList;
	}


	// ================================= global help methods =======================================
	public static void updateOrInsertValues(ContentResolver contentResolver, Cursor cursor, Uri uri, ContentValues values) {
		if (cursor != null && cursor.moveToFirst()) {
			do {
				contentResolver.update(ContentUris.withAppendedId(uri, getId(cursor)), values, null, null);
			} while (cursor.moveToNext());
		} else {
			contentResolver.insert(uri, values);
		}

		if (cursor != null) {
			cursor.close();
		}
	}

	public static String getString(Cursor cursor, String column) {
		return cursor.getString(cursor.getColumnIndex(column));
	}

	public static int getInt(Cursor cursor, String column) {
		return cursor.getInt(cursor.getColumnIndex(column));
	}

	public static long getLong(Cursor cursor, String column) {
		return cursor.getLong(cursor.getColumnIndex(column));
	}

	public static long getId(Cursor cursor) {
		return cursor.getLong(cursor.getColumnIndex(_ID));
	}

	public static String anyLikeMatch(String query) {
		return "%" + query + "%";
	}

	public static String startLikeMatch(String query) {
		return "%" + query;
	}

	public static String endLikeMatch(String query) {
		return query + "%";
	}

	public static int getDbVersion() {
		return DATABASE_VERSION;
	}

	private static int getIntFromBoolean(boolean value) {
		return value ? 1 : 0;
	}

}
