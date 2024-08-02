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

import java.lang.reflect.Method;
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

import com.blazebit.query.connector.base.FieldFieldAccessor;
import com.blazebit.query.connector.base.MethodFieldAccessor;
import com.blazebit.query.impl.calcite.converter.Converter;
import com.blazebit.query.impl.calcite.converter.DurationConverter;
import com.blazebit.query.impl.calcite.converter.EnumConverter;
import com.blazebit.query.impl.calcite.converter.EnumToStringConverter;
import com.blazebit.query.impl.calcite.converter.InstantConverter;
import com.blazebit.query.impl.calcite.converter.LocalDateConverter;
import com.blazebit.query.impl.calcite.converter.LocalDateTimeConverter;
import com.blazebit.query.impl.calcite.converter.LocalTimeConverter;
import com.blazebit.query.impl.calcite.converter.OffsetDateTimeConverter;
import com.blazebit.query.impl.calcite.converter.OffsetTimeConverter;
import com.blazebit.query.impl.calcite.converter.PeriodConverter;
import com.blazebit.query.impl.calcite.converter.ZonedDateTimeConverter;
import com.blazebit.query.spi.CollectionDataFormat;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.DataFormatField;
import com.blazebit.query.spi.DataFormatFieldAccessor;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRel;
import org.apache.calcite.adapter.enumerable.EnumerableRelImplementor;
import org.apache.calcite.adapter.enumerable.JavaRowFormat;
import org.apache.calcite.adapter.enumerable.PhysType;
import org.apache.calcite.adapter.enumerable.PhysTypeImpl;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.linq4j.function.Function1;
import org.apache.calcite.linq4j.tree.BlockBuilder;
import org.apache.calcite.linq4j.tree.BlockStatement;
import org.apache.calcite.linq4j.tree.ConstantUntypedNull;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.MethodCallExpression;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Statement;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.linq4j.tree.UnaryExpression;
import org.apache.calcite.plan.DeriveMode;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelCollationTraitDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.util.BuiltInMethod;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * {@link TableScan} implementation based on a {@link DataFetcher}.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class EnumerableTableScan extends TableScan implements EnumerableRel {
    private static final Method GET_DATA;
    private static final Map<Class<?>, Class<? extends Converter<?, ?>>> CONVERTERS;

    static {
        try {
            GET_DATA = DataFetcherTable.class.getMethod( "getData", DataContext.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException( e );
        }
        Map<Class<?>, Class<? extends Converter<?, ?>>> converters = new HashMap<>();
        converters.put(Instant.class, InstantConverter.class);
        converters.put(ZonedDateTime.class, ZonedDateTimeConverter.class);
        converters.put(OffsetDateTime.class, OffsetDateTimeConverter.class);
        converters.put(OffsetTime.class, OffsetTimeConverter.class);
        converters.put(LocalDate.class, LocalDateConverter.class);
        converters.put(LocalDateTime.class, LocalDateTimeConverter.class);
        converters.put(LocalTime.class, LocalTimeConverter.class);
        converters.put(Duration.class, DurationConverter.class);
        converters.put(Period.class, PeriodConverter.class);
        CONVERTERS = converters;
    }

    private final DataFormat elementType;

    public EnumerableTableScan(RelOptCluster cluster, RelTraitSet traitSet, RelOptTable table, DataFormat elementType) {
        super( cluster, traitSet, ImmutableList.of(), table );
        assert getConvention() instanceof EnumerableConvention;
        this.elementType = elementType;
    }

    @Override
    public @Nullable RelNode passThrough(final RelTraitSet required) {
        return null;
    }

    @Override
    public DeriveMode getDeriveMode() {
        return DeriveMode.PROHIBITED;
    }

    public static EnumerableTableScan create(
            RelOptCluster cluster,
            RelOptTable relOptTable) {
        final DataFetcherTable<?> table = relOptTable.unwrap(DataFetcherTable.class);
        final RelTraitSet traitSet =
                cluster.traitSetOf(EnumerableConvention.INSTANCE)
                        .replaceIfs(RelCollationTraitDef.INSTANCE, () -> {
                            if (table != null) {
                                return table.getStatistic().getCollations();
                            }
                            return ImmutableList.of();
                        } );
        return new EnumerableTableScan(cluster, traitSet, relOptTable, table.getDataFetcher().getDataFormat());
    }

    private Expression toRow(DataFormat dataFormat, PhysType physType, Expression expression, SimpleBlockBuilder blockBuilder) {
        int fieldCount = physType.getRowType().getFieldCount();
        List<RelDataTypeField> fieldList = physType.getRowType().getFieldList();
        List<DataFormatField> fields = dataFormat.getFields();
        List<Expression> expressionList = new ArrayList<>(fieldCount);
        for (int i = 0; i < fieldCount; i++) {
            expressionList.add(fieldExpression(expression, fieldList.get(i).getType(), fields.get(i), blockBuilder));
        }
        return physType.record(expressionList);
    }

    private Expression fieldExpression(
            Expression row,
            RelDataType relFieldType,
            DataFormatField dataFormatField,
            SimpleBlockBuilder blockBuilder) {
        DataFormatFieldAccessor accessor = dataFormatField.getAccessor();
        final Expression e;
        if (accessor instanceof MethodFieldAccessor) {
            MethodFieldAccessor methodAccessor = (MethodFieldAccessor) accessor;
            e = Expressions.call(row, methodAccessor.getMethod());
        } else if (accessor instanceof FieldFieldAccessor) {
            FieldFieldAccessor fieldAccessor = (FieldFieldAccessor) accessor;
            e = Expressions.field(row, fieldAccessor.getField());
        } else {
            throw new IllegalArgumentException("Unsupported field accessor: " + accessor);
        }
        Class<?> converter;
        if (dataFormatField.getFormat().isEnum()) {
            final Type type = dataFormatField.getFormat().getType();
            converter = type instanceof Class<?> && ( (Class<?>) type ).isEnum() ? EnumConverter.class : EnumToStringConverter.class;
        } else {
            converter = CONVERTERS.get(rawClass(dataFormatField.getFormat().getType()));
        }
        final Expression e0;
        if (converter == null) {
            e0 = e;
        } else if (converter == EnumConverter.class) {
            e0 = Expressions.call(Expressions.field( null, converter, "INSTANCE" ), "convert", Expressions.convert_(e, Enum.class));
        } else if (converter == EnumToStringConverter.class) {
            e0 = Expressions.call(Expressions.field( null, converter, "INSTANCE" ), "convert", Expressions.convert_(e, Object.class));
        } else {
            e0 = Expressions.call(Expressions.field( null, converter, "INSTANCE" ), "convert", e);
        }
        switch (relFieldType.getSqlTypeName()) {
            case ARRAY:
            case MULTISET:
                final RelDataType fieldType = requireNonNull(relFieldType.getComponentType(), () -> "relFieldType.getComponentType() for " + relFieldType);
                if (fieldType.isStruct()) {
                    // We can't represent a multiset or array as a List<Employee>, because
                    // the consumer does not know the element type.
                    // The standard element type is List.
                    // We need to convert to a List<List>.
                    final JavaTypeFactory typeFactory = (JavaTypeFactory) getCluster().getTypeFactory();
                    // Don't optimize the type since the JDBC driver expects to find Object[]
                    final PhysType elementPhysType = PhysTypeImpl.of(typeFactory, fieldType, JavaRowFormat.ARRAY, false);
                    ParameterExpression localVar = blockBuilder.createLocalVariable(dataFormatField.getFormat().getType());
                    blockBuilder.add(Expressions.declare(0, localVar, e));

                    ParameterExpression resultVar = blockBuilder.createLocalVariable(List.class);
                    blockBuilder.add(Expressions.declare(0, resultVar, null));

                    Expression e1 = Expressions.convert_(localVar, Iterable.class);
                    MethodCallExpression e2 = Expressions.call(BuiltInMethod.AS_ENUMERABLE2.method, e1);
                    SimpleBlockBuilder subBlockBuilder = new SimpleBlockBuilder(blockBuilder);
                    Expression e3 = toList((CollectionDataFormat) dataFormatField.getFormat(), elementPhysType, e2, subBlockBuilder);
                    subBlockBuilder.add(Expressions.statement(Expressions.assign(resultVar, e3)));

                    blockBuilder.add( Expressions.ifThenElse(
                            Expressions.equal(localVar, ConstantUntypedNull.INSTANCE),
                            Expressions.statement(Expressions.assign(resultVar, Expressions.constant(null))),
                            subBlockBuilder.toBlock()
                    ));
                    return resultVar;
                } else {
                    return e0;
                }
            default:
                if (relFieldType.isStruct()) {
                    final JavaTypeFactory typeFactory = (JavaTypeFactory) getCluster().getTypeFactory();
                    // Don't optimize the type since the JDBC driver expects to find Object[]
                    final PhysType elementPhysType = PhysTypeImpl.of(typeFactory, relFieldType, JavaRowFormat.ARRAY, false);
                    ParameterExpression localVar = blockBuilder.createLocalVariable(dataFormatField.getFormat().getType());
                    blockBuilder.add(Expressions.declare(0, localVar, e));

                    SimpleBlockBuilder subBlockBuilder = new SimpleBlockBuilder(blockBuilder);
                    Expression e1 = toRow(dataFormatField.getFormat(), elementPhysType, localVar, subBlockBuilder);
                    ParameterExpression resultVar = blockBuilder.createLocalVariable(e1.getType());
                    blockBuilder.add(Expressions.declare(0, resultVar, null));
                    subBlockBuilder.add(Expressions.statement(Expressions.assign(resultVar, e1)));

                    blockBuilder.add( Expressions.ifThenElse(
                            Expressions.equal(localVar, ConstantUntypedNull.INSTANCE),
                            Expressions.statement(Expressions.assign(resultVar, Expressions.constant(null))),
                            subBlockBuilder.toBlock()
                    ));
                    return resultVar;
                }
                return e0;
        }
    }

    private Expression toList(
            CollectionDataFormat dataFormat,
            PhysType elementPhysType,
            Expression expression,
            SimpleBlockBuilder blockBuilder) {
        final DataFormat elementFormat = dataFormat.getElementFormat();
        final ParameterExpression o = Expressions.parameter(elementFormat.getType(), "o");
        SimpleBlockBuilder subBlockBuilder = new SimpleBlockBuilder(blockBuilder);
        subBlockBuilder.add(toRow(elementFormat, elementPhysType, o, subBlockBuilder));
        final Expression selector = Expressions.lambda(Function1.class, subBlockBuilder.toBlock(), List.of(o));
        return Expressions.call(
                Expressions.call(expression, BuiltInMethod.SELECT.method, selector),
                BuiltInMethod.ENUMERABLE_TO_LIST.method
        );
    }

    @Override
    public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
        return new EnumerableTableScan(getCluster(), traitSet, table, elementType);
    }

    @Override
    public Result implement(EnumerableRelImplementor implementor, Prefer pref) {
        // Note that representation is ARRAY. This assumes that the table
        // returns a Object[] for each record. Actually a Table<T> can
        // return any type T. And, if it is a JdbcTable, we'd like to be
        // able to generate alternate accessors that return e.g. synthetic
        // records {T0 f0; T1 f1; ...} and don't box every primitive value.
        final PhysType physType =
                PhysTypeImpl.of(
                        implementor.getTypeFactory(),
                        getRowType(),
                        JavaRowFormat.ARRAY
                );

        final Expression expression = table.getExpression(Queryable.class);
        if (expression == null) {
            throw new IllegalStateException(
                    "Unable to implement " + RelOptUtil.toString(this, SqlExplainLevel.ALL_ATTRIBUTES)
                            + ": " + table + ".getExpression(Queryable.class) returned null" );
        }
        MethodCallExpression enumerableCall = (MethodCallExpression) expression;
        Expression tableExpression = ((UnaryExpression) enumerableCall.expressions.get(0)).expression;
        Expression dataFetcherTableExpression = Expressions.convert_(tableExpression, DataFetcherTable.class);
        MethodCallExpression dataExpression = Expressions.call(
                dataFetcherTableExpression,
                GET_DATA,
                enumerableCall.expressions.get(1)
        );
        Expression enumerableExpression = Expressions.call(BuiltInMethod.AS_ENUMERABLE2.method, dataExpression);
        assert Types.isAssignableFrom(Enumerable.class, enumerableExpression.getType());
        final ParameterExpression row = Expressions.parameter(elementType.getType(), "row");

        SimpleBlockBuilder simpleBlockBuilder = new SimpleBlockBuilder();
        Expression result = toRow(elementType, physType, row, simpleBlockBuilder);
        simpleBlockBuilder.add(result);

        BlockBuilder blockBuilder = new BlockBuilder();
        blockBuilder.add(Expressions.call(
                enumerableExpression,
                BuiltInMethod.SELECT.method,
                Expressions.lambda(Function1.class, simpleBlockBuilder.toBlock(), row)
        ) );
        return implementor.result(physType, blockBuilder.toBlock());
    }

    private static Class<?> rawClass(Type type) {
        if (type instanceof ParameterizedType ) {
            type = ( (ParameterizedType) type ).getRawType();
        }
        if (!(type instanceof Class<?>)) {
            throw new IllegalArgumentException("Field type unsupported: " + type);
        }
        return (Class<?>) type;
    }

    private static final class SimpleBlockBuilder {
        private final SimpleBlockBuilder root;
        private final BlockBuilder blockBuilder;
        private int count;

        public SimpleBlockBuilder() {
            this.root = null;
            this.blockBuilder = new BlockBuilder();
        }

        public SimpleBlockBuilder(SimpleBlockBuilder parent) {
            if ( parent == null) {
                this.root = null;
            } else if (parent.root == null) {
                this.root = parent;
            } else {
                this.root = parent.root;
            }
            this.blockBuilder = new BlockBuilder();
        }

        public void add(Expression expression) {
            blockBuilder.add(expression);
        }

        public void add(Statement statement) {
            blockBuilder.add(statement);
        }

        public BlockStatement toBlock() {
            return blockBuilder.toBlock();
        }

        public ParameterExpression createLocalVariable(Type type) {
            return root == null ? Expressions.parameter(type, "l" + (count++)) : root.createLocalVariable(type);
        }
    }
}
