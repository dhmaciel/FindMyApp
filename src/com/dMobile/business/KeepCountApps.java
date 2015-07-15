package com.dMobile.business;

import android.content.Context;

import com.dMobile.bean.MyApp;
import com.dMobile.repositorio.RepositorioApp;

public class KeepCountApps {

	private Context ctx;
	private RepositorioApp repositorioApp;

	public KeepCountApps() {
		super();
	}

	public KeepCountApps(Context context){
		this.ctx = context;
		repositorioApp = new RepositorioApp(ctx);
	}

	public void prepareTableKeepCount(){
		repositorioApp.dropTableKeepCountApps();
		repositorioApp.createTableKeepCountApps();
	}

	public void insertKeepCount(MyApp myApp){
		if(myApp != null){
			String pkg = myApp.getPkg();
			int countAccess = myApp.getCountAccess();
			repositorioApp.inserirKeepCountApps(pkg, countAccess);
		}
	}

	public void deleteKeepCount(String pkg){

	}

	public MyApp findAppKeepCount(String pkg, RepositorioApp repositorioApp){
		this.repositorioApp = repositorioApp;
		return repositorioApp.buscarApp(pkg);

		//insertKeepCount(myApp);
	}

	public Boolean isSamePkg(String pkgDeleted, String pkgNew){
		if(pkgDeleted.trim().equals(pkgNew.trim())){
			return true;
		}
		return false;
	}

}
