package commons;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JPanel;


/**
 * A set of utility methods for test applications and junit test.
 * 
 * @author hnad002
 *
 */
public class Utils {
	
	static private Comparator<Component> ascName;
	static {
	       ascName = new Comparator<Component>(){
	            @Override
	            public int compare(Component c1, Component c2){
	                return c1.getName().compareTo(c2.getName());
	            }
	        };
	}
	
	/**
	 * prints specification of GUI components.  
	 */
	public static void printComponents(String title, Component[] components) {		
		if (commons.TestConfigParams.debug) {
			Arrays.sort(components, ascName);
			System.out.println("---- " + title + " ----");
			for (Component c : components) {
				System.out.println(c.getName()+":X=" + c.getX() + ", Y=" + c.getY() + ", W="
						+ c.getWidth() + ", H=" + c.getHeight());
			}
		}
	}
	
	/**
	 * Returns a list of components which exist inside init in the layout.
	 * 
	 * @param init list of components that should be used as starting point.
	 */
	public static LinkedList<Component> getComponentList(Component [] init) {
		LinkedList <Component> allComponents = new LinkedList<Component>();
		for (Component c:init) {
			allComponents.add(c);
		}
		for (int i = 0; i < allComponents.size(); i++) {
			Component current = allComponents.get(i);
			if (current.getName().contains("panel")) {
					Component currentChilds[] = ((JPanel)current).getComponents();					
					for (Component c:currentChilds) {
						allComponents.add(c);
					}
			}
		}
		return allComponents;
	}
	
	/**
	 * Generates a junit test method which for the caller application and prints it in the console.
	 * @param name name of the method
	 * @param components list of components inside the layout of the application.
	 */
	public static void generateGBTest(String name, Component[] components) {
		StringBuffer method = new StringBuffer();
	
		Arrays.sort(components, ascName);
		
		if (commons.TestConfigParams.generateGBTest) {
			method.append("\t@Test\n");	
			method.append("\tpublic void " +  Pattern.compile(Character.toString(name.charAt(0)), Pattern.LITERAL).
					matcher(name).replaceFirst(Character.toString(Character.toLowerCase(name.charAt(0)))) + "() {\n");
			method.append("\t\tMap<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();\n");
			for (Component c : components) {
				method.append("\t\texpectedValues.put(\""+c.getName()+"\", new Integer[]{" + 
						c.getX() +", "+ c.getY() + ", "+ c.getWidth() + ", " + c.getHeight() + "});\n");
			}
			method.append("\n\t\tComponent [] components = "+name+"App.createAndShowGUI(\"ALMGridBagLayout-"+name+"\", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);\n");
			method.append("\n\t\tif (commons.TestConfigParams.debug) {\n");
			method.append("\t\t\tcommons.utils.printComponents(\""+name+"\", components);\n");
			method.append("\t\t}\n");
			method.append("\n\t\tcheckResults(expectedValues, components);\n");
			method.append("\t}");
			
			System.out.println(method.toString());
		}
		
	}	
}
