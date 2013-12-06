package org.xwiki.rendering.lyrics;


public class NewpageContent implements Content {
	
	private Song song;
	@Override
	public void setSong(Song song) {
		this.song = song;
	}
	
	@Override
	public String getContent() {
		return "{{html}}\n\n<SPAN CLASS=\"pagebreak\"/>{{/html}}\n";
	}

}
