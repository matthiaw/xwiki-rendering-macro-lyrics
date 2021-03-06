package org.xwiki.rendering.lyrics;

public class CommentContent implements Content {

	private Song song;
	@Override
	public void setSong(Song song) {
		this.song = song;
	}
	
	private String content;

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String getContent(Parser parser) {
	    
	    if (parser==Parser.OnSong) {
	        return "{comment: "+content+"}";  
	    } else {
	        return "{{box cssClass=\"lyrics_comment\"}}\n"+content+"\n{{/box}}";
	    }
	    
		
	}
}
