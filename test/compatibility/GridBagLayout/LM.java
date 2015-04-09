package compatibility.GridBagLayout;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

class LM {
	public GridBagLayout gbl;
	public alm.compatibility.GridBagLayout agbl;
	
	public LM(GridBagLayout gbl) {
		this.gbl = gbl;
		this.agbl = null;
	}
	
	public LM(alm.compatibility.GridBagLayout gbl) {
		this.agbl = gbl;
		this.gbl = null;
	}	
	
	public void setConstraints(Component comp, GridBagConstraints c) {
		if (agbl == null)
			gbl.setConstraints(comp, c);
		else
			agbl.setConstraints(comp, c);
	}	
}