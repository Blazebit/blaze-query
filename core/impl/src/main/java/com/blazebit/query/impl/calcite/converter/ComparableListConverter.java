/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite.converter;

import com.blazebit.query.impl.calcite.ListDelegate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.16
 */
public class ComparableListConverter implements Converter<Collection, List> {
	/**
	 * The {@link List} converter.
	 */
	public static final ComparableListConverter INSTANCE = new ComparableListConverter();

	private ComparableListConverter() {
	}

	@Override
	public List convert(Collection o) {
		if ( o == null ) {
			return null;
		}
		if ( o instanceof List list ) {
			return new FakeComparableList<>( list );
		}
		return new FakeComparableList<>( Arrays.asList( o.toArray() ) );
	}

	private static class FakeComparableList<T> extends ListDelegate<T> implements Comparable<List> {

		public FakeComparableList(List<T> delegate) {
			super( delegate );
		}

		@Override
		public int compareTo(List o) {
			//noinspection unchecked,rawtypes
			return compare( (List<Comparable>) delegate, o );
		}

		static <T extends Comparable<T>> int compare(List<T> list0, List<T> list1) {
			final int size0 = list0.size();
			final int size1 = list1.size();
			if ( size1 == size0 ) {
				return compare( list0, list1, size0 );
			}
			final int c = compare( list0, list1, Math.min( size0, size1 ) );
			if ( c != 0 ) {
				return c;
			}
			return size0 - size1;
		}

		static <T extends Comparable<T>> int compare(List<T> list0, List<T> list1, int size) {
			for ( int i = 0; i < size; i++ ) {
				int c = compare( list0.get( i ), list1.get( i ) );
				if ( c != 0 ) {
					return c;
				}
			}
			return 0;
		}

		static <T extends Comparable<T>> int compare(T a, T b) {
			if ( a == b ) {
				return 0;
			}
			if ( a == null ) {
				return -1;
			}
			if ( b == null ) {
				return 1;
			}
			return a.compareTo( b );
		}
	}
}
