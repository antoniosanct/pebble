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
package net.sourceforge.pebble.index;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import net.sourceforge.pebble.domain.Blog;
import net.sourceforge.pebble.domain.BlogEntry;
import net.sourceforge.pebble.domain.Category;
import net.sourceforge.pebble.domain.Comment;
import net.sourceforge.pebble.domain.StaticPage;
import net.sourceforge.pebble.domain.Tag;
import net.sourceforge.pebble.domain.TrackBack;
import net.sourceforge.pebble.search.SearchException;
import net.sourceforge.pebble.search.SearchHit;
import net.sourceforge.pebble.search.SearchResults;

/**
 * Wraps up the functionality to index blog entries. This is really just a
 * convenient wrapper around Lucene.
 *
 * @author Simon Brown
 */
public class SearchIndex implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5446053059077992063L;

	/** the log used by this class */
	private static final Log LOG = LogFactory.getLog(SearchIndex.class);

	private final Blog blog;

	public SearchIndex(Blog blog) {
		this.blog = blog;
	}

	/**
	 * Clears the index.
	 */
	public void clear() {
		File searchDirectory = new File(blog.getSearchIndexDirectory());
		if (!searchDirectory.exists()) {
			searchDirectory.mkdirs();
		}

		synchronized (blog) {
			try (Directory dir = FSDirectory.open(Paths.get(blog.getSearchIndexDirectory()))) {
				Analyzer analyzer = getAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				try (IndexWriter writer = new IndexWriter(dir, iwc)) {
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Allows a collection of blog entries to be indexed.
	 */
	public void indexBlogEntries(Collection<BlogEntry> blogEntries) {
		synchronized (blog) {
			try (Directory dir = FSDirectory.open(Paths.get(blog.getSearchIndexDirectory()))) {
				Analyzer analyzer = getAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				try (IndexWriter writer = new IndexWriter(dir, iwc)) {
					for (BlogEntry blogEntry : blogEntries) {
						index(blogEntry, writer);
					}
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Allows a collection of static pages to be indexed.
	 */
	public void indexStaticPages(Collection<StaticPage> staticPages) {
		synchronized (blog) {
			try (Directory dir = FSDirectory.open(Paths.get(blog.getSearchIndexDirectory()))) {
				Analyzer analyzer = getAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				try (IndexWriter writer = new IndexWriter(dir, iwc)) {
					for (StaticPage staticPage : staticPages) {
						index(staticPage, writer);
					}
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Allows a single blog entry to be (re)indexed. If the entry is already
	 * indexed, this method deletes the previous index before adding the new one.
	 *
	 * @param blogEntry the BlogEntry instance to index
	 */
	public void index(BlogEntry blogEntry) {
		try (Directory dir = FSDirectory.open(Paths.get(blog.getSearchIndexDirectory()))) {
			synchronized (blog) {
				// first delete the blog entry from the index (if it was there)
				unindex(blogEntry);

				Analyzer analyzer = getAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				try (IndexWriter writer = new IndexWriter(dir, iwc)) {
					index(blogEntry, writer);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Allows a single static page to be (re)indexed. If the page is already
	 * indexed, this method deletes the previous index before adding the new one.
	 *
	 * @param staticPage the StaticPage instance to index
	 */
	public void index(StaticPage staticPage) {
		synchronized (blog) {
			// first delete the blog entry from the index (if it was there)
			unindex(staticPage);
			try (Directory dir = FSDirectory.open(Paths.get(blog.getSearchIndexDirectory()))) {
				Analyzer analyzer = getAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				try (IndexWriter writer = new IndexWriter(dir, iwc)) {
					index(staticPage, writer);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Gets the Analyzer implementation to use.
	 *
	 * @return an Analyzer instance
	 * @throws Exception
	 */
	private Analyzer getAnalyzer() throws Exception {
		Class<?> c = Class.forName(blog.getLuceneAnalyzer());
		return (Analyzer) c.newInstance();
	}

	/**
	 * Removes the index for a single blog entry to be removed.
	 *
	 * @param blogEntry the BlogEntry instance to be removed
	 */
	public void unindex(BlogEntry blogEntry) {

		synchronized (blog) {
			try (Directory dir = FSDirectory.open(Paths.get(blog.getSearchIndexDirectory()))) {
				LOG.debug("Attempting to delete index for " + blogEntry.getTitle());
				Term term = new Term("id", blogEntry.getId());
				Analyzer analyzer = getAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				try (IndexWriter writer = new IndexWriter(dir, iwc)) {
					LOG.debug("Deleted " + writer.deleteDocuments(term) + " document(s) from the index");
				}

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Removes the index for a single blog entry to be removed.
	 *
	 * @param staticPage the StaticPage instance to be removed
	 */
	public void unindex(StaticPage staticPage) {

		synchronized (blog) {
			try (Directory dir = FSDirectory.open(Paths.get(blog.getSearchIndexDirectory()))) {
				LOG.debug("Attempting to delete index for " + staticPage.getTitle());
				Analyzer analyzer = getAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				try (IndexWriter writer = new IndexWriter(dir, iwc)) {
					Term term = new Term("id", staticPage.getId());
					LOG.debug("Deleted " + writer.deleteDocuments(term) + " document(s) from the index");
				}

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Helper method to index an individual blog entry.
	 *
	 * @param blogEntry the BlogEntry instance to index
	 * @param writer    the IndexWriter to index with
	 */
	private void index(BlogEntry blogEntry, IndexWriter writer) {
		if (!blogEntry.isPublished()) {
			return;
		}

		try {
			LOG.debug("Indexing " + blogEntry.getTitle());
			Document document = new Document();
			document.add(new StringField("id", blogEntry.getId(), Field.Store.YES));
			if (blogEntry.getTitle() != null) {
				document.add(new TextField("title", blogEntry.getTitle(), Field.Store.YES));
			} else {
				document.add(new TextField("title", "", Field.Store.YES));
			}
			if (blogEntry.getSubtitle() != null) {
				document.add(new TextField("subtitle", blogEntry.getSubtitle(), Field.Store.YES));
			} else {
				document.add(new TextField("subtitle", "", Field.Store.YES));
			}
			document.add(new StringField("permalink", blogEntry.getPermalink(), Field.Store.YES));
			document.add(
					new StoredField("date", DateTools.dateToString(blogEntry.getDate(), DateTools.Resolution.DAY)));
			if (blogEntry.getBody() != null) {
				document.add(new TextField("body", blogEntry.getBody(), Field.Store.NO));
			} else {
				document.add(new TextField("body", "", Field.Store.NO));
			}
			if (blogEntry.getTruncatedContent() != null) {
				document.add(new TextField("truncatedBody", blogEntry.getTruncatedContent(), Field.Store.YES));
			} else {
				document.add(new TextField("truncatedBody", "", Field.Store.YES));
			}

			if (blogEntry.getAuthor() != null) {
				document.add(new TextField("author", blogEntry.getAuthor(), Field.Store.YES));
			}

			// build up one large string with all searchable content
			// i.e. entry title, entry body and all response bodies
			StringBuilder searchableContent = new StringBuilder();
			searchableContent.append(blogEntry.getTitle());
			searchableContent.append(" ");
			searchableContent.append(blogEntry.getBody());

			for (Category category : blogEntry.getCategories()) {
				document.add(new TextField("category", category.getId(), Field.Store.YES));
			}

			for (Tag tag : blogEntry.getAllTags()) {
				document.add(new TextField("tag", tag.getName(), Field.Store.YES));
			}

			searchableContent.append(" ");
			Iterator<Comment> it = blogEntry.getComments().iterator();
			while (it.hasNext()) {
				Comment comment = it.next();
				if (comment.isApproved()) {
					searchableContent.append(comment.getBody());
					searchableContent.append(" ");
				}
			}
			Iterator<TrackBack> it2 = blogEntry.getTrackBacks().iterator();
			while (it.hasNext()) {
				TrackBack trackBack = it2.next();
				if (trackBack.isApproved()) {
					searchableContent.append(trackBack.getExcerpt());
					searchableContent.append(" ");
				}
			}

			// join the title and body together to make searching on them both easier
			document.add(new TextField("blogEntry", searchableContent.toString(), Field.Store.NO));

			writer.addDocument(document);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Helper method to index an individual blog entry.
	 *
	 * @param staticPage the Page instance instance to index
	 * @param writer     the IndexWriter to index with
	 */
	private void index(StaticPage staticPage, IndexWriter writer) {
		try {
			LOG.debug("Indexing " + staticPage.getTitle());
			Document document = new Document();
			document.add(new StringField("id", staticPage.getId(), Field.Store.YES));
			if (staticPage.getTitle() != null) {
				document.add(new TextField("title", staticPage.getTitle(), Field.Store.YES));
			} else {
				document.add(new TextField("title", "", Field.Store.YES));
			}
			document.add(new StringField("permalink", staticPage.getPermalink(), Field.Store.YES));
			document.add(
					new StoredField("date", DateTools.dateToString(staticPage.getDate(), DateTools.Resolution.DAY)));
			if (staticPage.getBody() != null) {
				document.add(new TextField("body", staticPage.getBody(), Field.Store.NO));
			} else {
				document.add(new TextField("body", "", Field.Store.NO));
			}
			if (staticPage.getTruncatedContent() != null) {
				document.add(new TextField("truncatedBody", staticPage.getTruncatedContent(), Field.Store.YES));
			} else {
				document.add(new TextField("truncatedBody", "", Field.Store.YES));
			}

			if (staticPage.getAuthor() != null) {
				document.add(new TextField("author", staticPage.getAuthor(), Field.Store.YES));
			}

			// build up one large string with all searchable content
			// i.e. entry title, entry body and all response bodies
			StringBuilder searchableContent = new StringBuilder();
			searchableContent.append(staticPage.getTitle());
			searchableContent.append(" ");
			searchableContent.append(staticPage.getBody());

			// join the title and body together to make searching on them both easier
			document.add(new TextField("blogEntry", searchableContent.toString(), Field.Store.NO));

			writer.addDocument(document);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public SearchResults search(String queryString) throws SearchException {

		LOG.debug("Performing search : " + queryString);

		SearchResults searchResults = new SearchResults();
		searchResults.setQuery(queryString);

		if (queryString != null && queryString.length() > 0) {
			IndexSearcher searcher = null;

			try (IndexReader reader = DirectoryReader
					.open(FSDirectory.open(Paths.get(blog.getSearchIndexDirectory())))) {
				searcher = new IndexSearcher(reader);
				QueryParser parser = new QueryParser("blogEntry", getAnalyzer());
				Query query = parser.parse(queryString);
				TopDocs results = searcher.search(query, 100);
				ScoreDoc[] hits = results.scoreDocs;

				for (int i = 0; i < results.totalHits.value; i++) {
					Document doc = searcher.doc(hits[i].doc);
					SearchHit result = new SearchHit(blog, doc.get("id"), doc.get("permalink"), doc.get("title"),
							doc.get("subtitle"), doc.get("truncatedBody"), DateTools.stringToDate(doc.get("date")),
							hits[i].score);
					searchResults.add(result);
				}
			} catch (ParseException pe) {
				LOG.error("Sorry, but there was an error. Please try another search", pe);
				searchResults.setMessage("Sorry, but there was an error. Please try another search");
			} catch (Exception e) {
				LOG.error("Sorry, but there was an error. Please try another search", e);
				throw new SearchException(e.getMessage());
			}
		}

		return searchResults;
	}

}
