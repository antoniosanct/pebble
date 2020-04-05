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
package net.sourceforge.pebble;

import java.net.URL;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.xml.XmlConfiguration;

import net.sourceforge.pebble.domain.Blog;
import net.sourceforge.pebble.domain.BlogEntry;
import net.sourceforge.pebble.domain.BlogManager;
import net.sourceforge.pebble.domain.BlogService;
import net.sourceforge.pebble.domain.StaticPage;

/**
 * A wrapper for a cache used to store blog entries and static pages.
 *
 * @author Simon Brown
 */
public class ContentCache {

	private static final ContentCache instance = new ContentCache();

	private Cache<String, BlogEntry> cacheBlogEntries;
	private Cache<String, StaticPage> cacheStaticPages;

	private ContentCache() {
		URL url = BlogService.class.getResource("/ehcache.xml");
		XmlConfiguration xmlConfig = new XmlConfiguration(url);
		CacheManager myCacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
		myCacheManager.init();
		cacheBlogEntries = myCacheManager.getCache("contentCacheBlogEntry", String.class, BlogEntry.class);
		cacheStaticPages = myCacheManager.getCache("contentCacheStaticPage", String.class, StaticPage.class);

		// size the cache (number of blogs * max elements in memory configured in the
		// ehcache.xml file)
		// Fix: Previously the number of blogs was calculated through
		// blogManager.getBlogs().getSize() which
		// caused the blog to load and access the Cache that is just now being
		// initialized.
		// This lead to NPE because instance is not yet set to this instance.
		ResourcePools pools = ResourcePoolsBuilder.newResourcePoolsBuilder()
				.heap(20L * BlogManager.getInstance().getNumberOfBlogs(), EntryUnit.ENTRIES).build();
		cacheBlogEntries.getRuntimeConfiguration().updateResourcePools(pools);
		cacheStaticPages.getRuntimeConfiguration().updateResourcePools(pools);
	}

	public static ContentCache getInstance() {
		return instance;
	}

	public synchronized void putBlogEntry(BlogEntry blogEntry) {
		cacheBlogEntries.put(getCompositeKeyForBlogEntry(blogEntry), blogEntry);
	}

	public synchronized BlogEntry getBlogEntry(Blog blog, String blogEntryId) {
		BlogEntry blogEntry = null;
		Object element = cacheBlogEntries.get(getCompositeKeyForBlogEntry(blog, blogEntryId));
		if (element != null) {
			blogEntry = (BlogEntry) element;
		}

		return blogEntry;
	}

	public synchronized void removeBlogEntry(BlogEntry blogEntry) {
		cacheBlogEntries.remove(getCompositeKeyForBlogEntry(blogEntry));
	}

	private String getCompositeKeyForBlogEntry(BlogEntry blogEntry) {
		return getCompositeKeyForBlogEntry(blogEntry.getBlog(), blogEntry.getId());
	}

	private String getCompositeKeyForBlogEntry(Blog blog, String blogEntryId) {
		return blog.getId() + "/blogEntry/" + blogEntryId;
	}

	public synchronized void putStaticPage(StaticPage staticPage) {
		cacheStaticPages.put(getCompositeKeyForStaticPage(staticPage), staticPage);
	}

	public synchronized StaticPage getStaticPage(Blog blog, String staticPageId) {
		StaticPage staticPage = null;
		Object element = cacheStaticPages.get(getCompositeKeyForStaticPage(blog, staticPageId));
		if (element != null) {
			staticPage = (StaticPage) element;
		}

		return staticPage;
	}

	public synchronized void removeStaticPage(StaticPage staticPage) {
		cacheStaticPages.remove(getCompositeKeyForStaticPage(staticPage));
	}

	private String getCompositeKeyForStaticPage(StaticPage staticPage) {
		return getCompositeKeyForStaticPage(staticPage.getBlog(), staticPage.getId());
	}

	private String getCompositeKeyForStaticPage(Blog blog, String staticPageId) {
		return blog.getId() + "/staticPage/" + staticPageId;
	}

}