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

package net.sourceforge.pebble.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import net.sourceforge.pebble.Constants;

/**
 * Tests for the DefaultUserDetails class.
 *
 * @author    Simon Brown
 */
public class DefaultUserDetailsServiceTest {

  private DefaultUserDetailsService service;
  private SecurityRealm securityRealm;

  @BeforeEach
  protected void setUp() {
    service = new DefaultUserDetailsService();
    securityRealm = new MockSecurityRealm();
    service.setSecurityRealm(securityRealm);
  }

  @Test public void testSecurityRealm() {
    assertSame(securityRealm, service.getSecurityRealm());
  }

  @Test public void testLoadByUsername() throws Exception {
    PebbleUserDetails pud = new PebbleUserDetails("username", "password", "name", "emailAddress", "website", "profile", new String[]{Constants.BLOG_OWNER_ROLE}, new HashMap<String,String>(), true);
    securityRealm.createUser(pud);
    UserDetails user = service.loadUserByUsername("username");

    assertNotNull(user);
    assertEquals("username", user.getUsername());
    assertEquals("password", user.getPassword());

    Collection<?> authorities = user.getAuthorities();
    assertEquals(2, authorities.size());
    assertTrue(authorities.contains(new SimpleGrantedAuthority(Constants.BLOG_OWNER_ROLE)));
    assertTrue(authorities.contains(new SimpleGrantedAuthority(Constants.BLOG_READER_ROLE)));
  }

  @Test public void testLoadByUsernameThrowsExceptionWhenUserDoesntExist() throws Exception {
    try {
      PebbleUserDetails pud = new PebbleUserDetails("username", "password", "name", "emailAddress", "website", "profile", new String[]{Constants.BLOG_OWNER_ROLE}, new HashMap<String,String>(), true);
      securityRealm.createUser(pud);
      service.loadUserByUsername("someotherusername");
      fail();
    } catch (UsernameNotFoundException e) {
    }
  }

}
