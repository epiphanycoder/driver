package org.ibfd.word2xml.common;

import org.dom4j.Element;

public class TreeContentRoot extends TreeContent {

	public TreeContentRoot(TreeContent parent, int level) {
		super(parent, level);
	}
	
	@Override
	public Element getElement() {
		return null;
	}

	
}
