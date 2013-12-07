/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.rendering.lyrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.context.Execution;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.match.MacroBlockMatcher;
import org.xwiki.rendering.macro.AbstractMacro;
import org.xwiki.rendering.macro.MacroContentParser;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.macro.descriptor.DefaultContentDescriptor;
import org.xwiki.rendering.transformation.MacroTransformationContext;

//import com.xpn.xwiki.XWikiContext;

@Component
@Named(LyricsIndexMacro.MACRO_NAME)
@Singleton
public class LyricsIndexMacro extends AbstractMacro<LyricsIndexMacroParameters> {
	/** The name of this macro. */
	public static final String MACRO_NAME = "lyricsindex";

	/** The description of the macro. */
	private static final String DESCRIPTION = "Generates index of lyrics";

	/** The description of the macro content. */
	private static final String CONTENT_DESCRIPTION = "";

	@Inject
	private MacroContentParser contentParser;

	@Inject
	@Named("context")
	private Provider<ComponentManager> componentManagerProvider;

	/**
	 * Create and initialize the descriptor of the macro.
	 */
	public LyricsIndexMacro() {
		super("LyricsIndex", DESCRIPTION, new DefaultContentDescriptor(CONTENT_DESCRIPTION), LyricsIndexMacroParameters.class);
		setDefaultCategory(DEFAULT_CATEGORY_CONTENT);
	}

	@Inject
	protected Execution execution;

	@Override
	public boolean supportsInlineMode() {
		return true;
	}

	@Override
	public int getPriority() {
		return 500;
	}

	@Override
	public List<Block> execute(LyricsIndexMacroParameters parameters, String content, MacroTransformationContext context) throws MacroExecutionException {

		StringBuilder buildContent = new StringBuilder();

		String mode = "artist";
		
		if (parameters!=null) {
			if (parameters.getMode()!=null) {
				mode = parameters.getMode();
			}
		}
		
		
		if (mode.equals("artist")) {
			Hashtable<String, ArrayList<Lyrics>> songTable = getLyricsByArtist();
			Vector<String> artists = new Vector<String>(songTable.keySet());
			Collections.sort(artists);
			for (String artist : artists) {
				buildContent.append("== " + artist + " ==\n");
				ArrayList<Lyrics> songs = songTable.get(artist);
				for (Lyrics song : songs) {
					buildContent.append("* [[" + song.getTitle() + ">>" + song.getDocument() + "]]\n");
				}
			}
		}
		
		if (mode.equals("title")) {
			int counter = 0;
			Hashtable<String, Lyrics> songs = new Hashtable<String, Lyrics>();
			ArrayList<String> titles = new ArrayList<String>();
			for (Lyrics song : getLyrics()) {
				
				String title  = song.getTitle();
				
				if (title.contains(")")) {
					title = title.substring(title.indexOf(")")+1, title.length()).trim();
				}
				
//				System.out.println(title+": "+song.getTitle() + " - "+title.substring(0, 1));
				titles.add(title + " " + counter);
				songs.put(title + " " + counter, song);
				counter++;
			}

			Collections.sort(titles);

			String startingLetter = "";

			for (String title : titles) {
				Lyrics song = songs.get(title);
				if (!startingLetter.equals(title.substring(0, 1))) {
					startingLetter = title.substring(0, 1);
//					System.out.println("Start: "+startingLetter);
					buildContent.append("== " + startingLetter + " ==\n");
				}
				
				buildContent.append("* [[" + song.getTitle() + ">>" + song.getDocument() + "]]\n");
			}
			
		}

		List<Block> result = this.contentParser.parse(buildContent.toString(), context, true, context.isInline()).getChildren();

		return result;
	}

	private List<Lyrics> getLyrics() {
		List<Lyrics> list = new ArrayList<Lyrics>();
		try {
			for (Object lyricsObj : componentManagerProvider.get().getInstanceList(Lyrics.class)) {
				Lyrics lyrics = (Lyrics) lyricsObj;
				list.add(lyrics);
			}
		} catch (ComponentLookupException e) {
			e.printStackTrace();
		}
		return list;
	}

	private ArrayList<String> getArtists() {
		List<Lyrics> songs = getLyrics();
		ArrayList<String> artists = new ArrayList<String>();
		for (Lyrics song : songs) {
			StringTokenizer tokens = new StringTokenizer(song.getArtist(), ",");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken().trim();

				if (token.equals("Unknown")) {
					token = "Unbekannt";
				}

				if (!artists.contains(token)) {
					artists.add(token);
				}
			}
		}

		Collections.sort(artists);

		return artists;
	}

	private Hashtable<String, ArrayList<Lyrics>> getLyricsByArtist() {
		Hashtable<String, ArrayList<Lyrics>> table = new Hashtable<String, ArrayList<Lyrics>>();
		List<Lyrics> allSongs = getLyrics();

		for (String artist : getArtists()) {
			ArrayList<Lyrics> songs = new ArrayList<Lyrics>();
			for (Lyrics song : allSongs) {
				if (song.getArtist().contains(artist)) {
					songs.add(song);
				}
				if (artist.equals("Unbekannt") && song.getArtist().contains("Unknown")) {
					songs.add(song);
				}
			}
			// Collections.sort(songs);
			table.put(artist, songs);
		}

		return table;
	}
}
