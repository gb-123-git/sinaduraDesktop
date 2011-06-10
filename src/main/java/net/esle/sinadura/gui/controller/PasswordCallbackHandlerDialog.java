/*
 * # Copyright 2008 zylk.net 
 * # 
 * # This file is part of Sinadura. 
 * # 
 * # Sinadura is free software: you can redistribute it and/or modify 
 * # it under the terms of the GNU General Public License as published by 
 * # the Free Software Foundation, either version 2 of the License, or 
 * # (at your option) any later version. 
 * # 
 * # Sinadura is distributed in the hope that it will be useful, 
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * # GNU General Public License for more details. 
 * # 
 * # You should have received a copy of the GNU General Public License 
 * # along with Sinadura. If not, see <http://www.gnu.org/licenses/>. [^] 
 * # 
 * # See COPYRIGHT.txt for copyright notices and details. 
 * #
 */
package net.esle.sinadura.gui.controller;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import net.esle.sinadura.core.exceptions.PasswordCallbackCanceledException;
import net.esle.sinadura.core.password.PasswordExtractor;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.view.main.PasswordDialog;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PasswordCallbackHandlerDialog implements CallbackHandler, PasswordExtractor {

	private Shell sShell = null;
	private PasswordProtection passwordProtection = null;
	

	public PasswordCallbackHandlerDialog(Shell sShell) {

		super();
		this.sShell = sShell;
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		
		for (Callback c : callbacks) {
			if (c instanceof PasswordCallback) {
				PasswordCallback pc = (PasswordCallback) c;
				this.initPassword();
				if (this.passwordProtection != null && this.passwordProtection.getPassword() != null) {
					pc.setPassword(this.passwordProtection.getPassword());
				} else {
					throw new IOException(new PasswordCallbackCanceledException());
				}
			}
		}
	}

	private void initPassword() {

		// por acceder desde el hilo de la progresbar
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				
				PasswordDialog passwordDialog = new PasswordDialog(sShell);
				String password = passwordDialog.open(LanguageUtil.getLanguage().getString("password.dialog.message"));
				if (password != null) {
					passwordProtection = new KeyStore.PasswordProtection(password.toCharArray());
				}
			}
		});
	}

	@Override
	public PasswordProtection getPasswordProtection() {

		return this.passwordProtection;
	}
}