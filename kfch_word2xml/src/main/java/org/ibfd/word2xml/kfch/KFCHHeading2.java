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
public class KFCHHeading2 extends TreeContent {
	
	/**
	 * XML element name
	 */
	private String elementName = null;
	
	/**
	 * XML id Attribute
	 */
	private String id = null;
	
	/**
	 * XML title element
	 */
	private String title = null;
	
	/**
	 * For future purpose
	 * XML extxref element's collection attribute
	 */
	private String extxrefCollectionAttr = null;
	
	/**
	 * For future purpose
	 * XML extxref element's target attribute
	 */
	private String extxrefTargetAttr = null;

	
	public KFCHHeading2(TreeContent parent, int level) {
		super(parent, level);
	}
	
	/**
	 * 
	 */
	@Override
	public Element getElement() {
		MyElement element = null;
		if (StringUtils.isNotEmpty(this.elementName)){
			element = new MyElement(this.elementName);
		}
		if (element == null) return (null);
		element.addAttribute("id", getId());
		//element.addAttribute("display", "heading2");
		
		MyElement titleEle = element.addEle("title");
		if(StringUtils.isNotEmpty(this.extxrefCollectionAttr) && StringUtils.isNotEmpty(this.extxrefTargetAttr)){
			MyElement extxref = titleEle.addEle("extxref", this.title, "collection", this.extxrefCollectionAttr);
			extxref.addAttribute("target", this.extxrefTargetAttr);
		}else{
			titleEle.setText(title);
		}
		
		List<TreeContent> contentChildren2 = this.getContentChildren();
		for (TreeContent treeContent : contentChildren2) {
			if (treeContent instanceof KFCHNormal) {
				KFCHNormal kFCHNormal = (KFCHNormal) treeContent;
				Element normal = kFCHNormal.getElement();
				if (normal != null) {
					element.add(normal);
				}
			}else if (treeContent instanceof KFCHHeading3) {
				KFCHHeading3 kFCHHeading3 = (KFCHHeading3) treeContent;
				Element normal = kFCHHeading3.getElement();
				if (normal != null) {
					element.add(normal);
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
	 * @return the extxrefCollectionAttr
	 */
	public String getExtxrefCollectionAttr() {
		return extxrefCollectionAttr;
	}

	/**
	 * @param extxrefCollectionAttr the extxrefCollectionAttr to set
	 */
	public void setExtxrefCollectionAttr(String extxrefCollectionAttr) {
		this.extxrefCollectionAttr = extxrefCollectionAttr;
	}

	/**
	 * @return the extxrefTargetAttr
	 */
	public String getExtxrefTargetAttr() {
		return extxrefTargetAttr;
	}

	/**
	 * @param extxrefTargetAttr the extxrefTargetAttr to set
	 */
	public void setExtxrefTargetAttr(String extxrefTargetAttr) {
		this.extxrefTargetAttr = extxrefTargetAttr;
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

//	/**
//	 * @return the nameValuePairs
//	 */
//	public List<KFCHNormal> getNameValuePairs() {
//		return nameValuePairs;
//	}
//
//	public void addNameValuePair(KFCHNormal normal) {
//		nameValuePairs.add(normal);
//	}


}
