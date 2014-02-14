package org.xwiki.rendering.lyrics;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.sheet.SheetBinder;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.internal.mandatory.AbstractMandatoryDocumentInitializer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.sheet.SheetBinder;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.classes.BooleanClass;

@Component
@Named(Lyrics.CLASS+"Initializer")
@Singleton
public class LyricsClassInitializer extends AbstractMandatoryDocumentInitializer {

	/**
	 * Default list separators of Graph.GraphClass fields.
	 */
	public static final String DEFAULT_FIELDS = "|";
	
	public static final String FIELD_TITLE = "title";
	public static final String FIELDPN_TITLE = "Title";
	
	public static final String FIELD_CCLI = "ccli";
	public static final String FIELDPN_CCLI = "CCLI";
	
	public static final String FIELD_ARTIST = "artist";
	public static final String FIELDPN_ARTIST = "Artist";
	
	public static final String FIELD_COPYRIGHT = "copyright";
	public static final String FIELDPN_COPYRIGHT = "Copyright";
	
	public static final String FIELD_CAPO = "capo";
	public static final String FIELDPN_CAPO = "Capo";
	
	public static final String FIELD_ONSONG = "onsong";
    public static final String FIELDPN_ONSONG = "OnSong";
    
	/**
	 * Used to bind a class to a document sheet.
	 */
	@Inject
	@Named("class")
	private SheetBinder classSheetBinder;

	/**
	 * Used to access current XWikiContext.
	 */
	@Inject
	private Provider<XWikiContext> xcontextProvider;

	/**
	 * Overriding the abstract class' private reference.
	 */
	private DocumentReference reference;

	public LyricsClassInitializer() {
		// Since we can`t get the main wiki here, this is just to be able to use the Abstract class.
		super(Constants.SPACE, Lyrics.CLASS+"Class");
	}

	@Override
	public boolean updateDocument(XWikiDocument document) {
		boolean needsUpdate = false;
		
		// Add missing class fields
		BaseClass baseClass = document.getXClass();
		needsUpdate |= baseClass.addTextField(FIELD_TITLE, FIELDPN_TITLE, 30);
		needsUpdate |= baseClass.addTextField(FIELD_ARTIST, FIELDPN_ARTIST, 30);
		needsUpdate |= baseClass.addTextField(FIELD_CAPO, FIELDPN_CAPO, 30);
		needsUpdate |= baseClass.addTextField(FIELD_CCLI, FIELDPN_CCLI, 30);
		needsUpdate |= baseClass.addTextField(FIELD_COPYRIGHT, FIELDPN_COPYRIGHT, 30);
		needsUpdate |= baseClass.addTextAreaField(FIELD_ONSONG, FIELDPN_ONSONG, 30, 5);
		
		// Add missing document fields
		needsUpdate |= setClassDocumentFields(document, Lyrics.CLASS+"Class");

		// Use Sheet to display documents having Class objects if no other class sheet is specified.
		if (this.classSheetBinder.getSheets(document).isEmpty()) {
			String wikiName = document.getDocumentReference().getWikiReference().getName();
			DocumentReference sheet = new DocumentReference(wikiName, Constants.SPACE, Lyrics.CLASS+"Class"+"Sheet");
			needsUpdate |= this.classSheetBinder.bind(document, sheet);
		}

		return needsUpdate;
	}

	/**
	 * Initialize and return the main wiki's class document reference.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public EntityReference getDocumentReference() {
		if (this.reference == null) {
			synchronized (this) {
				if (this.reference == null) {
					String mainWikiName = xcontextProvider.get().getMainXWiki();
					this.reference = new DocumentReference(mainWikiName, Constants.SPACE, Lyrics.CLASS+"Class");
				}
			}
		}

		return this.reference;
	}

}
