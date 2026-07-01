package com.tvd12.reflections.util;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public final class Sets {

	private Sets() {}
	
	public static <T> Set<T> newHashSet(T... ts) {
        return new HashSet<>(Arrays.asList(ts));
	}
	
	public static <T> Set<T> newHashSet(
		Iterable<T> iterable
	) {
		Set<T> set = new HashSet<>();
		for(T t : iterable) {
			set.add(t);
		}
		return set;
	}
	
	public static <T> Set<T> newLinkedHashSet(T... ts) {
        return new LinkedHashSet<>(Arrays.asList(ts));
	}

	public static Set<String> newSetFromMap(
		ConcurrentHashMap<String, Boolean> map
	) {
		return Collections.newSetFromMap(map);
	}
	
	public static <E> SetView<E> difference(Set<E> a, Set<E> b) {
		return () -> new AbstractIterator<E>() {
            final Iterator<E> itr = a.iterator();
            @Override
            protected E computeNext() {
                while (itr.hasNext()) {
                    E e = itr.next();
                    if (!b.contains(e)) {
                        return e;
                    }
                }
                return endOfData();
            }
        };
	}
	
	public interface SetView<T> extends Iterable<T> {

		@Nonnull
		Iterator<T> iterator();
	}
}
