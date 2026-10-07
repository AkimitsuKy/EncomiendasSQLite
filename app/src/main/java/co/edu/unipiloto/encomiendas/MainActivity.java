package co.edu.unipiloto.encomiendas;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.edu.unipiloto.encomiendas.model.Destinatario;
import co.edu.unipiloto.encomiendas.model.Encomienda;
import co.edu.unipiloto.encomiendas.model.Remitente;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper myDb;

    private TextInputLayout remNombre, remDocumento, remTelefono, remDireccion;
    private TextInputLayout desNombre, desDocumento, desTelefono, desDireccion, desCiudad;
    private TextInputLayout descripcion, peso, valor, buscar;
    private RadioGroup radioTipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Registro de Encomiendas");
        }

        myDb = new DatabaseHelper(this);

        remNombre = findViewById(R.id.editText_remNombre);
        remDocumento = findViewById(R.id.editText_remDocumento);
        remTelefono = findViewById(R.id.editText_remTelefono);
        remDireccion = findViewById(R.id.editText_remDireccion);

        desNombre = findViewById(R.id.editText_desNombre);
        desDocumento = findViewById(R.id.editText_desDocumento);
        desTelefono = findViewById(R.id.editText_desTelefono);
        desDireccion = findViewById(R.id.editText_desDireccion);
        desCiudad = findViewById(R.id.editText_desCiudad);

        descripcion = findViewById(R.id.editText_descripcion);
        peso = findViewById(R.id.editText_peso);
        valor = findViewById(R.id.editText_valor);
        radioTipo = findViewById(R.id.radioGroup_tipo);
        buscar = findViewById(R.id.editText_buscar);

        Button btnRegistrar = findViewById(R.id.button_registrar);
        Button btnConsultar = findViewById(R.id.button_consultar);
        Button btnBuscar = findViewById(R.id.button_buscar);

        btnRegistrar.setOnClickListener(v -> addData());
        btnConsultar.setOnClickListener(v -> viewAll());
        btnBuscar.setOnClickListener(v -> search());
    }

    private String text(TextInputLayout til) {
        return til.getEditText() == null ? "" : til.getEditText().getText().toString().trim();
    }

    private boolean required(TextInputLayout... fields) {
        boolean ok = true;
        for (TextInputLayout f : fields) {
            if (TextUtils.isEmpty(text(f))) {
                f.setError("Campo obligatorio");
                ok = false;
            } else {
                f.setError(null);
            }
        }
        return ok;
    }

    /** Botón 1: adiciona datos en SQLite. */
    private void addData() {
        if (!required(remNombre, remDocumento, remTelefono, desNombre, desTelefono,
                desDireccion, desCiudad, descripcion, peso, valor)) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double pesoKg, valorDec;
        try {
            pesoKg = Double.parseDouble(text(peso));
            valorDec = Double.parseDouble(text(valor));
        } catch (NumberFormatException ex) {
            peso.setError("Valor numérico inválido");
            return;
        }
        if (pesoKg <= 0) {
            peso.setError("El peso debe ser mayor a 0");
            return;
        }

        String tipo = radioTipo.getCheckedRadioButtonId() == R.id.radioButton_documento
                ? "Documento" : "Paquete";
        Date ahora = new Date();
        String guia = "ENC-" + new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault()).format(ahora);
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(ahora);

        Remitente r = new Remitente(text(remNombre), text(remDocumento),
                text(remTelefono), text(remDireccion));
        Destinatario d = new Destinatario(text(desNombre), text(desDocumento),
                text(desTelefono), text(desDireccion), text(desCiudad));
        Encomienda e = new Encomienda(guia, text(descripcion), pesoKg, valorDec, tipo, fecha);

        long id = myDb.insertEncomienda(r, d, e);
        if (id != -1) {
            showMessage("Encomienda registrada", "N° de guía: " + guia);
            clearForm();
        } else {
            Toast.makeText(this, "Datos no insertados", Toast.LENGTH_LONG).show();
        }
    }

    /** Botón 2: consulta todos los datos almacenados en SQLite. */
    private void viewAll() {
        Cursor res = myDb.getAllEncomiendas();
        showCursor(res, "Encomiendas registradas", "No hay encomiendas registradas");
    }

    /** Botón 3: consulta por guía o documento del remitente. */
    private void search() {
        String criterio = text(buscar);
        if (criterio.isEmpty()) {
            buscar.setError("Ingrese guía o documento");
            return;
        }
        buscar.setError(null);
        showCursor(myDb.findEncomiendas(criterio), "Resultado de búsqueda",
                "No se encontraron encomiendas para: " + criterio);
    }

    private void showCursor(Cursor res, String title, String emptyMsg) {
        try {
            if (res.getCount() == 0) {
                showMessage("Sin resultados", emptyMsg);
                return;
            }
            StringBuilder sb = new StringBuilder();
            while (res.moveToNext()) {
                sb.append("Guía: ").append(res.getString(0)).append("\n")
                  .append("Fecha: ").append(res.getString(1)).append("\n")
                  .append("Tipo: ").append(res.getString(2)).append("\n")
                  .append("Contenido: ").append(res.getString(3)).append("\n")
                  .append("Peso: ").append(res.getDouble(4)).append(" kg\n")
                  .append("Valor declarado: $").append(res.getDouble(5)).append("\n")
                  .append("— Remitente: ").append(res.getString(6))
                  .append(" (CC ").append(res.getString(7)).append(")\n")
                  .append("   Tel: ").append(res.getString(8))
                  .append(" | Dir: ").append(res.getString(9)).append("\n")
                  .append("— Destinatario: ").append(res.getString(10))
                  .append(" (CC ").append(res.getString(11)).append(")\n")
                  .append("   Tel: ").append(res.getString(12))
                  .append(" | Dir: ").append(res.getString(13))
                  .append(", ").append(res.getString(14)).append("\n\n");
            }
            showMessage(title + " (" + res.getCount() + ")", sb.toString());
        } finally {
            res.close();
        }
    }

    private void clearForm() {
        TextInputLayout[] all = {remNombre, remDocumento, remTelefono, remDireccion,
                desNombre, desDocumento, desTelefono, desDireccion, desCiudad,
                descripcion, peso, valor};
        for (TextInputLayout t : all) {
            if (t.getEditText() != null) t.getEditText().setText("");
            t.setError(null);
        }
        radioTipo.check(R.id.radioButton_paquete);
        remNombre.requestFocus();
    }

    public void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        myDb.close();
        super.onDestroy();
    }
}
