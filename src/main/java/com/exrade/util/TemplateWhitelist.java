package com.exrade.util;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

public class TemplateWhitelist extends Whitelist {

	public TemplateWhitelist() {
		//copied from Whitelist.relaxed() with the extension of allowing all data-* attributes
		addTags(
                "a", "b", "blockquote", "br", "caption", "cite", "code", "col",
                "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
                "i", "img", "li", "ol", "p", "pre", "q", "small", "span", "strike", "strong",
                "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u",
                "ul", "hr");;

        addAttributes("a", "href", "title");
        addAttributes("blockquote", "cite");
        addAttributes("col", "span", "width");
        addAttributes("colgroup", "span", "width");
        addAttributes("img", "align", "alt", "height", "src", "title", "width");
        addAttributes("ol", "start", "type");
        addAttributes("q", "cite");
        addAttributes("table", "summary", "width");
        addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width");
        addAttributes(
                "th", "abbr", "axis", "colspan", "rowspan", "scope",
                "width");
        addAttributes("ul", "type");
        addAttributes(":all", "class", "style", "id", "data-*"); // allowed class, style and data attributes to all

        addProtocols("a", "href", "ftp", "http", "https", "mailto");
        addProtocols("blockquote", "cite", "http", "https");
        addProtocols("cite", "cite", "http", "https");
        addProtocols("img", "src", "http", "https", "data"); // added data protocol to support based64 encoded src attribute
        addProtocols("q", "cite", "http", "https");
	}

	@Override
    protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
        return (isSafeTag(tagName)
                && attr.getKey().startsWith("data-")) ||
            super.isSafeAttribute(tagName, el, attr);
    }
}
