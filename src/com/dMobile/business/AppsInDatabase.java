package com.dMobile.business;

import java.util.List;

import com.dMobile.repositorio.RepositorioApp;
import com.dMobile.repositorio.RepositorioAppScript;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class AppsInDatabase {

	private Context ctx;
	private RepositorioApp repositorioApp;
	private List<ResolveInfo> apps;
	private PackageManager manager;

	public AppsInDatabase(Context context){
		this.ctx = context;
		repositorioApp = new RepositorioApp(ctx);
	}

	public void executeComparison (List<ResolveInfo> apps, PackageManager manager)throws Exception{
		this.apps = apps;
		this.manager = manager;
		repositorioApp.createTableTempApps();
		insertAppsTemp(apps, manager);
	}

	private void insertAppsTemp (List<ResolveInfo> apps, PackageManager manager)throws Exception{

		for(ResolveInfo info : apps){
			repositorioApp.inserirTempApps(info, manager);
		}
		compareAndDeleteApps();
	}

	private void compareAndDeleteApps()throws Exception{
		List<String> listDeleted = repositorioApp.compareDeletedInsertedApps(
				RepositorioAppScript.NOME_TABELA, RepositorioAppScript.NOME_TABELA_TEMP);
		if(listDeleted != null && listDeleted.size() > 0){
			for(String pkg : listDeleted){
				repositorioApp.deletar(pkg);
			}
		}
		compareAndInsertApps();
	}

	private void compareAndInsertApps()throws Exception{
		List<String> listDeleted = repositorioApp.compareDeletedInsertedApps(
				RepositorioAppScript.NOME_TABELA_TEMP, RepositorioAppScript.NOME_TABELA);
		if(listDeleted != null && listDeleted.size() > 0){
			for(String pkg : listDeleted){
				for(ResolveInfo infos : apps){
					if(infos.activityInfo.packageName.equals(pkg)){
						repositorioApp.inserirApps(infos, manager);
					}
					continue;
				}
			}
		}

		deleteTable();
	}

	private void deleteTable()throws Exception{
		repositorioApp.dropTableTempApps();
		closeDB();
	}

	private void closeDB()throws Exception{
		repositorioApp.fechar();
	}


}
