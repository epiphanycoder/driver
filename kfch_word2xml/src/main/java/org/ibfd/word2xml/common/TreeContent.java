package org.ibfd.word2xml.common;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

public abstract class TreeContent {
	
	/**
	 * 
	 */
	private List<TreeContent> contentChildren = new ArrayList<TreeContent>();
	
	/***
	 * 
	 */
	private TreeContent parent = null;
	
	/**
	 * 
	 */
	private int level = 0;
	
	public TreeContent(TreeContent parent, int level) {
		this.parent = parent;
		this.level = level;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract Element getElement() throws Exception;
	
	/**
	 * 
	 * @param tr
	 */
	public void addtableRowContentChild (TreeContent tr) {
		contentChildren.add(tr);
	}
	
	/**
	 * 
	 */
	public void tableRowContentClear (){
		if (contentChildren != null && contentChildren.size() > 0) {
			contentChildren.clear();
		}
	}

	/**
	 * @return the contentChildren
	 */
	public List<TreeContent> getContentChildren() {
		return contentChildren;
	}

	/**
	 * @return the parent
	 */
	public TreeContent getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(TreeContent parent) {
		this.parent = parent;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
}
