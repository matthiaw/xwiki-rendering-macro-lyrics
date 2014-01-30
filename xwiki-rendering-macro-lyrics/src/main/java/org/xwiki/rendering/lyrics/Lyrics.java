package org.xwiki.rendering.lyrics;

import org.xwiki.component.wiki.WikiComponent;

import com.xpn.xwiki.doc.XWikiDocument;
public interface Lyrics extends WikiComponent {

	public static final String CLASS = "Lyrics";

	public XWikiDocument getDocument();
	
	public XWikiDocument getDocument(boolean fret);

	public String getTitle();
	
	public String getCcli();
	
	public String getArtist();
	
	public int getCapo();
	
	public String getCopyright();
	
}
