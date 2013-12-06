package org.xwiki.rendering.lyrics;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

/**
 * 
 * 
 * @author Matthias Wegner
 *
 */
public class Song {

	private String artist;
	private String copyright;
	private String subtitle;
	private List<String> melodyFiles = new ArrayList<String>();
	private List<String> videoFiles = new ArrayList<String>();
	private String notes;
	private String guitarPro;
	private String number;
	private double chordScale;

	public String getArtist() {
		return artist;
	}

	public String getCopyright() {
		return copyright;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public List<String> getMelodyFiles() {
		return melodyFiles;
	}

	public List<String> getVideoFiles() {
		return videoFiles;
	}

	public String getNotes() {
		return notes;
	}

	public String getGuitarPro() {
		return guitarPro;
	}

	public String getNumber() {
		return number;
	}

	public String getSource() {
		return source;
	}

	public String getTitle() {
		return title;
	}

	public int getCapo() {
		return capo;
	}

	public Long getCcli() {
		return ccli;
	}

	public List<Content> getContent() {
		return content;
	}

	public List<String> getBibleVerses() {
		return bibleVerses;
	}

	private String source;
	private String title;
	private int capo;
	private Long ccli;
	private List<Content> content = new ArrayList<Content>();
	private List<String> bibleVerses = new ArrayList<String>();
	private ArrayList<Sequence> chords = new ArrayList<Sequence>();
	private ArrayList<Sequence> overallChords = new ArrayList<Sequence>();

	public String parse() {
		StringBuilder sb = new StringBuilder();

		if (content.get(0) instanceof BreaklineContent) {
			content.remove(0);
		}

		if (content.get(content.size() - 1) instanceof BreaklineContent) {
			content.remove(content.size() - 1);
		}
		for (Content c : content) {
			sb.append(c.getContent() + "\n");
		}

		VelocityContext context = new VelocityContext();
		context.put("content", sb.toString());
		context.put("title", title);
		context.put("subtitle", subtitle);
		context.put("artist", artist);
		context.put("score", getScore());
		if (capo == 0) {
			context.put("capo", "");
		} else {
			context.put("capo", capo);
		}
		context.put("number", number);
		context.put("copyright", copyright);
		context.put("ccli", ccli);
		context.put("source", source);

		String result = "";
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.init();
			StringWriter writer = new StringWriter();
			StringBuilder template = new StringBuilder();

			template.append("{{velocity}}\n");
			template.append("$xwiki.ssfx.use(\"js/xwiki/lyrics/lyrics.css\")\n");
			template.append("{{/velocity}}\n");
			
			template.append("\n{{html  clean=\"false\"}}\n");
			for (Chord c : drawableChords) {
				template.append("" + c.getDiagram(chordScale) + "\n");
			}
			if (drawableCapoChords.size()>0) {
				template.append("<BR/>\n");	
			}
			for (Chord c : drawableCapoChords) {
				template.append("" + c.getDiagram(chordScale) + "\n");
			}
			template.append("<BR/>{{/html}}\n");

			template.append("#if (\"$!subtitle\" == \"\")\n");
			template.append("#else\n");
			template.append("  **$subtitle**\n");
			template.append("#end\n");

			template.append("\n{{html}}\n");
			template.append("<div class=\"lyrics_info\">\n");
			template.append("<div class=\"lyrics_artist\">$artist</div><br/>\n");
			template.append("<div class=\"lyrics_score\">$score</div><br/>\n");
			template.append("#if (\"$!capo\" == \"\")\n");
			template.append("#else\n");
			template.append("<div class=\"lyrics_capo\">Capo: $capo</div></div>\n");
			template.append("#end\n");
			
			template.append("{{/html}}\n");

			template.append("$content\n");

			template.append(",,$!copyright,,\n");
			template.append("#if (\"$!ccli\" == \"\")\n");
			template.append("#else\n");
			template.append(",,CCLI: [[$!ccli>>http://de.songselect.com/songs/$!ccli/]],,\n");
			template.append("#end\n");
			template.append(",,$!source,,\n");

			try {
				// Template t = ve.getTemplate("lyrics.vm");
				// t.merge(context, writer);

				// String vm =
				// URI.create("/templates/lyrics.vm").normalize().toString();
				// String c = "";
				// result = XWikiVelocityRenderer.evaluate(c, vm,
				// (VelocityContext) xcontext.get("vcontext"), xcontext);

				Velocity.evaluate(context, writer, "log", template.toString());
				result = writer.toString();

			} catch (org.apache.velocity.exception.ResourceNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return result;
	}

	private String getScale(String key, boolean major) {
		int keyIndex = 1000;
		for (int i = 0; i < Constants.notesInDetail.length; i++) {
			if (key.equals(Constants.notesInDetail[i])) {
				keyIndex = i;
			}
		}

		StringBuilder sb = new StringBuilder();
		if (major) {
			// major
			while (keyIndex + 12 < Constants.notesInDetail.length) {
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 2;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 2;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 1;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 2;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 2;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 2;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 1;
			}
		} else {
			// minor
			while (keyIndex + 12 < Constants.notesInDetail.length) {
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 2;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 1;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 2;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 2;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 1;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 2;
				sb.append(Constants.notesInDetail[keyIndex] + " ");
				keyIndex = keyIndex + 2;
			}
		}

		return sb.toString();
	}

	public String getScore() {

		int max = 0;
		String key = "";
		for (int i = 0; i < 12; i++) {
			String majorScale = " " + getScale(Constants.notesInDetail[i], true) + " ";

			int noteCounter = 0;
			for (String note : getNotesInChords()) {
				if (majorScale.contains(" " + note + " ")) {
					noteCounter++;
				}
			}
			if (noteCounter > max) {
				max = noteCounter;
				key = Constants.notesInDetail[i] + "-Dur";
			}

			String minorScale = " " + getScale(Constants.notesInDetail[i], false) + " ";

			noteCounter = 0;
			for (String note : getNotesInChords()) {
				if (minorScale.contains(" " + note + " ")) {
					noteCounter++;
				}
			}
			if (noteCounter > max) {
				max = noteCounter;
				key = Constants.notesInDetail[i] + "-Moll";
			}

		}

		return key;
	}

	private ArrayList<String> getNotesInChords() {
		ArrayList<String> notesInChords = new ArrayList<String>();
		for (Sequence chord : chords) {
			ChordFamily chordInfo = ChordFamilys.getChordFamily(chord.getName());
			if (chordInfo != null) {
				StringTokenizer st = new StringTokenizer(chordInfo.getFirstChord().getNotes(), " ");
				while (st.hasMoreTokens()) {
					String note = st.nextToken();
					if (!notesInChords.contains(note.trim())) {
						notesInChords.add(note.trim());
					}
				}
			}
		}
		// System.out.println("Notes found: "+notesInChords.size());
		return notesInChords;
	}

	public Song(String song) {
		chordScale = 0.5;
		load(song);
	}

	private void load(String song) {
		LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(song));

		boolean isChorus = false;

		List<Content> chorusContent = new ArrayList<Content>();

		try {
			String line = lineNumberReader.readLine();
			do {
				try {
					if (0 != line.length()) { // skip empty lines

						ArrayList<Sequence> chordsInLine = new ArrayList<Sequence>();

						StringTokenizer lineToken = new StringTokenizer(line, "{:}\n");
						String token = lineToken.nextToken().toLowerCase();

						if ((token.equals("art")) || (token.equals("artist"))) {
							artist = tokenizeContent(lineToken);
						} else if ((token.equals("copy")) || (token.equals("copyright"))) {
							copyright = tokenizeContent(lineToken);
						} else if (token.equals("bible")) {
							bibleVerses.add(tokenizeContent(lineToken));
						} else if (token.equals("ccli")) {
							ccli = new Long(tokenizeContent(lineToken));
						} else if ((token.equals("st")) || (token.equals("su")) || (token.startsWith("subti"))) {
							subtitle = tokenizeContent(lineToken);
						} else if ((token.equals("url")) || (token.startsWith("source"))) {
							source = tokenizeContent(lineToken);
						} else if ((token.equals("guitarpro")) || (token.startsWith("gp6"))) {
							guitarPro = tokenizeContent(lineToken);
						} else if ((token.equals("notes"))) {
							notes = tokenizeContent(lineToken);
						} else if ((token.equals("scale"))) {
							chordScale = Double.parseDouble(tokenizeContent(lineToken));
						} else if (token.startsWith("capo") || token.startsWith("kapo")) {
							capo = new Integer(tokenizeContent(lineToken)).intValue();
						} else if (token.startsWith("data_number") || token.startsWith("number")) {
							number = tokenizeContent(lineToken);
						} else if ((token.equals("melody")) || (token.startsWith("mp3"))) {
							String melodyFile = tokenizeContent(lineToken);
							melodyFiles.add(melodyFile);
						} else if ((token.equals("video")) || (token.startsWith("mp4"))) {
							String videoFile = tokenizeContent(lineToken);
							videoFiles.add(videoFile);
						} else if ((token.equals("paragraph"))) {
							ParagraphContent paragraph = new ParagraphContent();
							paragraph.setContent(tokenizeContent(lineToken));
							paragraph.setSong(this);
							content.add(paragraph);
						} else if ((token.equals("c")) || (token.startsWith("commen"))) {
							String c = tokenizeContent(lineToken);
							CommentContent comment = new CommentContent();
							comment.setContent(c);
							comment.setSong(this);
							content.add(comment);
						} else if ((token.equals("t")) || (token.startsWith("title"))) {
							title = tokenizeContent(lineToken);
						} else if ((token.equals("newpage")) || (token.equals("np")) || (token.startsWith("newpag") || (token.startsWith("new_pag")))) {
							content.add(new NewpageContent());
						} else if ((token.equals("soc")) || (token.startsWith("start_of_cho"))) {
							isChorus = true;
						} else if ((token.equals("eoc")) || (token.startsWith("end_of_cho"))) {
							content.add(new ChorusContent(chorusContent));
							isChorus = false;
							chorusContent = new ArrayList<Content>();
						} else if ((token.equals("breakline")) || (token.equals("colb")) || (token.startsWith("column_break"))) {
							content.add(new BreaklineContent());
						} else { // Song Line

							line = "      " + line + "      ";
							StringCharacterIterator stringCharacterIterator = new StringCharacterIterator(line);

							char charAtIndex = stringCharacterIterator.first();
							int indexChar = stringCharacterIterator.getIndex();
							boolean wordFound = false;
							int indexStartChord = -1;
							int indexStartWord = -1;
							String lastChord = "";
							String lastWord = "";
							Sequence lastChordHolder = null;
							while (charAtIndex != 65535) {

								if (charAtIndex == '[') {
									indexStartChord = indexChar + 1;

									String preText = line.substring(0, indexChar);
									if (!(preText.contains("[") || preText.contains("]"))) {
										Sequence c = new Sequence("");
										c.setVerse(preText);
										chordsInLine.add(c);
									}
								}

								if (charAtIndex == ']') {
									int indexEndChord = indexChar;
									String chord = line.substring(indexStartChord, indexEndChord);
									lastChord = chord;
									indexStartWord = indexChar + 1;
									wordFound = true;
									lastChordHolder = new Sequence(lastChord);
									chordsInLine.add(lastChordHolder);

									if (!containsChord(lastChordHolder.getName())) {
										chords.add(lastChordHolder);
									}
									overallChords.add(lastChordHolder);

								}

								if (indexStartWord < indexChar && indexStartWord != -1 && wordFound == true && (charAtIndex == '[' || (indexChar + 1 == line.length()))) {
									int indexEndWord = indexChar;
									if (indexChar + 1 == line.length()) {
										indexEndWord += 1;
									}
									String word = line.substring(indexStartWord, indexEndWord);
									wordFound = false;
									indexStartWord = -1;
									lastWord = word;
									lastChordHolder.setVerse(lastWord);
									lastChord = "";
									lastWord = "";
								}

								charAtIndex = stringCharacterIterator.next();
								indexChar = stringCharacterIterator.getIndex();
							}

							if (chordsInLine.size() > 0) {
								for (Sequence c : chordsInLine) {
									if (capo != 0) {
										if (!c.hasFretPos()) {
											c.setName(c.getName() + ":" + capo);
										}
									}
								}
								VersesContent v = new VersesContent();
								v.setContent(chordsInLine);
								v.setSong(this);
								if (isChorus) {
									chorusContent.add(v);
								} else {
									content.add(v);
								}

							} else if (!line.trim().equals("") && !(line.contains("{") || line.contains("}"))) {
								VerseContent v = new VerseContent();
								v.setSong(this);
								v.setContent(line.trim());
								if (isChorus) {
									chorusContent.add(v);
								} else {
									content.add(v);
								}
							}
						}

					} else {
						content.add(new BreaklineContent());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} while ((line = lineNumberReader.readLine()) != null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Sequence chordInSequence : chords) {

			if (capo != 0) {
				if (!chordInSequence.hasFretPos()) {
					chordInSequence.setName(chordInSequence.getName() + ":" + capo);
				}
			}

			ChordFamily chord = ChordFamilys.getChordFamily(chordInSequence.getName());
			if (chord == null) {
				if (!containsMissedChord(chordInSequence.getName())) {
					missedChords.add(chordInSequence);
				}
			} else {

				if (chordInSequence.hasFretPos()) {
					int fret = chordInSequence.getFretPos();
					addChord(chord, fret, chordInSequence.getName());
				} else {
					addChord(chord, 1, chordInSequence.getName());
				}

			}
		}

		for (Sequence missedChord : missedChords) {
			addChord(null, 1, missedChord.getName());
		}
	}

	private List<Chord> drawableChords = new ArrayList<Chord>();
	private List<Chord> drawableCapoChords = new ArrayList<Chord>();

	private void addChord(ChordFamily chordFamily, int fretPos, String chordName) {

		boolean showMissingChordsOnSpecificFret = false;

		if (chordFamily != null) {
			Chord chord = null;
			if (fretPos == 1) {
				chord = chordFamily.getFirstChord();
			} else {
				chord = chordFamily.getChordOnFret(fretPos);
				if (chord == null) {
					chord = chordFamily.getFirstChord();
					if (showMissingChordsOnSpecificFret) {
						System.out.println("Kein Akkord " + chordFamily.getFamilyName() + " auf Bund " + fretPos + " (Capo: " + capo + ") gefunden in Lied " + title + "!");
					}
				}
			}
			drawableChords.add(chord);

			if (this.capo != 0) {
				Chord capoChord = ChordFamilys.getChordForCapoPattern(chord, capo);
				if (capoChord != null) {
					drawableCapoChords.add(capoChord);
				} else {
					drawableCapoChords.add(new Chord(chordName+":"+fretPos));
				}
			}

		} else {
			drawableChords.add(new Chord(chordName+":"+fretPos));
		}

	}

	private ArrayList<Sequence> missedChords = new ArrayList<Sequence>();

	public boolean containsMissedChord(String name) {
		for (Sequence chord : this.missedChords) {
			if (chord.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsChord(String name) {
		for (Sequence chord : chords) {
			if (chord.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	private String tokenizeContent(StringTokenizer lineToken) {
		try {
			String content = lineToken.nextToken("}\n");
			content = content.substring(1).trim();
			if (0 != content.length()) {
				return content;
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}

}
