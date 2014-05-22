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
 * @author Matthias Wegner
 */
public class Song {

	private String artist;

	private String copyright;

	private String subtitle;

	private List<String> melodies = new ArrayList<String>();

	private List<String> notes = new ArrayList<String>();

	private String guitarPro;

	private String number;

	private double chordScale = 0.0;

	public String getArtist() {
		return artist;
	}

	public String getCopyright() {
		return copyright;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public List<String> getMelodies() {
		return melodies;
	}

	public List<String> getNotes() {
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

	public void setScale(double scale) {
		if (chordScale == 0.0) {
			this.chordScale = scale;
		}

		// System.out.println("Set Scale: " + scale);
	}

	private boolean frets = true;

	public void frets(boolean frets) {
		this.frets = frets;
	}

	public String getOnSong() {
		StringBuilder sbOnsong = new StringBuilder();

		if (content.get(0) instanceof BreaklineContent) {
			content.remove(0);
		}

		if (content.get(content.size() - 1) instanceof BreaklineContent) {
			content.remove(content.size() - 1);
		}

		if (number != null) {
			if (!number.isEmpty()) {
				sbOnsong.append("{title: " + title + " (" + number + ")}\n");
			}
		}

		if (subtitle != null) {
			sbOnsong.append("{subtitle: " + subtitle.trim() + "}\n");
		}
		sbOnsong.append("{artist: " + artist + "}\n");
		if (capo != 0) {
			sbOnsong.append("{capo: " + capo + "}\n");
		}

		sbOnsong.append("\n");
		for (Content c : content) {
			sbOnsong.append(c.getContent(Parser.OnSong).trim()
					.replace("\"", "'")
					+ "\n");
		}

		sbOnsong.append("{copyright: " + copyright + "}\n");

		if (ccli != null) {
			sbOnsong.append("{ccli: " + ccli + "}\n");
		}

		// System.out.println(sbOnsong.toString());

		return sbOnsong.toString().replaceAll("/Newline/", "\n");
	}

	public String parse() {
		StringBuilder sbWiki = new StringBuilder();
		StringBuilder sbOnsong = new StringBuilder();

		if (content.get(0) instanceof BreaklineContent) {
			content.remove(0);
		}

		if (content.get(content.size() - 1) instanceof BreaklineContent) {
			content.remove(content.size() - 1);
		}

		if (number != null) {
			if (!number.isEmpty()) {
				sbOnsong.append("{title: " + title + " (" + number
						+ ")}/Newline/");
			}
		}

		if (subtitle != null) {
			sbOnsong.append("{subtitle: " + subtitle.trim() + "}/Newline/");
		}
		sbOnsong.append("{artist: " + artist + "}/Newline/");
		if (capo != 0) {
			sbOnsong.append("{capo: " + capo + "}/Newline/");
		}

		sbOnsong.append(" /Newline/");
		for (Content c : content) {
			
		//	if (c instanceof VersesContent) {
		//		sbWiki.append(c.getContent(Parser.XWiki) + "");
			//} else {
				sbWiki.append(c.getContent(Parser.XWiki) + "\n");	
			//}
			
			sbOnsong.append(c.getContent(Parser.OnSong).trim()
					.replace("\"", "'")
					+ " /Newline/");
		}

		sbOnsong.append("{copyright: " + copyright + "} /Newline/");

		if (ccli != null) {
			sbOnsong.append("{ccli: " + ccli + "} /Newline/");
		}

		VelocityContext context = new VelocityContext();
		context.put("content", sbWiki.toString());
		context.put("title", title);
		if (subtitle != null) {
			context.put("subtitle", subtitle.trim());
		}
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

		if (source != null) {
			if ((!source.equals("")) && source.startsWith("http")) {
				context.put("source", "[[" + source + ">>" + source + "]]");
			} else {
				context.put("source", source);
			}
		}

		String result = "";
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.init();
			StringWriter writer = new StringWriter();
			StringBuilder template = new StringBuilder();

			double scale = chordScale;
			if (scale <= 0.0) {
				scale = 0.30;
			}

			template.append("{{velocity}}\n");
			template.append("$xwiki.ssfx.use(\"js/xwiki/lyrics/lyrics.css\")\n");
			template.append("{{/velocity}}\n");
			//template.append("\n{{translation/}}\n");
			template.append("\n{{html clean=\"false\" wiki=\"true\"}}\n");
			// template.append("#if (\"$!subtitle\" == \"\")\n");
			// template.append("#else\n");
			// template.append("  **$subtitle**\n");
			// template.append("<div class=\"lyrics_subtitle\">$subtitle</div><br/>\n");
			// template.append("#end\n");

			template.append("<div class=\"lyrics_artist\">$artist</div>\n");

//			boolean useGoogleTranslate = true;
//			if (useGoogleTranslate) {
//				template.append("<div class=\"noPrint\" style=\"float:right;\" id=\"google_translate_element\"><script type=\"text/javascript\">\n"
//						+ "function googleTranslateElementInit() {\n"
//						+ "new google.translate.TranslateElement({pageLanguage: 'de', layout: google.translate.TranslateElement.InlineLayout.SIMPLE, autoDisplay: false}, 'google_translate_element');\n"
//						+ "}\n"
//						+ "</script>\n<script type=\"text/javascript\" src=\"//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit\"></script></div>");
//			}
			
			template.append("<div class=\"noPrint\" style=\"float: right;\"><form><button title=\"Download OnSong\" type=\"submit\" name=\"onsong\" value=\""
					+ sbOnsong.toString()
					+ "\"><img src=\"../../../resources/icons/silk/page_go.png\" alt=\"OnSong\"></button></form></div>\n");
			template.append("<br/><div class=\"lyrics_score\">$score</div><br/>");

			template.append("#if (\"$!capo\" == \"\")\n");
			template.append("#else\n");
			template.append("<div class=\"lyrics_capo\">Capo: $capo</div><br/>\n");
			template.append("#end\n");

			if (notes.size() > 0) {
				template.append("<div class=\"noPrint\">\n");

				for (int i = 0; i < notes.size(); i++) {
					String note = notes.get(i);
					template.append(" <img src=\"../../../resources/icons/silk/music.png\" alt=\"Notes\"> [[Notes>>attach:"
							+ note + "]]");
					if ((i + 1) < notes.size()) {
						template.append(" ");
					}
				}
				template.append("</div>\n");

			}

			if (melodies.size() > 0) {
				template.append("<div class=\"noPrint\">\n");
				boolean containsVideo = false;
				String video = "";
				for (int i = 0; i < melodies.size(); i++) {
					String melody = melodies.get(i);
					if (melody.startsWith("http")) {
						if (melody.contains(".youtube")) {
							containsVideo = true;
							video = video + "\n\n{{video url=\"" + melody
									+ "\" width=\"200\"/}}\n\n";// +
							template.append(" <img src=\"../../../resources/icons/silk/sound_none.png\" alt=\"Play\">[[Play>>"
									+ melody + "]]");
						} else {
							template.append(" <img src=\"../../../resources/icons/silk/sound_none.png\" alt=\"Play\">[[Play>>"
									+ melody + "]]");
						}
					} else {
						template.append(" <img src=\"../../../resources/icons/silk/sound_none.png\" alt=\"Play\">[[Play>>attach:"
								+ melody + "]]");
					}
					if ((i + 1) < melodies.size()) {
						template.append(" ");
					}

					if (containsVideo) {
						template.append("" + video + "\n");
					}
				}
				template.append("</div>\n");
			}

			if (ShowFretEntity.VALUE) {
				if (frets) {
					template.append("<BR/>\n");
					for (Chord c : drawableChords) {
						template.append("" + c.getDiagram(scale) + "\n");
					}
					if (drawableCapoChords.size() > 0) {
						template.append("<BR/>\n");
					}
					for (Chord c : drawableCapoChords) {
						template.append("" + c.getDiagram(scale) + "\n");
					}
				}
			}

			template.append("<BR/>{{/html}}\n");
			template.append("$content\n");

			String ss = "";
			if (source != null) {
				if (!source.equals("")) {
					ss = ", " + source;
				}
			}

			if (ccli != null) {
				if (ccli.intValue() != 0) {
					String ccliString = "\n{{html}}\n<script>\n"
							+ "if (navigator.userAgent.match(/Android/i)\n"
							+ "|| navigator.userAgent.match(/webOS/i)\n"
							+ "|| navigator.userAgent.match(/iPhone/i)\n"
							+ "|| navigator.userAgent.match(/iPad/i)\n"
							+ "|| navigator.userAgent.match(/iPod/i)\n"
							+ "|| navigator.userAgent.match(/BlackBerry/i)\n"
							+ "|| navigator.userAgent.match(/Windows Phone/i)){\n"
							+ " document.write(\"<sub>"
							+ copyright
							+ ss
							+ ", CCLI: <a href='http://mobile.songselect.com/songs/"
							+ ccli.longValue()
							+ "/'>"
							+ ccli.longValue()
							+ "</a></sub>\");\n"
							+ "} else {\n"
							+ " document.write(\"<sub>"
							+ copyright
							+ ss
							+ ", CCLI: <a href='http://de.songselect.com/songs/"
							+ ccli.longValue() + "/'>" + ccli.longValue()
							+ "</a></sub>\");\n" + "}\n"
							+ "</script>\n\n{{/html}}\n\n";
					template.append(ccliString);
				} else {
					String s = "\n\n\n{{html}}\n\n" + "<sub>" + copyright + ss
							+ "</sub>" + "\n{{/html}}\n\n";
					template.append(s);
				}
			} else {
				String s = "\n\n\n{{html}}\n\n" + "<sub>" + copyright + ss
						+ "</sub>" + "\n{{/html}}\n\n";
				template.append(s);
			}

			try {
				Velocity.evaluate(context, writer, "log", template.toString());

				template = new StringBuilder();
				template.append("{{velocity}}\n");
				template.append("#if($request.getParameter(\"onsong\"))\n");
				template.append("#set($content=$request.getParameter(\"onsong\"))\n");
				template.append("$response.setContentType(\"text/plain\");\n");
				template.append("$response.setCharacterEncoding('UTF-8')\n");
				template.append("$response.setHeader(\"Content-Disposition\", \"attachment; filename="
						+ simpleTokenReplace(title) + ".txt\");\n");
				template.append("$response.writer.print($content.replace(\"/Newline/\", \"\n");
				template.append("\"))\n");
				template.append("$context.setFinished(true);\n");
				template.append("#end\n");
				template.append("{{/velocity}}\n");

				result = template.toString() + writer.toString();

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

	private String simpleTokenReplace(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == 'ß') {
				sb.append("ss");
			} else if (c == 'ü') {
				sb.append("ue");
			} else if (c == 'ö') {
				sb.append("oe");
			} else if (c == 'Ä') {
				sb.append("Ae");
			} else if (c == 'Ö') {
				sb.append("Oe");
			} else if (c == 'Ü') {
				sb.append("Ue");
			} else if (c == 'ä') {
				sb.append("ae");
			} else if (c == ' ') {
				sb.append("_");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
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
			String majorScale = " "
					+ getScale(Constants.notesInDetail[i], true) + " ";

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

			String minorScale = " "
					+ getScale(Constants.notesInDetail[i], false) + " ";

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
			ChordFamily chordInfo = ChordFamilys
					.getChordFamily(chord.getName());
			if (chordInfo != null) {
				StringTokenizer st = new StringTokenizer(chordInfo
						.getFirstChord().getNotes(), " ");
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
		// chordScale = 0.5;
		load(song);
	}

	private void load(String song) {
		LineNumberReader lineNumberReader = new LineNumberReader(
				new StringReader(song));

		boolean isChorus = false;

		List<Content> chorusContent = new ArrayList<Content>();

		try {
			String line = lineNumberReader.readLine();
			do {
				try {
					if (0 != line.length()) { // skip empty lines

						ArrayList<Sequence> chordsInLine = new ArrayList<Sequence>();

						StringTokenizer lineToken = new StringTokenizer(line,
								"{:}\n");
						String token = lineToken.nextToken().toLowerCase();

						if ((token.equals("art")) || (token.equals("artist"))) {
							artist = tokenizeContent(lineToken);
						} else if ((token.equals("copy"))
								|| (token.equals("copyright"))) {
							copyright = tokenizeContent(lineToken);
						} else if (token.equals("bible")) {
							bibleVerses.add(tokenizeContent(lineToken));
						} else if (token.equals("ccli")) {
							try {
								ccli = new Long(tokenizeContent(lineToken));
							} catch (NumberFormatException ex) {
							}
						} else if ((token.equals("st")) || (token.equals("su"))
								|| (token.startsWith("subti"))) {
							subtitle = tokenizeContent(lineToken);
						} else if ((token.equals("url"))
								|| (token.startsWith("source"))) {
							source = tokenizeContent(lineToken);
						} else if ((token.equals("guitarpro"))
								|| (token.startsWith("gp6"))) {
							guitarPro = tokenizeContent(lineToken);
						} else if ((token.equals("notes"))) {
							String note = tokenizeContent(lineToken);
							notes.add(note);
						} else if ((token.equals("scale"))) {
							chordScale = Double
									.parseDouble(tokenizeContent(lineToken));
							System.out.println("Load: " + chordScale);
						} else if (token.startsWith("capo")
								|| token.startsWith("kapo")) {
							capo = new Integer(tokenizeContent(lineToken))
									.intValue();
						} else if (token.startsWith("data_number")
								|| token.startsWith("number")) {
							number = tokenizeContent(lineToken);
						} else if ((token.equals("melody"))
								|| (token.startsWith("mp3"))
								|| token.startsWith("mp4")) {
							String melodyFile = tokenizeContent(lineToken);
							melodies.add(melodyFile);
						} else if ((token.equals("paragraph"))) {
							ParagraphContent paragraph = new ParagraphContent();
							paragraph.setContent(tokenizeContent(lineToken));
							paragraph.setSong(this);
							content.add(paragraph);
						} else if ((token.equals("c"))
								|| (token.startsWith("commen"))) {
							String c = tokenizeContent(lineToken);
							CommentContent comment = new CommentContent();
							comment.setContent(c);
							comment.setSong(this);
							content.add(comment);
						} else if ((token.equals("t"))
								|| (token.startsWith("title"))) {
							title = tokenizeContent(lineToken);
						} else if ((token.equals("newpage"))
								|| (token.equals("np"))
								|| (token.startsWith("newpag") || (token
										.startsWith("new_pag")))) {
							content.add(new NewpageContent());
						} else if ((token.equals("soc"))
								|| (token.startsWith("start_of_cho"))) {
							isChorus = true;
						} else if ((token.equals("eoc"))
								|| (token.startsWith("end_of_cho"))) {
							content.add(new ChorusContent(chorusContent));
							isChorus = false;
							chorusContent = new ArrayList<Content>();
						} else if ((token.equals("breakline"))
								|| (token.equals("colb"))
								|| (token.startsWith("column_break"))) {
							content.add(new BreaklineContent());
						} else { // Song Line

							line = "      " + line + "      ";
							StringCharacterIterator stringCharacterIterator = new StringCharacterIterator(
									line);

							char charAtIndex = stringCharacterIterator.first();
							char charOnePosBeforeIndex = charAtIndex;
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

									String preText = line.substring(0,
											indexChar);
									if (!(preText.contains("[") || preText
											.contains("]"))) {
										Sequence c = new Sequence("");
										c.setVerse(preText);

										if (charAtIndex == '['
												&& charOnePosBeforeIndex != ' ') {
											c.setNoGap(true);
										}

										chordsInLine.add(c);
									}
								}

								if (charAtIndex == ']') {
									int indexEndChord = indexChar;
									String chord = line.substring(
											indexStartChord, indexEndChord);
									lastChord = chord;
									indexStartWord = indexChar + 1;
									wordFound = true;
									lastChordHolder = new Sequence(lastChord);

									chordsInLine.add(lastChordHolder);

									if (!containsChord(lastChordHolder
											.getName())) {
										chords.add(lastChordHolder);
									}
									overallChords.add(lastChordHolder);

								}

								if (indexStartWord < indexChar
										&& indexStartWord != -1
										&& wordFound == true
										&& (charAtIndex == '[' || (indexChar + 1 == line
												.length()))) {
									int indexEndWord = indexChar;
									if (indexChar + 1 == line.length()) {
										indexEndWord += 1;
									}
									String word = line.substring(
											indexStartWord, indexEndWord);
									wordFound = false;
									indexStartWord = -1;
									lastWord = word;
									lastChordHolder.setVerse(lastWord);

									if (charAtIndex == '['
											&& charOnePosBeforeIndex != ' ') {
										lastChordHolder.setNoGap(true);
									}

									lastChord = "";
									lastWord = "";
								}

								charOnePosBeforeIndex = charAtIndex;
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

							} else if (!line.trim().equals("")
									&& !(line.contains("{") || line
											.contains("}"))) {
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
					chordInSequence.setName(chordInSequence.getName() + ":"
							+ capo);
				}
			}

			ChordFamily chord = ChordFamilys.getChordFamily(chordInSequence
					.getName());
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
			if (fretPos <= 1) {
				chord = chordFamily.getFirstChord();

				// Changed!
				if (this.capo != 0) {
					Chord capoChord = ChordFamilys.getChordForCapoPattern(
							chord, capo);
					if (capoChord == null) {
						chord = chordFamily.getChordOnFret(fretPos);
						if (chord == null) {
							chord = chordFamily.getFirstChord();
						}
					}
				}

			} else {
				chord = chordFamily.getChordOnFret(fretPos);
				if (chord == null) {
					chord = chordFamily.getFirstChord();
					if (showMissingChordsOnSpecificFret) {
						System.out.println("Kein Akkord "
								+ chordFamily.getFamilyName() + " auf Bund "
								+ fretPos + " (Capo: " + capo
								+ ") gefunden in Lied " + title + "!");
					}
				}
			}
			drawableChords.add(chord);

			if (this.capo != 0) {
				Chord capoChord = ChordFamilys.getChordForCapoPattern(chord,
						capo);
				if (capoChord != null) {
					drawableCapoChords.add(capoChord);
				} else {
					drawableCapoChords
							.add(new Chord(chordName + ":" + fretPos));
				}
			}

		} else {
			drawableChords.add(new Chord(chordName + ":" + fretPos));
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
