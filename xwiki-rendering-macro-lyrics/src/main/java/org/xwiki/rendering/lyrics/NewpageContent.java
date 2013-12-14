package org.xwiki.rendering.lyrics;


public class NewpageContent implements Content {
	
	private Song song;
	@Override
	public void setSong(Song song) {
		this.song = song;
	}
	
	@Override
	public String getContent(Parser parser) {
	    
	    if (parser == Parser.OnSong) {
	        return "/Newline/";
	    } else {
	        return "{{html}}\n\n<SPAN CLASS=\"pagebreak\"/>{{/html}}\n";   
	    }
		
	}

}
