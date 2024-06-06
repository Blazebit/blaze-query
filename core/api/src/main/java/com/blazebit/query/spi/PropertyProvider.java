package com.blazebit.query.spi;

import com.blazebit.query.QuerySession;

/**
 * Dynamic provider to provide properties to a {@link DataFetchContext}.
 *
 * @author Max Hovens
 * @since 1.0.0
 */
@FunctionalInterface
public interface PropertyProvider {

  /**
   * Provides the Object. An {@link DataFetchContext} is supplied for the provider to use properties
   * from the context.
   *
   * @param context the {@link DataFetchContext} from the current {@link QuerySession}
   * @return the object to provide
   */
    Object provide(DataFetchContext context);
}
