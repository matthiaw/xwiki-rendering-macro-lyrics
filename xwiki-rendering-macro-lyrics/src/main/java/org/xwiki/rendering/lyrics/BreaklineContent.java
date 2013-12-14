package org.xwiki.rendering.lyrics;

/**
 * Empty Object. Breakline is always done btw content elements
 * @author Matthias Wegner
 *
 */
public class BreaklineContent implements Content {

	private Song song;
	@Override
	public void setSong(Song song) {
		this.song = song;
	}
	
	@Override
	public String getContent(Parser parser) {
		return "";
	}
}
