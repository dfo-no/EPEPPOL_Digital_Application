package CustomExceptionPEPPOL;


public class SigningError extends Exception {
	private static final long serialVersionUID = 1L;

	public SigningError(String message) {
        super("Signing eroor:"+message);
    }
}
