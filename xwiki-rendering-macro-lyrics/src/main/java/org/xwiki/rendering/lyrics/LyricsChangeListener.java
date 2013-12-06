package org.xwiki.rendering.lyrics;

import java.util.ArrayList;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.xwiki.bridge.event.DocumentUpdatedEvent;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.MacroBlock;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

@Component
@Named("LyricsEventListener")
public class LyricsChangeListener implements EventListener {

	@Inject
	@Named("context")
	private Provider<ComponentManager> componentManagerProvider;

	@Override
	public List<Event> getEvents() {
		List<Event> events = new ArrayList<Event>();
		events.add(new DocumentUpdatedEvent());
		return events;
	}

	@Override
	public String getName() {
		return "lyrics";
	}

	private BaseObject getLyricsObject(XWikiDocument doc) {
		EntityReference entRef = doc.resolveClassReference(Constants.SPACE + "." + Lyrics.CLASS + "Class");
		if (doc.getXObject(entRef) != null) {
			return doc.getXObject(entRef);
		}
		return null;
	}

	private boolean documentContainsLyricsObject(XWikiDocument doc) {
		EntityReference entRef = doc.resolveClassReference(Constants.SPACE + "." + Lyrics.CLASS + "Class");
		if (doc.getXObject(entRef) != null) {
			return true;
		}
		return false;
	}

	@Override
	public void onEvent(Event event, Object source, Object data) {

		XWikiDocument doc = (XWikiDocument) source;
		XWikiContext context = (XWikiContext) data;

		if ((event instanceof DocumentUpdatedEvent)) {
			List<MacroBlock> macros = new ArrayList<MacroBlock>();
			getLyricsMacros(macros, doc.getXDOM().getRoot());

			if (macros.size() > 0) {
				MacroBlock macro = macros.get(0);
				String content = macro.getContent();

				Song song = new Song(content);

				String title = song.getTitle();
				if (song.getNumber() != null) {
					if (!song.getNumber().equals("")) {
						title = song.getNumber() + ") " + song.getTitle();
					}
				}

				/**
				 * Set title of document if it is not the same as the title in the lyrics macro
				 */
				if (!doc.getTitle().equals(title)) {
					doc.setTitle(title);
					try {
						context.getWiki().saveDocument(doc, context);
					} catch (XWikiException e) {
						e.printStackTrace();
					}
				}

				/**
				 * Create Lyrics Object or update it 
				 */
				if (!documentContainsLyricsObject(doc)) {
					createLyrics(doc, context, song);
				} else {
					updateLyrics(doc, context, song);
				}
			}

		}

	}

	/**
	 * Update Lyrics Object
	 * @param doc
	 * @param context
	 * @param song
	 */
	private void updateLyrics(XWikiDocument doc, XWikiContext context, Song song) {
		BaseObject old = this.getLyricsObject(doc).clone();
		BaseObject obj = this.getLyricsObject(doc);

		String title = song.getTitle();
		if (song.getNumber() != null) {
			if (!song.getNumber().equals("")) {
				title = song.getNumber() + ") " + song.getTitle();
			}
		}

		obj.set("title", title, context);
		obj.set("ccli", song.getCcli() + "", context);
		obj.set("capo", song.getCapo() + "", context);
		obj.set("copyright", song.getCopyright(), context);
		obj.set("artist", song.getArtist(), context);

		if (!old.toXMLString().equals(obj.toXMLString())) {
			try {
				context.getWiki().saveDocument(doc, context);
			} catch (XWikiException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create Lyrics Object
	 * @param doc
	 * @param context
	 * @param song
	 */
	private void createLyrics(XWikiDocument doc, XWikiContext context, Song song) {
		EntityReference entRef = doc.resolveClassReference(Constants.SPACE + "." + Lyrics.CLASS + "Class");
		try {
			int objectIndex = doc.createXObject(entRef, context);
			BaseObject obj = doc.getXObjects(entRef).get(objectIndex);

			String title = song.getTitle();
			if (song.getNumber() != null) {
				if (!song.getNumber().equals("")) {
					title = song.getNumber() + ") " + song.getTitle();
				}
			}

			obj.set("title", title, context);
			obj.set("ccli", song.getCcli() + "", context);
			obj.set("capo", song.getCapo() + "", context);
			obj.set("copyright", song.getCopyright(), context);
			obj.set("artist", song.getArtist(), context);
			context.getWiki().saveDocument(doc, context);
		} catch (XWikiException e) {
			e.printStackTrace();
		}
	}

	private void getLyricsMacros(List<MacroBlock> list, Block block) {
		if (block instanceof MacroBlock) {
			MacroBlock mb = (MacroBlock) block;
			if (mb.getId().equals("lyrics")) {
				list.add(mb);
			}
		}
		List<Block> children = block.getChildren();
		for (Block cBlock : children) {
			getLyricsMacros(list, cBlock);
		}

	}

}
