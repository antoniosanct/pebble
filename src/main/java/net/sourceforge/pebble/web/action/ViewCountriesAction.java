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
package net.sourceforge.pebble.web.action;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.DatabaseReader.Builder;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;

import net.sourceforge.pebble.Constants;
import net.sourceforge.pebble.domain.Blog;
import net.sourceforge.pebble.logging.Log;
import net.sourceforge.pebble.logging.LogEntry;
import net.sourceforge.pebble.logging.Request;
import net.sourceforge.pebble.web.view.View;
import net.sourceforge.pebble.web.view.impl.CountriesView;

/**
 * Gets the visitor country information for the specified time period.
 *
 * @author Simon Brown
 */
public class ViewCountriesAction extends AbstractLogAction {

	/**
	 * Peforms the processing associated with this action.
	 *
	 * @param request  the HttpServletRequest instance
	 * @param response the HttpServletResponse instance
	 * @return the name of the next view
	 */
	public View process(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Blog blog = (Blog) getModel().get(Constants.BLOG_KEY);
		Log log = getLog(request, response);

		Set<String> countries = new TreeSet<String>(new Comparator<String>() {
			public int compare(String s1, String s2) {
				return s1 != null ? s1.compareToIgnoreCase(s2) : -1;
			}
		});
		Map<String, Integer> consolidatedCountries = new HashMap<>(1);
		Map<String, Integer> countriesForNewsFeeds = new HashMap<>(1);
		Map<String, Integer> countriesForPageViews = new HashMap<>(1);
		Map<String, Integer> countriesForFileDownloads = new HashMap<>(1);

		URL urlFilename = getClass().getResource("/geo-ip.dat");
		try (DatabaseReader dr = new Builder(urlFilename.openStream()).build()) {

			for (LogEntry logEntry : log.getLogEntries()) {
				Optional<CountryResponse> cr = dr.tryCountry(InetAddress.getByName(logEntry.getHost()));
				if (cr.isPresent()) {
					String country = cr.get().getCountry().getName();
					countries.add(country);
					register(country, countriesForNewsFeeds);
					register(country, countriesForPageViews);
					register(country, countriesForFileDownloads);
					register(country, consolidatedCountries);

					Request req = new Request(logEntry.getRequestUri(), blog);
					if (req.isNewsFeed()) {
						increment(country, countriesForNewsFeeds);
						increment(country, consolidatedCountries);
					} else if (req.isPageView()) {
						increment(country, countriesForPageViews);
						increment(country, consolidatedCountries);
					} else if (req.isFileDownload()) {
						increment(country, countriesForFileDownloads);
						increment(country, consolidatedCountries);
					}
				}
			}
		} catch (IOException ioe) {
			throw new ServletException(ioe);
		} catch (GeoIp2Exception e) {
			throw new ServletException(e);
		}

		getModel().put("logAction", "viewCountries");
		getModel().put("countries", countries);
		getModel().put("consolidatedCountries", consolidatedCountries);
		getModel().put("countriesForNewsFeeds", countriesForNewsFeeds);
		getModel().put("countriesForPageViews", countriesForPageViews);
		getModel().put("countriesForFileDownloads", countriesForFileDownloads);

		return new CountriesView();
	}

	private void register(String country, Map<String, Integer> map) {
		Integer count = map.get(country);
		if (count == null) {
			count = 0;
		}
		map.put(country, count);
	}

	private void increment(String country, Map<String, Integer> map) {
		Integer count = map.get(country);
		count = count + 1;
		map.put(country, count);
	}

}