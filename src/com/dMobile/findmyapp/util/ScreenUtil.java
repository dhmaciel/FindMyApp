package com.dMobile.findmyapp.util;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.dMobile.findmyapp.MainActivity;

public class ScreenUtil {

	private MainActivity context;
	private final int ICON_ORIGINAL_SIZE = 48;

	public ScreenUtil(MainActivity context) {
		this.context = context;
	} 	

	public ScreenUtil() {
		super();
	}

	/**
	 * Retorna a quantidade de apps que são exibidos em cada linha do gridView, depedendo do tamanho da Screen.
	 * @return int com a quantidade de apps.
	 */
	public int qtdAppsLinha(){
		float scalefactor = context.getResources().getDisplayMetrics().density * 100;
		int number = context.getWindowManager().getDefaultDisplay().getWidth();
		int columns = Math.round(((float) number / (float) scalefactor));
		int sumColumn = 0;
		//Verifica se o a orientação está como paisagem, se sim adiciona mais um elemento no GridView.
		//		if(isScreenLandscape()){		
		//		   columns = columns +1;            
		//		}

		if(isScreenLandscape()){
			sumColumn ++;
		}
		if((columns > 3 && columns <= 6) && screenSize() == Configuration.SCREENLAYOUT_SIZE_NORMAL){
			columns = columns * 2;
		}else if((columns > 3 && columns <= 12) && screenSize() == Configuration.SCREENLAYOUT_SIZE_LARGE){
			columns = (columns + sumColumn) * 3;
		}else if(columns > 3  && screenSize() == Configuration.SCREENLAYOUT_SIZE_XLARGE){
			columns = (columns + sumColumn) * 4;
		}

		return columns;

	}


	/**
	 * Verifica se a orientação da tela está em posição paisagem.
	 * @return
	 */
	public Boolean isScreenLandscape(){

		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int orientation = display.getRotation();

		if (orientation == Surface.ROTATION_90
				|| orientation == Surface.ROTATION_270) {
			return true;			
		}
		return false;		
	}

	/**
	 * Obtem o tamanho do ícone baseano no dispositivo.
	 * @return
	 */
	public Integer iconSize(){

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();

		Float densityDpi = metrics.density;

		Integer icon = (int)(ICON_ORIGINAL_SIZE * densityDpi);

		return icon;
	}

	/**
	 * Verifica se o icone está definido como valor padrão para não haver diferença no GridView. 
	 * @param btm
	 * @return Bitmap
	 */
	public Bitmap resizeImageicon (Bitmap btm){
		try {	
			Drawable image = new BitmapDrawable(btm); 
			Integer iconSize = getIconSize();
			//Drawable image = new BitmapDrawable(getResources(),btm);
			if(image.getIntrinsicHeight() != iconSize || image.getIntrinsicWidth() != iconSize){
				Bitmap b = ((BitmapDrawable)image).getBitmap();
				btm = Bitmap.createScaledBitmap(b, iconSize, iconSize, false);
			}
		} catch (Exception e) {
			return btm;
		}
		return btm;
	}

	/**
	 * Converte e comprime um Bitmap para byte[]
	 * @param bitmap
	 * @return byte[] 
	 */
	public byte[] getBitmapAsByteArray(Bitmap bitmap) throws Exception{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap = resizeImageicon(bitmap);
		bitmap.compress(CompressFormat.PNG, 0, outputStream);       
		return outputStream.toByteArray();
	}

	/**
	 * Ajusta o tamanho da imagem para o padrão do dispositivo.
	 * @param image
	 * @return
	 */
	public Drawable resizeImage(Drawable image) throws Exception{
		try {
			Integer iconSize = getIconSize();				
			if(image.getIntrinsicHeight() != iconSize || image.getIntrinsicWidth() != iconSize){
				Bitmap b = ((BitmapDrawable)image).getBitmap();
				Bitmap bitmapResized = Bitmap.createScaledBitmap(b, iconSize, iconSize, false);
				image = new BitmapDrawable(bitmapResized);
			}

		} catch (Exception e) {
			return image;
		}
		return image;
	}

	private Integer getIconSize(){
		Integer iconSize;
		if(MainActivity.iconSize != null){
			iconSize = MainActivity.iconSize;
		}else{
			iconSize = ReceiverAppInstallUnistall.iconSize;
		}
		if(iconSize != null && iconSize == 0){
			return null;
		}

		return iconSize;
	}

	public int screenSize(){
		int screenSize = context.getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK;

		return screenSize;
	}

}
