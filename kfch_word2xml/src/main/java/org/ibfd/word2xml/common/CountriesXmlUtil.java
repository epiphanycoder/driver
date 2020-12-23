package org.ibfd.word2xml.common;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * 
 * @author asfak.mahamud
 *
 */

public class CountriesXmlUtil {
    
	/**
	 * Instance of <code>CountriesXmlUtil</code> itself
	 */
	private static CountriesXmlUtil cXU = null;
	
	/**
	 * Document object for countries.xml
	 */
	private Document doc = null;
	
	/**
	 * Path of Countries.xml in the online.
	 */
	private static final String COUNTRIES_XML_PATH = "http://dtd.ibfd.org/dtd/config/countries.xml";
	
	/**
	 * private constructor
	 */
	private CountriesXmlUtil() {
		doc = DocUtils.readDocument(COUNTRIES_XML_PATH);
	}
	
	/**
	 * 
	 * @return CountriesXmlUtil
	 */
	public static CountriesXmlUtil getInstance() {
		if (cXU == null) {
			cXU = new CountriesXmlUtil();
		}
		return cXU;
	}
	
	/**
	 * <pre>
	 * For countryDivName = "Alberta" this function will return value cdc "ab".
	 * 
	 *&lt;country treaty="yes">
     *    &lt;shortname>Canada&lt;/shortname>
     *    &lt;name>CANADA&lt;/name>
     *    &lt;code>ca&lt;/code>
     *    &lt;countrydiv>
	 *        &lt;countrydivname>Provincial Taxation&lt;/countrydivname>
	 *            &lt;cdc>prvn&lt;/cdc>
	 *            &lt;type>province&lt;/type>
     *        &lt;/countrydiv>
     *        &lt;countrydiv>
	 *            &lt;countrydivname>Alberta&lt;/countrydivname>
	 *            &lt;cdc>ab&lt;/cdc>
	 *            &lt;type>province&lt;/type>
     *    &lt;/countrydiv>
     *&lt;/country>
     *
	 * </pre>
	 *  
	 * @param countryDivName
	 * @return
	 */
	public String getCDCCode (String countryDivName) throws Exception {
		countryDivName = getCorrectCountryDiv(countryDivName);
		Element cdc = (Element)this.doc.selectSingleNode("//country/countrydiv[countrydivname='" + countryDivName +"']/cdc");
		if (cdc == null) {
			throw new Exception ("For countrydiv '" + countryDivName + "' cdcCode is not found in " + COUNTRIES_XML_PATH);
		}
		return cdc.getStringValue().trim();
	}

	/**
	 * <pre>
	 * For countryDivName = "Alberta" this function will return value shortname "Canada".
	 * 
	 *&lt;country treaty="yes">
     *    &lt;shortname>Canada&lt;/shortname>
     *    &lt;name>CANADA&lt;/name>
     *    &lt;code>ca&lt;/code>
     *    &lt;countrydiv>
	 *        &lt;countrydivname>Provincial Taxation&lt;/countrydivname>
	 *            &lt;cdc>prvn&lt;/cdc>
	 *            &lt;type>province&lt;/type>
     *        &lt;/countrydiv>
     *        &lt;countrydiv>
	 *            &lt;countrydivname>Alberta&lt;/countrydivname>
	 *            &lt;cdc>ab&lt;/cdc>
	 *            &lt;type>province&lt;/type>
     *    &lt;/countrydiv>
     *&lt;/country>
     *
	 * </pre>
	 * 
	 * @param countryDivName
	 * @return
	 */
	public String getCountry (String countryDivName) {
		countryDivName = getCorrectCountryDiv(countryDivName);
		Element country = (Element)this.doc.selectSingleNode("//country[countrydiv/countrydivname='" + countryDivName +"']/shortname");
		if (country == null) {
			return (null);
		}
		return country.getStringValue().trim();
	}
	
	/**
	 * <pre>
	 * For countryDivName = "Alberta" this function will return value code "ca".
	 * 
	 *&lt;country treaty="yes">
     *    &lt;shortname>Canada&lt;/shortname>
     *    &lt;name>CANADA&lt;/name>
     *    &lt;code>ca&lt;/code>
     *    &lt;countrydiv>
	 *        &lt;countrydivname>Provincial Taxation&lt;/countrydivname>
	 *            &lt;cdc>prvn&lt;/cdc>
	 *            &lt;type>province&lt;/type>
     *        &lt;/countrydiv>
     *        &lt;countrydiv>
	 *            &lt;countrydivname>Alberta&lt;/countrydivname>
	 *            &lt;cdc>ab&lt;/cdc>
	 *            &lt;type>province&lt;/type>
     *    &lt;/countrydiv>
     *&lt;/country>
     *
	 * </pre>
	 * 
	 * @param countryDivName
	 * @return
	 */
	public String getCountryCode (String countryDivName) {
		countryDivName = getCorrectCountryDiv(countryDivName);
		Element countryCode = (Element)this.doc.selectSingleNode("//country[countrydiv/countrydivname='" + countryDivName +"']/code");
		if (countryCode == null) {
			return (null);
		}
		return countryCode.getStringValue().trim();
	}
	
	
	/**
	 * <pre>
	 * For countryDivName = "Alberta" this function will return value type "province".
	 * 
	 *&lt;country treaty="yes">
     *    &lt;shortname>Canada&lt;/shortname>
     *    &lt;name>CANADA&lt;/name>
     *    &lt;code>ca&lt;/code>
     *    &lt;countrydiv>
	 *        &lt;countrydivname>Provincial Taxation&lt;/countrydivname>
	 *            &lt;cdc>prvn&lt;/cdc>
	 *            &lt;type>province&lt;/type>
     *        &lt;/countrydiv>
     *        &lt;countrydiv>
	 *            &lt;countrydivname>Alberta&lt;/countrydivname>
	 *            &lt;cdc>ab&lt;/cdc>
	 *            &lt;type>province&lt;/type>
     *    &lt;/countrydiv>
     *&lt;/country>
     *
	 * </pre>
	 * 
	 * @param countryDivName
	 * @return
	 */
	public String getCountryDivType (String countryDivName) {
		countryDivName = getCorrectCountryDiv(countryDivName);
		Element countryDivType = (Element)this.doc.selectSingleNode("//country/countrydiv[countrydivname='" + countryDivName +"']/type");
		if (countryDivType == null) {
			return (null);
		}
		return countryDivType.getStringValue().trim();
	}
	
	/**
	 * @param countryDivName
	 * @return
	 */
	private String getCorrectCountryDiv(String countryDivName) {
		if(countryDivName.matches("^Neuch.*tel$")) {
			countryDivName = "Neuch�tel";
		}
		return countryDivName;
	}
	
	
//	public static void main (String [] args) throws Exception {
////		System.out.println(CountriesXmlUtil.getInstance().getCDCCode("Alberta"));
////		System.out.println(CountriesXmlUtil.getInstance().getCountry("Alberta"));
//		
//		CountriesXmlUtil instance = CountriesXmlUtil.getInstance();
////		List nodes = instance.doc.selectNodes("//countrydivname");
////		for (Object object : nodes) {
////			Node node = (Node) object;
////			System.out.println(node.getText());
////		}
//
//		System.out.println(instance.getCDCCode("Neuchấtel"));
//		
//	}
}
