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
		
		for (String chord : new Vector<String>(chordIntervals.keySet())) {
			createInversions(chord);
		}

		strings.put("E", Constants.notes_E);
		strings.put("A", Constants.notes_A);
		strings.put("d", Constants.notes_d);
		strings.put("g", Constants.notes_g);
		strings.put("h", Constants.notes_h);
		strings.put("e1", Constants.notes_e1);
	}

	public static String getChordSymbol(String rootNote, String chordPattern) {
		return getChordSymbol(rootNote, chordPattern, rootNote);
	}

	public static String getChordSymbol(String rootNote, String chordPattern, String bassNote) {

		chordPattern = getCorrectChordPattern(rootNote, chordPattern, bassNote);

		ArrayList<String> notes = findHarmonyNotes(rootNote, chordPattern, bassNote);
		String bass = "";
		if (notes.size() > 0) {
			bass = notes.get(0);
		}

		String name = "";

		for (String chord : chordIntervals.keySet()) {
			if (chordPattern.equals(chord) || (chordPattern.contains(chord) && chordPattern.contains("Inversion"))) {
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

	public static String getCorrectBassNote(String baseNote, String chord, String bassNote) {
		String correctedChord = getCorrectChordPattern(baseNote, chord, bassNote);

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

	public static String getCorrectChordName(String baseNote, String chord, String bassNote) {
		String correctedChord = getCorrectChordPattern(baseNote, chord, bassNote);

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
		// System.out.println(v.size());
		for (String s : v) {
			// System.out.println(s);
			if (inversions.get(s).equals(pattern)) {
				list.add(s);
			}
		}
		return list;
	}

	public static boolean hasBassNote(String note, String baseNote, String chord) {
		ArrayList<String> harmonyNotesWithoutBass = ChordCalculator.findHarmonyNotes(baseNote, chord);
		for (int i = 1; i < harmonyNotesWithoutBass.size(); i++) {
			if (harmonyNotesWithoutBass.get(i).equals(note)) {
				return true;
			}
		}
		return false;
	}

//	public static void calculate(String rootNote, String chordPattern) {
//		calculate(rootNote, chordPattern, rootNote);
//	}

	public static ArrayList<String> getNotesInHarmonys(String baseNote, String chord) {

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
						if (!notesInHarmonies.contains(Constants.notesInDetail[i + ii])) {
							notesInHarmonies.add(Constants.notesInDetail[i + ii]);

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
		String[] noteArray = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "H" };
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
						n = noteArray[i] + appendices.get(chord) + "/" + noteArray[j];
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

	public static String getCorrectChordPattern(String rootNote, String chordPattern, String bassNote) {
//	    System.out.println("root: "+rootNote);
//	    System.out.println("chord: "+chordPattern);
//	    System.out.println("bass: "+bassNote);
		if (chordPattern.contains("Inversion") && !rootNote.equals(bassNote)) {
			chordPattern = inversions.get(chordPattern);
		}
		return chordPattern;
	}
	
//	public static void calculate(String rootNote, String chordPatternOrig, String bassNote) {
//
//		String chordPattern = getCorrectChordPattern(rootNote, chordPatternOrig, bassNote);
//
//		String name = getCorrectChordName(rootNote, chordPatternOrig, bassNote);
//		String symbol = getChordSymbol(rootNote, chordPatternOrig, bassNote);
//		ArrayList<String> harmony = findHarmonyNotes(rootNote, chordPatternOrig, bassNote);
//		ArrayList<Integer> intervals = getIntervals(chordPatternOrig);
//		ChordFamily chordFamily = ChordFamilys.getChordFamily(symbol);
//
//		StringBuilder[] stringpattern = new StringBuilder[6];
//
//		for (int i = 0; i < stringpattern.length; i++) {
//			stringpattern[i] = new StringBuilder();
//		}
//
//		Hashtable<String, ArrayList<String>> notesInStrings = null;
//
//		if (rootNote.equals(bassNote)) {
//			notesInStrings = ChordCalculator.findHarmonyNotesOnString(rootNote, chordPattern);
//		} else {
//			notesInStrings = ChordCalculator.findHarmonyNotesOnString(rootNote, chordPattern, bassNote);
//		}
//
//		for (int i = 0; i < 25; i++) {
//			for (int j = 0; j < 6; j++) {
//				String stringName = Constants.stringNames[j];
//				if (containsStringHarmonyNoteOnFret(i, stringName, notesInStrings.get(stringName))) {
//
//					String note = strings.get(stringName)[i];
//					if (chordFamily != null) {
//						for (Chord chord : chordFamily.getChords()) {
//							if (chord.getNoteOnString(stringName).equals(note)) {
//								note = note + "!";
//							}
//						}
//					} else {
////						log.error("Keine Akkord-Infos zu '" + symbol + "' gefunden. Wurde ChordFamily-Name mit Erstennung an Calculator-Syntax angeglichen?");
//					}
//
//					stringpattern[j].append(addSpace(note, 3, ' ') + " | ");
//				} else {
//					stringpattern[j].append("    | ");
//				}
//			}
//		}
//
//		StringBuilder numbering = new StringBuilder();
//		for (int i = 0; i < 25; i++) {
//			numbering.append(addSpace(i + "", 3, ' ') + " | ");
//		}
//
//		StringBuilder sb = new StringBuilder();
//
//		sb.append("Akkord '" + name + "' hat Symbol '" + symbol + "' mit Harmonie " + harmony + " der Intervalle " + intervals + "\n<BR/>");
//
//		sb.append("    -------------------------------------------------------------------------------------------------------------------------------------------------\n<BR/>");
//		for (int i = stringpattern.length - 1; i >= 0; i--) {
//			sb.append(stringpattern[i] + "\n<BR/>");
//			sb.append("    -------------------------------------------------------------------------------------------------------------------------------------------------\n<BR/>");
//		}
//		sb.append(numbering);
//
//		StringBuilder chords = new StringBuilder();
//		if (chordFamily != null) {
//			for (Chord chord : chordFamily.getChords()) {
//
//				String genericBassNote = chord.getBassNote().toUpperCase().replace("1", "").replace("2", "").replace("3", "");
//				if (!bassNote.trim().equals(genericBassNote.trim())) {
////					log.error("'"+chord.getBassNote() + "' ist als Bassnote in Akkord '" + symbol + "' angegeben, wobei Bassnote '" + bassNote + "' sein sollte.");
//					chords.append("<FONT COLOR=\"#FF0000\">" + chord.getBassNote() + " is NOT Bass-Note in " + bassNote + "</FONT>");
//				}
//
//				String image = "<A COLOR=\"#FFFFFF\" HREF=\"../chords/" + chord.getNameWritable() + ".mid\"><IMG CLASS=\"openskies_chordinformation\" SRC=\"../chords/" + chord.getNameWritable()
//						+ ".png\" ALT=\"" + chordFamily.getCalculatedName() + ", Symbol: " + chordFamily.getCalculatedSymbol() + "\"  TITLE=\"" + chordFamily.getCalculatedName() + ", Symbol: "
//						+ chordFamily.getCalculatedSymbol() + "\"></A>";
//				chords.append(image);
//			}
//		} else {
//			chords.append("<FONT CLASS=\"openskies_chordmissed\">" + symbol + "</FONT>&nbsp;");
//		}
//
//		StringBuilder page = new StringBuilder();
//		page.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
//		page.append("<HTML xmlns=\"http://www.w3.org/1999/xhtml\">");
//		page.append("<HEAD>");
//		page.append("   <META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
//		page.append("   <META name=\"Generator\" content=\"Open Skies Lyrics\">");
//		page.append("   <TITLE>Lyrics Box - "+chordFamily.getFamilyName()+"</TITLE>");
//		page.append("   <LINK rel=\"stylesheet\" type=\"text/css\" href=\"../../templates/style.css\">");
//		page.append("</HEAD>");
//		page.append("<BODY>");
//		page.append("<H1 CLASS=\"openskies_title\">" + "Akkord '" + name + "' - " + symbol + "</H1>");
//		page.append("<TABLE CLASS=\"openskies_chordinformation\">");
//
//		StringBuilder namesString = new StringBuilder();
//		ArrayList<String> names = chordFamily.getNames();
//		for (int i = 0; i < names.size(); i++) {
//			String n = names.get(i);
//			namesString.append(n);
//			if (i + 1 < names.size()) {
//				namesString.append(", ");
//			}
//		}
//
//		page.append("<TR><TD><B>Alternative Namen</B></TD><TD>" + namesString.toString() + "</TD></TR>");
//		page.append("<TR><TD><B>Bassnote / Bass</B></TD><TD>" + bassNote + "</TD></TR>");
//		page.append("<TR><TD><B>Grundton / Root</B></TD><TD>" + rootNote + "</TD></TR>");
//		page.append("<TR><TD><B>Hamonie / Harmony</B></TD><TD>" + harmony + "</TD></TR>");
//		page.append("<TR><TD><B>Halbtonintervalle</B></TD><TD>" + intervals + "</TD></TR>");
//		StringBuilder keysDE = new StringBuilder();
//		StringBuilder keysEN = new StringBuilder();
//		StringBuilder namesDE = new StringBuilder();
//		StringBuilder namesEN = new StringBuilder();
//		for (int i = 0; i < intervals.size(); i++) {
//			int halbtonschritt = intervals.get(i);
//			keysDE.append(Constants.intervalKeysDE[halbtonschritt]);
//			keysEN.append(Constants.intervalKeysEN[halbtonschritt]);
//			namesDE.append(Constants.intervalNamesDE[halbtonschritt]);
//			namesEN.append(Constants.intervalNamesEN[halbtonschritt]);
//			if (i + 1 < intervals.size()) {
//				keysDE.append(", ");
//				keysEN.append(", ");
//				namesDE.append(", ");
//				namesEN.append(", ");
//			}
//		}
//		page.append("<TR><TD><B>Intervalsymbole (DE)</B></TD><TD>" + keysDE.toString() + "</TD></TR>");
//		page.append("<TR><TD><B>Intervalsymbole (EN)</B></TD><TD>" + keysEN.toString() + "</TD></TR>");
//		page.append("<TR><TD><B>Intervalnamen (DE)</B></TD><TD>" + namesDE.toString() + "</TD></TR>");
//		page.append("<TR><TD><B>Intervalnamen (EN)</B></TD><TD>" + namesEN.toString() + "</TD></TR>");
//		page.append("</TABLE><BR/>");
//		page.append("<TABLE><TR><TD>");
//		// page.append("");
//		// page.append("<CODE>" + sb.toString().replace(" ", "&nbsp;") +
//		// "</CODE><BR/>");
//		if (chordFamily != null) {
//			String image = "<IMG CLASS=\"openskies_chordinformation\" SRC=\"" + chordFamily.getWriteableFamilyName() + ".png\" ALT=\"" + chordFamily.getCalculatedName() + ", Symbol: "
//					+ chordFamily.getCalculatedSymbol() + "\"  TITLE=\"" + chordFamily.getCalculatedName() + ", Symbol: " + chordFamily.getCalculatedSymbol() + "\">";
//			page.append("" + image + "</TD><TD>");
//		} else {
//			String image = "<IMG CLASS=\"openskies_chordinformation\" SRC=\"" + symbol.replace("/", "_").replace("#", "x").replace("°", "dim").replace("M", "Major") + ".png\" ALT=\"" + name
//					+ ", Symbol: " + symbol + "\"  TITLE=\"" + name + ", Symbol: " + symbol + "\">";
//			page.append("" + image + "</TD><TD>");
//		}
//		page.append("" + chords.toString() + "</TD></TR></TABLE>");
//		page.append("</BODY>");
//		page.append("</HTML>");
//
//		createChordDiagram(rootNote, chordPatternOrig, bassNote);
//
//		containsHarmonyNotes(rootNote, chordPatternOrig, bassNote);

//		try {
//			if (chordFamily != null) {
//				String filename = "songs" + File.separatorChar + "patterns" + File.separatorChar + chordFamily.getWriteableFamilyName() + ".html";
//				FileUtils.writeStringToFile(new File(filename), page.toString(), "UTF-8");
//			} else {
//				String filename = "songs" + File.separatorChar + "patterns" + File.separatorChar + symbol.replace("/", "_").replace("#", "x").replace("°", "dim").replace("M", "Major") + ".html";
//				FileUtils.writeStringToFile(new File(filename), page.toString(), "UTF-8");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

//	}

//	public static void createChordDiagram(String rootNote, String chordPatternOrig, String bassNote) {
//		createChordDiagram(1.0, rootNote, chordPatternOrig, bassNote);
//	}

//	public static Color getFadeColor(Color start, Color end, int percentage) {
//		int rs = start.getRed();
//		int gs = start.getGreen();
//		int bs = start.getBlue();
//		
//		int re = end.getRed();
//		int ge = end.getGreen();
//		int be = end.getBlue();
//		
//		int rp = 0;
//		for (int r = 0; r <= 100; r++) {
//			if (r == percentage) {
//				rp = rs + r * ((re - rs) / 100);
//			}
//		}
//
//		int gp = 0;
//		for (int g = 0; g <= 100; g++) {
//			if (g == percentage) {
//				gp = gs + g * ((ge - gs) / 100);
//			}
//		}
//
//		int bp = 0;
//		for (int b = 0; b <= 100; b++) {
//			if (b == percentage) {
//
//				bp = bs + b * ((be - bs) / 100);
//			}
//		}
//
//		return new Color(rp, gp, bp);
//	}

//	public static void createChordDiagram(double scale, String rootNote, String chordPatternOrig, String bassNote) {
//		int width = 200;
//		int height = 739;
//		int left_border = 50;
//		int right_border = 20;
//		int upper_border = 70;
//		int lower_border = 20;
//		int numberOfFrets = 25;
//		int fretSpace = (height - lower_border - upper_border) / (numberOfFrets - 1);
//		int stringSpace = (width - right_border - left_border) / 5;
//
//		String symbol = getChordSymbol(rootNote, chordPatternOrig, bassNote);
//		ChordFamily chordFamily = ChordFamilys.getChordFamily(symbol);
//		String chordPattern = getCorrectChordPattern(rootNote, chordPatternOrig, bassNote);
//
//		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		Graphics2D g = (Graphics2D) image.getGraphics();
//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
//		g.setColor(Color.BLACK);
//		g.setFont(new Font("default", Font.BOLD, 12));
//
//		// draw Strings
//		for (int i = 0; i < 6; i++) {
//			g.drawLine(left_border + (i * stringSpace), upper_border, left_border + (i * stringSpace), (height - lower_border));
//		}
//
//		// draw Frets
//		for (int i = 0; i < numberOfFrets; i++) {
//			g.drawLine(left_border, upper_border + (i * fretSpace), (width - right_border), upper_border + (i * fretSpace));
//		}
//
//		Hashtable<String, ArrayList<String>> notesInStrings = null;
//
//		if (rootNote.equals(bassNote)) {
//			notesInStrings = ChordCalculator.findHarmonyNotesOnString(rootNote, chordPattern);
//		} else {
//			notesInStrings = ChordCalculator.findHarmonyNotesOnString(rootNote, chordPattern, bassNote);
//		}
//
//		int fingerSize = 20;
//
//		boolean color1Set = true;
//		Hashtable<Chord, Color> colors = new Hashtable<Chord, Color>();
//		if (chordFamily != null) {
//			for (Chord chord : chordFamily.getChords()) {
//
//				Color color;
//				if (color1Set) {
//					color = new Color(1f, 1f, 0f, 0.5f);// Color.YELLOW;//getFadeColor(Color.YELLOW,
//														// Color.ORANGE, 50);
//					color1Set = false;
//				} else {
//					color = new Color(0f, 1f, 1f, 0.5f);// Color.ORANGE;//getFadeColor(Color.ORANGE,
//														// Color.YELLOW, 50);
//					color1Set = true;
//				}
//
//				colors.put(chord, color);
//			}
//		}
//
//		g.setColor(Color.BLACK);
//		g.setFont(new Font("default", Font.BOLD, 26));
//		int textXT = 10;
//		int textYT = upper_border / 2 - 5;
//		g.drawString(symbol, textXT, textYT);
//
//		Composite reset = g.getComposite();
//
//		for (int i = 0; i < 25; i++) {
//			int fretPos = i;
//
//			g.setColor(Color.BLACK);
//			g.setFont(new Font("default", Font.PLAIN, 12));
//			if (i > 0) {
//				FontMetrics fontMetrics0 = g.getFontMetrics();
//				Rectangle stringBounds0 = fontMetrics0.getStringBounds(i + "", g).getBounds();
//				int textX0 = left_border - stringSpace - stringBounds0.width / 2;
//				int textY0 = upper_border + fretPos * fretSpace - fretSpace / 2 + fontMetrics0.getAscent() / 2;
//				g.drawString(i + "", textX0, textY0);
//			}
//
//			g.setFont(new Font("default", Font.BOLD, 12));
//			for (int j = 0; j < 6; j++) {
//				int stringPos = j;
//				String stringName = Constants.stringNames[j];
//				if (containsStringHarmonyNoteOnFret(i, stringName, notesInStrings.get(stringName))) {
//					g.setComposite(reset);
//					g.setColor(Color.BLACK);
//					String note = strings.get(stringName)[i];
//					if (fretPos != 0) {
//						if (j < 4 && note.toUpperCase().replace("1", "").replace("2", "").replace("3", "").equals(bassNote)) {
//							g.setColor(Color.BLACK);
//							fingerSize = 24;
//							g.fillOval(left_border + stringPos * stringSpace - fingerSize / 2, upper_border + fretPos * fretSpace - fingerSize / 2 - fretSpace / 2, fingerSize, fingerSize);
//							fingerSize = 20;
//							g.setColor(Color.LIGHT_GRAY);
//							g.fillOval(left_border + stringPos * stringSpace - fingerSize / 2, upper_border + fretPos * fretSpace - fingerSize / 2 - fretSpace / 2, fingerSize, fingerSize);
//						} else {
//							g.setColor(Color.LIGHT_GRAY);
//							g.fillOval(left_border + stringPos * stringSpace - fingerSize / 2, upper_border + fretPos * fretSpace - fingerSize / 2 - fretSpace / 2, fingerSize, fingerSize);
//						}
//
//						g.setColor(Color.BLACK);
//						FontMetrics fontMetrics = g.getFontMetrics();
//						Rectangle stringBounds = fontMetrics.getStringBounds(note, g).getBounds();
//						int textX = left_border + stringPos * stringSpace - stringBounds.width / 2;
//						int textY = upper_border + fretPos * fretSpace - fretSpace / 2 + fontMetrics.getAscent() / 2;
//						g.drawString(note, textX, textY);
//						g.setColor(Color.BLACK);
//					} else {
//						if (j < 4 && note.toUpperCase().replace("1", "").replace("2", "").replace("3", "").equals(bassNote)) {
//							g.setColor(Color.BLACK);
//							fingerSize = 24;
//							g.fillOval(left_border + stringPos * stringSpace - fingerSize / 2, upper_border + fretPos * fretSpace - fingerSize / 2 - fretSpace / 2, fingerSize, fingerSize);
//							fingerSize = 20;
//							g.setColor(Color.WHITE);
//							g.fillOval(left_border + stringPos * stringSpace - fingerSize / 2, upper_border + fretPos * fretSpace - fingerSize / 2 - fretSpace / 2, fingerSize, fingerSize);
//							g.setColor(Color.LIGHT_GRAY);
//							g.drawOval(left_border + stringPos * stringSpace - fingerSize / 2, upper_border + fretPos * fretSpace - fingerSize / 2 - fretSpace / 2, fingerSize, fingerSize);
//						} else {
//							g.setColor(Color.LIGHT_GRAY);
//							g.drawOval(left_border + stringPos * stringSpace - fingerSize / 2, upper_border + fretPos * fretSpace - fingerSize / 2 - fretSpace / 2, fingerSize, fingerSize);
//						}
//						g.setColor(Color.BLACK);
//						FontMetrics fontMetrics = g.getFontMetrics();
//						Rectangle stringBounds = fontMetrics.getStringBounds(note, g).getBounds();
//						int textX = left_border + stringPos * stringSpace - stringBounds.width / 2;
//						int textY = upper_border + fretPos * fretSpace - fretSpace / 2 + fontMetrics.getAscent() / 2;
//						g.drawString(note, textX, textY);
//					}
//
//					if (chordFamily != null) {
//						for (Chord chord : chordFamily.getChords()) {
//							g.setColor(colors.get(chord));
//							if (chord.getNoteOnString(stringName).equals(note)) {
//								g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
//								if (fretPos != 0) {
//									g.setComposite(reset);
//									g.setColor(colors.get(chord));
//									g.fillOval(left_border + stringPos * stringSpace - fingerSize / 2, upper_border + fretPos * fretSpace - fingerSize / 2 - fretSpace / 2, fingerSize, fingerSize);
//									g.setColor(Color.BLACK);
//									FontMetrics fontMetrics = g.getFontMetrics();
//									Rectangle stringBounds = fontMetrics.getStringBounds(note, g).getBounds();
//									int textX = left_border + stringPos * stringSpace - stringBounds.width / 2;
//									int textY = upper_border + fretPos * fretSpace - fretSpace / 2 + fontMetrics.getAscent() / 2;
//									g.drawString(note, textX, textY);
//									g.setColor(colors.get(chord));
//								} else {
//									g.fillOval(left_border + stringPos * stringSpace - fingerSize / 2, upper_border + fretPos * fretSpace - fingerSize / 2 - fretSpace / 2, fingerSize, fingerSize);
//									FontMetrics fontMetrics = g.getFontMetrics();
//									Rectangle stringBounds = fontMetrics.getStringBounds(note, g).getBounds();
//									int textX = left_border + stringPos * stringSpace - stringBounds.width / 2;
//									int textY = upper_border + fretPos * fretSpace - fretSpace / 2 + fontMetrics.getAscent() / 2;
//									g.drawString(note, textX, textY);
//								}
//							}
//						}
//					} else {
////						log.error("Keine Akkord-Infos zu '" + symbol + "' gefunden.");
//					}
//
//				}
//			}
//		}
//
//		try {
//			if (chordFamily != null) {
//				File file = new File("songs" + File.separatorChar + "patterns" + File.separatorChar + chordFamily.getWriteableFamilyName() + ".png");
//				ImageIO.write(image, "png", file);
//			} else {
//				File file = new File("songs" + File.separatorChar + "patterns" + File.separatorChar + symbol.replace("/", "_").replace("#", "x").replace("°", "dim").replace("M", "Major") + ".png");
//				ImageIO.write(image, "png", file);
//			}
//
//		} catch (FileNotFoundException e) {
//			System.err.println("Error creating chord-diagramm '" + symbol + "'.");
//		} catch (IOException e) {
//			System.err.println("Error creating chord-diagramm '" + symbol + "'.");
//		}
//
//	}

	public static boolean containsStringHarmonyNoteOnFret(int fret, String stringName, ArrayList<String> notesOnString) {
		for (String note : notesOnString) {
			if (fret == getFret(stringName, note)) {
				return true;
			}
		}

		return false;
	}

	public static boolean containsHarmonyNotes(String rootNote, String chordPatternOrig, String bassNote) {

		String symbol = getChordSymbol(rootNote, chordPatternOrig, bassNote);
		ChordFamily chordFamily = ChordFamilys.getChordFamily(symbol);
		String chordPattern = getCorrectChordPattern(rootNote, chordPatternOrig, bassNote);

		Hashtable<String, ArrayList<String>> notesInStrings = null;
		if (rootNote.equals(bassNote)) {
			notesInStrings = ChordCalculator.findHarmonyNotesOnString(rootNote, chordPattern);
		} else {
			notesInStrings = ChordCalculator.findHarmonyNotesOnString(rootNote, chordPattern, bassNote);
		}

		boolean status_overall = true;
		if (chordFamily != null) {
			for (Chord chord : chordFamily.getChords()) {
				for (int j = 0; j < 6; j++) {
					String stringName = Constants.stringNames[j];
					boolean status_ok = false;
					String missedNote = "";
					for (int i = 0; i < 25; i++) {
						if (containsStringHarmonyNoteOnFret(i, stringName, notesInStrings.get(stringName))) {
							String note = strings.get(stringName)[i];

							note = note.replace("B", "H").replace("b", "h");

							if (!chord.getNoteOnString(stringName).equals("x")) {
								if (chord.getNoteOnString(stringName).equals(note)) {
									status_ok = true;
								}
							} else {
								status_ok = true;
							}
						}
					}
					if (!status_ok) {
						status_overall = false;
//						log.error("Note '" + chord.getNoteOnString(stringName) + "' ist in Akkord '" + symbol + "' angegeben, existiert aber nicht im Akkord-Muster.");
					}
				}
			}
		}

		return status_overall;
	}

	public static int getFret(String stringName, String note) {
		String[] guitarString = strings.get(stringName);
		int index = -1;
		for (int i = 0; i < guitarString.length; i++) {
			if (guitarString[i].equals(note)) {
				return i;
			}
		}
		return -1;
	}

	public static Hashtable<String, ArrayList<String>> findHarmonyNotesOnString(String rootNote, String chordPattern, String bass) {

		ArrayList<String> harmonyNotes = ChordCalculator.findHarmonyNotes(rootNote, chordPattern, bass);

		Hashtable<String, ArrayList<String>> notesOnStrings = new Hashtable<String, ArrayList<String>>();

		for (int i = 0; i < Constants.stringNames.length; i++) {
			String stringName = Constants.stringNames[i];
			String[] guitarString = strings.get(stringName);
			ArrayList<String> notesOnString = new ArrayList<String>();
			for (int ii = 0; ii < guitarString.length; ii++) {
				String note = guitarString[ii];
				String genericNote = note.toUpperCase().replace("1", "").replace("2", "").replace("3", "").replace("H", "B").replace("h", "b");
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

	public static Hashtable<String, ArrayList<String>> findHarmonyNotesOnString(String rootNote, String chordPattern) {
		ArrayList<String> harmonyNotes = ChordCalculator.findHarmonyNotes(rootNote, chordPattern);

		Hashtable<String, ArrayList<String>> notesOnStrings = new Hashtable<String, ArrayList<String>>();

		for (int i = 0; i < Constants.stringNames.length; i++) {
			String stringName = Constants.stringNames[i];
			String[] guitarString = strings.get(stringName);
			ArrayList<String> notesOnString = new ArrayList<String>();
			for (int ii = 0; ii < guitarString.length; ii++) {
				String note = guitarString[ii];
				String genericNote = note.toUpperCase().replace("1", "").replace("2", "").replace("3", "").replace("H", "B").replace("h", "b");
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

	public static ArrayList<String> findHarmonyNotes(String rootNote, String chord, String bassNote) {

		bassNote = getCorrectBassNote(rootNote, chord, bassNote);
		chord = getCorrectChordPattern(rootNote, chord, bassNote);

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

	public static ArrayList<String> findHarmonyNotes(String rootNote, String chord) {
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

	private static void addInterval(String chordName, String shortkey, Integer... intervals) {
		chordIntervals.put(chordName, intervals);
		appendices.put(chordName, shortkey);
	}
}
