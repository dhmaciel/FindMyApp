package com.dMobile.findmyapp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("NewApi")
public class SQLiteHelper extends SQLiteOpenHelper{
	private static final String CATEGORIA = "apps";
	private String[] scriptSQLCreate;
	private String scriptSQLDelete;
	
	/**
	 * Cria uma instância de SQLiteHelper
	 * @param context
	 * @param nomeBanco
	 * @param versao
	 * @param scriptSQLCreate
	 * @param scriptSQLDelete
	 */
	public SQLiteHelper(Context context, String nomeBanco, int versao,
			String[] scriptSQLCreate, String scriptSQLDelete) {
		super(context, nomeBanco, null, versao);
		this.scriptSQLCreate = scriptSQLCreate;
		this.scriptSQLDelete = scriptSQLDelete;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(CATEGORIA, "Criando banco com SQl");
		int qtdeScripts = scriptSQLCreate.length;
		//Executa cada SQL passado com parâmetro
		for (int i = 0; i < qtdeScripts; i++) {
			String sql = scriptSQLCreate[i];
			Log.i(CATEGORIA, sql);
			//Cria o banco de dados executando o script de criação.
			db.execSQL(sql);
		}
		
	}

	// Mudou a versão
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(CATEGORIA, "Atualizando da versão " + oldVersion + " para " 
				+ newVersion + ". Todos os registros serão deletados.");
		Log.i(CATEGORIA, scriptSQLDelete);
		//Deleta as tabelas
		db.execSQL(scriptSQLDelete);
		//Cria novamente
		onCreate(db);	
		
	}

}
