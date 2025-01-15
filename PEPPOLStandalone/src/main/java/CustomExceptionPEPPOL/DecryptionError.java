package CustomExceptionPEPPOL;

public class DecryptionError extends Exception {
	private static final long serialVersionUID = 1L;
	
	public DecryptionError(String message) {
        super("005:Decryption Error:"+message);
    }

}
