package com.dMobile.findmyapp.util;

public class ReplaceAssento {

	public static String replace(String palavra){

		palavra = palavra.replaceAll("[ÂÀÁÄÃ]","A");  
		palavra = palavra.replaceAll("[âãàáä]","a");  
		palavra = palavra.replaceAll("[ÊÈÉË]","E");  
		palavra = palavra.replaceAll("[êèéë]","e");  
		palavra = palavra.replaceAll("[ÎÍÌÏ]","I");  
		palavra = palavra.replaceAll("[îíìï]","i");  
		palavra = palavra.replaceAll("[ÔÕÒÓÖ]","O");  
		palavra = palavra.replaceAll("[ôõòóö]","o");  
		palavra = palavra.replaceAll("[ÛÙÚÜ]","U");  
		palavra = palavra.replaceAll("[ûúùü]","u");  
		//		palavra = palavra.replaceAll("Ç","C");  
		//		palavra = palavra.replaceAll("ç","c");   
		palavra = palavra.replaceAll("[ıÿ]","y");  
		palavra = palavra.replaceAll("İ","Y");  
		palavra = palavra.replaceAll("ñ","n");  
		palavra = palavra.replaceAll("Ñ","N");  
		palavra = palavra.replaceAll("['<>|/]",""); 

		return palavra;
	}

}
