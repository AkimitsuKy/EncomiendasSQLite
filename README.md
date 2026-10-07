# Encomiendas SQLite — Persistencia con SQLite (Android, Java)

Laboratorio grupal — Universidad Piloto de Colombia.

## Objetivo
Persistir en la base de datos local **SQLite** la información de **encomiendas**, sus **remitentes** y sus **destinatarios**.

## Modelo de datos (`Encomiendas.db`)
| Tabla | Columnas |
|---|---|
| REMITENTE | ID (PK), NOMBRE, DOCUMENTO (UNIQUE), TELEFONO, DIRECCION |
| DESTINATARIO | ID (PK), NOMBRE, DOCUMENTO, TELEFONO, DIRECCION, CIUDAD |
| ENCOMIENDA | ID (PK), GUIA (UNIQUE), DESCRIPCION, PESO, VALOR_DECLARADO, TIPO, FECHA, ID_REMITENTE (FK), ID_DESTINATARIO (FK) |

## Funcionalidades
- **Registrar encomienda**: inserta remitente (reutiliza si el documento ya existe), destinatario y encomienda en una transacción (`SQLiteOpenHelper`, `ContentValues`, `insert()`). Genera un número de guía.
- **Consultar encomiendas**: lista todos los registros con `rawQuery` + `JOIN` y los muestra en un `AlertDialog`.
- **Buscar**: por número de guía o documento del remitente (consulta parametrizada).

## Estructura
```
app/src/main/java/co/edu/unipiloto/encomiendas/
├── MainActivity.java       # Formulario y botones
├── DatabaseHelper.java     # SQLiteOpenHelper: tablas, insert y consultas
└── model/ Remitente.java, Destinatario.java, Encomienda.java
```

## Ejecución
Abrir la carpeta en Android Studio → Sync Gradle → Run (minSdk 24).
