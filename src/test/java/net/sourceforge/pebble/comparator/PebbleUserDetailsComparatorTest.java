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

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import net.sourceforge.pebble.domain.SingleBlogTestCase;
import net.sourceforge.pebble.security.PebbleUserDetails;

/**
 * Tests for the PebbleUserDetailsComparator class.
 *
 * @author    Simon Brown
 */
public class PebbleUserDetailsComparatorTest extends SingleBlogTestCase {

  @Test public void testCompare() {
    PebbleUserDetailsComparator comp = new PebbleUserDetailsComparator();
    PebbleUserDetails pud1 = new PebbleUserDetails("username1", "", "User1", "", "", "", new String[]{}, new HashMap<String,String>(), true);
    PebbleUserDetails pud2 = new PebbleUserDetails("username2", "", "User2", "", "", "", new String[]{}, new HashMap<String,String>(), true);

    assertTrue(comp.compare(pud1, pud1) == 0);
    assertTrue(comp.compare(pud1, pud2) < 0);
    assertTrue(comp.compare(pud2, pud1) > 0);
  }

}
