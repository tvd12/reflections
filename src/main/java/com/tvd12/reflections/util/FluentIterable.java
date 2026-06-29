package com.tvd12.reflections.util;

import java.util.Iterator;

import javax.annotation.Nonnull;

@SuppressWarnings("unchecked")
public abstract class FluentIterable<E> implements Iterable<E> {

	@Nonnull
	@Override
	public abstract Iterator<E> iterator();

	public static <T> FluentIterable<T> concat(final Iterable<? extends Iterable<? extends T>> inputs) {
		return new FluentIterable<T>() {
			@Nonnull
			@Override
			public Iterator<T> iterator() {
				return Iterators.concat(
					Iterators.transform(inputs.iterator(), Iterables.toIterator())
				);
			}
		};
	}

	public static <T> FluentIterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b) {
		return concatNoDefensiveCopy(a, b);
	}

	private static <T> FluentIterable<T> concatNoDefensiveCopy(final Iterable<? extends T>... inputs) {
		return new FluentIterable<T>() {
			@Nonnull
			@Override
			public Iterator<T> iterator() {
				return Iterators.concat(
					new AbstractIndexedListIterator<Iterator<? extends T>>(inputs.length) {
						@Override
						public Iterator<? extends T> get(int i) {
							return inputs[i].iterator();
						}
					});
			}
		};
	}

}
