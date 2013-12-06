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

import java.util.Collections;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
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
@Named(LyricsMacro.MACRO_NAME)
@Singleton
public class LyricsMacro extends AbstractMacro<LyricsMacroParameters>
{
    /** The name of this macro. */
    public static final String MACRO_NAME = "lyrics";

    /** The description of the macro. */
    private static final String DESCRIPTION = "Generates lyrics of a song.";

    /** The description of the macro content. */
    private static final String CONTENT_DESCRIPTION = "the text of the song";

    @Inject
	private MacroContentParser contentParser;
    
    /**
     * Create and initialize the descriptor of the macro.
     */
    public LyricsMacro()
    {
        super("Lyrics", DESCRIPTION, new DefaultContentDescriptor(CONTENT_DESCRIPTION),
            LyricsMacroParameters.class);
        setDefaultCategory(DEFAULT_CATEGORY_CONTENT);
    }
    
    @Inject
    protected Execution execution; 

    @Override
    public boolean supportsInlineMode()
    {
        return true;
    }

    @Override
    public int getPriority()
    {
        return 500;
    }

//    private XWikiContext getXWikiContext() {
//    	return (XWikiContext) execution.getContext().getProperty("xwikicontext");
//    }
    
    @Override
    public List<Block> execute(LyricsMacroParameters parameters, String content, MacroTransformationContext context)
        throws MacroExecutionException
    {
    	Song song = new Song(content);
    	
        List<Block> result = this.contentParser.parse(song.parse(), context, true, context.isInline()).getChildren();

//        System.out.println(context.getXDOM());
        
        return result;
    }
}
