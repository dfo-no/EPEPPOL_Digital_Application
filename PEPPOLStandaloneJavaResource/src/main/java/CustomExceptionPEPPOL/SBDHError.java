package CustomExceptionPEPPOL;

public class SBDHError extends Exception {
	private static final long serialVersionUID = 1L;

	public SBDHError(String message) {
        super("003:"+message);
}
}
