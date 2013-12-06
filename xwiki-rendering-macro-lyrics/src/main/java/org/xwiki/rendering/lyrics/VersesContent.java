package org.xwiki.rendering.lyrics;
import java.util.ArrayList;

public class VersesContent implements Content {

	private Song song;
	@Override
	public void setSong(Song song) {
		this.song = song;
	}
	
	private ArrayList<Sequence> sequences;

	public void setContent(ArrayList<Sequence> sequences) {
		this.sequences = sequences;
	}

	@Override
	public String getContent() {

		int capoFret = song.getCapo();
		
		if (sequences != null) {
			StringBuilder result = new StringBuilder();
			StringBuilder chords = new StringBuilder();
			StringBuilder verses = new StringBuilder();
			for (Sequence sequence : sequences) {
				if (!((sequence.getName().trim().equals("")) && (sequence.getVerse().trim().equals("")))) {
					if (sequence.getName().trim().equals("")) {
						String verse = sequence.getVerse().trim() + " ";
						chords.append("|(% class=\"lyrics_chords\" %)");
						verses.append("|(% class=\"lyrics_verses\" %)"+verse);
					} else {
						
						String chord = sequence.getName();
						
						if (capoFret != 0) {
						ChordFamily family = ChordFamilys.getChordFamily(sequence.getName());
						if (family != null) {
							Chord c = family.getChordOnFret(sequence.getFretPos());
							if (c != null) {
								Chord capoChord = ChordFamilys.getChordForCapoPattern(c, capoFret);
								if (capoChord != null) {
									chord = chord + " (" + capoChord.getFamily().getCalculatedSymbol() + ")";
								} else {
//									log.warn("Es wurde kein Akkord für Kapo auf Bund " + capoFret + " für Akkord '" + chord.getName() + "' in Lied '" + rawTitle+"' gefunden.");
								}
							}
						}
					}
						
						
						String verse = sequence.getVerse().trim() + " ";
						chords.append("|(% class=\"lyrics_chords\" %)"+chord);
						verses.append("|(% class=\"lyrics_verses\" %)"+verse);
					}
				}
			}
			result.append("(% class=\"lyrics\" %)"+"\n");
			result.append(chords.toString()+"\n");
			result.append(verses.toString()+"\n");
			return result.toString();
		}

		return "";
		
//		StringBuilder sbChords = new StringBuilder();
//		StringBuilder sbVerses = new StringBuilder();
//		StringBuilder sb = new StringBuilder();
//		StringBuilder sbWithoutChords = new StringBuilder();
//
//		boolean foundVerse = false;
//		for (Sequence chord : sequences) {
//			sbChords.append("<TD class=\"lyrics_chord\">");
//			sbChords.append(chord.getName());
//
////			if (this.capoFret != 0) {
////				ChordFamily family = ChordFamilys.getChordFamily(chord.getName());
////				if (family != null) {
////					Chord c = family.getChordOnFret(chord.getFretPos());
////					if (c != null) {
////						Chord capoChord = Chords.getChordForCapoPattern(c, capoFret);
////						if (capoChord != null) {
////							sbChords.append(" (" + capoChord.getFamily().getCalculatedSymbol() + ")");
////						} else {
//////							log.warn("Es wurde kein Akkord für Kapo auf Bund " + capoFret + " für Akkord '" + chord.getName() + "' in Lied '" + rawTitle+"' gefunden.");
////						}
////					}
////				}
////			}
//
//			sbChords.append("</TD>");
//			sbVerses.append("<TD class=\"lyrics_verse\">");
//			if (chord.getVerse() != null) {
//				sbVerses.append(chord.getVerse());
//				foundVerse = true;
//			} else {
//				sbVerses.append("&nbsp;");
//			}
//			sbVerses.append("</TD>");
//		}
//
//		if (sequences.size() != 0) {
//			sb.append("	    <TABLE border=\"0\">\n");
//			sb.append("          <TR>" + sbChords.toString() + "</TR>\n");
//			if (foundVerse) {
//				sbWithoutChords.append("     <TABLE><TR>\n");
//				sb.append("          <TR>" + sbVerses.toString() + "</TR>\n");
//				sbWithoutChords.append(sbVerses.toString() + "\n");
//				sbWithoutChords.append("     </TR></TABLE><BR/>\n");
//			}
//			sb.append("	    </TABLE><BR/>\n");
//		}
//		return sb.toString();
//		
	}

}
