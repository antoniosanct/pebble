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

package net.sourceforge.pebble.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pebble.api.event.blogentry.BlogEntryEvent;
import net.sourceforge.pebble.api.event.blogentry.BlogEntryListener;
import net.sourceforge.pebble.api.event.comment.CommentEvent;
import net.sourceforge.pebble.api.event.comment.CommentListener;
import net.sourceforge.pebble.api.event.trackback.TrackBackEvent;
import net.sourceforge.pebble.api.event.trackback.TrackBackListener;

/**
 * Tests for the BlogService class.
 *
 * @author    Simon Brown
 */
public class BlogServiceTest extends SingleBlogTestCase implements Serializable {

  /**
	 * 
	 */
	private static final long serialVersionUID = 5657577659343984737L;
	
  private BlogService service;
  private BlogEntry blogEntry;

  @BeforeEach protected void setUp() throws Exception {
    super.setUp();

    service = new BlogService();
    blogEntry = new BlogEntry(blog);
    blogEntry.setTitle("A title");
    blogEntry.setBody("Some body");
    blogEntry.setExcerpt("Some excerpt");
    blogEntry.setAuthor("An author");
    blogEntry.setDate(new Date());
  }

  /**
   * Tests that listeners are fired when a comment is added.
   */
  @Test public void testListenersFiredWhenCommentAdded() throws Exception {
    final StringBuffer buf = new StringBuffer("123");
    final Comment comment = blogEntry.createComment("title", "body", "author", "email", "website", "avatar", "127.0.0.1");

    CommentListener listener = new CommentListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void commentAdded(CommentEvent event) {
        assertEquals(comment, event.getSource());
        buf.reverse();
      }

      @Test public void commentRemoved(CommentEvent event) {
        fail();
      }

      @Test public void commentApproved(CommentEvent event) {
        fail();
      }

      @Test public void commentRejected(CommentEvent event) {
        fail();
      }
    };

    blog.getEventListenerList().addCommentListener(listener);
    service.putBlogEntry(blogEntry);
    blogEntry.addComment(comment);
    service.putBlogEntry(blogEntry);
    assertEquals("321", buf.toString());
  }

  /**
   * Tests that listeners are fired when a comment is removed.
   */
  @Test public void testListenersFiredWhenCommentRemoved() throws Exception {
    final StringBuffer buf = new StringBuffer("123");
    final Comment comment = blogEntry.createComment("title", "body", "author", "email", "website", "avatar", "127.0.0.1");
    blogEntry.addComment(comment);
    service.putBlogEntry(blogEntry);

    CommentListener listener = new CommentListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void commentAdded(CommentEvent event) {
        fail();
      }

      @Test public void commentRemoved(CommentEvent event) {
        assertEquals(comment, event.getSource());
        buf.reverse();
      }

      @Test public void commentApproved(CommentEvent event) {
        fail();
      }

      @Test public void commentRejected(CommentEvent event) {
        fail();
      }
    };

    blog.getEventListenerList().addCommentListener(listener);
    blogEntry.removeComment(comment.getId());
    service.putBlogEntry(blogEntry);
    assertEquals("321", buf.toString());
  }

  /**
   * Tests that listeners are fired when a comment is approved.
   */
  @Test public void testListenersFiredWhenCommentApproved() throws Exception {
    final StringBuffer buf = new StringBuffer("123");
    final Comment comment = blogEntry.createComment("title", "body", "author", "email", "website", "avatar", "127.0.0.1");

    blogEntry.addComment(comment);
    comment.setPending();
    service.putBlogEntry(blogEntry);

    CommentListener listener = new CommentListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void commentAdded(CommentEvent event) {
        fail();
      }

      @Test public void commentRemoved(CommentEvent event) {
        fail();
      }

      @Test public void commentApproved(CommentEvent event) {
        assertEquals(comment, event.getSource());
        buf.reverse();
      }

      @Test public void commentRejected(CommentEvent event) {
        fail();
      }
    };

    blog.getEventListenerList().addCommentListener(listener);
    comment.setApproved();
    service.putBlogEntry(blogEntry);
    assertEquals("321", buf.toString());
  }

  /**
   * Tests that listeners are fired when a comment is rejected.
   */
  @Test public void testListenersFiredWhenCommentRejected() throws Exception {
    final StringBuffer buf = new StringBuffer("123");
    final Comment comment = blogEntry.createComment("title", "body", "author", "email", "website", "avatar", "127.0.0.1");
    blogEntry.addComment(comment);
    comment.setPending();
    service.putBlogEntry(blogEntry);

    CommentListener listener = new CommentListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void commentAdded(CommentEvent event) {
      }

      @Test public void commentRemoved(CommentEvent event) {
      }

      @Test public void commentApproved(CommentEvent event) {
      }

      @Test public void commentRejected(CommentEvent event) {
        assertEquals(comment, event.getSource());
        buf.reverse();
      }
    };

    blog.getEventListenerList().addCommentListener(listener);
    comment.setRejected();
    service.putBlogEntry(blogEntry);
    assertEquals("321", buf.toString());
  }

  /**
   * Tests that listeners are fired when a TrackBack is added.
   */
  @Test public void testListenersFiredWhenTrackBackAdded() throws Exception {
    final StringBuffer buf = new StringBuffer("123");
    final TrackBack trackBack = blogEntry.createTrackBack("title", "excerpt", "url", "blogName", "127.0.0.1");

    TrackBackListener listener = new TrackBackListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void trackBackAdded(TrackBackEvent event) {
        assertEquals(trackBack, event.getSource());
        buf.reverse();
      }

      @Test public void trackBackRemoved(TrackBackEvent event) {
        fail();
      }

      @Test public void trackBackApproved(TrackBackEvent event) {
        fail();
      }

      @Test public void trackBackRejected(TrackBackEvent event) {
        fail();
      }
    };

    blog.getEventListenerList().addTrackBackListener(listener);
    service.putBlogEntry(blogEntry);
    blogEntry.addTrackBack(trackBack);
    service.putBlogEntry(blogEntry);
    assertEquals("321", buf.toString());
  }

  /**
   * Tests that listeners are fired when a TrackBack is removed.
   */
  @Test public void testListenersFiredWhenTrackBackRemoved() throws Exception {
    final StringBuffer buf = new StringBuffer("123");
    final TrackBack trackBack = blogEntry.createTrackBack("title", "excerpt", "url", "blogName", "127.0.0.1");
    blogEntry.addTrackBack(trackBack);
    service.putBlogEntry(blogEntry);

    TrackBackListener listener = new TrackBackListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void trackBackAdded(TrackBackEvent event) {
        fail();
      }

      @Test public void trackBackRemoved(TrackBackEvent event) {
        assertEquals(trackBack, event.getSource());
        buf.reverse();
      }

      @Test public void trackBackApproved(TrackBackEvent event) {
        fail();
      }

      @Test public void trackBackRejected(TrackBackEvent event) {
        fail();
      }
    };

    blog.getEventListenerList().addTrackBackListener(listener);
    blogEntry.removeTrackBack(trackBack.getId());
    service.putBlogEntry(blogEntry);
    assertEquals("321", buf.toString());
  }

  /**
   * Tests that listeners are fired when a TrackBack is approved.
   */
  @Test public void testListenersFiredWhenTrackBackApproved() throws Exception {
    final StringBuffer buf = new StringBuffer("123");
    final TrackBack trackBack = blogEntry.createTrackBack("title", "excerpt", "url", "blogName", "127.0.0.1");
    blogEntry.addTrackBack(trackBack);
    trackBack.setPending();
    service.putBlogEntry(blogEntry);

    TrackBackListener listener = new TrackBackListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void trackBackAdded(TrackBackEvent event) {
        fail();
      }

      @Test public void trackBackRemoved(TrackBackEvent event) {
        fail();
      }

      @Test public void trackBackApproved(TrackBackEvent event) {
        assertEquals(trackBack, event.getSource());
        buf.reverse();
      }

      @Test public void trackBackRejected(TrackBackEvent event) {
        fail();
      }
    };

    blog.getEventListenerList().addTrackBackListener(listener);
    trackBack.setApproved();
    service.putBlogEntry(blogEntry);
    assertEquals("321", buf.toString());
  }

  /**
   * Tests that listeners are fired when a TrackBack is rejected.
   */
  @Test public void testListenersFiredWhenTrackBackRejected() throws Exception {
    final StringBuffer buf = new StringBuffer("123");
    final TrackBack trackBack = blogEntry.createTrackBack("title", "excerpt", "url", "blogName", "127.0.0.1");
    blogEntry.addTrackBack(trackBack);
    trackBack.setPending();
    service.putBlogEntry(blogEntry);

    TrackBackListener listener = new TrackBackListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void trackBackAdded(TrackBackEvent event) {
      }

      @Test public void trackBackRemoved(TrackBackEvent event) {
      }

      @Test public void trackBackApproved(TrackBackEvent event) {
      }

      @Test public void trackBackRejected(TrackBackEvent event) {
        assertEquals(trackBack, event.getSource());
        buf.reverse();
      }
    };

    blog.getEventListenerList().addTrackBackListener(listener);
    trackBack.setRejected();
    service.putBlogEntry(blogEntry);
    assertEquals("321", buf.toString());
  }

  /**
   * Tests that listeners are fired when a blog entry is published.
   */
  @Test public void testListenersFiredWhenBlogEntryPublished() throws Exception {
    final StringBuffer buf = new StringBuffer("123");
    blogEntry.setPublished(false);
    service.putBlogEntry(blogEntry);

    BlogEntryListener listener = new BlogEntryListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void blogEntryAdded(BlogEntryEvent event) {
        fail();
      }

      @Test public void blogEntryRemoved(BlogEntryEvent event) {
        fail();
      }

      @Test public void blogEntryChanged(BlogEntryEvent event) {
        fail();
      }

      @Test public void blogEntryPublished(BlogEntryEvent event) {
        assertEquals(blogEntry, event.getSource());
        buf.reverse();
      }

      @Test public void blogEntryUnpublished(BlogEntryEvent event) {
        fail();
      }
    };

    blog.getEventListenerList().addBlogEntryListener(listener);
    blogEntry.setPublished(true);
    service.putBlogEntry(blogEntry);
    assertEquals("321", buf.toString());
  }

  /**
   * Tests that listeners are fired when a blog entry is unpublished.
   */
  @Test public void testListenersFiredWhenBlogEntryUnpublished() throws Exception {
    final StringBuffer buf = new StringBuffer("123");
    blogEntry.setPublished(true);
    service.putBlogEntry(blogEntry);

    BlogEntryListener listener = new BlogEntryListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void blogEntryAdded(BlogEntryEvent event) {
        fail();
      }

      @Test public void blogEntryRemoved(BlogEntryEvent event) {
        fail();
      }

      @Test public void blogEntryChanged(BlogEntryEvent event) {
        fail();
      }

      @Test public void blogEntryPublished(BlogEntryEvent event) {
        fail();
      }

      @Test public void blogEntryUnpublished(BlogEntryEvent event) {
        assertEquals(blogEntry, event.getSource());
        buf.reverse();
      }
    };

    blog.getEventListenerList().addBlogEntryListener(listener);
    blogEntry.setPublished(false);
    service.putBlogEntry(blogEntry);
    assertEquals("321", buf.toString());
  }

  /**
   * Tests that listeners are fired when a blog entry is changed.
   */
  @Test public void testListenersFiredWhenBlogEntryChanged() throws Exception {
    BlogService service = new BlogService();
    service.putBlogEntry(blogEntry);

    final StringBuffer buf = new StringBuffer("123");

    BlogEntryListener listener = new BlogEntryListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void blogEntryAdded(BlogEntryEvent event) {
        fail();
      }

      @Test public void blogEntryRemoved(BlogEntryEvent event) {
        fail();
      }

      @Test public void blogEntryChanged(BlogEntryEvent event) {
        assertEquals(blogEntry, event.getSource());
        assertNotNull(event.getPropertyChangeEvents());
        buf.reverse();
      }

      @Test public void blogEntryPublished(BlogEntryEvent event) {
        fail();
      }

      @Test public void blogEntryUnpublished(BlogEntryEvent event) {
        fail();
      }
    };

    blog.getEventListenerList().addBlogEntryListener(listener);
    blogEntry.setTitle("A new title");
    service.putBlogEntry(blogEntry);
    assertEquals("321", buf.toString());
  }

  /**
   * Tests that comment listeners are fired when a blog entry is removed.
   */
  @Test public void testListenersFiredForCommentsWhenBlogEntryRemoved() throws Exception {
    final Comment comment1 = blogEntry.createComment("title", "body", "author", "email", "website", "avatar", "127.0.0.1");
    final Comment comment2 = blogEntry.createComment("title", "body", "author", "email", "website", "avatar", "127.0.0.1");
    final Comment comment3 = blogEntry.createComment("title", "body", "author", "email", "website", "avatar", "127.0.0.1");

    blogEntry.addComment(comment1);
    blogEntry.addComment(comment2);
    service.putBlogEntry(blogEntry);

    comment3.setParent(comment2);
    blogEntry.addComment(comment3);
    service.putBlogEntry(blogEntry);

    final List comments = new ArrayList();

    CommentListener listener = new CommentListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void commentAdded(CommentEvent event) {
        fail();
      }

      @Test public void commentRemoved(CommentEvent event) {
        comments.add(event.getSource());
      }

      @Test public void commentApproved(CommentEvent event) {
        fail();
      }

      @Test public void commentRejected(CommentEvent event) {
        fail();
      }
    };

    blog.getEventListenerList().addCommentListener(listener);
    service.removeBlogEntry(blogEntry);

    assertEquals(comment1, comments.get(0));
    assertEquals(comment2, comments.get(1));
    assertEquals(comment3, comments.get(2));
  }

  /**
   * Tests that TrackBack listeners are fired when a blog entry is removed.
   */
  @Test public void testListenersFiredForTrackBacksWhenBlogEntryRemoved() throws Exception {
    final TrackBack trackBack1 = blogEntry.createTrackBack("title", "excerpt", "url", "blogName", "127.0.0.1");
    final TrackBack trackBack2 = blogEntry.createTrackBack("title", "excerpt", "url", "blogName", "127.0.0.1");
    final TrackBack trackBack3 = blogEntry.createTrackBack("title", "excerpt", "url", "blogName", "127.0.0.1");

    blogEntry.addTrackBack(trackBack1);
    blogEntry.addTrackBack(trackBack2);
    blogEntry.addTrackBack(trackBack3);
    service.putBlogEntry(blogEntry);

    final List trackBacks = new ArrayList();

    TrackBackListener listener = new TrackBackListener() {
      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Test public void trackBackAdded(TrackBackEvent event) {
        fail();
      }

      @Test public void trackBackRemoved(TrackBackEvent event) {
        trackBacks.add(event.getSource());
      }

      @Test public void trackBackApproved(TrackBackEvent event) {
        fail();
      }

      @Test public void trackBackRejected(TrackBackEvent event) {
        fail();
      }
    };

    blog.getEventListenerList().addTrackBackListener(listener);
    service.removeBlogEntry(blogEntry);

    assertEquals(trackBack1, trackBacks.get(0));
    assertEquals(trackBack2, trackBacks.get(1));
    assertEquals(trackBack3, trackBacks.get(2));
  }
}
