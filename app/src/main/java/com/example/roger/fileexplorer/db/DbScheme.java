package com.example.roger.fileexplorer.db;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.roger.fileexplorer.BuildConfig;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 22.04.2016
 * Time: 12:42
 */
public class DbScheme {

	static final int DATABASE_VERSION = 1;  // change version on every DB scheme changes

	public static final String PROVIDER_NAME = BuildConfig.APPLICATION_ID + ".db_provider";

	public static final String CONTENT_PATH = "content://";
	public static final String SLASH = "/";

	/*
	 * DB table names
	 */
	static final String DATABASE_NAME = "Files DB";

	public enum Tables {
		FILES
	}

	// Content URI
	private static final ConcurrentHashMap<String, Uri> uriHashMap = new ConcurrentHashMap<String, Uri>(Tables.values().length);
	private ArrayList<String> commandsArray;

	DbScheme() {
		commandsArray = new ArrayList<String>();
	}

	static {
		for (Tables table : Tables.values()) {
			String tableName = table.name();
			uriHashMap.put(tableName, Uri.parse(CONTENT_PATH + PROVIDER_NAME + SLASH + tableName));
		}

	}


	/* common commands */
	private static final String CREATE_TABLE_IF_NOT_EXISTS = "create table if not exists ";
	private static final String INSERT_INTO_TABLE = "insert into ";
	private static final String SELECT_ALL_FROM_TABLE = "select * from ";
	private static final String SELECT = "select _id";
	private static final String FROM_TABLE = "from table ";
	private static final String ALTER_TABLE = "alter table ";
	private static final String RENAME_TO = "rename to ";
	private static final String ADD_COLUMN = "add column ";
	private static final String DROP_TABLE_IF_EXISTS = "drop table if exists ";

	private static final String BACKUP_POSTFIX = "_backup";
	private static final String _INT_NOT_NULL = " INT not null";
	private static final String _LONG_NOT_NULL = " LONG not null";
	private static final String _TEXT_NOT_NULL = " TEXT not null";
	private static final String _DEFAULT_ = " default ";
	private static final String _TEXT = " TEXT";
	private static final String _LONG = " LONG";
	private static final String _INT = " INT";
	private static final String _COMMA = ",";
	private static final String _CLOSE = ");";
	private static final String _SPACE = " ";
	private static final String EQUALS = " = ";
	private static final String ID_INTEGER_PRIMARY_KEY_AUTOINCREMENT = " (_id integer primary key autoincrement, ";

	// general fields
	public static final String _ID = "_id";
	public static final String V_PATH = "path";
	public static final String V_NAME = "name";
	public static final String V_SIZE = "size";
	public static final String V_IS_DIR = "is_dir";


/*
	FileInfo structure
	String path;
	String name;
	private boolean isDir;
	long size;
*/
	private void createMainTables() {
		commandsArray.add(createTableForName(Tables.FILES)
				+ addCreateTableField_Text(V_PATH)
				+ addCreateTableField_Text(V_NAME)
				+ addCreateTableField_Long(V_SIZE)
				+ addCreateTableField_Int(V_IS_DIR, true));

	}


	private String getTableName(Tables table) {
		return Tables.values()[table.ordinal()].toString();
	}

	private String createTableForName(Tables table) {
		return CREATE_TABLE_IF_NOT_EXISTS + getTableName(table) + ID_INTEGER_PRIMARY_KEY_AUTOINCREMENT;
	}

	private String insertAllIntoTableFromTable(String fromTable, String toTable) {
		return INSERT_INTO_TABLE + toTable + _SPACE + SELECT_ALL_FROM_TABLE + fromTable;
	}

	private String insertIntoTable(String toTable) {
		return INSERT_INTO_TABLE + toTable + _SPACE + SELECT;
	}

	private String fromTable(String toTable) {
		return _SPACE + FROM_TABLE + toTable;
	}

	private String addSelectionField(String columnName) {
		return _COMMA + columnName;
	}

	private String addColumnDef_Int(String columnName) {
		return _SPACE + columnName + _INT_NOT_NULL;
	}

	private String addColumnDef_IntDefault(String columnName, int defaultValue) {
		return addColumnDef_Int(columnName) + _DEFAULT_ + defaultValue;
	}

	private String addColumnDef_IntNullable(String columnName) {
		return _SPACE + columnName + _INT;
	}

	private String addColumnDef_Long(String columnName) {
		return _SPACE + columnName + _LONG_NOT_NULL;
	}

	private String addColumnDef_LongNullable(String columnName) {
		return _SPACE + columnName + _LONG;
	}

	private String addColumnDef_LongDefault(String columnName, long defaultValue) {
		return addColumnDef_Long(columnName) + _DEFAULT_ + defaultValue;
	}

	private String addColumnDef_Text(String columnName) {
		return _SPACE + columnName + _TEXT_NOT_NULL;
	}

	private String addColumnDef_TextNullable(String columnName) {
		return _SPACE + columnName + _TEXT;
	}

	private String addCreateTableField_Int(String columnName) {
		return addCreateTableField_Int(columnName, false);
	}

	private String addCreateTableField_Int(String columnName, boolean last) {
		return addColumnDef_Int(columnName) + (last ? _CLOSE : _COMMA);
	}

	private String addCreateTableField_IntDefault(String columnName, int defaultValue, boolean last) {
		return addColumnDef_IntDefault(columnName, defaultValue) + (last ? _CLOSE : _COMMA);
	}

	private String addCreateTableField_IntDefault(String columnName, int defaultValue) {
		return addCreateTableField_IntDefault(columnName, defaultValue, false);
	}

	private String addCreateTableField_Text(String columnName) {
		return addCreateTableField_Text(columnName, false);
	}

	private String addCreateTableField_Text(String columnName, boolean last) {
		return addColumnDef_Text(columnName) + (last ? _CLOSE : _COMMA);
	}

	private String addCreateTableField_Long(String columnName) {
		return addCreateTableField_Long(columnName, false);
	}

	private String addCreateTableField_Long(String columnName, boolean last) {
		return addColumnDef_Long(columnName) + (last ? _CLOSE : _COMMA);
	}

	private String addCreateTableField_IntNullable(String columnName) {
		return addCreateTableField_IntNullable(columnName, false);
	}

	private String addCreateTableField_IntNullable(String columnName, boolean last) {
		return addColumnDef_IntNullable(columnName) + (last ? _CLOSE : _COMMA);
	}

	private String addCreateTableField_TextNullable(String columnName) {
		return addCreateTableField_TextNullable(columnName, false);
	}

	private String addCreateTableField_TextNullable(String columnName, boolean last) {
		return addColumnDef_TextNullable(columnName) + (last ? _CLOSE : _COMMA);
	}

	private String addCreateTableField_LongNullable(String columnName) {
		return addCreateTableField_LongNullable(columnName, false);
	}

	private String addCreateTableField_LongNullable(String columnName, boolean last) {
		return addColumnDef_LongNullable(columnName) + (last ? _CLOSE : _COMMA);
	}

	private static String getBackupTableName(String tableName) {
		return tableName + BACKUP_POSTFIX;
	}

	private String makeBackupTable(String tableName) {
		return ALTER_TABLE + tableName + _SPACE + RENAME_TO + getBackupTableName(tableName);
	}

	/**
	 * Add column one at a time see <a href="http://www.sqlite.org/lang_altertable.html">alter table</a>
	 */
	private String addToTableColumn(Tables table) {
		return ALTER_TABLE + getTableName(table) + _SPACE + ADD_COLUMN;
	}

	static String dropTable(String tableName) {
		return DROP_TABLE_IF_EXISTS + tableName;
	}

	public void executeDbScript(SQLiteDatabase db) {
		for (String createTableCall : commandsArray) {
			db.execSQL(createTableCall);
		}
	}

	public void createTables() {
		createMainTables();

	}

	public void backupTables() {
		for (int i = 0; i < Tables.values().length; i++) {
			backupTable(Tables.values()[i]);
		}
	}

	public void backupTable(Tables table) {
		commandsArray.add(makeBackupTable(table.toString()));
	}

	public void dropTables() {
		for (int i = 0; i < Tables.values().length; i++) {
			commandsArray.add(dropTable(Tables.values()[i].toString()));
		}
	}

	public void dropBackupTables() {
		for (int i = 0; i < Tables.values().length; i++) {
			dropBackupTable(Tables.values()[i]);
		}
	}

	public void dropBackupTable(Tables table) {
		commandsArray.add(dropTable(getBackupTableName(table.toString())));
	}

	public void copyDataFromBackup() {
		for (int i = 0; i < Tables.values().length; i++) {
			String tableName = getTableName(Tables.values()[i]);
			commandsArray.add(insertAllIntoTableFromTable(getBackupTableName(tableName), tableName));
		}
	}

	/**
	 * Upgrades db scheme from previous version to the next one
	 * <p/>
	 * When changing db version, somebody MUST add migration code for his new version here.
	 * In simple cases somebody should use alter table (addToTableColumn) and create table (createTableForName) commands.
	 * Otherwise dummy migration can be used (it is slow) or any other (more intelligent) method.
	 * Sample of dummy migration is added in comments inside this function.
	 *
	 * @param newVersion, to which we are upgrading
	 */
	private void upgradeToVersionFromPrevious(int newVersion) {


	}

	/**
	 * Upgrades db scheme
	 * <p/>
	 * If old version already has info about daily pending moves, we can't just drop all tables and recreate them
	 * because they can contain info about moves, not sent to server and we will lost this data
	 * we need to copy old tables with data into temporary tables, than create new tables, than copy old data
	 * into new tables with new structure
	 * <p/>
	 * So from DATABASE_SMART_UPGRADE_START_VERSION we don't use drop-create method for upgrading db scheme.
	 * When migrating from DATABASE_SMART_UPGRADE_START_VERSION and further, we should use smart upgrade. It
	 * upgrades db version iteratively incrementally version by version using {@link #upgradeToVersionFromPrevious}.
	 * <p/>
	 * When changing db version, everyone MUST add migration code for his new version to
	 * {@link #upgradeToVersionFromPrevious}, otherwise exception will be thrown.
	 */
	public void upgradeDb(int oldVersion, int newVersion) {


	}

	/**
	 * Gets Uri for db table
	 *
	 * @param table to get uri for, not null
	 * @return uri for specified table
	 */
	public static Uri getUriForTable(Tables table) {
		return getUriForTableName(table.name());
	}

	/**
	 * Gets Uri for table or virtual table by it's name
	 *
	 * @param tableName name of table or virtual table
	 * @return uri for working with specified table
	 */
	private static Uri getUriForTableName(String tableName) {
		return uriHashMap.get(tableName);
	}
}
