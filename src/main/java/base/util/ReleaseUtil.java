package base.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.*;
import java.net.HttpURLConnection;

public class ReleaseUtil {
	private static final String TAG = ReleaseUtil.class.getSimpleName();

	public static void release(SQLiteDatabase object) {
		try {
			object.close();
			object = null;
		} catch (Exception e) {
		}
	}

	public static void release(SQLiteOpenHelper object) {
		try {
			object.close();
			object = null;
		} catch (Exception e) {
		}
	}

	public static void release(Cursor object) {
		try {
			object.close();
			object = null;
		} catch (Exception e) {
		}
	}

	public static void release(FileOutputStream object) {
		try {
			object.close();
			object = null;
		} catch (Exception e) {
		}
	}

	public static void release(InputStream object) {
		try {
			object.close();
			object = null;
		} catch (Exception e) {
		}
	}

	public static void release(FileInputStream object) {
		try {
			object.close();
			object = null;
		} catch (Exception e) {
		}
	}

	public static void release(DataInputStream object) {
		try {
			object.close();
			object = null;
		} catch (Exception e) {
		}
	}

	public static void release(RandomAccessFile object) {
		try {
			object.close();
			object = null;
		} catch (Exception e) {
		}
	}

	public static void release(FileReader object) {
		try {
			object.close();
			object = null;
		} catch (Exception e) {
		}
	}

	public static void release(BufferedReader object) {
		try {
			object.close();
			object = null;
		} catch (Exception e) {
		}
	}

	public static void release(HttpURLConnection object) {
		try {
			object.disconnect();
			object = null;
		} catch (Exception e) {
		}
	}

	public static void release(Bitmap object) {
		try {
			object.recycle();
			object = null;
		} catch (Exception e) {
		}
	}
}
