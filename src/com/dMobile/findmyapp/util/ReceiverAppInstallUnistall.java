package com.dMobile.findmyapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

import com.dMobile.bean.MyApp;
import com.dMobile.business.KeepCountApps;
import com.dMobile.findmyapp.MainActivity;
import com.dMobile.repositorio.RepositorioApp;

/**
 * Classe que monitora apps instalados ou desinstalados para atualizar o banco de dados.
 * @author douglas
 *
 */
public class ReceiverAppInstallUnistall extends BroadcastReceiver{

	private PackageManager manager;
	private RepositorioApp repositorioApp;
	private static String pkgKeep;
	private static Integer countAccessKeep;
	public static Integer iconSize;
	//	public static String objetoEmail = "";

	@Override
	public void onReceive(Context context, Intent intent) {		

		//Adiciona app no banco de dados (Obsoleto)
		//This constant was deprecated in API level 14.
		if ("android.intent.action.PACKAGE_INSTALL".equals(intent.getAction())) {

			IntentFilter intentFilter = new IntentFilter();	
			intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
			intentFilter.addDataScheme("package");

			Uri data = intent.getData();
			String packageName = data.getEncodedSchemeSpecificPart();

			try {
				manager = context.getPackageManager();
				ApplicationInfo app = manager.getApplicationInfo(packageName, 0);
				this.iconSize = getIconSize(context);
				long id = repositorioApp.inserir(app, manager);

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}catch (Exception e) {
				Log.e("Receiver package added", e.getMessage());
				//				StringWriter sw = new StringWriter();
				//				PrintWriter pw = new PrintWriter(sw);
				//				e.printStackTrace(pw);
				//				Intent it = new Intent(context, SendEmail.class);	
				//				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				//				it.putExtra("EMAIL", "Erro Receiver PACKAGE_INSTALL:\n"+ e + "\n"+sw);
				//				context.startActivity(it);

			} 

			// Remove app do Banco de dados ao ser desinstalado do sistema.
		}else if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())){

			try {				

				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
				intentFilter.addDataScheme("package");

				Uri data = intent.getData();			
				String packageName = data.getEncodedSchemeSpecificPart();

				KeepCountApps countApps = new KeepCountApps();

				repositorioApp = new RepositorioApp(context);
				MyApp  myApp = countApps.findAppKeepCount(packageName, repositorioApp);
				pkgKeep = myApp.getPkg();
				countAccessKeep = myApp.getCountAccess();
				repositorioApp.deletar(packageName);

				//Responsável por dar refresh no Grid, caso não seja chamado uma nova activity para dar refresh no grid.
				if( MainActivity.mainActivity != null){
					MainActivity.mainActivity.refreshGrids(MainActivity.mainActivity.findTxt.getText());	
					MainActivity.mainActivity = null;
				}
			} catch (Exception e) {
				Log.e("Erro receiver app removed", "Package removed");
				//				StringWriter sw = new StringWriter();
				//				PrintWriter pw = new PrintWriter(sw);
				//				e.printStackTrace(pw);
				//				Intent it = new Intent(context, SendEmail.class);	
				//				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				//				it.putExtra("EMAIL", "Erro Receiver PACKAGE_REMOVED:\n"+ e + "\n"+sw);
				//				context.startActivity(it);
			}

			//Adiciona app no banco de dados
		}else if("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())){

			try {			

				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);			
				intentFilter.addDataScheme("package");

				Uri data = intent.getData();			
				String packageName = data.getEncodedSchemeSpecificPart();

				manager = context.getPackageManager();
				ApplicationInfo app = manager.getApplicationInfo(packageName, 0);

				repositorioApp = new RepositorioApp(context);

				if(app != null && manager != null){					
					//					objetoEmail = "Receiver -NAME:"+ app.loadLabel(manager).toString()+ "\n "+
					//							"IMAGE: " + app.loadIcon(manager) + "\nPKG: " + app.packageName + "\n";
					this.iconSize = getIconSize(context);
					long id = repositorioApp.inserir(app, manager);
					if(pkgKeep != null && countAccessKeep != null){
						KeepCountApps keepCountApps = new KeepCountApps();
						if(app != null && keepCountApps.isSamePkg(pkgKeep, app.packageName)){
							repositorioApp.updateKeepCountAcces(app.packageName, countAccessKeep);
						}
					}
				}			

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}catch (Exception e) {
				Log.e("Erro receiver app added", "Package added");
				//				StringWriter sw = new StringWriter();
				//				PrintWriter pw = new PrintWriter(sw);
				//				e.printStackTrace(pw);
				//				Intent it = new Intent(context, SendEmail.class);	
				//				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				//				it.putExtra("EMAIL", "Erro Receiver PACKAGE_ADDED:\n"+ e + "\n"+sw + "\n"+ objetoEmail);
				//				context.startActivity(it);
				//				objetoEmail = null;
			} 

		}
		pkgKeep = null;
		countAccessKeep = null;
		iconSize = null;

	}

	private Integer getIconSize(Context context){
		Integer iconSize;
		try {
			PreferencesUtil util = new PreferencesUtil();
			iconSize = util.getSettings(PreferencesUtil.KEY_CONF_SIZE_ICON, context);
		} catch (Exception e) {
			return null;
		}

		return iconSize;
	}

}




