package com.dMobile.findmyapp.util;

public class ReplaceAssento {

	public static String replace(String palavra){

		palavra = palavra.replaceAll("[�����]","A");  
		palavra = palavra.replaceAll("[�����]","a");  
		palavra = palavra.replaceAll("[����]","E");  
		palavra = palavra.replaceAll("[����]","e");  
		palavra = palavra.replaceAll("[����]","I");  
		palavra = palavra.replaceAll("[����]","i");  
		palavra = palavra.replaceAll("[�����]","O");  
		palavra = palavra.replaceAll("[�����]","o");  
		palavra = palavra.replaceAll("[����]","U");  
		palavra = palavra.replaceAll("[����]","u");  
		//		palavra = palavra.replaceAll("�","C");  
		//		palavra = palavra.replaceAll("�","c");   
		palavra = palavra.replaceAll("[��]","y");  
		palavra = palavra.replaceAll("�","Y");  
		palavra = palavra.replaceAll("�","n");  
		palavra = palavra.replaceAll("�","N");  
		palavra = palavra.replaceAll("['<>|/]",""); 

		return palavra;
	}

}
