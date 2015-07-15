package com.dMobile.repositorio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.dMobile.bean.MyApp;
import com.dMobile.bean.MyApp.MyApps;
import com.dMobile.findmyapp.Preferences;
import com.dMobile.findmyapp.util.PreferencesUtil;
import com.dMobile.findmyapp.util.ReceiverAppInstallUnistall;
import com.dMobile.findmyapp.util.ReplaceAssento;
import com.dMobile.findmyapp.util.ScreenUtil;

public class RepositorioApp {

	private static final String CATEGORIA = "apps";
	private static final String NOME_BANCO = "find_my_app";
	public static final String NOME_TABELA = "my_app";
	protected SQLiteDatabase db;

	public RepositorioApp(Context ctx) {
		this.db = ctx.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
	}

	protected RepositorioApp(){

	}

	/**
	 * Cria tabela temporária.
	 */
	public void createTableTempApps(){
		db.execSQL(RepositorioAppScript.SCRIPT_TABLE_TEMP_CREATE);
	}
	/**
	 * Deleta tabela temporária.
	 */
	public void dropTableTempApps(){
		db.execSQL(RepositorioAppScript.SCRIPT_TABLE_TEMP_DELETE);
	}

	/**
	 * Cria tabela manter contagem dos apps.
	 */
	public void createTableKeepCountApps(){
		db.execSQL(RepositorioAppScript.SCRIPT_TABLE_KEEP_COUNT_USAGE_APP);
	}
	/**
	 * Deleta tabela manter contagem dos apps.
	 */
	public void dropTableKeepCountApps(){
		db.execSQL(RepositorioAppScript.SCRIPT_TABLE_KEEP_COUNT_USAGE_DELETE);
	}	

	public long salvar(ApplicationInfo appInfo, PackageManager manager){
		long id = 0;

		try {


			if(id != 0){
				//atualizar(appInfo, manager);
			}else{
				// Inserir novo
				id = inserir(appInfo, manager);
			}
		} catch (Exception e) {
			Log.e("Erro ao salvar", "Ocorreu um erro ao inserir um registro unico." + e.getMessage().toString());
		}
		return id;		
	}
	/**
	 * Busca o aplicativo pelo nome do pacote
	 * @param pkg
	 * @return
	 */
	public MyApp buscarApp(String pkg){
		Cursor c = null;
		MyApp app = new MyApp();
		try {

			c = db.rawQuery("SELECT * FROM "+ NOME_TABELA + " WHERE pkg = '"+ pkg +"'" 
					, null);
			if(c.moveToFirst()) {
				//Recupera os índices das colunas:				
				int idXId = c.getColumnIndex(MyApps._ID);
				int idXNome = c.getColumnIndex(MyApps.NAME);
				int idXImage = c.getColumnIndex(MyApps.IMAGE);
				int idXPkg = c.getColumnIndex(MyApps.PKG);
				int idXActivityName = c.getColumnIndex(MyApps.ACTIVITY_NAME);
				int idXCountAcces = c.getColumnIndex(MyApps.COUNT_ACCESS);
				//Loop até o final
				do{
					//Recupera os apps
					app.setId(c.getLong(idXId));
					app.setName(c.getString(idXNome));
					app.setImage(c.getBlob(idXImage));
					app.setPkg(c.getString(idXPkg));
					app.setActivityName(c.getString(idXActivityName));
					app.setCountAccess(c.getInt(idXCountAcces));

				}while(c.moveToNext());	

			}			

		} catch (SQLException sqlException) {
			Log.e(CATEGORIA, "Erro ao buscar app pelo pacote " + sqlException.toString());
		}catch (Exception e) {
			Log.e(CATEGORIA, "Erro no método buscarApp " + e.getMessage().toString());
		}finally{
			if(c != null){
				c.close();
			}
		}
		return app;		
	}

	public List<String> compareDeletedInsertedApps(String table1, String table2){
		Cursor c = null;
		List<String> listDeleted = new ArrayList<String>();
		try {

			c = db.rawQuery("SELECT pkg FROM "+ table1 + " WHERE pkg NOT IN (SELECT pkg FROM "+ table2 +")" 
					, null);
			if(c.moveToFirst()) {
				//Recupera os índices das colunas:				
				int idXPkg = c.getColumnIndex(MyApps.PKG);				
				//Loop até o final
				do{
					listDeleted.add(c.getString(idXPkg));					

				}while(c.moveToNext());	
			}			

		} catch (SQLException sqlException) {
			Log.e(CATEGORIA, "Erro ao buscar Compare deleted " + sqlException.toString());
		}catch (Exception e) {
			Log.e(CATEGORIA, "Erro no método compareDeletedApps " + e.getMessage().toString());
		}finally{
			if(c != null){
				c.close();
			}
		}
		return listDeleted;		
	}

	/**
	 * Insere um único app por vez (chamado ao instalar um novo app no sistema).
	 * @param appInfo
	 * @param manager
	 * @return
	 */	
	public long inserir(ApplicationInfo appInfo, PackageManager manager) throws Exception{
		long id = 0;	
		Drawable img = null;		
//		ReceiverAppInstallUnistall.objetoEmail += " REPOSITORIO -NAME: "+ appInfo.loadLabel(manager).toString()+ "\n ";
//		ReceiverAppInstallUnistall.objetoEmail += "IMAGE: " + appInfo.loadIcon(manager);
//		ReceiverAppInstallUnistall.objetoEmail += "\nPKG: " + appInfo.packageName + "\n";

		ScreenUtil screenUtil = new ScreenUtil();
		ContentValues values = new ContentValues();
		values.put(MyApps.NAME, appInfo.loadLabel(manager).toString());
		values.put(MyApps.NAME_NO_ACCENT, ReplaceAssento.replace(appInfo.loadLabel(manager).toString()));
		img = screenUtil.resizeImage(appInfo.loadIcon(manager));
		values.put(MyApps.IMAGE, screenUtil.getBitmapAsByteArray(((BitmapDrawable)img).getBitmap()));
		values.put(MyApps.PKG, appInfo.packageName);			
		appInfo.loadLabel(manager).toString();			
		values.put(MyApps.ACTIVITY_NAME, "");	
		values.put(MyApps.DATE_INSTALLED, obterDataAtual());
		id = inserir(values, NOME_TABELA);			

		return id;
	}

	/** 
	 * Retorna data e hora atual formatada.
	 * @return dd-MM-yyyy HH:mm:ss
	 */
	private String obterDataAtual(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "dd-MM-yyyy HH:mm:ss
		Date date = new Date();

		return dateFormat.format(date);		
	}

	public boolean inserirTempApps(ResolveInfo infos, PackageManager manager){
		try {
			ContentValues values = new ContentValues();
			values.put(MyApps.PKG, infos.activityInfo.packageName);
			inserir(values, RepositorioAppScript.NOME_TABELA_TEMP);
		} catch (Exception e) {
			Log.e(CATEGORIA, "Ocorreu um erro ao inserir a lista de apps: "+ e.getMessage().toString());
			return false;
		}
		return true;
	}

	/**
	 * Insere na tabela de contagem de apps
	 * @param pkg
	 * @param countAccess
	 * @return
	 */	
	public boolean inserirKeepCountApps(String pkg, int countAccess){
		try {
			ContentValues values = new ContentValues();
			values.put(MyApps.PKG, pkg);
			values.put(MyApps._COUNT, countAccess);
			inserir(values, RepositorioAppScript.NOME_TABELA_KEEP_COUNT_USAGE);
		} catch (Exception e) {
			Log.e(CATEGORIA, "Ocorreu um erro ao inserir a contagem de uso do app: "+ e.getMessage().toString());
			return false;
		}
		return true;
	}

	/**
	 * Insere uma lista de apps, usado na primeira vez que se usa o aplicativo.
	 * @param infos
	 * @param manager
	 * @return true ok false = erro.
	 */
	public boolean inserirApps(ResolveInfo infos, PackageManager manager){
		Drawable img = null;
		try {
			//for(ResolveInfo resolveInfo : infos){
			ScreenUtil screenUtil = new ScreenUtil();
			ContentValues values = new ContentValues();
			values.put(MyApps.NAME, infos.loadLabel(manager).toString());
			values.put(MyApps.NAME_NO_ACCENT, ReplaceAssento.replace(infos.loadLabel(manager).toString()));
			img =  screenUtil.resizeImage(infos.loadIcon(manager));	
			values.put(MyApps.IMAGE, screenUtil.getBitmapAsByteArray(((BitmapDrawable)img).getBitmap()));
			values.put(MyApps.PKG, infos.activityInfo.packageName);
			values.put(MyApps.ACTIVITY_NAME, infos.activityInfo.name);
			//long id = 
			inserir(values, NOME_TABELA);
			//}
		} catch (Exception e) {
			Log.e(CATEGORIA, "Ocorreu um erro ao inserir a lista de apps: "+ e.getMessage().toString());
			return false;
		}
		return true;
	}

	/**
	 * Insere um único registro.
	 * @param values
	 * @return
	 */
	private long inserir(ContentValues values, String nomeTabela) throws Exception{
		long id = 0;
		//try {			
		id = db.insert(nomeTabela, "", values);
		//} catch (SQLException sqlException) {
		//Log.e(CATEGORIA, "Ocorreu um erro ao inserir um registro unico." + sqlException.getMessage().toString());
		//}
		return id;
	}

	private int atualizar(ContentValues cv, String where, String[] whereArgs) throws Exception{
		//int count = 0;

		return db.update(NOME_TABELA, cv, where, whereArgs);	

	}

	public int deletar(String pkg) {
		String where = MyApps.PKG + "=?";
		String _pkg = pkg;
		String[] whereArgs = new String[] {_pkg};
		int count = deletar(where, whereArgs);
		return count;
	}
	/**
	 * Deleta o app através do seu pacote.
	 * @param where
	 * @param whereArgs
	 * @return
	 */
	public int deletar(String where, String[] whereArgs) {
		int count = 0;		
		try {
			count = db.delete(NOME_TABELA, where, whereArgs);			
		} catch (SQLException sqlException) {
			Log.e(CATEGORIA, "Ocorreu um erro ao deletar o registro." + sqlException.getMessage().toString());
		}
		Log.i(CATEGORIA, "Deletou [" + count +"] registros");
		return count;
	}

	/**
	 * Busca o(s) app(s) pelo filtro informado.
	 * @param filtro
	 * @return
	 */
	public List<MyApp> buscarApps(String filtro, Context context){
		List<MyApp> list = new ArrayList<MyApp>();
		Cursor c = null;
		try {
			//			Cursor c = db.query(true, NOME_TABELA, MyApp.colunas, MyApps.NAME + "=" + filtro, null,
			//					null, null, null, null);
			String piece = "";
			if(PreferencesUtil.preferencesChecked(Preferences.KEY_PREF_PIECE_NAME, context)){
				piece = "%";
			}

			c = db.rawQuery("SELECT * FROM "+ NOME_TABELA + " WHERE " + MyApps.NAME_NO_ACCENT + " LIKE '"+piece +filtro+"%'" 
					+ "ORDER BY "+MyApps.NAME_NO_ACCENT+" COLLATE NOCASE", null);

			if(c.moveToFirst()) {
				//Recupera os índices das colunas:
				int idXId = c.getColumnIndex(MyApps._ID);
				int idXNome = c.getColumnIndex(MyApps.NAME);
				int idXImage = c.getColumnIndex(MyApps.IMAGE);
				int idXPkg = c.getColumnIndex(MyApps.PKG);
				int idXActivityName = c.getColumnIndex(MyApps.ACTIVITY_NAME);
				//Loop até o final
				do{
					MyApp app = new MyApp();
					list.add(app);
					//Recupera os apps
					app.setId(c.getLong(idXId));
					app.setName(c.getString(idXNome));
					app.setImage(c.getBlob(idXImage));
					app.setPkg(c.getString(idXPkg));
					app.setActivityName(c.getString(idXActivityName));

				}while(c.moveToNext());	

			}			

		} catch (SQLException sqlException) {
			Log.e(CATEGORIA, "Erro ao buscar app pelo filtro: " + sqlException.toString());
		}catch (Exception e) {
			Log.e(CATEGORIA, "Erro no método buscar app: " + e.getMessage().toString());
		}finally{
			if(c != null){
				c.close();
			}
		}
		return list;
	}

	/**
	 * Obtem um cursor com todos os apps do DB ordenados pelo nome.
	 * @return Cursor
	 */
	public Cursor getCursor(){
		try {
			//select * from myA
			return db.query(NOME_TABELA, MyApp.colunas, null, null, null, null, MyApps.NAME_NO_ACCENT + " COLLATE NOCASE", null);

		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			Log.e(CATEGORIA, "Erro ao buscar todos os apps em getCursor: " +sqlException.getMessage().toString());
			return null;
		}
	}
	// no such table: my_apps: , while compiling: SELECT _id, name, image, pkg, activity_name FROM my_apps
	/**
	 * Lista todos os apps do DB.
	 * @return List<MyApp>
	 */
	public List<MyApp> listarApps(){
		List<MyApp> list = new ArrayList<MyApp>();
		Cursor c = null;
		try {

			c = getCursor();
			if(c.moveToFirst()) {
				//Recupera os índices das colunas:
				int idXId = c.getColumnIndex(MyApps._ID);
				int idXNome = c.getColumnIndex(MyApps.NAME);
				int idXImage = c.getColumnIndex(MyApps.IMAGE);
				int idXPkg = c.getColumnIndex(MyApps.PKG);
				int idXActivityName = c.getColumnIndex(MyApps.ACTIVITY_NAME);
				//Loop até o final
				do{
					MyApp app = new MyApp();
					list.add(app);
					//Recupera os apps
					app.setId(c.getLong(idXId));
					app.setName(c.getString(idXNome));
					app.setImage(c.getBlob(idXImage));
					app.setPkg(c.getString(idXPkg));
					app.setActivityName(c.getString(idXActivityName));

				}while(c.moveToNext());	

			}			

		} catch (SQLException sqlException) {
			Log.e(CATEGORIA, "Erro ao listar todos os apps: " + sqlException.toString());
		}catch (Exception e) {
			Log.e(CATEGORIA, "Erro no método listar todos os apps: " + e.getMessage().toString());
		}if(c != null){
			c.close();
		}
		return list;
	}

	/**
	 * Verifica se possui algum app no banco de dados 
	 * @return true se possui app false caso não possui.
	 */
	public boolean isAppsInDataBase(){
		Cursor cursor = null;
		try {

			cursor = getCursor();
			if(cursor != null && cursor.getCount() > 0){
				return true;
			}
		} catch (Exception e) {
			Log.e("Erro isAppsInDataBase()", "Erro ao verificar se existem apps na DB. " + e.getMessage());
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return false;
	}

	/**
	 * Atualiza a coluna Count_Access que indica que o app selecionado ganhou mais um ponto como favorito.
	 * @param pkg
	 */
	public void updateCountAccess(String pkg){
		Cursor c = null;
		Integer count = null;
		try {
			//			c = db.query(true, NOME_TABELA, MyApp.colunas, MyApps.COUNT_ACCESS + "= '" + pkg+"'", null,
			//					null, null, null, null);
			//			c = db.rawQuery("SELECT * FROM "+ NOME_TABELA + " WHERE " + MyApps.NAME_NO_ACCENT + " LIKE '" +filtro+"%'" 
			//					, null);

			c = db.rawQuery("SELECT "+MyApps.COUNT_ACCESS+" FROM "+ NOME_TABELA + " WHERE " + MyApps.PKG + " = '" +pkg+"'" 
					, null);

			if(c.moveToFirst()) {
				int idXCountAccess = c.getColumnIndex(MyApps.COUNT_ACCESS);
				do{
					count = c.getInt(idXCountAccess);

				}while(c.moveToNext());

			}				

			if(count != null){
				count++;
			}else{
				count = 1;
			}

			ContentValues cv = new ContentValues();
			cv.put(MyApps.COUNT_ACCESS, count);

			String where = MyApps.PKG + "= ?";
			String _pkg = pkg;
			String[] whereArgs = new String[] {_pkg};
			int result = atualizar(cv, where, whereArgs);

			//Log.e("Count", ""+result);

		} catch (SQLException sqlException) {
			Log.e(CATEGORIA, "Erro ao atualizar contagem de acesso do app: " + sqlException.toString());
		}catch (Exception e) {
			Log.e(CATEGORIA, "Erro no método updateCountAccess: " + e.getMessage().toString());
		}finally{
			if(c != null){
				c.close();
			}
		}

	}
	/**
	 * Mantém a contagem dos apps mesmo depois do aplicativo atualizado.
	 * @param pkg
	 */
	public void updateKeepCountAcces(String pkg, Integer countAccess){
		Cursor c = null;
		try {

			ContentValues cv = new ContentValues();
			cv.put(MyApps.COUNT_ACCESS, countAccess);			

			String where = MyApps.PKG + "= ?";
			String _pkg = pkg;
			String[] whereArgs = new String[] {_pkg};
			int result = atualizar(cv, where, whereArgs);

		} catch (SQLException sqlException) {
			Log.e(CATEGORIA, "Erro ao atualizar contagem de acesso do app: " + sqlException.toString());
		}catch (Exception e) {
			Log.e(CATEGORIA, "Erro no método updateCountAccess: " + e.getMessage().toString());
		}finally{
			if(c != null){
				c.close();
			}
		}

	}

	/**
	 * Retorna uma lista com os apps mais acessados.
	 * @return List MyApp
	 */
	public List<MyApp> findTopsApps(int qtdApps){
		List<MyApp> list = new ArrayList<MyApp>();
		Cursor c = null;
		try {

			c = db.rawQuery("SELECT * FROM "+ NOME_TABELA + " ORDER BY "+MyApps.COUNT_ACCESS + " DESC LIMIT "+ qtdApps 
					, null);
			if(c.moveToFirst()) {
				//Recupera os índices das colunas:
				int idXId = c.getColumnIndex(MyApps._ID);
				int idXNome = c.getColumnIndex(MyApps.NAME);
				int idXImage = c.getColumnIndex(MyApps.IMAGE);
				int idXPkg = c.getColumnIndex(MyApps.PKG);
				int idXActivityName = c.getColumnIndex(MyApps.ACTIVITY_NAME);
				int idXCountAccess = c.getColumnIndex(MyApps.COUNT_ACCESS);
				//Loop até o final
				do{
					if(c.getInt(idXCountAccess) == 0){
						continue;
					}
					MyApp app = new MyApp();
					list.add(app);
					//Recupera os apps
					app.setId(c.getLong(idXId));
					app.setName(c.getString(idXNome));
					app.setImage(c.getBlob(idXImage));
					app.setPkg(c.getString(idXPkg));
					app.setActivityName(c.getString(idXActivityName));
					app.setCountAccess(c.getInt(idXCountAccess));

				}while(c.moveToNext());	

			}			

		} catch (SQLException sqlException) {
			Log.e(CATEGORIA, "Erro ao buscar Top apps " + sqlException.toString());
		}catch (Exception e) {
			Log.e(CATEGORIA, "Erro no método findTopsApps " + e.getMessage().toString());
		}finally{
			if(c != null){
				c.close();
			}
		}
		return list;
	}

	public List<MyApp> findLastInstalled(int qtdApps){
		List<MyApp> list = new ArrayList<MyApp>();
		Cursor c = null;
		try {

			c = db.rawQuery("SELECT * FROM "+ NOME_TABELA + " WHERE "+MyApps.DATE_INSTALLED + " IS NOT NULL ORDER BY "+MyApps.DATE_INSTALLED + " DESC LIMIT "+qtdApps 
					, null);

			if(c.moveToFirst()) {
				//Recupera os índices das colunas:
				int idXId = c.getColumnIndex(MyApps._ID);
				int idXNome = c.getColumnIndex(MyApps.NAME);
				int idXImage = c.getColumnIndex(MyApps.IMAGE);
				int idXPkg = c.getColumnIndex(MyApps.PKG);
				int idXActivityName = c.getColumnIndex(MyApps.ACTIVITY_NAME);
				int idXDateIntalled = c.getColumnIndex(MyApps.DATE_INSTALLED);
				//int idXCountAccess = c.getColumnIndex(MyApps.COUNT_ACCESS);
				//Loop até o final
				do{				
					MyApp app = new MyApp();
					list.add(app);
					//Recupera os apps
					app.setId(c.getLong(idXId));
					app.setName(c.getString(idXNome));
					app.setImage(c.getBlob(idXImage));
					app.setPkg(c.getString(idXPkg));
					app.setActivityName(c.getString(idXActivityName));

				}while(c.moveToNext());	

			}			

		} catch (SQLException sqlException) {
			Log.e(CATEGORIA, "Erro ao buscar Top apps " + sqlException.toString());
		}catch (Exception e) {
			Log.e(CATEGORIA, "Erro no método findTopsApps " + e.getMessage().toString());
		}finally{
			if(c != null){
				c.close();
			}
		}
		return list;
	}

	/**
	 * Fecha conexão com 
	 */	
	public void fechar() {
		if(db != null && db.isOpen()){
			db.close();
		}		
	}

}
