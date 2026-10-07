package co.edu.unipiloto.encomiendas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import co.edu.unipiloto.encomiendas.model.Destinatario;
import co.edu.unipiloto.encomiendas.model.Encomienda;
import co.edu.unipiloto.encomiendas.model.Remitente;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Encomiendas.db";
    public static final int DATABASE_VERSION = 1;

    // Tabla REMITENTE
    public static final String T_REMITENTE = "REMITENTE";
    // Tabla DESTINATARIO
    public static final String T_DESTINATARIO = "DESTINATARIO";
    // Tabla ENCOMIENDA
    public static final String T_ENCOMIENDA = "ENCOMIENDA";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_REMITENTE + " ("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NOMBRE TEXT NOT NULL, "
                + "DOCUMENTO TEXT NOT NULL UNIQUE, "
                + "TELEFONO TEXT, "
                + "DIRECCION TEXT)");

        db.execSQL("CREATE TABLE " + T_DESTINATARIO + " ("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NOMBRE TEXT NOT NULL, "
                + "DOCUMENTO TEXT, "
                + "TELEFONO TEXT, "
                + "DIRECCION TEXT NOT NULL, "
                + "CIUDAD TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + T_ENCOMIENDA + " ("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "GUIA TEXT NOT NULL UNIQUE, "
                + "DESCRIPCION TEXT, "
                + "PESO REAL, "
                + "VALOR_DECLARADO REAL, "
                + "TIPO TEXT, "
                + "FECHA TEXT, "
                + "ID_REMITENTE INTEGER NOT NULL, "
                + "ID_DESTINATARIO INTEGER NOT NULL, "
                + "FOREIGN KEY(ID_REMITENTE) REFERENCES " + T_REMITENTE + "(ID), "
                + "FOREIGN KEY(ID_DESTINATARIO) REFERENCES " + T_DESTINATARIO + "(ID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_ENCOMIENDA);
        db.execSQL("DROP TABLE IF EXISTS " + T_DESTINATARIO);
        db.execSQL("DROP TABLE IF EXISTS " + T_REMITENTE);
        onCreate(db);
    }

    /**
     * Inserta remitente (o reutiliza uno existente por documento), destinatario y encomienda
     * dentro de una transacción. Retorna el ID de la encomienda o -1 si falla.
     */
    public long insertEncomienda(Remitente r, Destinatario d, Encomienda e) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            long idRemitente = findRemitenteId(db, r.getDocumento());
            if (idRemitente == -1) {
                ContentValues cvRem = new ContentValues();
                cvRem.put("NOMBRE", r.getNombre());
                cvRem.put("DOCUMENTO", r.getDocumento());
                cvRem.put("TELEFONO", r.getTelefono());
                cvRem.put("DIRECCION", r.getDireccion());
                idRemitente = db.insert(T_REMITENTE, null, cvRem);
                if (idRemitente == -1) return -1;
            }

            ContentValues cvDes = new ContentValues();
            cvDes.put("NOMBRE", d.getNombre());
            cvDes.put("DOCUMENTO", d.getDocumento());
            cvDes.put("TELEFONO", d.getTelefono());
            cvDes.put("DIRECCION", d.getDireccion());
            cvDes.put("CIUDAD", d.getCiudad());
            long idDestinatario = db.insert(T_DESTINATARIO, null, cvDes);
            if (idDestinatario == -1) return -1;

            ContentValues cvEnc = new ContentValues();
            cvEnc.put("GUIA", e.getGuia());
            cvEnc.put("DESCRIPCION", e.getDescripcion());
            cvEnc.put("PESO", e.getPeso());
            cvEnc.put("VALOR_DECLARADO", e.getValorDeclarado());
            cvEnc.put("TIPO", e.getTipo());
            cvEnc.put("FECHA", e.getFecha());
            cvEnc.put("ID_REMITENTE", idRemitente);
            cvEnc.put("ID_DESTINATARIO", idDestinatario);
            long idEncomienda = db.insert(T_ENCOMIENDA, null, cvEnc);
            if (idEncomienda == -1) return -1;

            db.setTransactionSuccessful();
            return idEncomienda;
        } finally {
            db.endTransaction();
        }
    }

    private long findRemitenteId(SQLiteDatabase db, String documento) {
        Cursor c = db.rawQuery("SELECT ID FROM " + T_REMITENTE + " WHERE DOCUMENTO = ?",
                new String[]{documento});
        try {
            return c.moveToFirst() ? c.getLong(0) : -1;
        } finally {
            c.close();
        }
    }

    private static final String SELECT_DETALLE =
            "SELECT e.GUIA, e.FECHA, e.TIPO, e.DESCRIPCION, e.PESO, e.VALOR_DECLARADO, "
            + "r.NOMBRE, r.DOCUMENTO, r.TELEFONO, r.DIRECCION, "
            + "d.NOMBRE, d.DOCUMENTO, d.TELEFONO, d.DIRECCION, d.CIUDAD "
            + "FROM " + T_ENCOMIENDA + " e "
            + "JOIN " + T_REMITENTE + " r ON e.ID_REMITENTE = r.ID "
            + "JOIN " + T_DESTINATARIO + " d ON e.ID_DESTINATARIO = d.ID ";

    /** Consulta todas las encomiendas con su remitente y destinatario. */
    public Cursor getAllEncomiendas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(SELECT_DETALLE + "ORDER BY e.ID DESC", null);
    }

    /** Busca por número de guía o documento del remitente. */
    public Cursor findEncomiendas(String criterio) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(SELECT_DETALLE + "WHERE e.GUIA = ? OR r.DOCUMENTO = ? ORDER BY e.ID DESC",
                new String[]{criterio, criterio});
    }
}
