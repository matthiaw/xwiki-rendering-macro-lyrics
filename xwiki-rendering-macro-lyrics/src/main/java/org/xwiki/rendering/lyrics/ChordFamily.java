package org.xwiki.rendering.lyrics;

import java.util.ArrayList;

import java.util.Collections;
import java.util.StringTokenizer;

public class ChordFamily {

	ArrayList<Chord> chords = new ArrayList<Chord>();

	public Chord getFirstChord() {
		return chords.get(0);
	}
	
	public Chord getChordOnFret(int fret) {

		Chord found = null;
		for (Chord chord : chords) {
			if ((found==null)&&(chord.getFretPositionBase() == fret)) {
				Chord capoChord = ChordFamilys.getChordForCapoPattern(chord, fret);
				if (capoChord != null) {
					found = chord;
				}
			}
		}
		if (found!=null) {
			return found;
		}

		// Wenn spezifischer nicht gefunden, dann n�chst h�herer Akkord
		for (Chord chord : chords) {
			if ((found==null)&&(chord.getFretPositionBase() > fret)) {
				Chord capoChord = ChordFamilys.getChordForCapoPattern(chord, fret);
				if (capoChord != null) {
					found = chord;
				}
			}
		}
		if (found!=null) {
			return found;
		}
		
		for (Chord chord : chords) {
			if (chord.getFretPositionBase() == fret) {
				found = chord;
			}
		}
		if (found!=null) {
			return found;
		}
		
		// Wenn spezifischer nicht gefunden, dann n�chst h�herer Akkord
		for (Chord chord : chords) {
			if (chord.getFretPositionBase() > fret) {
				found = chord;
			}
		}
		return found;
	}

	public ChordFamily(String nameOfFamily) {
		
//		System.out.println("!!!"+nameOfFamily);
		
		StringTokenizer tokenizer = new StringTokenizer(nameOfFamily, " ");
		String[] info = null;

		if (nameOfFamily==null) {
			return;
		}
		
		if (nameOfFamily.equals("")) {
			return;
		}
		
		String name = tokenizer.nextToken();
		if (!name.contains(",")) {
			addName(name);
			info = ChordCalculator.getChordInformations(name);
		} else {
			addName(name.replace(",", "").trim());
			if (info == null) {
				info = ChordCalculator.getChordInformations(name.replace(",", "").trim());
			}

			while (tokenizer.hasMoreTokens()) {
				name = tokenizer.nextToken();
				name = name.replace(",", "").trim();
				addName(name);
				if (info == null) {
					info = ChordCalculator.getChordInformations(name);
				}
			}
		}

		if (info != null) {
			baseNoteHarmony = info[0];
			chordIdentity = info[1];
			bassNote = info[2];

		} else {
//			System.err.println("Keine Akkord-Information gefunden: " + names.get(0) + " from " + nameOfFamily);
		}
	}

	private String baseNoteHarmony;

	private String chordIdentity;

	private String bassNote;

	public ArrayList<Chord> getChords() {
		return chords;
	}

	public boolean containsName(String name) {
		for (String n : names) {
			if (n.equals(name)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<String> getHarmonys() {
		return ChordCalculator.getHarmonys(baseNoteHarmony, chordIdentity);
	}

	public ArrayList<String> getNotesInHarmonys() {
		return ChordCalculator.getNotesInHarmonys(baseNoteHarmony, chordIdentity);
	}

	public String getCalculatedSymbol() {
		return ChordCalculator.getChordSymbol(baseNoteHarmony, chordIdentity, bassNote);
	}

	public String getCalculatedName() {
		return ChordCalculator.getCorrectChordName(baseNoteHarmony, chordIdentity, bassNote);
	}

	private ArrayList<String> names = new ArrayList<String>();

	public ArrayList<String> getNames() {
		return names;
	}

	public String getFamilyName() {
		return names.get(0);
	}

	public String getWriteableFamilyName() {
		return getFamilyName().replace("/", "_").replace("#", "x").replace("�", "dim").replace("M", "Major");
	}

	public void addName(String name) {
		if (!names.contains(name.trim())) {
			names.add(name.trim());
		}
	}

	public void add(Chord... chordsToAdd) {

		for (Chord c : chordsToAdd) {

			chords.add(c);
			c.setWritableName(getWriteableFamilyName() + "_" + chords.size());
			c.setFamily(this);
		}

		Collections.sort(chords);
	}

}
