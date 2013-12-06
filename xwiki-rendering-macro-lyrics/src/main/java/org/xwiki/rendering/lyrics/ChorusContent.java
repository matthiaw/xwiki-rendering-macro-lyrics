package org.xwiki.rendering.lyrics;

import java.util.ArrayList;
import java.util.List;


public class ChorusContent implements Content {
	
	private Song song;
	@Override
	public void setSong(Song song) {
		this.song = song;
	}
	
	private List<Content> chorus;
	
	public ChorusContent(List<Content> chorus) {
		this.chorus = chorus;
	}
	
	@Override
	public String getContent() {
		
		StringBuilder result = new StringBuilder();
		if (chorus != null) {
			result.append("{{box cssClass=\"lyrics_chorus\"}}\n");
			for (Content content : chorus) {
				result.append(content.getContent()+ "\n");
			}
			result.append("{{/box}}");
			return result.toString();
		}
		
		return "";
	}

}
