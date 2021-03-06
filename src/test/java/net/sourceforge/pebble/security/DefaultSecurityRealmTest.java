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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.sourceforge.pebble.Constants;
import net.sourceforge.pebble.PebbleContext;
import net.sourceforge.pebble.domain.SingleBlogTestCase;


/**
 * Tests for the DefaultSecurityRealm class.
 *
 * @author    Simon Brown
 */
public class DefaultSecurityRealmTest extends SingleBlogTestCase {

  private DefaultSecurityRealm realm;
  private PasswordEncoder passwordEncoder;
  private GrantedAuthority blogOwnerRole;
  private GrantedAuthority blogReaderRole;
  
  @BeforeEach protected void setUp() throws Exception {
    super.setUp();

    realm = new DefaultSecurityRealm();
    realm.setConfiguration(PebbleContext.getInstance().getConfiguration());

    passwordEncoder = (PasswordEncoder) testApplicationContext.getBean("passwordEncoder");
    realm.setPasswordEncoder(passwordEncoder);

    realm.onApplicationEvent(new ContextRefreshedEvent(testApplicationContext));
    
    blogOwnerRole = new SimpleGrantedAuthority(Constants.BLOG_OWNER_ROLE);
    blogReaderRole = new SimpleGrantedAuthority(Constants.BLOG_READER_ROLE);
  }

  @AfterEach protected void tearDown() throws Exception {
    super.tearDown();

    realm.removeUser("username");
  }

  @Test public void testConfigured() {
    assertSame(passwordEncoder, realm.getPasswordEncoder());
  }

  @Test public void testGetUser() throws Exception {
    Map<String,String> preferences = new HashMap<String,String>();
    preferences.put("testPreference", "true");
    PebbleUserDetails pud = new PebbleUserDetails("testuser", "password", "name", "emailAddress", "website", "profile", new String[]{Constants.BLOG_OWNER_ROLE}, preferences, true);
    realm.createUser(pud);
    PebbleUserDetails user = realm.getUser("testuser");

    assertNotNull(user);
    assertEquals("testuser", user.getUsername());
    // bCrypt set new salt after new execution. skipping...
//    assertEquals("password{testuser}", user.getPassword());
    assertEquals("name", user.getName());
    assertEquals("emailAddress", user.getEmailAddress());
    assertEquals("website", user.getWebsite());
    assertEquals("profile", user.getProfile());
    assertEquals("true", user.getPreference("testPreference"));

    Collection<GrantedAuthority> authorities = user.getAuthorities();
    assertEquals(2, authorities.size());
    assertTrue(authorities.contains(blogOwnerRole));
    assertTrue(authorities.contains(blogReaderRole));
  }

  @Test public void testGetUserWhenUserDoesntExist() throws Exception {
    PebbleUserDetails user = realm.getUser("someotherusername");
    assertNull(user);
  }

  @Test public void testRemoveUser() throws Exception {
    PebbleUserDetails pud = new PebbleUserDetails("testuser", "password", "name", "emailAddress", "website", "profile", new String[]{Constants.BLOG_OWNER_ROLE}, new HashMap<String,String>(), true);
    realm.createUser(pud);

    PebbleUserDetails user = realm.getUser("testuser");
    assertNotNull(user);

    realm.removeUser("testuser");
    user = realm.getUser("testuser");
    assertNull(user);
  }

  @Test public void testRemoveUserThatDoesntExists() throws Exception {
    PebbleUserDetails user = realm.getUser("someotherusername");
    assertNull(user);

    realm.removeUser("someotherusername");
    user = realm.getUser("someotherusername");
    assertNull(user);
  }

}
