package alm.compatibility;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import alm.Area;

/**
 * This class stores extra information about ALM areas in a map. The key
 * is the ALM area. When an ALM area is created by GridBagLayout, the extra
 * information is stored in this map. A mapping from component to area is  
 * is also stored in this class.
 *
 * @author hnad002
 */

public class AreaInfoStore {
		private Map<Area, AreaInfo> areaMap; // a map to store extra information (outer row and column) about ALM areas. Key is the area.
		private Map<Component, Area> compToAreaMap; // component to area map. key is the component.
		
		/**
		 * constructor.
		 */
		public AreaInfoStore () {
			areaMap = new HashMap<Area, AreaInfo>();
			compToAreaMap = new HashMap<Component, Area>();
		}
		
		/**
		 * Returns extra information about an area. 
		 */
		public AreaInfo getAreaInfo(Area area) {
			return areaMap.get(area);
		}
		
		/**
		 * Adds extra information about an area to the map. 
		 */		
		public void AddAreaInfo(Area area, AreaInfo info, Component comp) {
			areaMap.put(area, info);
			compToAreaMap.put(comp, area);
		}	
		
		/**
		 * Maps a component to its containing area.  
		 */
		public Area getArea(Component comp) {
			return compToAreaMap.get(comp);
		}
}
