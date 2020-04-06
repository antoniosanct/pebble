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

package net.sourceforge.pebble.util.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pebble.domain.Blog;
import net.sourceforge.pebble.domain.BlogEntry;
import net.sourceforge.pebble.domain.Comment;
import net.sourceforge.pebble.domain.SingleBlogTestCase;

public class MovableTypeImporterTest extends SingleBlogTestCase {
  private File testCasesDir = new File(TEST_RESOURCE_LOCATION, "mt_testcases");

  @Test public void testImport() throws Exception {
    blog.setProperty(Blog.TIMEZONE_KEY, "Japan/Tokyo");
    File source = new File(testCasesDir, "exported.txt");
    MovableTypeImporter.main(new String[]{source.getAbsolutePath(), blog.getRoot(), "Tokyo/Japan"});
    blog.reindex();
    List list = blog.getBlogEntries();
    assertEquals(1, list.size(), "size of entry");
    BlogEntry entry = (BlogEntry) list.get(0);
    assertEquals("excerpt", entry.getContent(), "excerpt");
    assertEquals("excerpt", entry.getExcerpt(), "excerpt");
    //body part needs to include extended body
    assertEquals("body<br />extended body", entry.getBody(), "body");
  }

  @Test public void testNoPrimaryCategory() throws Exception {
    blog.setProperty(Blog.TIMEZONE_KEY, "Japan/Tokyo");
    File source = new File(testCasesDir, "noprimarycategory.txt");
    MovableTypeImporter.main(new String[]{source.getAbsolutePath(), blog.getRoot(), "Tokyo/Japan"});
    blog.reindex();
    List list = blog.getBlogEntries();
    assertEquals(1, list.size(), "size of entry");
  }

  @Test public void testMultipleSubCategory() throws Exception {
    blog.setProperty(Blog.TIMEZONE_KEY, "Japan/Tokyo");
    File source = new File(testCasesDir, "multiplesubcategory.txt");
    MovableTypeImporter.main(new String[]{source.getAbsolutePath(), blog.getRoot(), "Tokyo/Japan"});
    blog.reindex();
    blog.reindex();
    List list = blog.getBlogEntries();
    assertEquals(1, list.size(), "size of entry");
//    BlogEntry entry = (BlogEntry)list.get(0);
//    Set<Category> categories = entry.getCategories();
//    assertEquals("# of categories",3, categories.size());
//    assertTrue(categories.contains("mycategory"));
//    assertTrue(categories.contains("subcategory"));
//    assertTrue(categories.contains("subcategory2"));
  }

  @Test public void testNoExcerpt() throws Exception {
    blog.setProperty(Blog.TIMEZONE_KEY, "Japan/Tokyo");
    File source = new File(testCasesDir, "noexcerpt.txt");
    MovableTypeImporter.main(new String[]{source.getAbsolutePath(), blog.getRoot(), "Tokyo/Japan"});
    blog.reindex();
    List list = blog.getBlogEntries();
    assertEquals(1, list.size(), "size of entry");
    BlogEntry entry = (BlogEntry) list.get(0);
    assertEquals("body", entry.getContent(), "content");
    assertEquals("body", entry.getExcerpt(), "excerpt");
    //body part needs to include extended body
    assertEquals("body<br />extended body", entry.getBody(), "body");
  }

  @Test public void testNoExcerptNoExtendedBody() throws Exception {
    blog.setProperty(Blog.TIMEZONE_KEY, "Japan/Tokyo");
    File source = new File(testCasesDir, "noexcerpt_noextendedbody.txt");
    MovableTypeImporter.main(new String[]{source.getAbsolutePath(), blog.getRoot(), "Tokyo/Japan"});
    blog.reindex();
    List list = blog.getBlogEntries();
    assertEquals(1, list.size(), "size of entry");
    BlogEntry entry = (BlogEntry) list.get(0);
    assertEquals("body", entry.getContent(), "content");
    assertEquals("", entry.getExcerpt(), "excerpt");
    //body part needs to include extended body
    assertEquals("body", entry.getBody(), "body");
  }

  @Test public void testUTF8() throws Exception {
    blog.setProperty(Blog.TIMEZONE_KEY, "Japan/Tokyo");
    File source = new File(testCasesDir, "utf8.txt");
    MovableTypeImporter.main(new String[]{source.getAbsolutePath(), blog.getRoot(), "Tokyo/Japan"});
    blog.reindex();
    List list = blog.getBlogEntries();
    assertEquals(1, list.size(), "size of entry");
    BlogEntry entry = (BlogEntry) list.get(0);
    assertEquals("\u65E5\u672C\u8A9Eexcerpt", entry.getContent(), "content");
    assertEquals("\u65E5\u672C\u8A9E", entry.getTitle(), "title");
  }

  @Test public void testPublished() throws Exception {
    blog.setProperty(Blog.TIMEZONE_KEY, "Japan/Tokyo");
    File source = new File(testCasesDir, "exported.txt");
    MovableTypeImporter.main(new String[]{source.getAbsolutePath(), blog.getRoot(), "Tokyo/Japan"});
    blog.reindex();
    List list = blog.getBlogEntries();
    assertEquals(1, list.size(), "size of entry");
    BlogEntry entry = (BlogEntry) list.get(0);
    assertTrue(entry.isPublished(), "publised");
  }
  @Test public void testComment() throws Exception {
    blog.setProperty(Blog.TIMEZONE_KEY, "Japan/Tokyo");
    File source = new File(testCasesDir, "withcomment.txt");
    MovableTypeImporter.main(new String[]{source.getAbsolutePath(), blog.getRoot(), "Tokyo/Japan"});
    blog.reindex();
    List list = blog.getBlogEntries();
    assertEquals(1, list.size(), "size of entry");
    BlogEntry entry = (BlogEntry) list.get(0);
    assertTrue(entry.isPublished(), "publised");
    List<Comment> comments = entry.getComments();
    assertEquals(1, comments.size(), "size of comments");
    System.out.println(blog.getRoot());
  }
}
