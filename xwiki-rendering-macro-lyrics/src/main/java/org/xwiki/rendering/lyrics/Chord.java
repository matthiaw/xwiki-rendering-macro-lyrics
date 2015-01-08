package org.xwiki.rendering.lyrics;

import java.util.StringTokenizer;

import org.jgroups.util.UUID;

/**
 * Chord Class for holding relevant informations and creating chord diagrams
 * 
 * @author Matthias Wegner
 *
 */
public class Chord implements Comparable {

	public String toString() {
		String n = "";

		if (fretBase < 10) {
			n = "0";
		}

		return n + this.fretBase + " " + this.getFamily().getCalculatedSymbol();
	}

	/**
	 * Return true if one finger is marked as capo
	 * 
	 * @return boolean value if capo is needed in chord
	 */
	public boolean containsCapoAsFinger() {
		if (this.finger1.equals("C")) {
			return true;
		}
		if (this.finger2.equals("C")) {
			return true;
		}
		if (this.finger3.equals("C")) {
			return true;
		}
		if (this.finger4.equals("C")) {
			return true;
		}
		if (this.finger5.equals("C")) {
			return true;
		}
		if (this.finger6.equals("C")) {
			return true;
		}
		return false;
	}

	/**
	 * Position of Fret-Base
	 */
	private int fretBase;

	/**
	 * Fret-Position of finger on String e1
	 */
	private String fret1_e1;

	/**
	 * Fret-Position of finger on String h
	 */
	private String fret2_h;

	/**
	 * Fret-Position of finger on String g
	 */
	private String fret3_g;

	/**
	 * Fret-Position of finger on String d
	 */
	private String fret4_d;

	/**
	 * Fret-Position of finger on String A
	 */
	private String fret5_A;

	/**
	 * Fret-Position of finger on String E
	 */
	private String fret6_E;

	private String finger1;

	private String finger2;

	private String finger3;

	private String finger4;

	private String finger5;

	private String finger6;

	/**
	 * String of ChordPro-Format of Chord
	 * 
	 * @return Chord-Representation as String in ChordPro-Format
	 */
	public String toChordProString() {
		return "{define: " + family.getFamilyName() + " base-fret " + fretBase
				+ " frets " + getFretPositions() + " fingers " + finger6 + " "
				+ finger5 + " " + finger4 + " " + finger3 + " " + finger2 + " "
				+ finger1 + "}";
	}

	public String getBassNote() {

		String note = this.getNoteOnStringE();
		if (!note.equals("x")) {
			return note;
		}
		note = this.getNoteOnStringA();
		if (!note.equals("x")) {
			return note;
		}
		note = this.getNoteOnString_d();
		if (!note.equals("x")) {
			return note;
		}
		note = this.getNoteOnString_g();
		if (!note.equals("x")) {
			return note;
		}
		note = this.getNoteOnString_h();
		if (!note.equals("x")) {
			return note;
		}
		note = this.getNoteOnString_e1();
		if (!note.equals("x")) {
			return note;
		}

		return "";
	}

	public String getNoteOnString(String stringName) {
		if (stringName.equals("E")) {
			return this.getNoteOnStringE();
		}
		if (stringName.equals("A")) {
			return this.getNoteOnStringA();
		}
		if (stringName.equals("d")) {
			return this.getNoteOnString_d();
		}
		if (stringName.equals("g")) {
			return this.getNoteOnString_g();
		}
		if (stringName.equals("h")) {
			return this.getNoteOnString_h();
		}
		if (stringName.equals("e1")) {
			return this.getNoteOnString_e1();
		}
		return "";
	}

	public String getNoteOnStringE() {
		int pos = getFretPosition6_E();
		if (pos != -1) {
			return Constants.notes_E[pos];
		}
		return "x";
	}

	public String getNoteOnStringA() {
		int pos = getFretPosition5_A();
		if (pos != -1) {
			return Constants.notes_A[pos];
		}
		return "x";
	}

	public String getNoteOnString_d() {
		int pos = getFretPosition4_d();
		if (pos != -1) {
			return Constants.notes_d[pos];
		}
		return "x";
	}

	public String getNoteOnString_g() {
		int pos = getFretPosition3_g();
		if (pos != -1) {
			return Constants.notes_g[pos];
		}
		return "x";
	}

	public String getNoteOnString_h() {
		int pos = getFretPosition2_h();
		if (pos != -1) {
			return Constants.notes_h[pos];
		}
		return "x";
	}

	public String getNoteOnString_e1() {
		int pos = getFretPosition1_e1();
		if (pos != -1) {
			return Constants.notes_e1[pos];
		}
		return "x";
	}

	public String getNotesTransposed(int halfnotesteps) {
		try {
			StringBuilder sb = new StringBuilder();
			int pos = getFretPosition6_E();
			if (pos != -1) {
				sb.append(Constants.notes_E[pos + halfnotesteps] + " ");
			}
			pos = getFretPosition5_A();
			if (pos != -1) {
				sb.append(Constants.notes_A[pos + halfnotesteps] + " ");
			}
			pos = getFretPosition4_d();
			if (pos != -1) {
				sb.append(Constants.notes_d[pos + halfnotesteps] + " ");
			}
			pos = getFretPosition3_g();
			if (pos != -1) {
				sb.append(Constants.notes_g[pos + halfnotesteps] + " ");
			}
			pos = getFretPosition2_h();
			if (pos != -1) {
				sb.append(Constants.notes_h[pos + halfnotesteps] + " ");
			}
			pos = getFretPosition1_e1();
			if (pos != -1) {
				sb.append(Constants.notes_e1[pos + halfnotesteps] + " ");
			}
			return " " + sb.toString().trim() + " ";
		} catch (ArrayIndexOutOfBoundsException e) {
			return "Error";
		}
	}

	public String getNotes() {
		StringBuilder sb = new StringBuilder();
		int pos = getFretPosition6_E();
		if (pos != -1) {
			sb.append(Constants.notes_E[pos] + " ");
		}
		pos = getFretPosition5_A();
		if (pos != -1) {
			sb.append(Constants.notes_A[pos] + " ");
		}
		pos = getFretPosition4_d();
		if (pos != -1) {
			sb.append(Constants.notes_d[pos] + " ");
		}
		pos = getFretPosition3_g();
		if (pos != -1) {
			sb.append(Constants.notes_g[pos] + " ");
		}
		pos = getFretPosition2_h();
		if (pos != -1) {
			sb.append(Constants.notes_h[pos] + " ");
		}
		pos = getFretPosition1_e1();
		if (pos != -1) {
			sb.append(Constants.notes_e1[pos] + " ");
		}
		return " " + sb.toString().trim() + " ";
	}

	public int getFretPositionBase() {
		return new Integer(fretBase).intValue();
	}

	public int getFretMin() {
		int min = 30;

		min = Math.min(min, this.getFretPosition1_e1());
		min = Math.min(min, this.getFretPosition2_h());
		min = Math.min(min, this.getFretPosition3_g());
		min = Math.min(min, this.getFretPosition4_d());
		min = Math.min(min, this.getFretPosition5_A());
		min = Math.min(min, this.getFretPosition6_E());

		return min;
	}

	public int getFretPosition6_E() {
		if (!fret6_E.toLowerCase().equals("x")) {
			return new Integer(fret6_E).intValue();
		}
		return -1;
	}

	public int getFretPosition5_A() {
		if (!fret5_A.toLowerCase().equals("x")) {
			return new Integer(fret5_A).intValue();
		}
		return -1;
	}

	public int getFretPosition4_d() {
		if (!fret4_d.toLowerCase().equals("x")) {
			return new Integer(fret4_d).intValue();
		}
		return -1;
	}

	public int getFretPosition3_g() {
		if (!fret3_g.toLowerCase().equals("x")) {
			return new Integer(fret3_g).intValue();
		}
		return -1;
	}

	public int getFretPosition2_h() {
		if (!fret2_h.toLowerCase().equals("x")) {
			return new Integer(fret2_h).intValue();
		}
		return -1;
	}

	public int getFretPosition1_e1() {
		if (!fret1_e1.toLowerCase().equals("x")) {
			return new Integer(fret1_e1).intValue();
		}
		return -1;
	}

	public String getFretPositions() {
		return fret6_E + " " + fret5_A + " " + fret4_d + " " + fret3_g + " "
				+ fret2_h + " " + fret1_e1;
	}

	/**
	 * Writable Name of Chord
	 */
	private String writableName = "";

	/**
	 * ChordFamily of Chord
	 */
	private ChordFamily family;

	/**
	 * Set writable Name of Chord, which could be used as entity representation
	 * for writing to a file
	 * 
	 * @param name
	 *            Name of chord
	 */
	public void setWritableName(String name) {
		writableName = name;
	}

	/**
	 * Draw line as String to a HTML-2D-Canvas-Context named ctx
	 * 
	 * @param x1
	 *            Start at x-Position of Line
	 * @param y1
	 *            Start at y-Position of Line
	 * @param x2
	 *            End at x-Position of Line
	 * @param y2
	 *            End at y-Position of Line
	 * @return String of Line Draw Code for HTML-Canvas
	 */
	private String drawLine(double x1, double y1, double x2, double y2) {
		return "  ctx.beginPath();\n  ctx.moveTo(" + x1 + "," + y1
				+ ");\n  ctx.lineTo(" + x2 + "," + y2 + ");\n  ctx.stroke();\n";
	}

	/**
	 * Draw circle as String to a HTML-2D-Canvas-Context named ctx
	 * 
	 * @param x
	 *            x-Position of Circle
	 * @param y
	 *            y-Position of Circle
	 * @param r
	 *            Radius of Circle
	 * @param fillStyle
	 *            Fill-Style of Circle
	 * @param strokeStyle
	 *            Stroke-Style of Circle
	 * @return String of HTML5-Code to draw a circle
	 */
	private String drawCircle(double x, double y, double r, String fillStyle,
			String strokeStyle) {
		StringBuilder result = new StringBuilder();

		result.append("  ctx.beginPath();\n");
		result.append("  ctx.arc(" + x + ", " + y + ", " + r
				+ ", 0, 2 * Math.PI, false);\n");

		if (fillStyle != null) {
			result.append("  ctx.fillStyle = '" + fillStyle + "';\n");
			result.append("  ctx.fill();\n");
		} else {
			result.append("  ctx.stroke();\n");
		}

		if (strokeStyle != null) {
			result.append("  ctx.strokeStyle = '" + strokeStyle + "';\n");
			result.append("  ctx.stroke();\n");
		}

		return result.toString();
	}

	/**
	 * Absolute width of fret-diagram in pixel
	 */
	private final int width = 200;
	/**
	 * Absolut height in pixel of fret-diagram
	 */
	private final int height = 280;
	/**
	 * Absolut width of left-border in pixel
	 */
	private final int left_border = 50;
	/**
	 * Absolut width of right-border in pixel
	 */
	private final int right_border = 20;
	/**
	 * Absolute hight of upper border in pixel
	 */
	private final int upper_border = 70;
	/**
	 * Absolute height of lower border in pixel
	 */
	private final int lower_border = 20;
	/**
	 * Absolute diameter of circle in fret-diagram for finger position in pixel
	 */
	private final int fingerSize = 12;

	/**
	 * Size of Fret Diagramm as float-value (100% = 1.0; 50%=0.5)
	 */
	private final float SIZE = 1;

	/**
	 * String of HTML5-Canvas-Representation of the fret-diagram in default size
	 * 
	 * @return HTML5-Cancas-Code as String
	 */
	public String getDiagram() {
		return getDiagram(SIZE);
	}

	/**
	 * String of HTML5-Canvas-Representation of the fret-diagram in scaled size
	 * (1.0=100% of default absolut size)
	 * 
	 * @param scale
	 *            Zoom-Factor of fret
	 * @return HTML5-Cancas-Code as String
	 */
	public String getDiagram(double scale) {

		StringBuilder result = new StringBuilder();

		int fretSpace = (height - lower_border - upper_border) / 5;
		int stringSpace = (width - right_border - left_border) / 5;

		/**
		 * Canvases must be differentiated if lyrics are agreggated in one page,
		 * so an uuid is used.
		 */
		UUID uuid = UUID.randomUUID();

		result.append("<canvas id=\"" + getNameWritable() + "_"
				+ uuid.toString() + "\" width=\"" + width * scale
				+ "\" height=\"" + height * scale + "\"></canvas>\n");
		result.append("<script>\n");
		result.append("  var c=document.getElementById('" + getNameWritable()
				+ "_" + uuid.toString() + "');\n");
		result.append("  var ctx=c.getContext('2d');\n");

		if (this.fretBase == -1) {
			result.append("  ctx.strokeStyle='#ff0000';\n");
			result.append("  ctx.fillStyle='#ff0000';\n");
		} else {
			result.append("  ctx.strokeStyle='#000000';\n");
			result.append("  ctx.fillStyle='#000000';\n");
		}

		result.append("  ctx.scale(" + scale + "," + scale + ");\n");

		// draw Strings
		result.append("  ctx.lineWidth = 1;\n");
		for (int i = 0; i < 6; i++) {
			double stringXpos = left_border + (i * stringSpace);
			double stringTopPos = upper_border;
			double stringLowPos = height - lower_border;
			result.append(drawLine(stringXpos, stringTopPos, stringXpos,
					stringLowPos));
		}

		// draw Frets
		for (int i = 0; i < 6; i++) {
			double fretPos = upper_border + (i * fretSpace);
			result.append(drawLine(left_border, fretPos,
					(width - right_border), fretPos));
		}

		// draw Bund
		result.append("  ctx.lineWidth = 3;\n");
		if (this.getFretPositionBase() <= 1) {
			result.append(drawLine(left_border, upper_border,
					(width - right_border), upper_border));
		}

		result.append("  ctx.font = 'bold 26pt Arial';\n");
		result.append("  ctx.fillText('" + family.getFamilyName() + "', 10, "
				+ (upper_border / 2 - 5) + ");\n");

		// If chord is a missing chord
		if (this.fretBase == -1) {
			result.append("</script>\n");
		} else {
			result.append("  ctx.lineWidth = 1;\n");

			result.append(draw(0, Constants.notes_E));
			result.append(draw(1, Constants.notes_A));
			result.append(draw(2, Constants.notes_d));
			result.append(draw(3, Constants.notes_g));
			result.append(draw(4, Constants.notes_h));
			result.append(draw(5, Constants.notes_e1));

			result.append("  ctx.font = 'bold 18pt Arial';\n");
			if (getFretPositionBase() != 0) {
				result.append("  var textWidth = ctx.measureText('"
						+ getFretPositionBase() + "').width;\n");
				result.append("  ctx.fillText('" + getFretPositionBase()
						+ "', " + left_border / 2 + "-textWidth, "
						+ (upper_border + fretSpace / 2 + 9) + ");\n");
			}
		}

		result.append("</script>\n");

		return result.toString();
	}

	/**
	 * Helper method to draw String at given position
	 * 
	 * @param stringPos
	 *            Position of String
	 * @param string
	 *            Array of notes of String
	 * @return String as HTML5-Code for Guitar-String
	 */
	private String draw(int stringPos, String[] string) {

		StringBuilder result = new StringBuilder();

		int fretPos = 0;
		int fretPosString = -1;
		int fretSpace = (height - lower_border - upper_border) / 5;
		int stringSpace = (width - right_border - left_border) / 5;

		String finger = "-";
		if (stringPos == 0) {
			fretPos = 0;
			fretPosString = this.getFretPosition6_E();
			finger = finger6;
		}
		if (stringPos == 1) {
			fretPosString = this.getFretPosition5_A();
			finger = finger5;
		}
		if (stringPos == 2) {
			fretPosString = this.getFretPosition4_d();
			finger = finger4;
		}
		if (stringPos == 3) {
			fretPosString = this.getFretPosition3_g();
			finger = finger3;
		}
		if (stringPos == 4) {
			fretPosString = this.getFretPosition2_h();
			finger = finger2;
		}
		if (stringPos == 5) {

			fretPosString = this.getFretPosition1_e1();
			finger = finger1;
		}

		double circlePosX = left_border + stringPos * stringSpace;

		if (fretPosString == -1) {
			// draw unplayed string
			result.append("  ctx.lineWidth = 1;\n");
			double circlePosY = upper_border + fingerSize - fretSpace;
			result.append(this.drawLine(circlePosX - fingerSize, circlePosY
					- fingerSize / 2, circlePosX + fingerSize, circlePosY
					+ fingerSize + fingerSize / 2));
			result.append(this.drawLine(circlePosX - fingerSize, circlePosY
					+ fingerSize + fingerSize / 2, circlePosX + fingerSize,
					circlePosY - fingerSize / 2));
		}

		if (fretPosString != -1) {
			int textHeight = 12;
			result.append(" ctx.font = 'bold " + textHeight + "pt Arial';\n");
			String note = string[fretPosString];
			result.append("  var textWidth = ctx.measureText('" + note
					+ "').width;\n");
			result.append("  ctx.fillText('"
					+ note
					+ "', "
					+ circlePosX
					+ "-textWidth/2, "
					+ (upper_border + fretPos * fretSpace - fretSpace / 2 + textHeight / 2)
					+ ");\n");

			int base = 1;
			if (this.getFretPositionBase() <= 1) {
				base = 1;
			} else {
				base = this.getFretPositionBase();
			}
			fretPos = fretPosString - base;
			if (fretPos < 0) {
				fretPos = 0;
			}

			if (finger.equals("C")) {
				// draw capo
				result.append("  ctx.lineWidth = 12;\n");
				double capoY = upper_border + fretPos * fretSpace - fingerSize
						/ 2 + fretSpace / 2 * 3 / 2;
				result.append(drawLine(left_border, capoY,
						(width - right_border), capoY));
				result.append("  ctx.lineWidth = 1;\n");
			} else {
				if (fretPos == 0) {
					if (this.getFretPositionBase() > 1) {
						fretPos += 1;
						result.append(this.drawCircle(circlePosX, upper_border
								+ fretPos * fretSpace - fretSpace / 2,
								fingerSize, "black", null));
					} else {
						if (fretPosString == 1) {
							fretPos += 1;
							result.append(this.drawCircle(circlePosX,
									upper_border + fretPos * fretSpace
											- fretSpace / 2, fingerSize,
									"black", null));
						} else {
							result.append(this.drawCircle(circlePosX,
									upper_border + fretPos * fretSpace
											- fretSpace / 2, fingerSize, null,
									null));
						}
					}
				} else {
					fretPos += 1;
					result.append(this.drawCircle(circlePosX, upper_border
							+ fretPos * fretSpace - fretSpace / 2, fingerSize,
							"black", null));
				}

				if (finger != "-") {
					result.append("  ctx.fillStyle='#ffffff';\n");
					result.append("  ctx.font = 'bold " + textHeight
							+ "pt Arial';\n");
					result.append("  var textWidth = ctx.measureText('"
							+ finger + "').width;\n");
					result.append("  ctx.fillText('"
							+ finger
							+ "', "
							+ (left_border + stringPos * stringSpace)
							+ "-textWidth/2, "
							+ (upper_border + fretPos * fretSpace - fretSpace
									/ 2 + textHeight / 2) + ");\n");
					result.append("  ctx.fillStyle='#000000';\n");
				}
			}
		}

		return result.toString();
	}

	public String getNameWritable() {

		if (this.fretBase == -1) {
			return "Missed_" + writableName;
		}

		return writableName;
	}

	/**
	 * Constructor of Chord by String of ChordPro-Definition, ie. {define: A
	 * base-fret 1 frets 0 0 2 2 2 0 fingers - - 4 3 2 -}
	 * 
	 * @param chordproDefinition
	 *            Definition String
	 */
	public Chord(String chordproDefinition) {
		if (!chordproDefinition.contains("{")) {
			fretBase = -1;
			ChordFamily cf = new ChordFamily(chordproDefinition);
			cf.add(this);
			return;
		}

		StringTokenizer tokenizer = new StringTokenizer(chordproDefinition
				.replace("{define: ", "").replace("}", ""), " ");

		String skipName = "";
		while (!skipName.equals("base-fret")) {
			skipName = tokenizer.nextToken();
		}

		fretBase = new Integer(tokenizer.nextToken()).intValue();
		tokenizer.nextToken();
		fret6_E = tokenizer.nextToken();
		fret5_A = tokenizer.nextToken();
		fret4_d = tokenizer.nextToken();
		fret3_g = tokenizer.nextToken();
		fret2_h = tokenizer.nextToken();
		fret1_e1 = tokenizer.nextToken();

		tokenizer.nextToken();
		finger6 = tokenizer.nextToken();
		finger5 = tokenizer.nextToken();
		finger4 = tokenizer.nextToken();
		finger3 = tokenizer.nextToken();
		finger2 = tokenizer.nextToken();
		finger1 = tokenizer.nextToken();
	}

	/**
	 * Returns ChordFamily of Chord
	 * 
	 * @return Family of Chord
	 */
	public ChordFamily getFamily() {
		return family;
	}

	/**
	 * Set ChordFamily of Chord
	 * 
	 * @param family
	 *            ChordFamily
	 */
	public void setFamily(ChordFamily family) {
		this.family = family;
	}

	/**
	 * Comparator for Chord
	 */
	@Override
	public int compareTo(Object o) {

		if (o instanceof Chord) {
			Chord chord = (Chord) o;
			return this.toString().compareTo(chord.toString());
		}

		return 0;
	}

}
