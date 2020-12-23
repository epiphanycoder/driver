package org.ibfd.word2xml.kfch;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.ibfd.word2xml.common.MyElement;
import org.ibfd.word2xml.common.TreeContent;

/**
 * 
 * @author asfak.mahamud
 *
 */
public class KFCHHeading1 extends TreeContent {

	/**
	 * XML element name
	 */
	private String elementName = null;
	
	/**
	 * Xml title content
	 */
	private String title = null;
	
	/**
	 * XML id attribute
	 */
	private String id = null;
	
	
	public KFCHHeading1(TreeContent parent, int level){
		super(parent, level);
	}
	/**
	 * 
	 */
	@Override
	public Element getElement() throws Exception {
		MyElement element = null;
		if (StringUtils.isNotEmpty(this.elementName)){
			element = new MyElement(this.elementName);
		}
		if (element == null) return (null);
		element.addAttribute("id", this.id);
		//element.addAttribute("display", "heading1");
		
		element = addTaxTopics(element);
		
		element.addEle("title", this.title);
		
		List<TreeContent> contentChildren2 = this.getContentChildren();
		for (TreeContent treeContent : contentChildren2) {
			if (treeContent instanceof KFCHHeading2) {
				KFCHHeading2 kFCHHeading2 = (KFCHHeading2) treeContent;
				Element heading2Ele = kFCHHeading2.getElement();
				if (heading2Ele != null){
					element.add(heading2Ele);
				}
			}if (treeContent instanceof KFCHHeading3) {
				KFCHHeading3 kFCHHeading3 = (KFCHHeading3) treeContent;
				Element heading3Ele = kFCHHeading3.getElement();
				if (heading3Ele != null){
					element.add(heading3Ele);
				}
			}else if(treeContent instanceof KFCHNormal){
				KFCHNormal kFCHNormal = (KFCHNormal) treeContent;
				Element normalEle = kFCHNormal.getElement();
				if (normalEle != null) {
					element.add(normalEle);
				}
			}
		}
		
		return element;
	}

	/**
	 * @return the elementName
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 * @param elementName the elementName to set
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @param heading1Element
	 * @return
	 */
	private MyElement addTaxTopics (MyElement heading1Element) {
		
		//create a taxtopics element
		MyElement taxtopicsElement = heading1Element.addEle("taxtopics");
		
		//add taxtopic elements
		if (heading1Element.getName().equals(KFCHAppConstant.XML_HEADING1_A_COMPANIES)) {
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_1", "3", "Corporate Taxation");
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_3", "2", "Withholding Taxes");
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_1_22_2", "3", "Local / Municipal / Cantonal");
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_1_21_2", "3", "Local / Municipal / Cantonal");
		}
		else if (heading1Element.getName().equals(KFCHAppConstant.XML_HEADING1_B_INDIVIDUALS)) {
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_2", "3", "Individual Taxation");
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_3", "2", "Withholding Taxes");
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_1_22_2", "3", "Local / Municipal / Cantonal");
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_1_21_2", "3", "Local / Municipal / Cantonal");
		}
		else if (heading1Element.getName().equals(KFCHAppConstant.XML_HEADING1_C_OTHER_TAXES)) {
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_5", "3", "Other Taxes");
		}
		else if (heading1Element.getName().equals(KFCHAppConstant.XML_HEADING1_D_TURNOVER_TAXES)) {
			taxtopicsElement = addTaxTopic(taxtopicsElement, "1_4", "3", "VAT");
		}
		return heading1Element;
	}
	
	/**
	 * 
	 * @param taxtopicsElement
	 * @param tc
	 * @param score
	 * @param text
	 * @return
	 */
	private MyElement addTaxTopic (MyElement taxtopicsElement, String tc, String score, String text) {
		Element taxtopic = taxtopicsElement.addEle("taxtopic", text);
		taxtopic.addAttribute("tc", tc);
		taxtopic.addAttribute("score", score);
		return taxtopicsElement;
	}

}
