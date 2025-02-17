package no.dfo.peppol.peppolcustomexception;

public class SignatureValidationErrorOuter extends Exception {
	private static final long serialVersionUID = 1L;

	public SignatureValidationErrorOuter(String message) {
        super("002:Signature Validation error Outer ASiC:"+message);
    }
}
