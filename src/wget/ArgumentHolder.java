package wget;


/**
 * Can and must be initialized only once in order to be used.
 * After initialization, publicly offers getters to get the main parameters.
 * 
 * @author Jaume i Miquel
 *
 */
public class ArgumentHolder {
	
	private static String file=null;
	private static boolean aArg=false;
	private static boolean zArg=false;
	private static boolean gzArg=false;
	
	/**
	 * Initializes the ArgumentHolder to hold the main parameter values.
	 */
	public static void init(String p, boolean a, boolean z, boolean gz){
		
		if(file!=null || p==null){		// si el String del nom es null, o si el ArgumentHolder ja ha sigut inicialitzar, excepcio
			throw new UnsupportedOperationException();
		}
		else{
			//carregar el ArgumentHolder amb les variables rebudes
			file=p;
			aArg=a;
			zArg=z;
			gzArg=gz;
		}

	}
	
	//getters pels camps del ArgumentHolder. si no s'ha inicialitzat en demanar, excepcio
	/**
	 * Returns the recieved file name.
	 * @return
	 */
	public static String getFileName(){
		if(file==null){
			throw new UnsupportedOperationException();
		}
		return file;
	}
	
	/**
	 * Returns whether the -a parameter was recieved or not.
	 * @return
	 */
	public static boolean getA() {
		if(file==null){
			throw new UnsupportedOperationException();
		}
		return aArg;
	}

	/**
	 * Returns whether the -z parameter was recieved or not.
	 * @return
	 */
	public static boolean getZ() {
		if(file==null){
			throw new UnsupportedOperationException();
		}
		return zArg;
	}

	/**
	 * Returns whether the -gz parameter was recieved or not.
	 * @return
	 */
	public static boolean getGZ() {
		if(file==null){
			throw new UnsupportedOperationException();
		}
		return gzArg;
	}

	
}
