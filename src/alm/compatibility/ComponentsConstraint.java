package alm.compatibility;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.swing.JComponent;
/**  
 *
 * This class stores two versions of constraints. processed version is modified by 
 * preProcessConstraints and postProcessConstraints. original contains the original
 * version of GridBagConstraints.
 * notAdded is a list of constraints which are attached to the components, using
 * setConstraints, before they are actually added to the layout.
 * 
 * @author hnad002
 */

public class ComponentsConstraint {
	private LinkedHashMap<JComponent, GridBagConstraints> processed; // List of processed constraints; searchable by component.
	private LinkedHashMap<JComponent, GridBagConstraints> original; // List of original constraints; searchable by component.
	private LinkedHashMap<JComponent, GridBagConstraints> notAdded; // List of constraints which are not added to the layout yet; searchable component. 
	
	/**
	 * Class Constructor.
	 */
	public ComponentsConstraint() {
		processed = new LinkedHashMap<JComponent, GridBagConstraints>(); 
		original = new LinkedHashMap<JComponent, GridBagConstraints>(); 
		notAdded = new LinkedHashMap<JComponent, GridBagConstraints>(); 
	}
	
	/**
	 * removes the constraint associated with a component from both processed and
	 * original lists.
	 */
	public void remove(Component comp) {
		removeprocessed(comp);
		removeOriginal(comp);
	}
	
	// methods for accessing and manipulating processed constraints. 
	
	/**
	 * Returns list of components in the processed list.
	 */
	public LinkedHashMap<JComponent, GridBagConstraints> getProcessed() {
		return processed;
	}

	/**
	 * Returns the contraint object associated to a component in processed list.
	 */
	public GridBagConstraints get(Component comp) {
		return processed.get(comp);
	}

	/**
	 * Returns the set of components that exists in the processed list.
	 */
	public Set <JComponent> getKeySet() {
		return processed.keySet();
	}

	/**
	 * Checks to see whether a component exists in the processed list. 
	 */
	public boolean containsKey(Component comp) {
		return processed.containsKey(comp);
	}
	
	/**
	 * Adds a component and its associated constraints to the processed list. 
	 */
	public void add(Component comp, GridBagConstraints c) {
		processed.put((JComponent)comp, c);		
	}
	
	/**
	 * Removes a component from processed list. 
	 */
	private GridBagConstraints removeprocessed(Component comp) {
		return processed.remove(comp);		
	}	


	// methods for accessing and manipulating original constraints.
	
	/**
	 * Returns the contraint object associated to a component in the original list.
	 */	
	public LinkedHashMap<JComponent, GridBagConstraints> getOriginal() {
		return original;
	}

	/**
	 * Returns constraints associated with the component in the original list. 
	 */
	public GridBagConstraints getOriginal(Component comp) {
		return original.get(comp);
	}	

	/**
	 * Checks whether a component exists in the original list. 
	 */
	public boolean originalContainsKey(Component comp) {
		return original.containsKey(comp);
	}

	/**
	 * Returns components in the original list as a set.
	 */
	public Set <JComponent> getOriginalKeySet() {
		return original.keySet();
	}

	/**
	 * Adds a component and its associated constraints to the original list. 
	 */
	public void addOriginal(Component comp, GridBagConstraints c) {
		original.put((JComponent)comp, c);		
	}
	
	/**
	 * Removes a component from the original list. 
	 */
	public GridBagConstraints removeOriginal(Component comp) {
		return original.remove(comp);		
	}


	// methods for accessing and manipulating temporary list notAdded.
	
	/**
	 * Returns a list of components and their associated constraints in the notAdded list.
	 */
	public LinkedHashMap<JComponent, GridBagConstraints> getNotAdded() {
		return notAdded;
	}

	/**
	 * Returns constraints associated with a component in the notAdded list.
	 */
	public GridBagConstraints getNotAdded(Component comp) {
		return notAdded.get(comp);
	}
	
	/**
	 * Adds a component to the notAdded list. 
	 */
	public void addNotAdded(Component comp, GridBagConstraints c) {
		notAdded.put((JComponent)comp, c);		
	}

	/**
	 * Removes a component from the notAdded list. 
	 */
	public GridBagConstraints removeNotAdded(Component comp) {
		return notAdded.remove(comp);		
	}

	/**
	 * Checks whether a component exists in the not Added list. 
	 */
	public boolean notAddedContainsKey(Component comp) {
		return notAdded.containsKey(comp);
	}
	
	/**
	 * Returns a list of components which exists in the notAdded list. 
	 */
	public Set <JComponent> getNotAddedKeySet() {
		return notAdded.keySet();
	}

}
