package org.xwiki.rendering.lyrics;


public class Sequence {

	public Sequence(String name) {
		this.name = name;
	}

	private String name;

	private String verse = "";

	public String getFullName() {
		return name;
	}
	
	public String getName() {
		String tname = name;
		if (name.contains(":")) {
			tname = name.substring(0, name.indexOf(":")).trim();
		}
		return tname;
	}
	
	public int getFretPos() {
		if (name.contains(":")) {
			return new Integer(name.substring(name.indexOf(":")+1, name.length()).trim()).intValue();
		}
		return 0;
	}
	
	public boolean hasFretPos() {
		if (name.contains(":")) {
			return true;
		}
		return false;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVerse() {
		return verse;
	}

	public void setVerse(String verse) {
		this.verse = verse;//.replace("_", " ");
	}

}
