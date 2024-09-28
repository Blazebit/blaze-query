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
package com.blazebit.query.app;

import java.sql.Connection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.blazebit.query.QuerySession;
import com.blazebit.query.impl.QueryContextImpl;
import com.blazebit.query.impl.QuerySessionImpl;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.PropertyProvider;
import org.apache.calcite.avatica.Meta;
import org.apache.calcite.avatica.remote.LocalService;

public class BlazeQueryService extends LocalService implements PropertyProvider<Supplier<DataFetchContext>> {

    private final ThreadLocal<QuerySession> threadLocalSession = new ThreadLocal<>();
    QueryContextImpl queryContext;

    public BlazeQueryService(Meta meta) {
        super( meta );
    }

    @Override
    public Supplier<DataFetchContext> provide(DataFetchContext context) {
        return () -> new DataFetchContext() {
            @Override
            public <T> T findProperty(String key) {
                return context.findProperty(key);
            }

            @Override
            public QuerySession getSession() {
                return threadLocalSession.get();
            }
        };
    }

    private QuerySession createQuerySession() {
        return new QuerySessionImpl( queryContext, Map.of() ) {
            @Override
            public Connection connection() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private <IN, OUT> OUT intercept(Function<IN, OUT> function, IN in, String connectionId) {
        try {
            threadLocalSession.set(createQuerySession());
            return function.apply(in);
        } finally {
            threadLocalSession.remove();
        }
    }

    @Override
    public ResultSetResponse apply(CatalogsRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public ResultSetResponse apply(SchemasRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public ResultSetResponse apply(TablesRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public ResultSetResponse apply(TableTypesRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public ResultSetResponse apply(TypeInfoRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public ResultSetResponse apply(ColumnsRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public PrepareResponse apply(PrepareRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public ExecuteResponse apply(PrepareAndExecuteRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public FetchResponse apply(FetchRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public ExecuteResponse apply(ExecuteRequest request) {
        return intercept( super::apply, request, request.statementHandle.connectionId );
    }

    @Override
    public CreateStatementResponse apply(CreateStatementRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public CloseStatementResponse apply(CloseStatementRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public OpenConnectionResponse apply(OpenConnectionRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public CloseConnectionResponse apply(CloseConnectionRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public ConnectionSyncResponse apply(ConnectionSyncRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public DatabasePropertyResponse apply(DatabasePropertyRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public SyncResultsResponse apply(SyncResultsRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public CommitResponse apply(CommitRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public RollbackResponse apply(RollbackRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public ExecuteBatchResponse apply(PrepareAndExecuteBatchRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }

    @Override
    public ExecuteBatchResponse apply(ExecuteBatchRequest request) {
        return intercept( super::apply, request, request.connectionId );
    }
}
