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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.pebble.Constants;
import net.sourceforge.pebble.PebbleContext;
import net.sourceforge.pebble.security.PebbleUserDetails;
import net.sourceforge.pebble.security.SecurityRealm;
import net.sourceforge.pebble.security.SecurityRealmException;
import net.sourceforge.pebble.util.SecurityUtils;
import net.sourceforge.pebble.web.security.RequireSecurityToken;
import net.sourceforge.pebble.web.security.SecurityTokenValidatorCondition;
import net.sourceforge.pebble.web.validation.ValidationContext;
import net.sourceforge.pebble.web.view.ForbiddenView;
import net.sourceforge.pebble.web.view.View;
import net.sourceforge.pebble.web.view.impl.ChangePasswordView;
import net.sourceforge.pebble.web.view.impl.PasswordChangedView;

/**
 * Changes the user's password.
 *
 * @author    Simon Brown
 */
@RequireSecurityToken(ChangePasswordAction.ChangePasswordCondition.class)
public class ChangePasswordAction extends SecureAction {

  /**
   * Peforms the processing associated with this action.
   *
   * @param request  the HttpServletRequest instance
   * @param response the HttpServletResponse instance
   * @return the name of the next view
   */
  public View process(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    try {
      SecurityRealm realm = PebbleContext.getInstance().getConfiguration().getSecurityRealm();
      PebbleUserDetails currentUserDetails = SecurityUtils.getUserDetails();
      String password1 = request.getParameter("password1");
      String password2 = request.getParameter("password2");
      String submit = request.getParameter("submit");

      // can the user change their user details?
      if (!currentUserDetails.isDetailsUpdateable()) {
        return new ForbiddenView();
      }

      if (submit == null || submit.length() == 0) {
        return new ChangePasswordView();
      }

      ValidationContext validationContext = new ValidationContext();

      if (password1 == null || password1.length() == 0) {
        validationContext.addError("Password can not be empty");
      } else if (!password1.equals(password2)) {
        validationContext.addError("Passwords do not match");
      }

      if (!validationContext.hasErrors()) {
          realm.changePassword(currentUserDetails.getUsername(), password1);

          return new PasswordChangedView();
      }

      getModel().put("validationContext", validationContext);
      return new ChangePasswordView();
    } catch (SecurityRealmException e) {
      throw new ServletException(e);
    }
  }

  /**
   * Gets a list of all roles that are allowed to access this action.
   *
   * @return  an array of Strings representing role names
   * @param request
   */
  public String[] getRoles(HttpServletRequest request) {
    return new String[]{Constants.ANY_ROLE};
  }

  /**
   * The same action is used for viewing the password changed screen as changing the password.  Displaying the screen
   * is detected by the lack of a submit parameter above, so validate the security token if there is a submit parameter.
   */
  public static class ChangePasswordCondition implements SecurityTokenValidatorCondition
  {
    public boolean shouldValidate(HttpServletRequest request) {
      return request.getParameter("submit") != null;
    }
  }

}