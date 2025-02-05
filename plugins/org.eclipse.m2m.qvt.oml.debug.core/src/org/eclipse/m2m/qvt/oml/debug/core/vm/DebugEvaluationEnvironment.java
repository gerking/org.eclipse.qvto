/*******************************************************************************
 * Copyright (c) 2009, 2018 R.Dvorak and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Radek Dvorak - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.debug.core.vm;

import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalEvaluationEnv;
import org.eclipse.m2m.qvt.oml.util.IContext;

public class DebugEvaluationEnvironment extends QvtOperationalEvaluationEnv implements QVTODebugEvalEnvironment {
	
	private final long fID;
	
	public DebugEvaluationEnvironment(IContext context, QvtOperationalEvaluationEnv parent, long id) {
		super(context, parent);
		fID = id;
	}
	
	public final long getID() {
		return fID;
	}
	
	@Override
	public QvtOperationalEvaluationEnv cloneEvaluationEnv() {
		QvtOperationalEvaluationEnv env = new DebugEvaluationEnvironment(getContext(), getParent(), fID);
		return copyEnv(env);
	}

	@Override
	public QvtOperationalEvaluationEnv createDeferredExecutionEnvironment() {
		QvtOperationalEvaluationEnv parent = (getRoot() == this) ? null : getRoot();
		QvtOperationalEvaluationEnv result = new DebugEvaluationEnvironment(getContext(), parent, fID);		
		return copyEnv(result);
	}	
	
}