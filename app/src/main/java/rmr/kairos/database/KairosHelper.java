package rmr.kairos.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Clase Helper que hereda de SQLiteOpenHelper, la cual crea la BDD y permite actualizarla
 * entre otras funcionalidades
 * @author Rafa M.
 * @version 2.0
 * @since 1.0
 */
public class KairosHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "kairos.db";
    public static final String TABLE_USERS = "t_users";
    public static final String TABLE_TAGS = "t_tags";
    public static final String TABLE_STATS = "t_stats";


    public KairosHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_USERS+"(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR(100) NOT NULL," +
                "password TEXT NOT NULL," +
                "email VARCHAR(100) NOT NULL," +
                "UNIQUE ('username','email') )");

        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_TAGS+"(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre_tag VARCHAR(50) NOT NULL DEFAULT '0'," +
                "color_tag VARCAHR(50) NOT NULL DEFAULT '0')");
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_STATS+"( dia_semana VARCHAR(20) PRIMARY KEY," +
                "tiempo INTEGER DEFAULT '0')");
        sqLiteDatabase.execSQL("INSERT INTO "+TABLE_STATS+" (dia_semana) VALUES ('lunes'),('martes'),('miercoles'),('jueves'),('viernes'),('sabado'),('domingo');");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE "+TABLE_USERS);
        sqLiteDatabase.execSQL("DROP TABLE "+TABLE_TAGS);
    }
}