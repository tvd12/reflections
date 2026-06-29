package com.tvd12.reflections.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class AbstractMultimap<K, V> implements Multimap<K, V> {

	protected final Map map;
	protected final Supplier factory;
	
	public AbstractMultimap(
			Map<K, ? extends Collection<V>> map, 
			Supplier<? extends Collection<V>> factory) {
		this.map = map;
		this.factory = factory;
	}
	
	private Collection<V> getOrCreate(K key) {
		Collection<V> answer = (Collection<V>) map.get(key);
		if (answer == null) {
			answer = (Collection<V>) factory.get();
			map.put(key, answer);
		}
		return answer;
	}

	@Override
	public boolean put(K key, V item) {
		return getOrCreate(key).add(item);
	}
	
	@Override
	public boolean putAll(Multimap<K, V> multimap) {
		boolean changed = false;
	    for (Entry<? extends K, ? extends V> entry : multimap.entries()) {
	      changed |= put(entry.getKey(), entry.getValue());
	    }
	    return changed;
	}
	
	@Override
	public Collection<V> get(K key) {
		Collection<V> answer = (Collection<V>) map.get(key);
		return answer != null ? answer : Collections.emptySet();
	}
	
	@Override
	public Set<K> keySet() {
		return map.keySet();
	}
	
	@Override
	public Collection<V> values() {
		return map.values();
	}
	
	@Override
	public Iterable<Entry<K, V>> entries() {
		List<Entry<K, V>> result = new ArrayList<>();
		for (Object key : map.keySet()) {
			Collection<V> items = (Collection<V>) map.get(key);
			for (V value : items)
				result.add(new ImmutableEntry<>((K) key, value));
		}
		return result;
	}
	
	@Override
	public Map<K, Collection<V>> asMap() {
		return map;
	}
	
	@Override
	public int size() {
		return map.size();
	}
	
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
}
