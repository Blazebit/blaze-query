/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl.calcite;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.blazebit.query.QuerySession;
import com.blazebit.query.spi.CollectionDataFormat;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.DataFormatField;
import com.blazebit.query.spi.MapDataFormat;
import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.avatica.util.TimeUnit;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

/**
 * {@link ScannableTable} implementation based on a {@link DataFetcher}.
 *
 * @param <T> The native data type
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DataFetcherTable<T> extends AbstractTable implements ScannableTable, TranslatableTable {

	private static final Map<Class<?>, SqlTypeName> JAVA_TYPE_MAPPINGS;

	static {
		Map<Class<?>, SqlTypeName> javaTypeMappings = new HashMap<>();
		// Timestamp with time zone support in Apache Calcite is very messy and doesn't really work, so we use TIMESTAMP
		javaTypeMappings.put( Instant.class, SqlTypeName.TIMESTAMP );
		javaTypeMappings.put( ZonedDateTime.class, SqlTypeName.TIMESTAMP );
		javaTypeMappings.put( OffsetDateTime.class, SqlTypeName.TIMESTAMP );
		javaTypeMappings.put( LocalDateTime.class, SqlTypeName.TIMESTAMP );
		javaTypeMappings.put( LocalDate.class, SqlTypeName.DATE );
		javaTypeMappings.put( OffsetTime.class, SqlTypeName.TIME );
		javaTypeMappings.put( LocalTime.class, SqlTypeName.TIME );
		javaTypeMappings.put( Duration.class, SqlTypeName.INTERVAL_DAY_SECOND );
		javaTypeMappings.put( Period.class, SqlTypeName.INTERVAL_YEAR_MONTH );
		JAVA_TYPE_MAPPINGS = javaTypeMappings;
	}

	private final Class<T> tableClass;
	private final DataFetcher<T> dataFetcher;
	private final Supplier<DataFetchContext> dataContextSupplier;
	private RelDataType rowType;

	/**
	 * Creates new table.
	 *
	 * @param tableClass The table class
	 * @param dataFetcher The data fetcher
	 * @param dataFetchContextSupplier The data fetch context supplier
	 */
	public DataFetcherTable(Class<T> tableClass, DataFetcher<T> dataFetcher, Supplier<DataFetchContext> dataFetchContextSupplier) {
		this.tableClass = tableClass;
		this.dataFetcher = dataFetcher;
		this.dataContextSupplier = dataFetchContextSupplier;
	}

	public DataFetcher<T> getDataFetcher() {
		return dataFetcher;
	}

	@Override
	public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		if ( rowType == null ) {
			rowType = deduceRowType( (JavaTypeFactory) typeFactory, dataFetcher.getDataFormat() );
		}
		return rowType;
	}

	@Override
	public RelNode toRel(RelOptTable.ToRelContext context, RelOptTable table) {
		return EnumerableTableScan.create( context.getCluster(), table );
	}

	private static RelDataType deduceRowType(JavaTypeFactory typeFactory, DataFormat format) {
		final List<DataFormatField> fields = format.getFields();
		final List<RelDataType> types = new ArrayList<>( fields.size() );
		final List<String> names = new ArrayList<>( fields.size() );
		for ( DataFormatField field : fields ) {
			names.add( field.getName() );
			types.add( deduceType( typeFactory, field.getFormat() ) );
		}
		return typeFactory.createStructType( Pair.zip( names, types ) );
	}

	private static RelDataType deduceType(JavaTypeFactory typeFactory, DataFormat format) {
		if ( format instanceof MapDataFormat mapFormat ) {
			RelDataType keyRelDataType = deduceType( typeFactory, mapFormat.getKeyFormat() );
			RelDataType elementRelDataType = deduceType( typeFactory, mapFormat.getElementFormat() );
			return typeFactory.createTypeWithNullability(
					typeFactory.createMapType( keyRelDataType, elementRelDataType ), true );
		}
		else if ( format instanceof CollectionDataFormat collectionFormat ) {
			RelDataType elementRelDataType = deduceType( typeFactory, collectionFormat.getElementFormat() );
			return typeFactory.createTypeWithNullability( typeFactory.createArrayType( elementRelDataType, -1L ),
					true );
		}
		else if ( !format.getFields().isEmpty() ) {
			return typeFactory.createTypeWithNullability( deduceRowType( typeFactory, format ), true );
		}
		else if ( format.isEnum() ) {
			return typeFactory.createJavaType( String.class );
		}
		else {
			Class<?> clazz = rawClass( format.getType() );
			SqlTypeName sqlTypeName = JAVA_TYPE_MAPPINGS.get( clazz );
			if ( sqlTypeName == null ) {
				return typeFactory.createJavaType( clazz );
			}
			else if ( sqlTypeName == SqlTypeName.INTERVAL_DAY_SECOND ) {
				return typeFactory.createTypeWithNullability( typeFactory.createSqlIntervalType(
						new SqlIntervalQualifier( TimeUnit.DAY, TimeUnit.SECOND, SqlParserPos.ZERO ) ), true );
			}
			else if ( sqlTypeName == SqlTypeName.INTERVAL_YEAR_MONTH ) {
				return typeFactory.createTypeWithNullability( typeFactory.createSqlIntervalType(
						new SqlIntervalQualifier( TimeUnit.YEAR, TimeUnit.MONTH, SqlParserPos.ZERO ) ), true );
			}
			else {
				return typeFactory.createTypeWithNullability( typeFactory.createSqlType( sqlTypeName ), true );
			}
		}
	}

	private static Class<?> rawClass(Type type) {
		if ( type instanceof ParameterizedType ) {
			type = ((ParameterizedType) type).getRawType();
		}
		if ( !(type instanceof Class<?>) ) {
			throw new IllegalArgumentException( "Field type unsupported: " + type );
		}
		return (Class<?>) type;
	}

	public List<? extends T> getData(DataContext dataContext) {
		final DataFetchContext dataFetchContext = dataContextSupplier.get();
		QuerySession session = dataFetchContext.getSession();
		List<? extends T> objects = session.get( tableClass );
		if ( objects == null ) {
			objects = dataFetcher.fetch( dataFetchContext );
			session.put( tableClass, objects );
		}
		return objects;
	}

	@Override
	public Enumerable<Object[]> scan(DataContext root) {
		throw new UnsupportedOperationException( "This should be handled in EnumerableTableScan#implement" );
	}
}
