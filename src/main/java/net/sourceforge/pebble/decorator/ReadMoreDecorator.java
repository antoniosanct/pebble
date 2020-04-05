/*
 * Copyright (c) 2003-2011, Simon Brown
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *   - Neither the name of Pebble nor the names of its contributors may
 *     be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pebble.decorator;

import net.sourceforge.pebble.api.decorator.ContentDecoratorContext;
import net.sourceforge.pebble.domain.Blog;
import net.sourceforge.pebble.domain.BlogEntry;
import net.sourceforge.pebble.util.I18n;

/**
 * Adds a read more link :
 *  - when the entry is aggregated
 *  - when an excerpt is present, in the summary view
 *
 * @author Simon Brown
 */
public class ReadMoreDecorator extends ContentDecoratorSupport {


  /**
	 * 
	 */
	private static final long serialVersionUID = 338234636709866323L;

/**
   * Decorates the specified blog entry.
   *
   * @param context   the context in which the decoration is running
   * @param blogEntry the blog entry to be decorated
   */
  public void decorate(ContentDecoratorContext context, BlogEntry blogEntry) {
    Blog blog = blogEntry.getBlog();

    if ((blogEntry.getExcerpt() != null && blogEntry.getExcerpt().length() > 0 && context.getView() == ContentDecoratorContext.SUMMARY_VIEW)) {
      StringBuffer buf = new StringBuffer();
      buf.append(blogEntry.getExcerpt());

      buf.append("<p class=\"readMore\"><a href=\"");
      buf.append(blogEntry.getPermalink());
      buf.append("\">");
      buf.append(I18n.getMessage(blog, "common.readMore"));
      buf.append("</a></p>");

      blogEntry.setExcerpt(buf.toString());
    } else if (blogEntry.isAggregated()) {
      StringBuffer buf = new StringBuffer();
      buf.append(blogEntry.getBody());

      buf.append("<p class=\"readMore\"><a href=\"");
      buf.append(blogEntry.getPermalink());
      buf.append("\">");
      buf.append(I18n.getMessage(blog, "common.readMore"));
      buf.append("</a></p>");

      blogEntry.setBody(buf.toString());
    }
  }

}