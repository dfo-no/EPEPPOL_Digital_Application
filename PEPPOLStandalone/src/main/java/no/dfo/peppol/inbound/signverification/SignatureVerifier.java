package no.dfo.peppol.inbound.signverification;

import java.util.Collection;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.util.Store;

import CustomExceptionPEPPOL.SignatureValidationErrorInner;
import CustomExceptionPEPPOL.SignatureValidationErrorOuter;
import no.dfo.peppol.common.functions.BCHelper;
/**
 * @author erlend
 */
public class SignatureVerifier {

   

    private static JcaSimpleSignerInfoVerifierBuilder jcaSimpleSignerInfoVerifierBuilder =
            new JcaSimpleSignerInfoVerifierBuilder().setProvider(BCHelper.getProvider());

    
    public boolean SignVerify(byte[] data, byte[] signature,String packageType) throws SignatureValidationErrorInner, SignatureValidationErrorOuter {
    	 ////////////////////////////////
        boolean verified = false;

        try {
            CMSSignedData cms = new CMSSignedData(new CMSProcessableByteArray(data), signature);
            Store store = cms.getCertificates();
            SignerInformationStore signers = cms.getSignerInfos();
            Collection c = signers.getSigners();
            for (SignerInformation signerInformation : signers.getSigners()) {
            	 X509CertificateHolder x509Certificate = (X509CertificateHolder) store.getMatches(signerInformation.getSID()).iterator().next();
					/*
					 * Collection certCollection = store.getCertificates(signer.getSID()); Iterator
					 * certIt = certCollection.iterator(); X509Certificate cert = (X509Certificate)
					 * certIt.next();
					 */
            	 
                verified = signerInformation.verify(jcaSimpleSignerInfoVerifierBuilder.build(x509Certificate));
            }
        } catch (Exception e) {
        	 verified = false;
        	 if(packageType.equalsIgnoreCase("Inner_Asic")) {
        		 throw new  SignatureValidationErrorInner(e.getMessage());
        	 }else {
        		 throw new  SignatureValidationErrorOuter(e.getMessage());
        	 }
        	
           
           
        }

    	
		return verified;
    	
    	
    }
	
}
