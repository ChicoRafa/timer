package rmr.kairos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import rmr.kairos.model.Estadistica;
import rmr.kairos.model.Tag;
import rmr.kairos.model.Usuario;

/**
 * Clase que contiene todos los métodos utilizados por la BDD con objeto de manejar los datos
 *
 * @author Rafa M.
 * @version 2.0
 * @since 1.0
 */
public class KairosDB extends KairosHelper {
    Context context;

    public KairosDB(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertUser(String username, String password, String email) {
        long id = 0;
        try {
            KairosHelper helper = new KairosHelper(context);
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            values.put("email", email);
            id = db.insert(TABLE_USERS, null, values);
        } catch (Exception e) {
            e.toString();
        }
        return id;
    }

    public long insertTag(String tagName, String tagColor) {
        long id = 0;
        try {
            KairosHelper helper = new KairosHelper(context);
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nombre_tag", tagName);
            values.put("color_tag", tagColor);
            //values.put("color_code",tagColorCode);
            id = db.insert(TABLE_TAGS, null, values);
        } catch (Exception e) {
            e.toString();
        }
        return id;
    }

    public long insertStat(int workTime) {
        long id = 0;
        try {
            KairosHelper helper = new KairosHelper(context);
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("tiempo", workTime);
            //values.put("color_code",tagColorCode);
            id = db.insert(TABLE_STATS, null, values);
        } catch (Exception e) {
            e.toString();
        }
        return id;
    }

    public ArrayList<Usuario> selectUsers() {
        KairosHelper helper = new KairosHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<Usuario> listaUsuarios = new ArrayList<>();
        Usuario usuario = null;
        Cursor cursorUsuarios = null;
        cursorUsuarios = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        if (cursorUsuarios.moveToFirst()) {
            do {
                usuario = new Usuario();
                usuario.setId(cursorUsuarios.getInt(0));
                usuario.setUsername(cursorUsuarios.getString(1));
                usuario.setPassword(cursorUsuarios.getString(2));
                usuario.setEmail(cursorUsuarios.getString(3));
                listaUsuarios.add(usuario);
            } while (cursorUsuarios.moveToNext());
        }
        cursorUsuarios.close();
        return listaUsuarios;
    }

    public ArrayList<Estadistica> selectStats() {
        KairosHelper helper = new KairosHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<Estadistica> listaEstadistica = new ArrayList<>();
        Estadistica stat = null;
        Cursor cursorStat = null;
        cursorStat = db.rawQuery("SELECT * FROM " + TABLE_STATS, null);
        if (cursorStat.moveToFirst()) {
            do {
                stat = new Estadistica();
                stat.setDia(cursorStat.getString(0));
                stat.setWorkTime(cursorStat.getInt(1));
                listaEstadistica.add(stat);
            } while (cursorStat.moveToNext());
        }
        cursorStat.close();
        return listaEstadistica;
    }

    public ArrayList<Tag> selectTags() {
        KairosHelper helper = new KairosHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<Tag> listaEtiquetas = new ArrayList<>();
        Tag tag = null;
        Cursor cursorEtiquetas = null;
        cursorEtiquetas = db.rawQuery("SELECT * FROM " + TABLE_TAGS, null);
        if (cursorEtiquetas.moveToFirst()) {
            do {
                tag = new Tag();
                tag.setId(cursorEtiquetas.getInt(0));
                tag.setTagName(cursorEtiquetas.getString(1));
                tag.setTagColor(cursorEtiquetas.getString(2));
                listaEtiquetas.add(tag);
            } while (cursorEtiquetas.moveToNext());
        }
        cursorEtiquetas.close();
        return listaEtiquetas;
    }

    public Estadistica selectSingleStat(String dia) {
        KairosHelper helper = new KairosHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<Estadistica> listaEstadistica = new ArrayList<>();
        Estadistica stat = null;
        Cursor cursorStat = null;
        cursorStat = db.rawQuery("SELECT * FROM " + TABLE_STATS + " WHERE dia_semana = '" + dia + "' LIMIT 1", null);
        if (cursorStat.moveToFirst()) {
            stat = new Estadistica();
            stat.setDia(cursorStat.getString(0));
            stat.setWorkTime(cursorStat.getInt(1));
            listaEstadistica.add(stat);
        }
        cursorStat.close();
        return stat;
    }

    public Tag selectSingleTag(String name) {
        KairosHelper helper = new KairosHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<Tag> listaEtiquetas = new ArrayList<>();
        Tag tag = null;
        Cursor cursorEtiquetas = null;
        cursorEtiquetas = db.rawQuery("SELECT * FROM " + TABLE_TAGS + " WHERE nombre_tag = '"
                + name + "' LIMIT 1", null);
        if (cursorEtiquetas.moveToFirst()) {
            tag = new Tag();
            tag.setId(cursorEtiquetas.getInt(0));
            tag.setTagName(cursorEtiquetas.getString(1));
            tag.setTagColor(cursorEtiquetas.getString(2));
            listaEtiquetas.add(tag);
        }
        cursorEtiquetas.close();
        return tag;
    }

    public boolean updateStat(String dia, int workTime) {
        boolean correcto = false;
        KairosHelper helper = new KairosHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_STATS +
                    " SET tiempo = '" + workTime + "' WHERE dia_semana = '" + dia + "' ");
            correcto = true;
        } catch (Exception e) {
            e.toString();
            correcto = false;
        } finally {
            db.close();
        }
        return correcto;
    }
    public boolean updateAllStats(){
        boolean correcto = false;
        KairosHelper helper = new KairosHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_STATS +
                    " SET tiempo = 0");
            correcto = true;
        } catch (Exception e) {
            e.toString();
            correcto = false;
        } finally {
            db.close();
        }
        return correcto;
    }

    public boolean updateTag(int id, String tagName, String tagColor) {
        boolean correcto = false;
        KairosHelper helper = new KairosHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_TAGS + " SET nombre_tag ='" + tagName + "', " +
                    " color_tag = '" + tagColor + "' WHERE id = '" + id + "' ");
            correcto = true;
        } catch (Exception e) {
            e.toString();
            correcto = false;
        } finally {
            db.close();
        }
        return correcto;
    }

    public boolean deleteTag(int id) {
        boolean correcto = false;
        KairosHelper helper = new KairosHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_TAGS + " WHERE id ='" + id + "'");
            correcto = true;
        } catch (Exception e) {
            e.toString();
            correcto = false;
        } finally {
            db.close();
        }
        return correcto;
    }


}