package org.ibfd.word2xml.kfch;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.ibfd.word2xml.common.CountriesXmlUtil;
import org.ibfd.word2xml.common.TreeContent;
import org.ibfd.word2xml.common.TreeContentRoot;

import com.aspose.words.Cell;
import com.aspose.words.CellCollection;
import com.aspose.words.Document;
import com.aspose.words.DocumentVisitor;
import com.aspose.words.Font;
import com.aspose.words.Node;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphCollection;
import com.aspose.words.Row;
import com.aspose.words.Shading;

import java.util.Locale;

/**
 * 
 * @author asfak.mahamud
 *
 */
public class KFCHWordParser extends DocumentVisitor {
	/**
	 * Store KFCHWordData
	 */
	private KFCHWordData kFCHWordData;
	
	/**
	 * Checks whether first row is passed or not
	 */
	private boolean isFirstRowPassed = false;
	
	private TreeContent parent = null;
	
	
	
	
	/**
	 * File Cdc code means &lt;cdc/> value from countris.xml file in online
	 * 
	 * For example
	 * For countryDivName = "Alberta" fileCdcCode is "ab".
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
	 */
	private String fileCdcCode = null;
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public KFCHWordData parse(String fileName) {
		kFCHWordData = new KFCHWordData();
		isFirstRowPassed = false;

		Document doc;
		try {
			
			TreeContentRoot treeContentRoot = new TreeContentRoot(null, 0);
			parent = treeContentRoot;
			kFCHWordData.setTreeContentRoot(treeContentRoot);
			
			doc = new Document(fileName);
			doc.acceptAllRevisions();
			
		} catch (Exception e) {
			KFCHMain.handleException(e);
			return null;
		}

		try {
			doc.accept(this);
		} catch (Exception e) {
			KFCHMain.handleException(e);
			return null;
		}

		return kFCHWordData;
	}

	/**
	 *  Visits all rows....
	 */
	@Override
	 public int visitRowStart(Row row) throws Exception{
		//System.out.println(row.getText() + " cell foreground color: " + row.getCells().get(0).getCellFormat().getShading().getForegroundPatternColor());
		//System.out.println(row.getText() + " cell background color: " + row.getCells().get(0).getCellFormat().getShading().getBackgroundPatternColor());
		if (!isFirstRowPassed && isFirstRow(row)) {
			processFirstRow(row);
			isFirstRowPassed = true;
		}
		else if (isFirstRowPassed) {
			
			if (isLastReviewed(row)) {
				processLastReviewed(row);
			}
			else if (isHeading1(row)){
				processHeading1(row);
			}
            /*else if (isHeading3(row)){
            	processHeading3(row);
            }*/
			else if (isHeading2(row)){
				processHeading2(row);
			}
			else if (isNormal(row)){
				processNormal(row);
			}
		}
		return super.visitRowStart(row);
	}
	
	
	/**
	 * 
	 * @param row
	 */
	private void processLastReviewed(Row row) {
		String text = KFCHUtil.cleanText(row.getText());
		if (text.contains(":")) {
		    String name = null;
	        String date = null;
	        String sortdate = null;
	        int lastIndexOfColon = text.lastIndexOf(":");
	        if (text.length() > (lastIndexOfColon)) {
	            name = text.substring(0, lastIndexOfColon);
	            date = text.substring(lastIndexOfColon + 1).trim();

	            SimpleDateFormat fromFormat = new SimpleDateFormat("d MMM yyyy", Locale.US);
	            Date parsedDate;
	            try {
	                parsedDate = fromFormat.parse(date);
	                SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	                sortdate = toFormat.format(parsedDate);

	                //AppLogger.verboseDebug("## Date: [" + date + "] Sortdate: [" + sortdate + "]");
	                //setEffectiveReviewedValue(state, name, date, sortdate);
	                kFCHWordData.setReviewedName(name);
	                kFCHWordData.setReviewedDate(date);
	                kFCHWordData.setReviewedSortDate(sortdate);
	                
	            } catch (ParseException e) {
	                //AppLogger.verboseDebug("##" + state + " date: [" + date + "] parse error.");
	            }

	        }
            /*kFCHWordData.setReviewedName(paraText.substring(0,paraText.indexOf(":") + 1).trim());
            String dateValue = paraText.substring(paraText.indexOf(":") + 1).trim();
            
            String regex = "[^0-9]*([0-9]+)[[st]?|[nd]?|[rd]?|[th]?]+([^0-9]+)([0-9]+).*$";
            
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dateValue);
            String day = null, month = null, year = null;
            if (m.find() && m.groupCount() == 3) {
            	day   = m.group(1);
            	month = m.group(2).trim();
            	year  = m.group(3);
            }
            
            String ddMMMMyyyyDateStr = day + " " + month + " " + year;
            kFCHWordData.setReviewedDate(ddMMMMyyyyDateStr);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            Date ddMMMMyyyyDate = null;
            try {
            	ddMMMMyyyyDate = dateFormat.parse(ddMMMMyyyyDateStr);
            } catch (ParseException e) {
                 e.printStackTrace();
            }
            
            SimpleDateFormat sortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            kFCHWordData.setReviewedSortDate(sortDateFormat.format(ddMMMMyyyyDate));*/
			
			
		}
		
	}

	private boolean isLastReviewed(Row row) {
		if (KFCHUtil.isStr1ContainsStr2(row.getText(), "Last Reviewed"))
			return true;
		return false;
	}
	
	/**
	 * 
	 * @param row
	 */
	private void processHeading1(Row row) {
		KFCHMain.logger.info("Degug: Headin1 Row " + row.getText() );
		
		TreeContent currentParent = getCurrentParent(1);
		if (currentParent == null) return;
		
		KFCHHeading1 heading1 = new KFCHHeading1(currentParent, 1);
		String rowText = row.getText();
		
		if (KFCHUtil.isStr1ContainsStr2(rowText, "Companies")){
			setHeading1(heading1, KFCHAppConstant.XML_HEADING1_A_COMPANIES, "kfch_" + this.fileCdcCode + "_companies" , "A. Companies");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(rowText, "Individuals")){
			setHeading1(heading1, KFCHAppConstant.XML_HEADING1_B_INDIVIDUALS, "kfch_" + this.fileCdcCode + "_individuals", "B. Individuals");			
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(rowText, "Other Taxes")){
			setHeading1(heading1, KFCHAppConstant.XML_HEADING1_C_OTHER_TAXES, "kfch_" + this.fileCdcCode + "_other_taxes", "C. Other Taxes");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(rowText, "Turnover Taxes")){
			setHeading1(heading1, KFCHAppConstant.XML_HEADING1_D_TURNOVER_TAXES, "kfch_" + this.fileCdcCode + "_turnover_taxes", "D. Turnover Taxes");
		}
		
		currentParent.addtableRowContentChild(heading1);
		parent = heading1;
		
	}

	
	/**
	 * 
	 * @param row
	 */
	private void processHeading2(Row row) {
		
		TreeContent currentParent = getCurrentParent(2);
		if (currentParent == null) return;
		
		KFCHHeading2 heading2 = new KFCHHeading2(currentParent, 2);
		String rowText = row.getText();
		
		//First KFCHHeading2
		if (KFCHUtil.isStr1ContainsStr2(rowText, "1. Resident companies")){
			setHeading2(heading2, "resident_companies", rowText, ("kfch_" + this.fileCdcCode + "_resident_companies"), "");
		}
		
		//Second KFCHHeading2
		else if (KFCHUtil.isStr1ContainsStr2(rowText, "2. Non-resident Companies")){
			setHeading2(heading2, "non_resident_companies", rowText, ("kfch_" + this.fileCdcCode + "_non_resident_companies"), "");
		}
		
		//Third KFCHHeading2
		else if (KFCHUtil.isStr1ContainsStr2(rowText, "3. Specific issues")){
			setHeading2(heading2, "specific_issues", rowText, ("kfch_" + this.fileCdcCode + "_specific_issues"), "");
		}
		
		//4th KFCHHeading2
		else if (KFCHUtil.isStr1ContainsStr2(rowText, "1. Resident individuals")){
			setHeading2(heading2, "resident_individuals", rowText, ("kfch_" + this.fileCdcCode + "_resident_individuals"), "");
		}
		
		//5th KFCHHeading2
		else if (KFCHUtil.isStr1ContainsStr2(rowText, "2. Non-resident individuals")){
			setHeading2(heading2, "non_resident_individuals", rowText, ("kfch_" + this.fileCdcCode + "_non_resident_individuals"), "");
		}
		
		if (StringUtils.isNotEmpty(heading2.getElementName())) {
			currentParent.addtableRowContentChild(heading2);
			parent = heading2;
		}
		
	}


	private void processHeading3(Row row) {
		TreeContent currentParent = getCurrentParent(3);
		if (currentParent == null) return;
		KFCHHeading3 heading3 = new KFCHHeading3(currentParent, 3);
		String rowText = KFCHUtil.cleanText(row.getText());
		
		if (currentParent instanceof KFCHHeading2) {
			KFCHHeading2 parentHeading2 = (KFCHHeading2) currentParent;
			if (parentHeading2.getElementName().equals("non_resident_companies")) {
				setHeading2(heading3, "companies_final_withholding_tax_rates", rowText, "kfch_" + this.fileCdcCode + "_companies_final_withholding_tax_rates", "");
			}
			else if (parentHeading2.getElementName().equals("non_resident_individuals")) {
				setHeading2(heading3, "ind_final_withholding_tax_rates", rowText, "kfch_" + this.fileCdcCode + "_ind_final_withholding_tax_rates", "");
			}
		}
		
		currentParent.addtableRowContentChild(heading3);
		parent = heading3;
	}

	
	/**
	 * 
	 * @param row
	 */
	private void processNormal(Row row) {
		
		TreeContent currentParent = getCurrentParent(4);
		if (currentParent == null) return;
		
		KFCHNormal normal = new KFCHNormal(currentParent, 4);
		CellCollection cells = row.getCells();
		
		//Left cell contains name
		Cell leftCell = cells.get(0);
		String name = leftCell.getText();
		
		if (StringUtils.isEmpty(name)) {return;}
		name = name.trim();
		if (StringUtils.isEmpty(name)) {return;}//check again after trimming.
		
		normal.setName(KFCHUtil.cleanText(name));
		
		// Special Case
		// If left column contains 1. Corporate income tax rates then
		// name element will have <extxref like the following.
		/*
		 * <kfca_corp_tax_rates id="kfca_corp_tax_rates_ns">
		 *	<name>
		 *		<extxref target="nathsuba_ca_ns_s_1.1.">1. Corporate income tax rates</extxref>
		 *	</name>
		 * */
		
		
		// Right cell contains values
		Cell rightCell = cells.get(1);
		List<String> values = new ArrayList<String>();
		for (Node node : rightCell.getParagraphs()) {
			Paragraph para = (Paragraph)node;
			String paraText = para.getText();
			String[] splitedParaText = paraText.split(Character.toString(((char)11)));
			int len = splitedParaText.length;
			for(int i=0; i<len; i++){
				values.add(KFCHUtil.cleanText(splitedParaText[i]));
			}
		}
		normal.setValues(values);
		setNormal(normal, name, currentParent);
		
		if (currentParent instanceof KFCHHeading1) {
			KFCHHeading1 heading1 = (KFCHHeading1) currentParent;
			heading1.addtableRowContentChild(normal);
		}else if (currentParent instanceof KFCHHeading2) {
			KFCHHeading2 heading2 = (KFCHHeading2) currentParent;
			heading2.addtableRowContentChild(normal);
		}else if (currentParent instanceof KFCHHeading3) {
			KFCHHeading3 heading3 = (KFCHHeading3) currentParent;
			heading3.addtableRowContentChild(normal);
		}
		
		
		
	}

	/**
	 * 
	 * @param elementName
	 * @param title
	 * @param extxrefTargetAttrPostfix
	 */
	private void setHeading2 (KFCHHeading2 heading2, String elementName, String title, String id, String extxrefTargetAttrPostfix) {
		heading2.setElementName(elementName);
		heading2.setId(id);
		heading2.setTitle(KFCHUtil.cleanText(title));
	}
	
	
	private TreeContent getCurrentParent(int currentLevel) {
		if (parent == null || parent.getLevel() < currentLevel) {
			return parent;
		}
		else if (parent.getLevel() >= currentLevel) {
			parent = parent.getParent();
		}
		return getCurrentParent(currentLevel);
	}

	/**
	 * 
	 * @param elementName
	 * @param taxpenId
	 * @param title
	 */
	private void setHeading1 (KFCHHeading1 heading1, String elementName, String taxpenId, String title){
		heading1.setElementName(elementName);
		heading1.setId(taxpenId);
		heading1.setTitle(KFCHUtil.cleanText(title));
	}
	
	/**
	 * 
	 * @param row
	 * @throws Exception
	 */
	private void processFirstRow(Row row) throws Exception {
		//KFCHMain.logger.info("Degug: Processing first Row " + row.getText() );
		ParagraphCollection paragraphs = row.getCells().get(0).getParagraphs();
		for (Node node : paragraphs) {
			String paraText = ((Paragraph)node).getText().trim();
			if (paraText == null ) {continue;}
			paraText = KFCHUtil.cleanText(paraText);
			
            /*if (paraText.contains("(") && paraText.contains(")")) {
            	String cantonName = getCantonName(paraText);
            	this.fileCdcCode = CountriesXmlUtil.getInstance().getCDCCode(cantonName);
            	this.fileCdcCode = ((this.fileCdcCode == null) ? "" : this.fileCdcCode );
            	kFCHWordData.setCountryDivName(cantonName);
            	break;
            }*/
			
			
			if (paraText.toLowerCase().contains("key") && paraText.toLowerCase().contains("-")) {
                String[] splitText = paraText.split("-");
                if (StringUtils.isEmpty(splitText[0])) {
                    return; 
                }
                String countryDivName = splitText[0].trim();
                
                //read countries.xml online for CDCCode this will be used in many places
                this.fileCdcCode = CountriesXmlUtil.getInstance().getCDCCode(countryDivName);
                this.fileCdcCode = ((this.fileCdcCode == null) ? "" : this.fileCdcCode );
                
                kFCHWordData.setCountryDivName(countryDivName);
                break;
            }
			
			
		}
	}

	/**
	 * @param paraText
	 * @return
	 */
	private String getCantonName(String paraText) {
		String cantonName  = paraText.substring(paraText.indexOf("(") + 1, paraText.indexOf(")")).trim();
		return cantonName;
	}

	/**
	 * 
	 * KFCHHeading2 and KFCHNormal are almost same in the word file.
	 * Only 1 difference is found but that is very much weak.
	 * 
	 * Both KFCHHeading2 and KFCHNormal row has same color and same number of columns (two columns).
	 * In KFCHHeading2 the second column is empty.
	 * If in any how the second column of KFCHNormal row becomes empty then 
	 * there will be no difference between KFCHHeading2 and KFCHNormal.
	 * 
	 *  
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private boolean isNormal (Row row) throws Exception {
		boolean isNormal = false;
		//1. background color 255 255 255
		//2. foreground color 255 255 0
		//3. two columns
		//4. second column MUST BE FILLED WITH TEXT.
		
		Shading shading = row.getCells().get(0).getCellFormat().getShading();
        Color bgpc = shading.getBackgroundPatternColor();
		boolean isBackgroundColorRight = bgpc.equals(new Color(255, 255, 204));
		
		boolean hasTwoColumns = false;
		boolean isSecondColumnNotEmpty = false;
		
		if (isBackgroundColorRight) {
			hasTwoColumns = (row.getCells().getCount() == 2);
			String secondColumnText = null;
			if (hasTwoColumns) {
				secondColumnText = row.getCells().get(1).getText();
				if (secondColumnText != null) {
					secondColumnText = KFCHUtil.cleanText(secondColumnText).trim();
				}
				isSecondColumnNotEmpty = hasTwoColumns && StringUtils.isNotEmpty(secondColumnText);
			}
		}
		isNormal = isBackgroundColorRight && hasTwoColumns && isSecondColumnNotEmpty;
		return isNormal;
	}
	
	/**
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private boolean isHeading2 (Row row) throws Exception {
		//1. background color 255 255 255
		//2. foreground color 255 255 0
		//3. One column
        //Left cell contains name
	    boolean isHeading2 = false;
		Shading shading = row.getCells().get(0).getCellFormat().getShading();
        Color bgpc = shading.getBackgroundPatternColor();
		boolean isBackgroundColorRight = bgpc.equals(new Color(255, 255, 204));
		//boolean isBold = row.getCells().get(0).getParagraphs().get(0).getParagraphFormat().getStyle().getFont().getBold();
		boolean hasOneColumn = false;
		if (isBackgroundColorRight) {
			hasOneColumn = row.getCells().getCount() == 1;
		}
		//isHeading2 = isBackgroundColorRight && hasOneColumn && isBold;
		isHeading2 = isBackgroundColorRight && hasOneColumn;
		return isHeading2;
	}
	
	private boolean isHeading3(Row row) {
		boolean isBold = true;
		try {
			isBold = row.getCells().get(0).getParagraphs().get(0).getParagraphFormat().getStyle().getFont().getBold();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!isBold && KFCHUtil.isStr1ContainsStr2(row.getText(), "Final withholding tax rates")) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * ForegroundColor must be 128 0 0 and columns count must be 1
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private boolean isFirstRow (Row row) throws Exception{
		boolean isFirstRow = false;
		//System.out.println("isFirstRow: Row Text: " + row.getText());
		//Color color = new Color (128,0,0);
		Color backgroundColor = new Color (128, 0, 0);
		Shading shading = row.getCells().get(0).getCellFormat().getShading();
		Color fgpc = shading.getForegroundPatternColor();
        Color bgpc = shading.getBackgroundPatternColor();
		//if (row.getCells().getCount() == 1 && row.getCells().get(0).getCellFormat().getShading().getForegroundPatternColor().equals(color))
        if (bgpc.equals(backgroundColor)){
			isFirstRow = true;
        }
		return isFirstRow;
	}
	
	/**
	 * BackgroundColor 192 192 192
	 * ForegroundColor 255 255 0
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private boolean isHeading1 (Row row) throws Exception {
		boolean isHeading1 = false;
		Color backgroundColor = new Color (204, 204, 153);
		Shading shading = row.getCells().get(0).getCellFormat().getShading();
		Color bgpc = shading.getBackgroundPatternColor();        
		if (bgpc.equals(backgroundColor)){
			isHeading1 = true;
		}
		return isHeading1;
	}
	
	
	private void setNormal(KFCHNormal normal, String name, TreeContent currentParent) {
		name = name.trim();
		
		if (KFCHUtil.isStr1ContainsStr2(name, "Corporate tax rates")) {
			if (currentParent instanceof KFCHHeading2) {
				KFCHHeading2 heading2 = (KFCHHeading2) currentParent;
				if (heading2.getElementName().equals("resident_companies")) {
					normal.setElementName("corp_tax_rates_res");
					normal.setId("kfch_" + this.fileCdcCode + "_corp_tax_rates_res");
					normal.setExtxrefTargetAttr("gtha_ch_" + this.fileCdcCode + "_s_1.2.");
					//normal.setExtxrefCollectionAtt("gth");
				}
				else if (heading2.getElementName().equals("non_resident_companies")) {
					normal.setElementName("corp_tax_rates_nonres");
					normal.setId("kfch_" + this.fileCdcCode + "_corp_tax_rates_nonres");
					normal.setExtxrefTargetAttr("gtha_ch_" + this.fileCdcCode + "_s_1.2.");
					//normal.setExtxrefCollectionAtt("gth");
				}
			}
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Tax base")) {
			normal.setElementName("tax_base");
			normal.setId("kfch_" + this.fileCdcCode + "_tax_base");
			normal.setExtxrefTargetAttr("gtha_ch_canmun_s_1.3.1.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Unilateral double taxation relief")) {
			if (currentParent instanceof KFCHHeading2) {
				KFCHHeading2 heading2 = (KFCHHeading2) currentParent;
				if (heading2.getElementName().equals("resident_companies")) {
					normal.setElementName("companies_unilat_double_tax_relief");
					normal.setId("kfch_" + this.fileCdcCode + "_companies_unilat_double_tax_relief");
					normal.setExtxrefTargetAttr("gtha_ch_canmun_s_6.2.1.");
					//normal.setExtxrefCollectionAtt("gth");
				}
				else if (heading2.getElementName().equals("resident_individuals")) {
					normal.setElementName("ind_unilat_double_tax_relief");
					normal.setId("kfch_" + this.fileCdcCode + "_ind_unilat_double_tax_relief");
					normal.setExtxrefTargetAttr("gthb_ch_canmun_s_6.2.1.");
					//normal.setExtxrefCollectionAtt("gth");
				}
			}
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Capital gains on sale of shares in resident companies")) {
			if (currentParent instanceof KFCHHeading2) {
				KFCHHeading2 heading2 = (KFCHHeading2) currentParent;
				if (heading2.getElementName().equals("non_resident_companies")) {
					normal.setElementName("companies_capital_gains_nonres");
					normal.setId("kfch_" + this.fileCdcCode + "_companies_capital_gains_nonres");
					normal.setExtxrefTargetAttr("gtha_ch_s_2.2.");
					//normal.setExtxrefCollectionAtt("gth");
				}
				else if (heading2.getElementName().equals("non_resident_individuals")) {
					normal.setElementName("ind_capital_gains_nonres");
					normal.setId("kfch_" + this.fileCdcCode + "_ind_capital_gains_nonres");
					normal.setExtxrefTargetAttr("gthb_ch_" + this.fileCdcCode + "_s_1.3.");
					//normal.setExtxrefCollectionAtt("gth");
				}
			}
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Capital gains")) {
			if (currentParent instanceof KFCHHeading2) {
				KFCHHeading2 heading2 = (KFCHHeading2) currentParent;
				if (heading2.getElementName().equals("resident_companies")) {
					normal.setElementName("companies_capital_gains_res");
					normal.setId("kfch_" + this.fileCdcCode + "_companies_capital_gains_res");
					normal.setExtxrefTargetAttr("gtha_ch_" + this.fileCdcCode+ "_s_1.1.");
					//normal.setExtxrefCollectionAtt("gth");
				}
				else if (heading2.getElementName().equals("resident_individuals")) {
					normal.setElementName("ind_capital_gains_res");
					normal.setId("kfch_" + this.fileCdcCode + "_ind_capital_gains_res");
					normal.setExtxrefTargetAttr("gthb_ch_" + this.fileCdcCode + "_s_1.3.");
					//normal.setExtxrefCollectionAtt("gth");
				}
			}
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Branch profits")) {
			normal.setElementName("branch_profits");
			normal.setId("kfch_" + this.fileCdcCode + "_branch_profits");
			normal.setExtxrefTargetAttr("gtha_ch_s_3.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Dividends")) {
			if (currentParent instanceof KFCHHeading3) {
				KFCHHeading3 heading3 = (KFCHHeading3) currentParent;
				if (heading3.getElementName().equals("companies_final_withholding_tax_rates")) {
					normal.setElementName("dividends");
					normal.setId("kfch_" + this.fileCdcCode + "_dividends");
					normal.setExtxrefTargetAttr("gtha_ch_canmun_s_6.3.");
					//normal.setExtxrefCollectionAtt("gth");
				}
				else if (heading3.getElementName().equals("ind_final_withholding_tax_rates")) {
					normal.setElementName("ind_dividends");
					normal.setId("kfch_" + this.fileCdcCode + "_ind_dividends");
					normal.setExtxrefTargetAttr("gthb_ch_canmun_s_6.2.3.");
					//normal.setExtxrefTargetAttr("gthb_ch_" + this.fileCdcCode + "_s_4.");
					//normal.setExtxrefCollectionAtt("gth");
				}
			}
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Interest")) {
			if (currentParent instanceof KFCHHeading3) {
				KFCHHeading3 heading3 = (KFCHHeading3) currentParent;
				if (heading3.getElementName().equals("companies_final_withholding_tax_rates")) {
					normal.setElementName("interest");
					normal.setId("kfch_" + this.fileCdcCode + "_interest");
					normal.setExtxrefTargetAttr("gtha_ch_" + this.fileCdcCode + "_s_5.");
					//normal.setExtxrefCollectionAtt("gth");
				}
				else if (heading3.getElementName().equals("ind_final_withholding_tax_rates")) {
					normal.setElementName("ind_interest");
					normal.setId("kfch_" + this.fileCdcCode + "_ind_interest");
					normal.setExtxrefTargetAttr("gthb_ch_" + this.fileCdcCode + "_s_4.");
					// normal.setExtxrefTargetAttr("gthb_ch_s_6.3.");
					//normal.setExtxrefCollectionAtt("gth");
				}
			}
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Royalties")) {
			if (currentParent instanceof KFCHHeading3) {
				KFCHHeading3 heading3 = (KFCHHeading3) currentParent;
				if (heading3.getElementName().equals("companies_final_withholding_tax_rates")) {
					normal.setElementName("royalties");
					normal.setId("kfch_" + this.fileCdcCode + "_royalties");
					normal.setExtxrefTargetAttr("gtha_ch_canmun_s_6.3.");
					//normal.setExtxrefCollectionAtt("gth");
				}
				else if (heading3.getElementName().equals("ind_final_withholding_tax_rates")) {
					normal.setElementName("ind_royalties");
					normal.setId("kfch_" + this.fileCdcCode + "_ind_royalties");
					normal.setExtxrefTargetAttr("gthb_ch_canmun_s_6.2.3.");
					//normal.setExtxrefCollectionAtt("gth");
				}
			}
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Fees (technical)")) {
			if (currentParent instanceof KFCHHeading3) {
				KFCHHeading3 heading3 = (KFCHHeading3) currentParent;
				if (heading3.getElementName().equals("companies_final_withholding_tax_rates")) {
					normal.setElementName("fees_tech");
					normal.setId("kfch_" + this.fileCdcCode + "_fees_tech");
					normal.setExtxrefTargetAttr("gtha_ch_canmun_s_6.3.");
					//normal.setExtxrefCollectionAtt("gth");
				}
				else if (heading3.getElementName().equals("ind_final_withholding_tax_rates")) {
					normal.setElementName("ind_fees_tech");
					normal.setId("kfch_" + this.fileCdcCode + "_ind_fees_tech");
					normal.setExtxrefTargetAttr("gthb_ch_canmun_s_6.2.3.");
					//normal.setExtxrefCollectionAtt("gth");
				}
			}
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Fees (management)")) {
			normal.setElementName("fees_mgt");
			normal.setId("kfch_" + this.fileCdcCode + "_fees_mgt");
			normal.setExtxrefTargetAttr("gtha_ch_canmun_s_6.3.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Fees (directors)")) {
			normal.setElementName("ind_fees_reduced");
			normal.setId("kfch_" + this.fileCdcCode + "_ind_fees_reduced");
			normal.setExtxrefTargetAttr("gthb_ch_" + this.fileCdcCode + "_s_4.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Participation relief")) {
			normal.setElementName("participation_relief");
			normal.setId("kfch_" + this.fileCdcCode + "_participation_relief");
			normal.setExtxrefTargetAttr("gtha_ch_canmun_s_2.2.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Group treatment")) {
			normal.setElementName("group_treatment");
			normal.setId("kfch_" + this.fileCdcCode + "_group_treatment");
			normal.setExtxrefTargetAttr("gtha_ch_canmun_s_2.1.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Incentives")) {
			normal.setElementName("incentives");
			normal.setId("kfch_" + this.fileCdcCode + "_incentives");
			normal.setExtxrefTargetAttr("gtha_ch_canmun_s_1.7.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Anti-avoidance")) {
			normal.setElementName("anti-avoidance");
			normal.setId("kfch_" + this.fileCdcCode + "_anti-avoidance");
			normal.setExtxrefTargetAttr("gtha_ch_canmun_s_7.");
			//normal.setExtxrefCollectionAtt("gth");
		}
	
		else if (KFCHUtil.isStr1ContainsStr2(name, "Income tax rates")) {
			if (currentParent instanceof KFCHHeading2) {
				KFCHHeading2 heading2 = (KFCHHeading2) currentParent;
				if (heading2.getElementName().equals("resident_individuals")) {
					normal.setElementName("income_tax_rates_res");
					normal.setId("kfch_" + this.fileCdcCode + "_income_tax_rates_res");
					normal.setExtxrefTargetAttr("gthb_ch_" + this.fileCdcCode + "_s_1.6.");
					//normal.setExtxrefCollectionAtt("gth");
				}else if (heading2.getElementName().equals("non_resident_individuals")) {
					normal.setElementName("income_tax_rates_nonres");
					normal.setId("kfch_" + this.fileCdcCode + "_income_tax_rates_nonres");
					normal.setExtxrefTargetAttr("gthb_ch_" + this.fileCdcCode + "_s_1.6.");
					//normal.setExtxrefCollectionAtt("gth");
				}
			}
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Employment income")) {
			normal.setElementName("ind_employment_income");
			normal.setId("kfch_" + this.fileCdcCode + "_ind_employment_income");
			normal.setExtxrefTargetAttr("gthb_ch_canmun_s_6.2.3.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Net wealth tax")) {
			normal.setElementName("net_wealth_tax");
			normal.setId("kfch_" + this.fileCdcCode + "_net_wealth_tax");
			normal.setExtxrefTargetAttr("gthb_ch_" + this.fileCdcCode + "_s_2.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Inheritance and gift taxes")) {
			normal.setElementName("inheritance_gift_taxes");
			normal.setId("kfch_" + this.fileCdcCode + "_inheritance_gift_taxes");
			normal.setExtxrefTargetAttr("gthb_ch_" + this.fileCdcCode + "_s_3.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "VAT/GST (standard)")) {
			normal.setElementName("vat_gst_standard");
			normal.setId("kfch_" + this.fileCdcCode + "_vat_gst_standard");
			normal.setExtxrefTargetAttr("gtha_ch_s_8.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "VAT/GST (reduced)")) {
			normal.setElementName("vat_gst_reduced");
			normal.setId("kfch_" + this.fileCdcCode + "_vat_gst_reduced");
			normal.setExtxrefTargetAttr("gtha_ch_s_8.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "VAT/GST (increased)")) {
			normal.setElementName("vat_gst_increased");
			normal.setId("kfch_" + this.fileCdcCode + "_vat_gst_increased");
			normal.setExtxrefTargetAttr("gtha_ch_s_8.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
		else if (KFCHUtil.isStr1ContainsStr2(name, "Other")) {
			normal.setElementName("turnover_other");
			normal.setId("kfch_" + this.fileCdcCode + "_turnover_other");
			normal.setExtxrefTargetAttr("gtha_ch_s_8.");
			//normal.setExtxrefCollectionAtt("gth");
		}
		
	}

}
