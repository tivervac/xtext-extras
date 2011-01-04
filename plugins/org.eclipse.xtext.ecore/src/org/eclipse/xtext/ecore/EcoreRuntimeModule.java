/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ecore;

import org.eclipse.xtext.resource.generic.AbstractGenericResourceRuntimeModule;

/**
 * Default Guice bindings for managing Ecore resources in the context of Xtext.
 *  
 * @author koehnlein - Initial contribution and API
 */
public class EcoreRuntimeModule extends AbstractGenericResourceRuntimeModule {

	@Override
	protected String getLanguageName() {
		return "org.eclipse.emf.ecore.presentation.EcoreEditorID";
	}

	@Override
	protected String getFileExtensions() {
		return "ecore";
	}
	
}
