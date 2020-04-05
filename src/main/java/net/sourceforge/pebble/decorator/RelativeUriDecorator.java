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

import net.sourceforge.pebble.domain.Attachment;
import net.sourceforge.pebble.domain.BlogEntry;
import net.sourceforge.pebble.domain.StaticPage;
import net.sourceforge.pebble.api.decorator.ContentDecoratorContext;

/**
 * Translates relative URIs in blog entries and static pages into absolute URLs.
 * 
 * @author Simon Brown
 */
public class RelativeUriDecorator extends ContentDecoratorSupport {

  /**
	 * 
	 */
	private static final long serialVersionUID = 4104796844882837650L;

/**
   * Decorates the specified blog entry.
   *
   * @param context   the context in which the decoration is running
   * @param blogEntry the blog entry to be decorated
   */
  public void decorate(ContentDecoratorContext context, BlogEntry blogEntry) {
    blogEntry.setBody(replaceCommonUris(blogEntry.getBody()));
    blogEntry.setExcerpt(replaceCommonUris(blogEntry.getExcerpt()));

    Attachment attachment = blogEntry.getAttachment();
    if (attachment != null) {
      String attachmentUrl = attachment.getUrl();
      if (attachmentUrl.startsWith("./")) {
        attachment.setUrl(getBlog().getUrl() + attachmentUrl.substring(2));
      }
    }
  }

  /**
   * Decorates the specified static page.
   *
   * @param context    the context in which the decoration is running
   * @param staticPage the static page to be decorated
   */
  public void decorate(ContentDecoratorContext context, StaticPage staticPage) {
    staticPage.setBody(replaceCommonUris(staticPage.getBody()));
  }

  /**
   * Helper method to replace common relative URIs with their absolute values.
   *
   * @param s   the String containing relative URIs
   * @return    a new String containing absolute URLs
   */
  private String replaceCommonUris(String s) {
    s = s.replaceAll("href=\"\\./", "href=\"" + getBlog().getUrl());
    s = s.replaceAll("href='\\./", "href='" + getBlog().getUrl());
    s = s.replaceAll("src=\"\\./", "src=\"" + getBlog().getUrl());
    s = s.replaceAll("src='\\./", "src='" + getBlog().getUrl());
    return s;
  }

}