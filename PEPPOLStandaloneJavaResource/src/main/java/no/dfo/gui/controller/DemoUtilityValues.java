package no.dfo.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class DemoUtilityValues {

	URL demoValuesResource = this.getClass().getResource("/application.properties");
	Properties demoAppProps = new Properties();

	String DemoKeystorePath;
	String DemoKeystorePassword;
	String DemoEncKeystoreAlias;
	String DemoSignKeystoreAlias;
	String DemoOrgnaizationNumber;
	String userIdentifier;
	String customerIdentifier;
	String divisionIdentifier;

	public DemoUtilityValues() throws IOException {
		demoAppProps.load(demoValuesResource.openStream());
	}

	

	public String getDemoKeystorePath() {
		return demoAppProps.getProperty("DemoKeystorePath").toString();
	}

	public String getDemoKeystorePassword() {
		return demoAppProps.getProperty("DemoKeystorePassword").toString();
	}

	public String getDemoEncKeystoreAlias() {
		return demoAppProps.getProperty("DemoEncKeystoreAlias").toString();
	}

	public String getDemoSignKeystoreAlias() {
		return demoAppProps.getProperty("DemoSignKeystoreAlias").toString();
	}

	public String getDemoOrgnaizationNumber() {
		return demoAppProps.getProperty("DemoOrgnaizationNumber").toString();
	}

	public String getUserIdentifier() {
		return demoAppProps.getProperty("userIdentifier").toString();
	}

	public String getCustomerIdentifier() {
		return demoAppProps.getProperty("customerIdentifier").toString();
	}

	public String getDivisionIdentifier() {
		return demoAppProps.getProperty("divisionIdentifier").toString();
	}

	
	
}
