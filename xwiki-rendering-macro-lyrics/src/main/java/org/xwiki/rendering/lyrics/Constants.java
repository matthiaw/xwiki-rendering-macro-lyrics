package org.xwiki.rendering.lyrics;
public class Constants {

	public static final String SPACE = "XWiki";
	
	// 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21
	public static String[] intervalKeysDE = { "1", "2-", "2+", "3-", "3+", "4", "b5", "5", "6-", "6+", "7-", "7+", "8", "9-", "9+", "#9", "b11", "11", "#11", "12", "13-", "13+", "14-", "14", "15-",
			"15", "16-", "16" };

	public static String[] intervalKeysEN = { "1", "m2", "2", "m3", "3", "4", "m5", "5", "m6", "6", "m7", "7", "8", "m9", "9", "#9", "m11", "11", "#11", "12", "m13", "13", "m14", "14", "m15", "15",
			"m16", "16" };

	public static String[] intervalNamesDE = { "Prime", "Kleine Sekunde", "Groﬂe Sekunde", "Kleine Terz", "Groﬂe Terz/Verminderte Quarte", "Reine Quarte", "‹berm‰ﬂige Quarte/Verminderte Quinte",
			"Reine Quinte", "‹berm‰ﬂige Quinte/Kleine Sexte", "Groﬂe Sexte", "Kleine Septime", "Groﬂe Septime", "Oktave", "Kleine None", "Groﬂe None", "‹berm‰ﬂige None", "Verminderte Undezime",
			"Undezime", "‹berm‰ﬂige Undezime", "Duodezime", "Kleine Tredezime", "Groﬂe Tredezime", "Kleine Quartuordezime", "Groﬂe Quartuordezime", "m15th", "15th", "m16th", "16th" };

	public static String[] intervalNamesEN = { "unison", "minor 2nd", "major 2nd", "minor 3rd", "major 3rd/dimished 4th", "perfect 4th", "augmented 4th/dimished 5th", "perfect 5th", "minor 6th",
			"augmented 5th/major 6th", "minor 7th", "major 7th", "octave", "flat 9th", "9th", "sharp 9th", "dimished 11th", "11th", "augmented 11th", "12th", "minor 13th", "major 13th", "minor 14th",
			"major 14th", "m15th", "15th", "m16th", "16th" };
	
	public static final String[] stringNames = { "E", "A", "d", "g", "h", "e1" };
	
	public static final String[] notes_e1 = { "e1", "f1", "f#1", "g1", "g#1", "a1", "a#1", "h1", "c2", "c#2", "d2", "d#2", "e2", "f2", "f#2", "g2", "g#2", "a2", "a#2", "h2", "c3", "c3#", "d3", "d3#",
			"e3" };

	public static final String[] notes_h = { "h", "c1", "c#1", "d1", "d#1", "e1", "f1", "f#1", "g1", "g#1", "a1", "a#1", "h1", "c2", "c#2", "d2", "d#2", "e2", "f2", "f#2", "g2", "g#2", "a2", "a#2",
			"h2" };

	public static final String[] notes_g = { "g", "g#", "a", "a#", "h", "c1", "c#1", "d1", "d#1", "e1", "f1", "f#1", "g1", "g#1", "a1", "a#1", "h1", "c2", "c#2", "d2", "d#2", "e2", "f2", "f#2", "g2" };

	public static final String[] notes_d = { "d", "d#", "e", "f", "f#", "g", "g#", "a", "a#1", "h", "c1", "c#1", "d1", "d#1", "e1", "f1", "f#1", "g1", "g#1", "a1", "a#1", "h1", "c2", "c#2", "d2" };

	public static final String[] notes_A = { "A", "A#", "H", "c", "c#", "d", "d#", "e", "f", "f#", "g", "g#", "a", "a#", "h", "c1", "c#1", "d1", "d#1", "e1", "f1", "f#1", "g1", "g#1", "a1" };

	public static final String[] notes_E = { "E", "F", "F#", "G", "G#", "A", "A#", "H", "c", "c#", "d", "d#", "e", "f", "f#", "g", "g#", "a", "a#", "h", "c1", "c#1", "d1", "d#1", "e1" };

	public static final String[] notesFugue_e1 = { "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6", "C#6", "D6",
			"D#6", "E6" };

	public static final String[] notesFugue_h = { "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5",
			"A#5", "B5" };

	public static final String[] notesFugue_g = { "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5", "F5",
			"F#5", "G5" };

	public static final String[] notesFugue_d = { "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "F#4", "A4", "A#4", "B4", "C5",
			"C#5", "D5" };

	public static final String[] notesFugue_A = { "A2", "A#2", "B2", "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4",
			"G#4", "A4" };

	public static final String[] notesFugue_E = { "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2", "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C4#", "C4",
			"D#4", "E4" };

	public static String[] notes = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F",
			"F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#" };
	
	public static String[] notesInDetail = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "H", "c", "c#", "d", "d#", "e", "f", "f#", "g", "g#", "a", "a#", "h", "c1", "c#1", "d1",
			"d#1", "e1", "f1", "f#1", "g1", "g#1", "a1", "a#1", "h1", "c2", "c#2", "d2", "d#2", "e2", "f2", "f#2", "g2", "g#2", "a2", "a#2", "h2", "c3", "c3#", "d3", "d3#", "e3", "f3", "f#3", "g3",
			"g#3", "a3", "a#3", "h3", "c4", "c#4", "d4", "d#4", "e4", "f4", "f#4", "g4", "g#4", "a4", "a#4", "h4" };

}
