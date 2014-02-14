package org.xwiki.rendering.lyrics;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.wiki.WikiComponent;
import org.xwiki.component.wiki.WikiComponentBuilder;
import org.xwiki.component.wiki.WikiComponentException;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.query.Query;
import org.xwiki.query.QueryManager;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.user.api.XWikiRightService;

@Component
@Singleton
@Named(Lyrics.CLASS)
public class LyricsClassBuilder implements WikiComponentBuilder {

	@Inject
	@Named("context")
	private Provider<ComponentManager> componentManagerProvider;

	@Inject
	private Execution execution;

	@Inject
	private QueryManager queryManager;

	@Inject
	private EntityReferenceSerializer<String> serializer;

	@Override
	public List<DocumentReference> getDocumentReferences() {
		List<DocumentReference> references = new ArrayList<DocumentReference>();

		try {
			Query query = queryManager.createQuery("SELECT doc.space, doc.name FROM Document doc, doc.object(" + Constants.SPACE + "." + Lyrics.CLASS + "Class) AS obj", Query.XWQL);
			List<Object[]> results = query.execute();
			for (Object[] result : results) {
				references.add(new DocumentReference(getXWikiContext().getDatabase(), (String) result[0], (String) result[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return references;
	}

	@Override
	public List<WikiComponent> buildComponents(DocumentReference reference) throws WikiComponentException {
		List<WikiComponent> components = new ArrayList<WikiComponent>();
		DocumentReference documentReference = new DocumentReference(getXWikiContext().getDatabase(), Constants.SPACE, Lyrics.CLASS + "Class");

		try {
			XWikiDocument doc = getXWikiContext().getWiki().getDocument(reference, getXWikiContext());

			List<BaseObject> objects = doc.getXObjects(documentReference);

			for (final BaseObject obj : objects) {
				if (obj != null) {
					String roleHint = serializer.serialize(obj.getReference());
					DefaultLyrics n = new DefaultLyrics();
					n.setTitle(obj.getStringValue("title"));
					n.setCcli(obj.getStringValue("ccli"));
					n.setArtist(obj.getStringValue("artist"));
					try {
						n.setCapo(Integer.valueOf(obj.getStringValue("capo")));
					} catch (Exception e) {

					}
					n.setCopyright(obj.getStringValue("copyright"));
					n.setOnSong(obj.getStringValue("onsong"));
					n.setAuthorReference(doc.getAuthorReference());
					n.setRoleHint(roleHint);
					n.setDocumentReference(reference);
					n.setDocument(doc);
					components.add(n);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WikiComponentException(String.format("Failed to build " + Lyrics.CLASS + "Class components from document [%s]", reference.toString()), e);
		}

		return components;
	}

	private XWikiContext getXWikiContext() {
		return (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
	}

}
