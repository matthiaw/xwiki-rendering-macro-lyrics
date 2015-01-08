package org.xwiki.rendering.lyrics;


import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class ChordFamilys {

//	public static void main(String... args) {
////		for (ChordFamily f : ChordFamilys.getFamilys()) {
////			System.out.println(f.getFamilyName());
////		}
//		
//		Song s = new Song(" Text [A] , wwwdwefwf");
//	}
	
	private static Hashtable<String, ChordFamily> map = new Hashtable<String, ChordFamily>();
	
	public static ArrayList<ChordFamily> getFamilys() {
		ArrayList<ChordFamily> list = new ArrayList<ChordFamily>();

		for (Iterator<ChordFamily> iterator = map.values().iterator(); iterator.hasNext();) {
			ChordFamily chordFamily = iterator.next();
			if (!list.contains(chordFamily)) {
				list.add(chordFamily);
			}
		}

		return list;
	}
	
	public static Chord getChordForCapoPattern(Chord chord, int fret) {
		ChordFamily family = chord.getFamily();

		ArrayList<Chord> foundOnes = new ArrayList<Chord>();

		if (chord != null) {
			for (ChordFamily fam : list) {
				if (!family.getCalculatedName().equals(fam.getCalculatedName())) {
					for (Chord c : fam.getChords()) {
						//if (family.getCalculatedName().contains("A-Major")) {
						//   System.out.println("Searching "+fam.getCalculatedName()+" for "+family.getCalculatedName()+" on "+fret+": "+chord.getNotes()+":"+chord.toChordProString()+"=="+c.getNotesTransposed(fret)+":"+chord.toChordProString());
						//}
						if (chord.getNotes().equals(c.getNotesTransposed(fret))) {
							foundOnes.add(c);
							//System.out.println("Found on "+fret+": "+chord.getNotes());
						}
					}
				}
			}
		} else {
			 System.out.println("No capo-chord on fret "+fret+" for "+chord.getFamily().getCalculatedSymbol());
		}

		if (foundOnes.size() == 1) {
			return foundOnes.get(0);
		}
		if (foundOnes.size() > 1) {
			for (Chord c2 : foundOnes) {
				if (c2.containsCapoAsFinger()) {
					return c2;
				}
			}
			return foundOnes.get(0);
		}

		return null;
	}

	public static ChordFamily getChordFamily(String name) {
		return map.get(name);
	}

	private static ArrayList<ChordFamily> list = new ArrayList<ChordFamily>();

	private static void add(String familyName, Chord... chords) {
		ChordFamily family = new ChordFamily(familyName);
		family.add(chords);
		list.add(family);
	}

	private static void readPositions(String filename) {
		LineNumberReader lineNumberReader = null;

//		File f = new File(filename);
//		if (!f.exists()) {
//			System.err.println("File "+f.getAbsolutePath()+" not exists!");
//		}
		
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			if (classLoader == null) {
			    classLoader = Class.class.getClassLoader();
			}
			
			InputStream is = classLoader.getResourceAsStream(filename);
			
			lineNumberReader = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 

		try {
			String line = lineNumberReader.readLine();
//			System.out.println("Read: "+lineNumberReader.getLineNumber());
			ArrayList<Chord> definitions = new ArrayList<Chord>();
			String chordNames = "";
			do {

				try {
					if (0 != line.trim().length()) { // skip empty lines

						if (!line.trim().startsWith("{")) {

							if (definitions.size() != 0) {
								add(chordNames, definitions.toArray(new Chord[definitions.size()]));
							}

							chordNames = line;
							definitions = new ArrayList<Chord>();
						} else {
							definitions.add(new Chord(line));
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				line = lineNumberReader.readLine();

				if (line == null) {
					add(chordNames, definitions.toArray(new Chord[definitions.size()]));
				}

			} while (line != null);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static {
		readPositions("chordPositions.txt");

		for (ChordFamily family : list) {
			for (String name : family.getNames()) {
				map.put(name, family);
			}
		}
		
		if (list.size()==0) {
			System.err.println("Chord-Definitions could not be loaded!");
		} else {
			System.out.println(list.size()+" Chord-Definitions loaded.");
		}
	}

}
