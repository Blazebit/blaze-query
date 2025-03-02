/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.connector.gitlab.GitlabGroup;
import com.blazebit.query.connector.gitlab.GitlabProject;
import com.blazebit.query.connector.gitlab.GitlabSchemaProvider;
import com.blazebit.query.connector.gitlab.GitlabUser;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public class GitlabGraphQlProviderTest {
	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new GitlabSchemaProvider() );
		builder.registerSchemaObjectAlias( GitlabUser.class, "GitlabGraphQlUser" );
		builder.registerSchemaObjectAlias( GitlabGroup.class, "GitlabGraphQlGroup" );
		builder.registerSchemaObjectAlias( GitlabProject.class, "GitlabGraphQlProject" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_user() {
		try (var session = CONTEXT.createSession()) {
			session.put( GitlabUser.class, Collections.singletonList( TestObjects.user() ) );

			var typedQuery =
					session.createQuery( "select u.* from GitlabGraphQlUser u", new TypeReference<Map<String, Object>>() {} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

}
