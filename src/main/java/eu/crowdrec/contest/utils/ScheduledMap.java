package eu.crowdrec.contest.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class implements an batch aggregation. Entries collected in a map are stored batch-wise in a new processing structure
 * @author andreas
 *
 * @param <K>
 * @param <V>
 */
public class ScheduledMap<K, V> implements Map<K, V>{
	
    	
    	/**
    	 * the alternating map
    	 */
    	private final Map<K,V>[] shortTermMap;
    	
    	/**
    	 * the window size for computing the batches
    	 */
    	private final long windowSize;
    	
    	/** the scheduler for running the persist task */
    	private final ScheduledExecutorService scheduler;

    	/** the timeStamp of the last operation */
    	private long lastTimeStamp;
    	
    	// defines which map is actively used in the shortTermMap
    	private int activeElement;
    	
    	/** the number of calls for the method persist */
    	private int persistorCount;
    	
    	/**
    	 * The constructor.
    	 * We define a ring-buffer of maps
    	 * 
    	 * @param _numberOfMaps
    	 * @param _windowsize
    	 */
    	@SuppressWarnings("unchecked")
    	public ScheduledMap(final int _numberOfMaps, final long _windowSize) {
    		
    		this.windowSize = _windowSize;
    		this.shortTermMap = new HashMap[_numberOfMaps];
    		for (int i = 0; i < shortTermMap.length; i++) {
				shortTermMap[i] = new HashMap<K, V>();
			}
    		this.scheduler = Executors.newScheduledThreadPool(10);
    		this.activeElement = 0;
    		persistorCount = 0;
    		this.lastTimeStamp = System.currentTimeMillis();
    		
    		// start the scheduler in a new Thread
    		Thread persistorThread = new Thread(){
    			@Override
    			public void run() {
    				persistTheMap();
    			}
    		};
    		this.scheduler.scheduleWithFixedDelay(persistorThread, 15, 15, TimeUnit.MINUTES);
    	}
    	
    	/** 
    	 * clear the active part, the inactive part is not affected
    	 */
    	@Override
    	public void clear() {
    		this.shortTermMap[activeElement].clear();
    	}
    	
    	/**
    	 * checks whether the active part contains the key
    	 */
    	@Override
    	public boolean containsKey(Object arg0) {
    		return this.shortTermMap[activeElement].containsKey(arg0);
    	}
    	
    	/**
    	 * checks whether the active part contains the value
    	 */
    	@Override
    	public boolean containsValue(Object arg0) {
    		return this.shortTermMap[activeElement].containsValue(arg0);

    	}
    	
    	/**
    	 * return the entries for the active element
    	 */
    	@Override
    	public Set<java.util.Map.Entry<K, V>> entrySet() {
    		return this.shortTermMap[activeElement].entrySet();
    	}
    	
    	@Override
    	public V get(Object arg0) {
    		return this.shortTermMap[activeElement].get(arg0);
    	}
    	@Override
    	public boolean isEmpty() {
    		return this.shortTermMap[activeElement].isEmpty();
    	}
    	@Override
    	public Set<K> keySet() {
    		return this.shortTermMap[activeElement].keySet();
    	}
    	@Override
    	public V put(K arg0, V arg1) {
    		return this.shortTermMap[activeElement].put(arg0, arg1);
    	}
    	
    	public void inializeByCloning() {
    		for (int i = 1; i < shortTermMap.length; i++) {
				shortTermMap[i] = new HashMap<K, V>();
				shortTermMap[i].putAll(shortTermMap[0]);
			}
    	}
    	
    	@Override
    	public void putAll(Map<? extends K, ? extends V> arg0) {
    		this.shortTermMap[activeElement].putAll(arg0);
    		
    	}
    	@Override
    	public V remove(Object arg0) {
    		return this.shortTermMap[activeElement].remove(arg0);
    	}
    	@Override
    	public int size() {
    		return this.shortTermMap[activeElement].size();
    	}
    	@Override
    	public Collection<V> values() {
    		return this.shortTermMap[activeElement].values();
    	}
    	
    	private void persistTheMap() {
    		
    		// request the current time
    		long currentTimeStamp = System.currentTimeMillis();
    		
    		// check for scheduling errors
//    		if (currentTimeStamp-lastTimeStamp < windowSize) {
//    			return;
//    		}
    		
    		// increment the activeIndex
    		int oldActiveElement = activeElement;
    		activeElement = (activeElement++) % this.shortTermMap.length;
    		
    		
    		// Write the map into ... TODO
    		for (Map.Entry<K, V> entry : this.shortTermMap[oldActiveElement].entrySet()) {
    			
			}

    		// clean the values in  the map
    		Map<K, V> tmpMap = this.shortTermMap[oldActiveElement];
    		for (Map.Entry<?, ?> tmpMapEntry : tmpMap.entrySet()) {
    			((Map<?,?>) tmpMapEntry.getValue()).clear();
			}
    		this.persistorCount++;
    	}
   }


