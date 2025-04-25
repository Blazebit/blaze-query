/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.connector.github.graphql.GitHubOrganization;
import com.blazebit.query.connector.github.graphql.GitHubRepository;
import com.blazebit.query.connector.github.graphql.GitHubSchemaProvider;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public class GitHubGraphQlQueryTest {
	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new GitHubSchemaProvider() );
		builder.registerSchemaObjectAlias( GitHubRepository.class, "GitHubRepository" );
		builder.registerSchemaObjectAlias( GitHubOrganization.class, "GitHubOrganization" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_github_repository() {
		try (var session = CONTEXT.createSession()) {
			GitHubRepository repository = GitHubGraphQlTestObjects.repository();
			session.put(
					GitHubRepository.class, List.of(repository));

			var typedQuery =
					session.createQuery( "select r.* from GitHubRepository r", new TypeReference<Map<String, Object>>() {});

			assertThat(typedQuery.getResultList()).extracting(result -> result.get("id")).containsExactly( repository.id());
		}
	}

	@Test
	void should_return_github_organization() {
		try (var session = CONTEXT.createSession()) {
			GitHubOrganization organization = GitHubGraphQlTestObjects.organization();
			session.put(
					GitHubOrganization.class, List.of(organization));

			var typedQuery =
					session.createQuery( "select o.* from GitHubOrganization o", new TypeReference<Map<String, Object>>() {});

			assertThat(typedQuery.getResultList()).extracting(result -> result.get("id")).containsExactly( organization.id());
		}
	}
}
