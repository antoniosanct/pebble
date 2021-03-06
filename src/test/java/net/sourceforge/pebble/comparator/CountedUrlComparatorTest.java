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
package net.sourceforge.pebble.comparator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pebble.logging.CountedUrl;
import net.sourceforge.pebble.logging.LogEntry;
import net.sourceforge.pebble.logging.Referer;

/**
 * Tests for the CountedUrlByCountComparator class.
 *
 * @author    Simon Brown
 */
public class CountedUrlComparatorTest {

  @Test public void testCompare() {
    CountedUrlByCountComparator comp = new CountedUrlByCountComparator();
    CountedUrl c1 = new Referer("http://www.google.com");
    CountedUrl c2 = new Referer("http://www.yahoo.com");

    assertTrue(comp.compare(c1, c1) == 0);
    assertTrue(comp.compare(c1, c2) < 0);
    assertTrue(comp.compare(c2, c1) > 0);

    c1.addLogEntry(new LogEntry());
    assertTrue(comp.compare(c1, c2) < 0);
    assertTrue(comp.compare(c2, c1) > 0);

    c2.addLogEntry(new LogEntry());
    c2.addLogEntry(new LogEntry());
    assertTrue(comp.compare(c1, c2) > 0);
    assertTrue(comp.compare(c2, c1) < 0);
  }

}
