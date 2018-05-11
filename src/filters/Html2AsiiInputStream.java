package filters;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * This class extends BufferedInputStream.
 * Overrides its read() method to avoid returning the characters inside the HTML tags and comments it detects.
 * Uses its mark(), reset() and skip(int) tools to avoid the data.
 * 
 * @author Jaume
 * @see BufferedInputStream
 */
public class Html2AsiiInputStream extends BufferedInputStream {
	
	private static final String S_COMMENT = "<!--";
	private static final String E_COMMENT = "-->";

	/**
	 * Public and unique constructor.
	 * 
	 * @param is
	 * 		Recieves the InputStream the object will be created from.
	 * 
	 */
	public Html2AsiiInputStream(InputStream is){
		super(is);
	}
	
	/**
	 * @return the next byte of data removing the encountered HTML tags and comments 
	 * 
	 */
	@Override
	public int read() throws IOException {
		
		int[] arr = new int[4];
		
		while(true){
			mark(6);
			lecture(arr);
			
			if(arr[0]!='<'){
				reset();
				break;
			}
			else if(matchesStart(arr)){
				deleteComment();
			}
			else{
				reset();
				skip(1);

				while(true){
					mark(4);
					lecture(arr);
					if(matchesStart(arr)){
						deleteComment();
					}
					else if(arr[0]=='>'){
						reset();
						skip(1);
						break;
					}
					else if(arr[0]==-1){
						return -1;
					}
					else{
						reset();
						skip(1);
					}
					
				}
			}
			
		}

		return super.read();
	}
	
	
	private void deleteComment() throws IOException{
		
		int[] arr = new int[3];
		
		while(true){
			mark(6);
			lecture(arr);
			
			if(matchesEnd(arr)){
				break;
			}
			else{
				reset();
				skip(1);
			}
		}
		
	}
	
	private void lecture(int[] arr) throws IOException{
		
		for(int i=0;i<arr.length;i++){
			arr[i]=super.read();
		}
	}
	
	private boolean matchesStart(int[] arr){
		
		for(int i=0;i<S_COMMENT.length();i++){
			if(arr[i]!=S_COMMENT.charAt(i)){
				return false;
			}
		}
		
		return true;
	}
	
	private boolean matchesEnd(int[] arr){
		
		for(int i=0;i<E_COMMENT.length();i++){
			if(arr[i]!=E_COMMENT.charAt(i)){
				return false;
			}
		}
		
		return true;
	}
	
	
}
