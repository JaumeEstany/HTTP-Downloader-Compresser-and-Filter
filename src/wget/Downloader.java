package wget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import filters.Html2AsiiInputStream;

/**
 * Recieving an URL and a number NUM, downloads to visualize locally a webpage from the URL passed into a file generated from the URL and the NUM passed.
 * Prints an explaining sentence about the download.
 * 
 * This class extends Thread, then implements Runnable. Calling .start() on a Downloader instance will create a new thread which will do the download and transform job.
 * 
 * Attributes:
 * 		LINK: final String that stores the link it's suppoused to download from.
 * 		NUM: final int that stores the number that corresponds to this Downloader
 * 
 * @author Jaume i Miquel
 * @see Thread
 */
public class Downloader extends Thread {
	
	private static final char[] NOT_ADDMITTED_CHARS={'~','<','>','\\','|',':'};
	private static final String HTML_CONTENT_TYPE = "text/html";
	
	private final String LINK;				//link del que ha de descarregar els bytes
	private final int NUM;					//el numero que cal afegir al final del nom del fitxer
	private boolean mustAscii;				//quan es crea el nom, es guarda si se li ha d'aplicar el filtre ascii
	
	/**
	 * Checks if the passed char is not in the private final static array NOT_ADDMITTED_CHARS.
	 * 
	 * @param c
	 * 		char: target character to check
	 * 
	 * @return
	 * 		boolean: returns whether the C character is addmitted or not
	 */
	private static boolean addmittedChar(char c){
		
		for(char i :NOT_ADDMITTED_CHARS){
			if(c==i){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the file name from the intern info and the open URLConnection.
	 * 
	 * 
	 * @param d
	 * 		Downloader object: it's used to set the proper flags
	 * 
	 * @param url
	 * 		@see URLConnection
	 * 			It's used to check whether the contentType of the page is HTML or not.
	 * 
	 * @return
	 * 		the name for the file
	 */
	private String makeFileName(URLConnection url){		//funcio encarregada de construir el nom del fitxer a partir de la URL i el numero que segueix el fitxer
		
		String aux=LINK.split("//")[1];		//eliminem la part http://
		
		String[] arr = aux.split("/");		//dividim el string per /
		
		if(arr.length>1){					//si en te, agafem la ultima part
			aux = arr[arr.length-1];
		}
		else{								//si no, substituim per index.html
			aux="index.html";
		}
		
		arr=aux.split("\\.");				//dividim el string per punts .
		
		if(arr.length<=1){			//si no contenia punts, index.html
			arr = new String[2];
			arr[0]="index";
			arr[1]="html";
		}
		
		arr[arr.length-2]=arr[arr.length-2]+NUM;	//afegim el numero al final de la penultima part
		aux="";					//array auxiliar on sumar
		for(String s : arr){
			aux+=s+'.';			//sumem al array els elements separats per punts
		}
		
		//evitar segons quins caracters al nom  del nostre arxiu
		StringBuilder sb = new StringBuilder(aux.length()+4);	//capacitat inicial de tots els caracters+4 per si afegissim .asc
		for(int i=0;i<aux.length()-1;i++){						//fins length-1 per treure el ultim caracter, que ens quedava pendent del bucle anterior
			final char c=aux.charAt(i);
			if(addmittedChar(c)){			//si el caracter no es un caracter extrany, l'afegim
				sb.append(c);
			}
		}
		
		if(ArgumentHolder.getA() && url.getContentType().startsWith(HTML_CONTENT_TYPE)){	//aprofitem el StringBuilder per afegir la extensio
			mustAscii=true;
			sb.append(".asc");
		}
		
		aux=new String(sb);		//passem un altre cop a String
		
		return aux;				//i retornem
		
	}
	
	/**
	 * Unique constructor for Downloader.
	 * 
	 * @param url
	 * @param NUM
	 */
	public Downloader(String url, int num){
		
		this.LINK=url;		//carreguem les variables de classe amb les rebudes pel constructor
		this.NUM=num;
		this.mustAscii=false;	//mustAscii es false per defecte
	}
	
	/**
	 * Taking into account the ArgumentHolder values, downloads the URL webpage into a new created file.
	 * If the target URL content is HTML, will remove the tags and comments from the file if the -a parameter was passed.
	 * If the -z parameter was passed will also compress the file into a .zip file.
	 * If the -gz parameter was passed will also compress the file into a .gz file.
	 * 
	 */
	public void run(){
		
		try {
			URLConnection url = (new URL(LINK)).openConnection();						//creem un Obj URLConnection amb el link
			InputStream is = url.getInputStream();					//rebem el stream de input
			
			final String NAME = makeFileName(url);			//creem un String amb el nom del arxiu mes el 
			
			final String EXTENDED_NAME = NAME+((ArgumentHolder.getZ())?(".zip"):(""))+((ArgumentHolder.getGZ())?(".gz"):(""));
															//EXTENDED_NAME es el nom del arxiu amb les extensions
			
			if(mustAscii){
				is=new Html2AsiiInputStream(is);
			}
			
			OutputStream os = new FileOutputStream(new File(EXTENDED_NAME));		//el FileOutputStream ara escriu a f
			
			//si es vol compressio GZIP, creem un GZIPOutputStream amb el OutputStream que teniem i el deixem a la mateixa referencia
			if(ArgumentHolder.getGZ()){
				os = new GZIPOutputStream(os);
			}
			
			//si es vol compressio ZIP, creem un ZipOutputStream amb el OutputStream que teniem i el deixem a la mateixa referencia
			//despres colï¿½loquem com a seguent entrada del zip el nom del arxiu sense terminacions de zip o de gzip
			if(ArgumentHolder.getZ()){
				os = new ZipOutputStream(os);
				((ZipOutputStream)os).putNextEntry(new ZipEntry(NAME));
			}
			

			//en el bucle, passar els bytes del is al os
			int b;
			while( (b=is.read()) != -1){
				os.write(b);
			}
			
			
			//tanquem els Streams
			is.close();
			os.close();
			System.out.println("Pàgina: \""+LINK+"\" descarregada correctament.");
		} catch (IOException e) {
			//si hi ha un error de IO, es que no s'ha pogut establir connexio amb la pagina
			System.out.println("ERROR: No s'ha pogut establir connexió amb la pàgina \""+LINK+"\".");
		}
		
	}
	
	
}
