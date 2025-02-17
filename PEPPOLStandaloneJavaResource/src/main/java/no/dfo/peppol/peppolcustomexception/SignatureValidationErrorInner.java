package no.dfo.peppol.peppolcustomexception;

public class SignatureValidationErrorInner extends Exception{
	
	/**
	 * This class is used to throw custom exception
	 * Occurred in inner asice validation in Inbound flow
	 */
	private static final long serialVersionUID = 1L;

	public SignatureValidationErrorInner(String message) {
        super("001:Signature Validation error inner ASiC:"+message);
    }

}