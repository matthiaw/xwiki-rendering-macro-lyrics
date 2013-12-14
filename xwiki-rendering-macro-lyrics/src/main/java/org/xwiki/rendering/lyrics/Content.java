package org.xwiki.rendering.lyrics;


public interface Content {

	public String getContent(Parser parser);
	
	public void setSong(Song song);
	
}
