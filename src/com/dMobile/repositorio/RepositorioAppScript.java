package com.dMobile.repositorio;

import android.content.Context;
import android.util.Log;

import com.dMobile.findmyapp.util.SQLiteHelper;

public class RepositorioAppScript extends RepositorioApp{
	//Script para fazer drop na tabela
	private static final String SCRIPT_TABLE_DELETE = "DROP TABLE IF EXISTS my_app";
	//Cria tabela com id sequencial
	private static final String[] SCRIPT_TABLE_CREATE = new String[] {"CREATE TABLE my_app"
			+ "(_id integer primary key autoincrement,"
			+ "name text not null,"
			+ "name_no_accent text not null,"
			+ "image blob not null,"
			+ "pkg text not null,"
			+ "activity_name text not null,"
			+ "count_access integer,"
			+ "date_installed datetime);"};

	private static final String NOME_BANCO = "find_my_app";
	private static final int VERSAO_BANCO = 1;
	public static final String NOME_TABELA = "my_app";

	public static final String NOME_TABELA_TEMP = "temp_apps";	
	public static final String SCRIPT_TABLE_TEMP_DELETE = "DROP TABLE IF EXISTS temp_apps";
	// Script para tabela temporária.	
	public static final String SCRIPT_TABLE_TEMP_CREATE = "CREATE TABLE temp_apps "
			+ "(pkg text not null);";

	public static final String NOME_TABELA_KEEP_COUNT_USAGE = "keep_count_apps";	
	public static final String SCRIPT_TABLE_KEEP_COUNT_USAGE_DELETE = "DROP TABLE IF EXISTS keep_count_apps";
	// Mantém a contagem do app para que quando for atualizado, não começar sua contagem novamente.
	public static final String SCRIPT_TABLE_KEEP_COUNT_USAGE_APP = "CREATE TABLE keep_count_apps "
			+ "(pkg text not null,"
			+ "count_access integer);";

	private SQLiteHelper dbHelper;

	public RepositorioAppScript(Context ctx){
		try {			
			//Criar utilizando um script
			dbHelper = new SQLiteHelper(ctx, RepositorioAppScript.NOME_BANCO,
					RepositorioAppScript.VERSAO_BANCO, RepositorioAppScript.SCRIPT_TABLE_CREATE,
					RepositorioAppScript.SCRIPT_TABLE_DELETE);
			//Abre o banco no modo escrita para poder alterar tbm.
			db = dbHelper.getWritableDatabase();		
		} catch (Exception e) {
			Log.e("RepositórioAppScript", "Erro ao criar banco de dados." + e.getMessage());
		}
	}
	/**
	 * Fecha conexão com DB.
	 */
	@Override
	public void fechar(){
		super.fechar();
		if(dbHelper != null){
			dbHelper.close();
		}
	}
}
