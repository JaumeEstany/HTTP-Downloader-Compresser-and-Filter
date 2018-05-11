package wget;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Pure static class only publicly containing the main executable method.
 * 
 * 
 * @author Jaume i Miquel
 *
 */
public class Wget {
	
	/**
	 * Finishes the current thread with a little exit message.
	 */
	private static void endThread(){
		System.out.println("Exiting.");
		System.exit(-1);
	}
	
	/**
	 * Main executable method
	 * @param args
	 * 		Should recieve:
	 * 			"-f": indicates the next parameter is the path to the file.
	 * 			PATH: path where the URLs will be read from
	 * 
	 * 		Can recieve:
	 * 			"-a": the HTML content pages will get its tags and comments removed
	 * 			"-z": will compress the output file in a .zip file
	 * 			"-gz": will compress the output file in a .gz file
	 * 
	 */
	public static void main(String[] args) {
		
		for(String s:args){
			System.out.print(s+" ");
		}
		System.out.println("\n");
		
		//bloc per inicialitzar el holder d'arguments
		try {
			String name=null;		//referencia String on guardarem el nom del fitxer
			boolean a=false;		//boolea on guardem si s'ha trobat el parametre A
			boolean z=false;		//boolea on guardem si s'ha trobat el parametre Z 
			boolean gz=false;		//boolea on guardem si s'ha trobat el parametre GZ
			
			for(int i=0;i<args.length;i++){
				switch (args[i]) {
					case "-f":		//si trobem -f, el seguent element sera el nom del fitxer
							i++;
							if (i >= args.length) {				//si -f era l'ultim element, error i acabem
								System.out.println("The file path couldn't be gotten.");
								Wget.endThread();
							}
							name = args[i];
							break;
					//si s'ha trobat algun dels parametres, posem els respectius booleans a true 
					case "-a":
							a = true;
							break;
					case "-z":
							z = true;
							break;
					case "-gz":
							gz = true;
							break;
					//si no era res d'aquests parametres, error
					default:
							System.out.println("Unreconized argument.");
							Wget.endThread();
							break;
				}
				
			}
			//si el nom es null, error, ja que significa que no se li va passar per parametre -f
			if(name==null){
				System.out.println("The file path couldn't be gotten.");
				Wget.endThread();
			}
			
			ArgumentHolder.init(name, a, z, gz);		//inicialitzem el argumentHolder amb els resultats del tractament dels parametres
			
		} catch (Exception e) {		//si ha sorgit alguna excepcio, missatge d'error i acabar
			System.out.println("An error ocurred during the argument load.");
			Wget.endThread();
		}
		
		
		//try que considera totes les excepcions que poden sortir
		try {
			//creem el BufferedReader preparat per llegir el arxiu urls.txt
			BufferedReader br = new BufferedReader(new FileReader(ArgumentHolder.getFileName()));
			
			//comencem el bucle per llegir el fixer i anar creant threads per la descarrega
			int i=1;		//i es el comptador que dira quin num d'arxiu es cadascun
			String aux=br.readLine();
			while(aux!=null){
				//System.out.println(aux);	//imprimim link
				
				Downloader d= new Downloader(aux,i);	//creem el downloader i deixem que s'executi
				d.start();
				
				aux = br.readLine();	//actualitzem aux i i
				i++;
			}
			
			
			br.close();		//tanquem el BufferedReader
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			Wget.endThread();
		} catch (IOException e) {
			System.out.println("Error when reading or closing the buffer.");
			Wget.endThread();
		}
		
	}
	
	
}
