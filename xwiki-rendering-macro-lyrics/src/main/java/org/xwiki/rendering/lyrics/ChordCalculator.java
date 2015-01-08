package org.xwiki.rendering.lyrics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.imageio.ImageIO;

/**
 * Calculator for correct chord pattern, inversions, ...
 * 
 * @author Matthias Wegner
 *
 */
public class ChordCalculator {

	private static Hashtable<String, Integer[]> chordIntervals = new Hashtable<String, Integer[]>();

	private static Hashtable<String, String[]> strings = new Hashtable<String, String[]>();

	private static Hashtable<String, String> inversions = new Hashtable<String, String>();

	private static Hashtable<String, String> appendices = new Hashtable<String, String>();

	public static void createInversions(String chordPattern) {
		Integer[] intervals = chordIntervals.get(chordPattern);
		Integer[] inversionIntervals = new Integer[intervals.length];
		Integer[] intervalsBackup = new Integer[intervals.length];

		for (int j = 0; j < intervals.length; j++) {
			intervalsBackup[j] = intervals[j];
		}

		for (int i = 1; i < intervals.length; i++) {
			String inversionName = chordPattern + " (" + i + ". Inversion)";
			inversionIntervals = new Integer[intervals.length];

			for (int j = 0; j < intervals.length - 1; j++) {
				inversionIntervals[j] = intervals[j + 1];
			}
			inversionIntervals[intervals.length - 1] = intervals[0] + 12;
			addInterval(inversionName, inversionIntervals);
			inversions.put(inversionName, chordPattern);
			for (int j = 0; j < inversionIntervals.length; j++) {
				intervals[j] = inversionIntervals[j];
			}
		}

		for (int j = 0; j < intervalsBackup.length; j++) {
			intervals[j] = intervalsBackup[j];
		}
	}

	/**
	 * Definitions of Chord-Intervals
	 */
	static {
		addInterval("Major", "", 0, 4, 7);
		addInterval("Minor", "m", 0, 3, 7);
		addInterval("Major Add 9th", "add9", 0, 4, 7, 14);
		addInterval("Major Add 11th", "add11", 0, 4, 7, 17);
		addInterval("Major Add 2nd", "add2", 0, 2, 4, 7);
		addInterval("Minor Add 2nd", "m(add2)", 0, 2, 3, 7);
		addInterval("Major Add 4th", "add4", 0, 4, 5, 7);
		addInterval("Minor Add 4th", "m(add4)", 0, 3, 5, 7);
		addInterval("Minor Add 9th", "m(add9)", 0, 3, 7, 14);
		addInterval("Minor Add 11th", "m(add11)", 0, 3, 7, 17);
		addInterval("Dimished", "°", 0, 3, 6);
		addInterval("Augmented", "+", 0, 4, 8);
		addInterval("Suspended 2nd", "sus2", 0, 2, 7);
		addInterval("Suspended 4th", "sus4", 0, 5, 7);
		addInterval("Suspended 2th Sharp 5th", "sus2#5", 0, 2, 7, 8);
		addInterval("Suspended 4th Sharp 5th", "sus4#5", 0, 5, 7, 8);
		addInterval("Add 2nd Suspended 4th", "2sus4", 0, 2, 5, 7);
		addInterval("Suspended 2nd Add 6th", "sus2(add6)", 0, 2, 7, 9);
		addInterval("Suspended 2nd Add 9th", "sus2(add9)", 0, 2, 7, 14);
		addInterval("Suspended 4th Add 6th", "sus4(add6)", 0, 5, 7, 9);
		addInterval("Suspended 4th Add 9th", "sus4(add9)", 0, 5, 7, 14);
		addInterval("Powerchord", "5", 0, 7, 12);
		addInterval("6th", "6", 0, 4, 7, 9);
		addInterval("Minor 6th", "m6", 0, 3, 7, 9);
		addInterval("Minor 6th Add 7th", "m6(add7)", 0, 3, 7, 9, 11);
		addInterval("6th Add 9th", "6/9", 0, 4, 7, 9, 14);
		addInterval("7th", "7", 0, 4, 7, 10);
		addInterval("7th supended 4th", "7sus4", 0, 4, 5, 7, 10);
		addInterval("7th add 9th", "7add9", 0, 4, 7, 10, 14);
		addInterval("Major 7th", "maj7", 0, 4, 7, 11);
		addInterval("Major 7th Add 2nd", "maj7(add2)", 0, 2, 4, 7, 11);
		addInterval("Major 7th Add 9th", "maj7(add9)", 0, 2, 4, 7, 14);
		addInterval("Minor 7th", "m7", 0, 3, 7, 10);
		addInterval("Minor Major 7th", "mM7", 0, 3, 7, 11);
		addInterval("Augmented 7th", "+7", 0, 4, 8, 10);
		addInterval("Dimished 7th", "°7", 0, 3, 6, 9);
		addInterval("7th Flat 5th", "7b5", 0, 4, 6, 10);
		addInterval("Minor 7th Flat 5th", "m7b5", 0, 3, 6, 10);
		addInterval("7th Flat 9th", "7b9", 0, 4, 7, 10, 13);
		addInterval("9th", "9", 0, 4, 7, 10, 14);
		addInterval("Minor 9th", "m9", 0, 3, 7, 10, 14);
		addInterval("Major 9th", "M9", 0, 4, 7, 11, 14);
		addInterval("9th Flat 5th", "9b5", 0, 4, 6, 10, 14);
		addInterval("9th Suspended 4th", "9sus4", 0, 5, 7, 10, 14);
		addInterval("11th", "11", 0, 7, 10, 14, 17);
		addInterval("Minor 11th", "m11", 0, 3, 7, 10, 14, 17);
		addInterval("13th", "13", 0, 4, 7, 10, 20);
		addInterval("Major 13th", "M13", 0, 4, 7, 11, 20);
		addInterval("Minor 13th", "m13", 0, 3, 7, 10, 20);
		addInterval("7th Suspended 2nd", "7sus2", 0, 2, 7, 10);
		addInterval("7th Suspended 4th", "7sus4", 0, 5, 7, 10);
		addInterval("7th Suspended 4th add 9th", "7sus4add9", 0, 5, 7, 10, 14);

		// Create all Inversions of the Harmony
		for (String chord : new Vector<String>(chordIntervals.keySet())) {
			createInversions(chord);
		}

		// Add Notes on Strings
		strings.put("E", Constants.notes_E);
		strings.put("A", Constants.notes_A);
		strings.put("d", Constants.notes_d);
		strings.put("g", Constants.notes_g);
		strings.put("h", Constants.notes_h);
		strings.put("e1", Constants.notes_e1);
	}

	/**
	 * Get entity symbol of chord
	 * 
	 * @param rootNote
	 *            bass or root note of chord
	 * @param chordPattern
	 *            Pattern of chord
	 * @return Symbol of chord
	 */
	public static String getChordSymbol(String rootNote, String chordPattern) {
		return getChordSymbol(rootNote, chordPattern, rootNote);
	}

	public static String getChordSymbol(String rootNote, String chordPattern,
			String bassNote) {

		chordPattern = getCorrectChordPattern(rootNote, chordPattern, bassNote);

		ArrayList<String> notes = findHarmonyNotes(rootNote, chordPattern,
				bassNote);

		if (notes == null) {
			return null;
		}

		String bass = "";
		if (notes.size() > 0) {
			bass = notes.get(0);
		}

		String name = "";

		for (String chord : chordIntervals.keySet()) {
			if (chordPattern.equals(chord)
					|| (chordPattern.contains(chord) && chordPattern
							.contains("Inversion"))) {
				name = rootNote + appendices.get(chord);
			}
		}

		if (chordPattern.contains("Inversion")) {
			name = name + "/" + bass;
		} else {
			if (!rootNote.equals(bassNote)) {
				name = name + "/" + bassNote;
			}
		}

		return name;
	}

	public static String getCorrectBassNote(String baseNote, String chord,
			String bassNote) {
		String correctedChord = getCorrectChordPattern(baseNote, chord,
				bassNote);

		if (hasBassNote(bassNote, baseNote, correctedChord)) {
			for (String c : getInversionPatternsFromChord(correctedChord)) {
				ArrayList<String> hn = findHarmonyNotes(baseNote, c);
				if (hn.get(0).equals(bassNote)) {
					bassNote = baseNote;
				}
			}
		}

		return bassNote;
	}

	public static String getCorrectChordName(String baseNote, String chord,
			String bassNote) {
		String correctedChord = getCorrectChordPattern(baseNote, chord,
				bassNote);

		if (correctedChord == null) {
			return null;
		}

		String addOn = "";
		if (!correctedChord.equals(chord)) {
			addOn = "-Slash";
		}

		if (hasBassNote(bassNote, baseNote, correctedChord)) {
			for (String c : getInversionPatternsFromChord(correctedChord)) {

				ArrayList<String> hn = findHarmonyNotes(baseNote, c);
				if (hn.get(0).equals(bassNote)) {
					correctedChord = c;
					bassNote = baseNote;
				}
			}
		} else {
			if (!baseNote.equals(bassNote)) {
				addOn = "-Slash";
			}
		}

		if (addOn.equals("-Slash")) {
			addOn = addOn + " " + bassNote;
		}

		String finalName = correctedChord + addOn;

		return baseNote + "-" + finalName;
	}

	public static ArrayList<String> getInversionPatternsFromChord(String pattern) {
		Vector<String> v = new Vector<String>(inversions.keySet());
		ArrayList<String> list = new ArrayList<String>();
		for (String s : v) {
			if (inversions.get(s).equals(pattern)) {
				list.add(s);
			}
		}
		return list;
	}

	public static boolean hasBassNote(String note, String baseNote, String chord) {
		ArrayList<String> harmonyNotesWithoutBass = ChordCalculator
				.findHarmonyNotes(baseNote, chord);

		if (harmonyNotesWithoutBass == null) {
			return false;
		}

		for (int i = 1; i < harmonyNotesWithoutBass.size(); i++) {
			if (harmonyNotesWithoutBass.get(i).equals(note)) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<String> getNotesInHarmonys(String baseNote,
			String chord) {

		int baseIndex = 1000;
		for (int i = 0; i < Constants.notesInDetail.length; i++) {
			if (Constants.notesInDetail[i].equals(baseNote)) {
				baseIndex = i;
			}
		}
		ArrayList<String> notesInHarmonies = new ArrayList<String>();
		if (baseIndex < 1000) {
			for (int i = baseIndex; i < Constants.notesInDetail.length; i += 12) {
				for (Integer ii : getIntervals(chord)) {
					int noteIndex = i + ii;
					if (i + ii < Constants.notesInDetail.length) {
						if (!notesInHarmonies
								.contains(Constants.notesInDetail[i + ii])) {
							notesInHarmonies
									.add(Constants.notesInDetail[i + ii]);

						}
					}
				}
			}
		}

		return notesInHarmonies;
	}

	public static ArrayList<String> getHarmonys(String baseNote, String chord) {

		int baseIndex = 1000;
		for (int i = 0; i < Constants.notesInDetail.length; i++) {
			if (Constants.notesInDetail[i].equals(baseNote)) {
				baseIndex = i;
			}
		}

		ArrayList<String> harmonys = new ArrayList<String>();

		if (baseIndex < 1000) {
			for (int i = baseIndex; i < Constants.notesInDetail.length; i += 12) {
				StringBuilder sb = new StringBuilder();

				for (Integer ii : getIntervals(chord)) {
					if (i + ii < Constants.notesInDetail.length) {
						sb.append(Constants.notesInDetail[i + ii] + " ");
					}
				}

				harmonys.add(sb.toString().trim());
			}
		}

		return harmonys;
	}

	public static String[] getChordInformations(String name) {
		String[] noteArray = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#",
				"A", "A#", "H" };
		Vector<String> chords = new Vector<String>(chordIntervals.keySet());
		String[] s = new String[3];

		for (String chord : chords) {
			for (int i = 0; i < noteArray.length; i++) {
				String n = noteArray[i] + appendices.get(chord);

				if (n.equals(name)) {
					s[0] = noteArray[i];
					s[1] = chord;
					s[2] = noteArray[i];
					return s;
				}

				if (name.contains("/")) {
					for (int j = 0; j < noteArray.length; j++) {
						n = noteArray[i] + appendices.get(chord) + "/"
								+ noteArray[j];
						if (n.equals(name)) {
							s[0] = noteArray[i];
							s[1] = chord;
							s[2] = noteArray[j];
							return s;
						}
					}
				}
			}
		}

		return null;

	}

	public static String getCorrectChordPattern(String rootNote,
			String chordPattern, String bassNote) {

		if (chordPattern == null) {
			return null;
		}
		if (rootNote == null) {
			return null;
		}

		if (chordPattern.contains("Inversion") && !rootNote.equals(bassNote)) {
			chordPattern = inversions.get(chordPattern);
		}
		return chordPattern;
	}

	public static boolean containsStringHarmonyNoteOnFret(int fret,
			String stringName, ArrayList<String> notesOnString) {
		for (String note : notesOnString) {
			if (fret == getFret(stringName, note)) {
				return true;
			}
		}

		return false;
	}

	public static boolean containsHarmonyNotes(String rootNote,
			String chordPatternOrig, String bassNote) {

		String symbol = getChordSymbol(rootNote, chordPatternOrig, bassNote);
		ChordFamily chordFamily = ChordFamilys.getChordFamily(symbol);
		String chordPattern = getCorrectChordPattern(rootNote,
				chordPatternOrig, bassNote);

		Hashtable<String, ArrayList<String>> notesInStrings = null;
		if (rootNote.equals(bassNote)) {
			notesInStrings = ChordCalculator.findHarmonyNotesOnString(rootNote,
					chordPattern);
		} else {
			notesInStrings = findHarmonyNotesOnString(rootNote, chordPattern,
					bassNote);
		}

		boolean status_overall = true;
		if (chordFamily != null) {
			for (Chord chord : chordFamily.getChords()) {
				for (int j = 0; j < 6; j++) {
					String stringName = Constants.stringNames[j];
					boolean status_ok = false;
					for (int i = 0; i < 25; i++) {
						if (containsStringHarmonyNoteOnFret(i, stringName,
								notesInStrings.get(stringName))) {
							String note = strings.get(stringName)[i];

							note = note.replace("B", "H").replace("b", "h");

							if (!chord.getNoteOnString(stringName).equals("x")) {
								if (chord.getNoteOnString(stringName).equals(
										note)) {
									status_ok = true;
								}
							} else {
								status_ok = true;
							}
						}
					}
					if (!status_ok) {
						status_overall = false;
					}
				}
			}
		}

		return status_overall;
	}

	public static int getFret(String stringName, String note) {
		String[] guitarString = strings.get(stringName);
		for (int i = 0; i < guitarString.length; i++) {
			if (guitarString[i].equals(note)) {
				return i;
			}
		}
		return -1;
	}

	public static Hashtable<String, ArrayList<String>> findHarmonyNotesOnString(
			String rootNote, String chordPattern, String bass) {

		ArrayList<String> harmonyNotes = ChordCalculator.findHarmonyNotes(
				rootNote, chordPattern, bass);

		Hashtable<String, ArrayList<String>> notesOnStrings = new Hashtable<String, ArrayList<String>>();

		for (int i = 0; i < Constants.stringNames.length; i++) {
			String stringName = Constants.stringNames[i];
			String[] guitarString = strings.get(stringName);
			ArrayList<String> notesOnString = new ArrayList<String>();
			for (int ii = 0; ii < guitarString.length; ii++) {
				String note = guitarString[ii];
				String genericNote = note.toUpperCase().replace("1", "")
						.replace("2", "").replace("3", "").replace("H", "B")
						.replace("h", "b");
				for (String harmonyNote : harmonyNotes) {
					if (genericNote.equals(harmonyNote)) {
						notesOnString.add(note);
					}
				}
			}
			notesOnStrings.put(stringName, notesOnString);
		}

		return notesOnStrings;

	}

	public static Hashtable<String, ArrayList<String>> findHarmonyNotesOnString(
			String rootNote, String chordPattern) {
		ArrayList<String> harmonyNotes = ChordCalculator.findHarmonyNotes(
				rootNote, chordPattern);

		Hashtable<String, ArrayList<String>> notesOnStrings = new Hashtable<String, ArrayList<String>>();

		for (int i = 0; i < Constants.stringNames.length; i++) {
			String stringName = Constants.stringNames[i];
			String[] guitarString = strings.get(stringName);
			ArrayList<String> notesOnString = new ArrayList<String>();
			for (int ii = 0; ii < guitarString.length; ii++) {
				String note = guitarString[ii];
				String genericNote = note.toUpperCase().replace("1", "")
						.replace("2", "").replace("3", "").replace("H", "B")
						.replace("h", "b");
				for (String harmonyNote : harmonyNotes) {
					if (genericNote.equals(harmonyNote)) {
						notesOnString.add(note);
					}
				}
			}
			notesOnStrings.put(stringName, notesOnString);
		}

		return notesOnStrings;

	}

	public static ArrayList<Integer> getIntervals(String chord) {
		Integer[] interval = chordIntervals.get(chord);
		if (interval != null) {
			ArrayList<Integer> inveralList = new ArrayList<Integer>();
			for (int i = 0; i < interval.length; i++) {
				inveralList.add(interval[i]);
			}
			return inveralList;
		}
		return null;
	}

	public static ArrayList<String> findHarmonyNotes(String rootNote,
			String chord, String bassNote) {

		bassNote = getCorrectBassNote(rootNote, chord, bassNote);
		chord = getCorrectChordPattern(rootNote, chord, bassNote);

		if (chord == null) {
			return null;
		}

		Integer[] interval = chordIntervals.get(chord);
		ArrayList<String> note = new ArrayList<String>();

		if (rootNote.equals("H")) {
			rootNote = "B";
		}

		int baseIndex = -1;
		for (int i = 0; i < Constants.notes.length; i++) {
			if (Constants.notes[i].equals(rootNote)) {
				baseIndex = i;
				break;
			}
		}

		if (!rootNote.equals(bassNote)) {
			if (bassNote.equals("H")) {
				bassNote = "B";
			}
			if (!note.contains(bassNote)) {
				note.add(bassNote);
			}
		}
		for (int i = 0; i < interval.length; i++) {
			String n = Constants.notes[baseIndex + interval[i].intValue()];
			if (!note.contains(n)) {
				note.add(n);
			}
		}

		return note;
	}

	public static ArrayList<String> findHarmonyNotes(String rootNote,
			String chord) {

		if (chord == null) {
			return null;
		}

		Integer[] interval = chordIntervals.get(chord);
		ArrayList<String> note = new ArrayList<String>();

		if (rootNote.equals("H")) {
			rootNote = "B";
		}

		int baseIndex = -1;
		for (int i = 0; i < Constants.notes.length; i++) {
			if (Constants.notes[i].equals(rootNote)) {
				baseIndex = i;
				break;
			}
		}

		for (int i = 0; i < interval.length; i++) {
			note.add(Constants.notes[baseIndex + interval[i].intValue()]);
		}

		return note;
	}

	private static String addSpace(String string, int space, char fill) {

		int spacer = 0;
		StringBuffer sb = new StringBuffer();

		if (space > string.length())
			spacer = space - string.length();

		for (int i = 0; i < spacer; i++) {
			sb.append(fill);
		}

		return sb.append(string).toString();
	}

	private static void addInterval(String chordName, Integer... intervals) {
		chordIntervals.put(chordName, intervals);
	}

	private static void addInterval(String chordName, String shortkey,
			Integer... intervals) {
		chordIntervals.put(chordName, intervals);
		appendices.put(chordName, shortkey);
	}
}
