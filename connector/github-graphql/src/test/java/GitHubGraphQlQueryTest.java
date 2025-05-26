/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.connector.github.graphql.GitHubBranchProtectionRule;
import com.blazebit.query.connector.github.graphql.GitHubOrganization;
import com.blazebit.query.connector.github.graphql.GitHubPullRequest;
import com.blazebit.query.connector.github.graphql.GitHubRepository;
import com.blazebit.query.connector.github.graphql.GitHubRuleset;
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
		builder.registerSchemaObjectAlias( GitHubRuleset.class, "GitHubRuleset" );
		builder.registerSchemaObjectAlias( GitHubPullRequest.class, "GitHubPullRequest" );
		builder.registerSchemaObjectAlias( GitHubBranchProtectionRule.class, "GitHubBranchProtectionRule" );
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

	@Test
	void should_return_github_ruleset() {
		try (var session = CONTEXT.createSession()) {
			GitHubRuleset ruleset = GitHubGraphQlTestObjects.rulesets().get(0);
			session.put(
					GitHubRuleset.class, List.of(ruleset));

			var typedQuery =
					session.createQuery( "select r.* from GitHubRuleset r", new TypeReference<Map<String, Object>>() {});

			assertThat(typedQuery.getResultList()).extracting(result -> result.get("id")).containsExactly( ruleset.id());
		}
	}

	@Test
	void should_return_github_pull_request() {
		try (var session = CONTEXT.createSession()) {
			GitHubPullRequest pullRequest = GitHubGraphQlTestObjects.pullRequests().get( 0 );
			session.put(
					GitHubPullRequest.class, List.of(pullRequest));

			var typedQuery =
					session.createQuery( "select p.* from GitHubPullRequest p", new TypeReference<Map<String, Object>>() {});

			assertThat(typedQuery.getResultList()).extracting(result -> result.get("id")).containsExactly( pullRequest.id());
		}
	}

	@Test
	void should_return_github_branch_protection_rule() {
		try (var session = CONTEXT.createSession()) {
			GitHubBranchProtectionRule branchProtectionRule = GitHubGraphQlTestObjects.branchProtectionRules().get( 0 );
			session.put(
					GitHubBranchProtectionRule.class, List.of(branchProtectionRule));

			var typedQuery =
					session.createQuery( "select b.* from GitHubBranchProtectionRule b", new TypeReference<Map<String, Object>>() {});

			assertThat(typedQuery.getResultList()).extracting(result -> result.get("id")).containsExactly( branchProtectionRule.id());
		}
	}
}
