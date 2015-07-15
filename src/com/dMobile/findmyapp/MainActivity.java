package com.dMobile.findmyapp;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.dMobile.bean.MyApp;
import com.dMobile.business.AppsInDatabase;
import com.dMobile.business.KeepCountApps;
import com.dMobile.findmyapp.util.BackgroundUtil;
import com.dMobile.findmyapp.util.KeyboardUtil;
import com.dMobile.findmyapp.util.PreferencesUtil;
import com.dMobile.findmyapp.util.ReceiverLanguageChanged;
import com.dMobile.findmyapp.util.ScreenUtil;
import com.dMobile.repositorio.RepositorioApp;
import com.dMobile.repositorio.RepositorioAppScript;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class MainActivity extends SherlockActivity implements Runnable{

	public EditText findTxt;
	private GridView apkGridView;
	private GridView apkGridViewLastInstalled;
	private List<ResolveInfo> apps = null;
	private List<MyApp> apks = null;
	private List<MyApp> apksLastInstalled = null;
	private PackageManager packageManager = null;
	private MainActivity activity = null;
	private Intent mainIntent = null; 
	private RepositorioAppScript repositorioAppScript = null;
	private RepositorioApp repositorioApp = null;

	private static final int DIALOG_PROGRESS = 1;
	private ProgressDialog mProgressDialog;
	private int mProgress;
	private int MAX_PROGRESS;
	private Handler hd;
	private TextView topApps;
	private TextView lastInstalledApps;
	private RelativeLayout linearLayout;
	private ImageButton deleteImgButton;
	private ScreenUtil screenUtil;
	private int qtdAppsLinha; // quantidade de apps que são exibidos em cada linha do gridView.
	public static Intent localService;
	private View viewGrid = null; 
	private Boolean isScreenLandscape; // true = paisagem, falso = retrato.
	private int screenSize; // tamanho da tela.
	private String moreUsed;
	private boolean isRestart;
	private boolean isAllApps;
	public static Integer iconSize; 
	public static MainActivity mainActivity; 
	private Context context;
	private ProgressDialog m_dialog;
	private Boolean isdefaultBackground = null;
	private AdView adView;
	private static final String AD_UNIT_ID = "ca-app-pub-9986252378745376/8839352642";
	private AdRequest adRequest;
	private boolean isAds;
	private boolean isShowKeyboard;
	private LinearLayout linearLayouAdMob;//linearLayout2
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {		

			adView = new AdView(this);
			adView.setAdUnitId(AD_UNIT_ID);
			adView.setAdSize(AdSize.BANNER);

			activity = this;
			ScreenUtil screenUtil = new ScreenUtil(activity);

			// Variável para iniciar e parar a notificação.
			if(localService == null){
				localService = new Intent("SERVICE_NOTIFICATION");			
			}

			// Carrega o xml preferences com valores padr�o. 
			PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

			//Inicia serviço e coloca o app em backgroud se a opção estiver selecionada em preferences.
			if(PreferencesUtil.preferencesChecked(Preferences.KEY_PREF_VIEW_NOTIFICATION, this) && (
					ReceiverLanguageChanged.countChangedConfiguration >= 2) ){		
				startService(localService);
				ReceiverLanguageChanged.countChangedConfiguration = 0;
				//			ReceiverLanguageChanged.isLanguageChangedDefault = true;
				//			ReceiverLanguageChanged.isLanguageChangedConfiguration = false;			
			}

			//Exibe ou esconde o teclado dependendo da opção em preferences.
			if(PreferencesUtil.preferencesChecked(Preferences.KEY_PREF_HIDE_KEYBOARD, this)){		
				/*Esconde o teclado */
				KeyboardUtil.hideSoftKeyboardOnConfiguration(activity);				
			}else{
				/*Exibe o teclado */
				this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);	
			}

			// Image Button delete
			deleteImgButton = (ImageButton) findViewById(R.id.deleteImgBtn);
			deleteImgButton.setOnTouchListener(gDeleteImgAction);

			linearLayout = (RelativeLayout) findViewById(R.id.linearLayoutMain);
			//		
			topApps = (TextView) findViewById(R.id.topApps);
			moreUsed = (String) topApps.getText();
			lastInstalledApps = (TextView) findViewById(R.id.LastApps);

			repositorioAppScript = new RepositorioAppScript(this);
			repositorioApp = new RepositorioApp(this);

			PreferencesUtil util = new PreferencesUtil();
			iconSize = util.getSettings(PreferencesUtil.KEY_CONF_SIZE_ICON, activity);

			if(iconSize == 0){
				util.saveIconSizeConfigurationData(activity);
				iconSize = util.getSettings(PreferencesUtil.KEY_CONF_SIZE_ICON, activity);
			}

			boolean isAppsInDB = repositorioApp.isAppsInDataBase();

			if(isAppsInDB == false){
				KeepCountApps countApps = new KeepCountApps(this);
				countApps.prepareTableKeepCount();
				BackgroundUtil backgroundUtil = new BackgroundUtil();
				backgroundUtil.applyBackground((RelativeLayout) findViewById(R.id.linearLayoutMain));
				isdefaultBackground = false;

				mainIntent = new Intent(Intent.ACTION_MAIN,null);
				mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				packageManager = getPackageManager();
				apps = packageManager.queryIntentActivities(mainIntent, 0);

				try {
					inserirApps(apps);
				} catch (Exception e) {
					Log.e("Salvar banco de dados.", "Erro ao gravar no banco de dados." + e.getMessage());
					e.printStackTrace();
				}
				//Esconde o teclado.
				KeyboardUtil.hideSoftKeyboardOnConfiguration(activity);	
			}
			// Aplica background padrão
			if(isdefaultBackground == null){
				BackgroundUtil.applyDefaultBackground((RelativeLayout) findViewById(R.id.linearLayoutMain));
				isdefaultBackground = true;
			}

			qtdAppsLinha = screenUtil.qtdAppsLinha();
			isScreenLandscape = screenUtil.isScreenLandscape();

			if(isScreenLandscape){
				RelativeLayout layout = (RelativeLayout) findViewById(R.id.linearLayoutMain);
				BackgroundUtil.removeBackground(layout);
				layout.setBackgroundColor(0xFFEBEBEB);
			}

			screenSize = screenUtil.screenSize();

			showTopApps(qtdAppsLinha);
			showLastAppsInstalled(qtdAppsLinha);
			hideOrShowGridLastAppsInstalled(); // verifica a posição da tela e o tamanho para esconder o grid Last Installed

			findTxt = (EditText) findViewById(R.id.editTextFind);
			findTxt.setOnTouchListener(gEditTextFind);
			/*Capturar as teclas pressionadas */
			findTxt.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {

					//topApps.setVisibility(TextView.GONE); // Esconde a barra de subtitulo.
					//				changeImageButtonEditText(s);
					showAds(false);
					isAllApps = false;
					refreshGrids(s);

					if(gGoMyEndScroll != null){
						apkGridView.setOnScrollListener(gGoMyEndScroll);					
					}
					isShowKeyboard = false;				
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					//caracter = s.toString();
					//Log.e(caracter, "Depois");

				}
			});

		} catch (Exception e) {
			Log.e("Main activity", "Erro em Main activity - onCreate");
		}

	}
	
	/**
	 * Método para buscar os apps depedendo da busca no editText.
	 * @param s
	 */
	public void refreshGrids(CharSequence s) {
		lastInstalledApps.setVisibility(TextView.GONE);// Esconde a barra de subtitulo.
		apkGridViewLastInstalled.setVisibility(GridView.GONE);// Esconde a os itens last Installed.
		try {

			if(!s.toString().equals("")){
				//			deleteImgButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_erase));
				deleteImgButton.setVisibility(ImageView.VISIBLE);
				deleteImgButton.setClickable(true);
				apks = repositorioApp.buscarApps(s.toString(), activity);
				topApps.setText((apks.size() <= 1) ? apks.size() +" "+ getResources().getString( R.string.app_found)+":"
						: apks.size() +" "+ getResources().getString( R.string.apps_found)+":");
				topApps.setVisibility(TextView.VISIBLE);
				apkGridView.setAdapter(new ApkAdapter(activity, apks, packageManager));
				apkGridView.setOnItemClickListener(gGoMyAppMoreUsed);			
			}else{
				//			deleteImgButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.search));
				deleteImgButton.setVisibility(ImageView.INVISIBLE);
				deleteImgButton.setClickable(false);
				topApps.setText(moreUsed);
				hideOrShowGridLastAppsInstalled();
				showTopApps(qtdAppsLinha);
				showLastAppsInstalled(qtdAppsLinha);
				hiddenMainLastGrid();
			}
		} catch (Exception e) {
			Log.e("Erro Main Activity", "Erro regresh grids");
		}
	}
	
	/**
	 * Esconde ou exibe o gridView e a barra de titulo dependendo da orientação e do tamanho da tela.
	 */
	private void hideOrShowGridLastAppsInstalled(){
		if(isScreenLandscape){// && screenSize != Configuration.SCREENLAYOUT_SIZE_XLARGE
			lastInstalledApps.setVisibility(TextView.GONE);// Esconde a barra de subtitulo.
			apkGridViewLastInstalled.setVisibility(GridView.GONE);// Esconde a os itens last Installed.
//		}
		}else if(apksLastInstalled.size() != 0){
			lastInstalledApps.setVisibility(TextView.VISIBLE);// Exibe a barra de subtitulo.
			apkGridViewLastInstalled.setVisibility(GridView.VISIBLE);// Exibe a os itens last Installed.
		}
	}
	
	/**
	 * Esconde o teclado.
	 */
	private void hiddenSoftKeyboard(EditText editText){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * Busca e exibe os apps mais utilizados.
	 */
	private void showTopApps(int qtdApps) {
		apkGridView = (GridView) findViewById(R.id.gridViewApps);
		registerForContextMenu(apkGridView);
		apks = repositorioApp.findTopsApps(qtdApps);			
		if(apks.size() > 0){
			apkGridView.setAdapter(new ApkAdapter(activity, apks, packageManager));
			apkGridView.setOnItemClickListener(gGoMyAppMoreUsed);
		}else{
			topApps.setVisibility(TextView.GONE);
		}

	}
	/**
	 * Se a listagem for vazia (ainda n�o ter nenhum app em more e last ), 
	 */
	private void hiddenMainLastGrid(){
		if(apks.size() == 0){
			apkGridView.setAdapter(null);
		}if(apksLastInstalled.size() == 0){
			apkGridViewLastInstalled.setAdapter(null);
		}
	}

	/**
	 * Busca e exibe os últimos apps instalados.
	 */
	private void showLastAppsInstalled(int qtdApps) {
		apkGridViewLastInstalled = (GridView) findViewById(R.id.gridViewInstalledApps);
		registerForContextMenu(apkGridViewLastInstalled);
		apksLastInstalled = repositorioApp.findLastInstalled(qtdApps);	

		if(apksLastInstalled.size() > 0){
			apkGridViewLastInstalled.setAdapter(new ApkAdapter(activity, apksLastInstalled, packageManager));
			apkGridViewLastInstalled.setOnItemClickListener(gGoMyAppLatInstalled);
		}else{
			lastInstalledApps.setVisibility(TextView.GONE);			
		}
	}

	/**
	 * Classe anônima para definir a ação do imageButton Delete.
	 */
	private OnTouchListener gDeleteImgAction = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			findTxt.setText("");
			refreshGrids(findTxt.getText());
			return false;
		}

	};
	
	private OnTouchListener gEditTextFind = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			isShowKeyboard = true;
			//gGoMyEndScroll = null;
			if(linearLayouAdMob != null){
				showAds(false);
			}
			
			//			linearLayout2
			return false;			
		}

	};

	/**
	 * Classe anônima usada para chamar o app selecionado no gridView principal(More used).
	 */
	private OnItemClickListener gGoMyAppMoreUsed = new OnItemClickListener(){			
		public void onItemClick(AdapterView<?> parent, View v, int posicao,
				long id) {

			try {				

				MyApp app = apks.get(posicao);

				String pkg = app.getPkg();
				String activityname = app.getActivityName();

				callSelectedApp(pkg, activityname);
			} catch (Exception e) {
				showTopApps(qtdAppsLinha);
			}
		}
	};

	/**
	 * Classe anônima usada para chamar o app selecionado no gridView secundário(Last installed).
	 */
	private OnItemClickListener gGoMyAppLatInstalled = new OnItemClickListener(){			
		public void onItemClick(AdapterView<?> parent, View v, int posicao,
				long id) {

			try {

				MyApp app = apksLastInstalled.get(posicao);

				String pkg = app.getPkg();
				String activityname = app.getActivityName();

				callSelectedApp(pkg, activityname);
			} catch (Exception e) {
				showLastAppsInstalled(qtdAppsLinha);
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		mProgressDialog = new ProgressDialog(MainActivity.this);
		mProgressDialog.setIcon(android.R.drawable.ic_dialog_alert);
		mProgressDialog.setTitle(getResources().getString(R.string.preparing_list));
		mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(MAX_PROGRESS);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);

		return mProgressDialog;
	}

	private void inserirApps(List<ResolveInfo> apps) throws Exception{
		MAX_PROGRESS = apps.size();
		showDialog(DIALOG_PROGRESS);
		mProgress = 0;
		hd = new Handler();
		Thread td = new Thread(MainActivity.this);
		td.start();

		mProgressDialog.setProgress(0);       

	}

	@Override
	public void run(){
		try {
			for (ResolveInfo info : apps) {

				boolean isSaveApps = repositorioApp.inserirApps(info, packageManager);

				hd.post(new Runnable() {

					@Override
					public void run() {								              
						mProgress++;
						mProgressDialog.incrementProgressBy(1);
					}
				});
			}
			mProgressDialog.dismiss();

		} catch (Exception e) {
			Log.e("banco de dados os apps", "Erro ao inserir no banco de dados os apps. " + e.getMessage());
		}
	}

	/**
	 * Inicia o app selecionado.
	 * @param pkg
	 * @param activityname
	 */
	private void callSelectedApp (final String pkg, String activityname){

		Intent intent = null;

		try {			

			intent = new Intent(Intent.ACTION_MAIN, null);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent = getPackageManager().getLaunchIntentForPackage(pkg);
			hiddenSoftKeyboard(findTxt);

			//startActivity(intent);

			if(intent != null){
				repositorioApp.updateCountAccess(pkg);
				activity.startActivity(intent);
				checkPrefCloseApp();
			}else{			
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle(getResources().getString(R.string.unknown_app));
				builder.setMessage(getResources().getString(R.string.not_run_app));
				builder.setPositiveButton(getResources().getString(R.string.yes_button), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						repositorioApp.deletar(pkg);
						refreshGrids("");					
					}
				});

				builder.setNegativeButton(getResources().getString(R.string.no_button), null);
				builder.show();
			}
		} catch (Exception e) {
			callContactsActivity(pkg);
		}
		
	}
	
	/**
	 * Chamado caso ocorra algum erro de � encontrar a activity.
	 * @param pkg
	 */
	private void callContactsActivity(String pkg){

		try {			
			if(pkg != null && pkg.contains("contacts")){				
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("content://contacts/people/"));
				repositorioApp.updateCountAccess(pkg);
				startActivity(intent);
				checkPrefCloseApp();
			}
		} catch (Exception e) {
			Log.e("Erro em callContactsActivity", e.getMessage());
		}
	}	
	/**
	 * Fecha o FindMyApp se a opção estiver marcada em preferences.
	 */
	private void checkPrefCloseApp(){
		if(PreferencesUtil.preferencesChecked(Preferences.KEY_PREF_CLOSE_APP, this)){	
			this.closeApp();				
		}
	}

	 @Override
	public void onStop() {
		super.onStop();	
	}
	
	@Override
	public void onPause (){
	
		if(adView != null){
			adView.pause();
		}		
		apks = null;
		//closeReferences();		
		super.onPause();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		repositorioApp.fechar();
		repositorioAppScript.fechar();
//		eraseGridImageReferences();
//		repositorioApp = null;
//		repositorioAppScript = null;
		
		if(adView != null){
			adView.destroy();			
		}
		
	}

	
	@Override
	protected void onResume() {
		if(adView != null){
			adView.resume();
		}
		super.onResume();
	}
	
	@Override
	public void onRestart (){
		super.onRestart();
		refreshGrids((findTxt.getText()!= null) ? findTxt.getText() : "" );
		
	}

	/**
	 * Fecha a aplicação e as conexões com o BD.
	 */
	private void closeApp(){		
		this.finish();       
	}
	
	private void eraseGridImageReferences(){
		try {

			int count = apkGridView.getCount();
			for (int i = 0; i < count; i++) {
				RelativeLayout relativeLayout = (RelativeLayout) apkGridView.getChildAt(i);
				if(relativeLayout != null){
					ImageView v = (ImageView) relativeLayout.getChildAt(0);
					if (v != null) {
						if (v.getDrawable() != null) v.getDrawable().setCallback(null);
						ImageView iv = (ImageView)v;
						iv.setImageDrawable(null);
					}
				}
			}
			int count2 = apkGridViewLastInstalled.getCount();
			for (int i = 0; i < count2; i++) {
				RelativeLayout relativeLayout = (RelativeLayout) apkGridViewLastInstalled.getChildAt(i);
				if(relativeLayout != null){
					ImageView v = (ImageView) relativeLayout.getChildAt(0);
					if (v != null) {
						if (v.getDrawable() != null) v.getDrawable().setCallback(null);
						ImageView iv = (ImageView)v;
						iv.setImageDrawable(null);
					}
				}
			}
		} catch (Exception e) {
			Log.e("eraseGridImageReferences()", "Erro ao limpar imagens do grid.");
		}
	}	

	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
	       com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
	       inflater.inflate(R.menu.action_bar, (com.actionbarsherlock.view.Menu) menu);
	       return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		//Handle item selection
		switch (item.getItemId()) {
			
		case R.id.action_all_apps:
			if(!isAllApps){
			findTxt.setText("");
			apks = repositorioApp.listarApps();
			apkGridView.setAdapter(new ApkAdapter(activity, apks, packageManager));
			topApps.setText((apks.size() <= 1) ? apks.size() +" "+ getResources().getString( R.string.app_found)+":"
					: apks.size() +" "+ getResources().getString( R.string.apps_found)+":");
			topApps.setVisibility(TextView.VISIBLE);
			isAllApps = true;
			apkGridView.setOnItemClickListener(gGoMyAppMoreUsed);
			
			adRequest = new AdRequest.Builder()
		   // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // Todos os emuladores
		   // .addTestDevice("13FDBFF654AC200481002CF9B35B59D9") // Meu telefone de teste Moto G
		    .build();
			apkGridView.setOnScrollListener(gGoMyEndScroll);
			
			//showAds(true);
			}else{
				findTxt.setText("");
				refreshGrids("");
				isAllApps = false;
				showAds(false);
				adRequest = null;
				//gGoMyEndScroll = null;
			}
			deleteImgButton.setVisibility(ImageButton.INVISIBLE);
			return true;
			
		case R.id.action_submenu_refresh_apps:
			isAllApps = false;
			if(m_dialog == null){
				m_dialog = new ProgressDialog(this);
			}
			mainIntent = new Intent(Intent.ACTION_MAIN,null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			packageManager = getPackageManager();
			apps = packageManager.queryIntentActivities(mainIntent, 0);
			if(mainActivity == null){
				mainActivity = this;
			}
			new RefreshTableApps().execute(apps);
			return true;
			
		case R.id.action_submenu_settings:		
			isAllApps = false;
			Intent intent = new Intent(MainActivity.this, Preferences.class);
			startActivity(intent);
			return true;
			
		default:
 			return super.onOptionsItemSelected(item);
		}
	}
	
	private void showAds(Boolean isShowAds){

		linearLayouAdMob = (LinearLayout) findViewById(R.id.banner_layout);
		if(isShowAds){
			if(adRequest != null){
				linearLayouAdMob.addView(adView);

				//				AdRequest adRequest = new AdRequest.Builder().build();

				adView.loadAd(adRequest);
				isAds = true;
			}
						
		}else{
			//adView.destroy();
			linearLayouAdMob.removeAllViews();
			isAds = false;
		}
	}
	
	/**
	 * Evento para detectar o fim do scroll e esconder o adMob no final.
	 */
	private OnScrollListener gGoMyEndScroll = new OnScrollListener(){

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			if(view.getLastVisiblePosition()==(totalItemCount-1)){
				showAds(false);
			}
			else if(!isAds){
				showAds(true);
			}				


		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub

		}

	};

	
	/**
	 * Abre um menu ao dar um long click em um app.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if (v.getId() == R.id.gridViewApps || v.getId() == R.id.gridViewInstalledApps) {
			viewGrid = v;

			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

			// adicionar o titulo ao context menu.
			if(info != null && info.targetView != null){
				String title = (String) ((TextView) info.targetView.findViewById(R.id.appname)).getText();				 
				menu.setHeaderTitle(title);				 
			}

			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_list, menu);

		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		String pkg = null;
		String activityname = null;

		if(viewGrid != null && viewGrid.getId() == R.id.gridViewApps){
			MyApp app = apks.get(info.position);
			pkg = app.getPkg();
			activityname = app.getActivityName();

		}else if(viewGrid.getId() == R.id.gridViewInstalledApps){
			MyApp app = apksLastInstalled.get(info.position);
			pkg = app.getPkg();
			activityname = app.getActivityName();
		}

		switch(item.getItemId()) {
		case R.id.run_app:
			if(pkg != null && activityname != null){
				callSelectedApp(pkg, activityname);				
			}
			return true;
		case R.id.uninstall_app:
			if(pkg != null){
				uninstallApp(pkg);
			}

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	/** 
	 * Método para desinstalar um app a partir de seu pacote.
	 * @param pkg
	 */
	private void uninstallApp (String pkg){
		mainActivity = this;
		Uri packageURI = Uri.parse("package:"+pkg);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivity(uninstallIntent);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		View v = getCurrentFocus();

		if (v != null && 
				(ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && 
				v instanceof EditText && 
				!v.getClass().getName().startsWith("android.webkit."))
		{
			int scrcoords[] = new int[2];
			v.getLocationOnScreen(scrcoords);
			float x = ev.getRawX() + v.getLeft() - scrcoords[0];
			float y = ev.getRawY() + v.getTop() - scrcoords[1];

			if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
				KeyboardUtil.hideKeyboardOnTouch(activity);
			isShowKeyboard = false;

			if(isdefaultBackground == false){
				BackgroundUtil.applyDefaultBackground((RelativeLayout) findViewById(R.id.linearLayoutMain));
				isdefaultBackground = true;
			}
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private class RefreshTableApps extends AsyncTask<List<ResolveInfo>, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// initialize the dialog
			m_dialog.setTitle(getResources().getString(R.string.refresh));
			m_dialog.setMessage(getResources().getString(R.string.refresh_msg));
			m_dialog.setIndeterminate(true);
			m_dialog.setCancelable(false);
			m_dialog.show();
		}

		@Override
		protected Boolean doInBackground(List<ResolveInfo>... params) {
			try {

				packageManager = getPackageManager();
				AppsInDatabase appsInDatabase = new AppsInDatabase(MainActivity.this);
				appsInDatabase.executeComparison(params[0], packageManager);
			} catch (Exception e) {
				Log.e("RefreshTableApps", "Ocorreu um erro ao atualizar apps no banco.");
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				mainActivity.refreshGrids(mainActivity.findTxt.getText());
				mainActivity = null;
			}
			// close the dialog
			m_dialog.dismiss();
			//do any other UI related task
		}
	}
}
