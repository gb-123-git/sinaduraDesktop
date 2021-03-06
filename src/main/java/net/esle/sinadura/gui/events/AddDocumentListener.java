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
package net.esle.sinadura.gui.events;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.esle.sinadura.core.util.FileUtil;
import net.esle.sinadura.gui.exceptions.FileNotValidException;
import net.esle.sinadura.gui.model.DocumentInfo;
import net.esle.sinadura.gui.util.DocumentInfoUtil;
import net.esle.sinadura.gui.util.LanguageUtil;
import net.esle.sinadura.gui.util.LoggingDesktopController;
import net.esle.sinadura.gui.util.PreferencesUtil;
import net.esle.sinadura.gui.view.main.DocumentsTable;
import net.esle.sinadura.gui.view.main.FileDialogs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;


public class AddDocumentListener implements SelectionListener {
	
	private static Log log = LogFactory.getLog(AddDocumentListener.class);
	
	private DocumentsTable tableDocument = null;
	
	public AddDocumentListener(DocumentsTable t) {
		
		this.tableDocument = t;
	}
	
	public void widgetSelected(SelectionEvent event) {
		
		List<DocumentInfo> newDocs = new ArrayList<DocumentInfo>();
		try {
			
			List<File> fileList = FileDialogs.openFilesDialog(this.tableDocument.getShell(), new String[] { FileUtil.EXTENSION_PDF,				FileUtil.EXTENSION_SAR, FileUtil.EXTENSION_XML, FileUtil.EXTENSION_P7S }, true);
			newDocs = DocumentInfoUtil.fileToDocumentInfo(fileList);

			// TODO duplicado
			if (PreferencesUtil.getBoolean(PreferencesUtil.AUTO_VALIDATE)) {
				
				ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(this.tableDocument.getShell());
				progressMonitorDialog.run(true, true, new ValidatePDFProgress(newDocs));
			}
		}catch(FileNotValidException e){
			// runtimes - error inesperado
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.file.invalid"), e.getFilePath());
			log.error("", e);
			LoggingDesktopController.printError(m);
			
		} catch (InvocationTargetException e) {

			// runtimes - error inesperado
			String m = MessageFormat.format(LanguageUtil.getLanguage().getString("error.validation.unexpected"), e.getCause().toString());
			log.error("", e);
			LoggingDesktopController.printError(m);

		} catch (InterruptedException e) {

			String m = LanguageUtil.getLanguage().getString("error.operacion_cancelada");
			LoggingDesktopController.printError(m);
			log.error(m);
		}
			
		
		this.tableDocument.addDocuments(newDocs);	
		this.tableDocument.reloadTable();
	}

	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);
	}
}