package org.eclipse.m2m.internal.qvt.oml.debug.ui;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;

public class DebugCodeMining extends LineContentCodeMining {

	public DebugCodeMining(String label, int start, int length, ICodeMiningProvider provider) {
		super(new Position(start, length), provider);

		setLabel(label);
	}

	@Override
	public boolean isResolved() {
		return true;
	}

}
