/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.query.impl.calcite;

import com.blazebit.query.spi.DataFetcherException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.blazebit.query.QuerySession;
import com.blazebit.query.impl.calcite.converter.AccessorConverter;
import com.blazebit.query.impl.calcite.converter.CollectionConverter;
import com.blazebit.query.impl.calcite.converter.Converter;
import com.blazebit.query.impl.calcite.converter.MapConverter;
import com.blazebit.query.impl.calcite.converter.ObjectArrayConverter;
import com.blazebit.query.spi.CollectionDataFormat;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.DataFormatField;
import com.blazebit.query.spi.MapDataFormat;
import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.util.Pair;

/**
 * {@link ScannableTable} implementation based on a {@link DataFetcher}.
 *
 * @param <T> The native data type
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DataFetcherTable<T> extends AbstractTable implements ScannableTable, TranslatableTable {

    private final Class<T> tableClass;
    private final DataFetcher<T> dataFetcher;
    private final Supplier<DataFetchContext> dataContextSupplier;
    private final Converter<T, Object[]> converter;
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
        this.converter = new ObjectArrayConverter<>( createConverters( dataFetcher.getDataFormat() ) );
    }

    private static <Source> Converter<Source, ?>[] createConverters(DataFormat dataFormat) {
        List<DataFormatField> fields = dataFormat.getFields();
        Converter<Source, ?>[] converters = new Converter[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            DataFormatField field = fields.get(i);
            DataFormat fieldFormat = field.getFormat();
            Converter<?, ?> converter;
            if ( fieldFormat instanceof MapDataFormat ) {
                MapDataFormat mapFormat = (MapDataFormat) fieldFormat;
                converter = new MapConverter<>(
                        field.getAccessor(),
                        createConverter( mapFormat.getKeyFormat() ),
                        createConverter( mapFormat.getElementFormat() )
                );
            } else if ( fieldFormat instanceof CollectionDataFormat ) {
                CollectionDataFormat collectionFormat = (CollectionDataFormat) fieldFormat;
                converter = new CollectionConverter<>(
                        field.getAccessor(),
                        createConverter( collectionFormat.getElementFormat() )
                );
            } else {
                converter = new AccessorConverter<>( field.getAccessor() );
            }
            //noinspection unchecked
            converters[i] = (Converter<Source, ?>) converter;
        }
        return converters;
    }

    private static <SourceType, TargetType> Converter<SourceType, TargetType> createConverter(DataFormat dataFormat) {
        List<DataFormatField> fields = dataFormat.getFields();
        //noinspection unchecked
        return fields.isEmpty()
                ? null
                : (Converter<SourceType, TargetType>) new ObjectArrayConverter<>( createConverters( dataFormat ) );
    }

//    private static <SourceType> Converter<SourceType, Object[]> arrayConverter(Converter[] converters, @Nullable int[] fields) {
//        if (fields == null) {
//            return new ObjectArrayConverter<>( null, converters );
//        } else {
//            Converter[] fieldConverters = new Converter[fields.length];
//            for ( int i = 0; i < fields.length; i++ ) {
//                fieldConverters[i] = converters[fields[i]];
//            }
//            return new ObjectArrayConverter<>( null, fieldConverters );
//        }
//    }

    public DataFetcher<T> getDataFetcher() {
        return dataFetcher;
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        if (rowType == null) {
            rowType = deduceRowType( (JavaTypeFactory) typeFactory, dataFetcher.getDataFormat());
        }
        return rowType;
    }

    @Override
    public RelNode toRel(RelOptTable.ToRelContext context, RelOptTable table) {
        return EnumerableTableScan.create( context.getCluster(), table );
    }

    private static RelDataType deduceRowType(JavaTypeFactory typeFactory, DataFormat format) {
        final List<DataFormatField> fields = format.getFields();
        final List<RelDataType> types = new ArrayList<>(fields.size());
        final List<String> names = new ArrayList<>(fields.size());
        for ( DataFormatField field : fields ) {
            names.add(field.getName());
            types.add(deduceType( typeFactory, field.getFormat()));
        }
        return typeFactory.createStructType(Pair.zip( names, types));
    }

    private static RelDataType deduceType(JavaTypeFactory typeFactory, DataFormat format) {
        if ( format instanceof MapDataFormat ) {
            MapDataFormat mapFormat = (MapDataFormat) format;
            RelDataType keyRelDataType = deduceType( typeFactory, mapFormat.getKeyFormat() );
            RelDataType elementRelDataType = deduceType( typeFactory, mapFormat.getElementFormat() );
            return typeFactory.createTypeWithNullability( typeFactory.createMapType( keyRelDataType, elementRelDataType ), true );
        } else if (format instanceof CollectionDataFormat ) {
            CollectionDataFormat collectionFormat = (CollectionDataFormat) format;
            RelDataType elementRelDataType = deduceType( typeFactory, collectionFormat.getElementFormat() );
            return typeFactory.createTypeWithNullability( typeFactory.createArrayType( elementRelDataType, -1L ), true );
        } else if (!format.getFields().isEmpty()) {
            return typeFactory.createTypeWithNullability( deduceRowType( typeFactory, format ), true );
        } else {
            return typeFactory.createJavaType( (Class<?>) format.getType() );
        }
    }

    public List<? extends T> getData(DataContext dataContext) {
        final DataFetchContext dataFetchContext = dataContextSupplier.get();
        QuerySession session = dataFetchContext.getSession();
        List<? extends T> objects = session.get( tableClass );
        if ( objects == null ) {
            try {
                objects = dataFetcher.fetch( dataFetchContext );
            } catch (DataFetcherException e) {
                objects = Collections.emptyList();
            }
            session.put( tableClass, objects );
        }
        return objects;
    }

    @Override
    public Enumerable<Object[]> scan(DataContext root) {
        final DataFetchContext dataFetchContext = dataContextSupplier.get();
        return new AbstractEnumerable<>() {
            @Override
            public Enumerator<Object[]> enumerator() {
                QuerySession session = dataFetchContext.getSession();
                List<? extends T> objects = session.get( tableClass );
                if ( objects == null ) {
                    try {
                        objects = dataFetcher.fetch( dataFetchContext );
                    } catch (DataFetcherException e) {
                        objects = Collections.emptyList();
                    }
                    session.put( tableClass, objects );
                }
                return new ConverterListEnumerator( objects, converter);
            }
        };
    }
}
