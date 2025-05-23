/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.connector.github.graphql.GitHubBranchProtectionRule;
import com.blazebit.query.connector.github.graphql.GitHubBranchProtectionRuleMatchingRef;
import com.blazebit.query.connector.github.graphql.GitHubBranchRef;
import com.blazebit.query.connector.github.graphql.GitHubOrganization;
import com.blazebit.query.connector.github.graphql.GitHubPullRequest;
import com.blazebit.query.connector.github.graphql.GitHubPullRequestReviewDecision;
import com.blazebit.query.connector.github.graphql.GitHubPullRequestState;
import com.blazebit.query.connector.github.graphql.GitHubRepository;
import com.blazebit.query.connector.github.graphql.GitHubRepositoryMinimal;
import com.blazebit.query.connector.github.graphql.GitHubRepositoryOwner;
import com.blazebit.query.connector.github.graphql.GitHubRepositoryOwnerType;
import com.blazebit.query.connector.github.graphql.GitHubRepositoryVisibility;
import com.blazebit.query.connector.github.graphql.GitHubRule;
import com.blazebit.query.connector.github.graphql.GitHubRuleset;
import com.blazebit.query.connector.github.graphql.GitHubRulesetCondition;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public class GitHubGraphQlTestObjects {
	private GitHubGraphQlTestObjects() {
	}

	public static GitHubRepository repository() {
		return new GitHubRepository(
				"123",
				"My Repository",
				"Description of my repository",
				false,
				false,
				true,
				true,
				false,
				true,
				GitHubRepositoryVisibility.INTERNAL,
				OffsetDateTime.of(2025, 1, 1, 9, 30, 0, 0, ZoneOffset.UTC),
				new GitHubBranchRef("REF_someId123", "main"),
				new GitHubRepositoryOwner("org_123", "My Organization",
						GitHubRepositoryOwnerType.ORGANIZATION));
	}


	public static List<GitHubRuleset> rulesets() {
		GitHubRule.PullRequestParameters pullRequestParameters =
				new GitHubRule.PullRequestParameters(
						false,
						0,
						false,
						false,
						false,
						false
				);

		GitHubRule pullRequestRule = new GitHubRule("PULL_REQUEST", pullRequestParameters, null);
		GitHubRulesetCondition condition1 = new GitHubRulesetCondition(List.of());
		GitHubRuleset ruleset =
				new GitHubRuleset("BRANCH", "ACTIVE", condition1, List.of(pullRequestRule));
		return List.of(ruleset);
	}

	public static List<GitHubBranchProtectionRule> branchProtectionRules() {
		GitHubBranchProtectionRuleMatchingRef matchingRef =
				new GitHubBranchProtectionRuleMatchingRef("REF_someId123", "main");
		GitHubRepositoryMinimal repository = new GitHubRepositoryMinimal("123", "My Repository");

		GitHubBranchProtectionRule rule =
				new GitHubBranchProtectionRule(
						"BPR_anotherId321",
						false,
						true,
						false,
						true,
						true,
						1,
						true,
						true,
						true,
						true,
						true,
						true,
						true,
						repository,
						List.of(matchingRef));

		return List.of(rule);
	}

	public static List<GitHubPullRequest> pullRequests(){
		GitHubBranchRef baseRef = new GitHubBranchRef( "REF_someId123", "main" );
		GitHubRepositoryMinimal repository = new GitHubRepositoryMinimal( "123", "My Repository" );

		GitHubPullRequest pullRequest = new GitHubPullRequest(
				"PR_someId456",
				"Refactor the function",
				OffsetDateTime.of( 2024, 1, 1, 11, 0, 0, 0, ZoneOffset.UTC ),
				true,
				OffsetDateTime.of( 2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC ),
				true,
				OffsetDateTime.of( 2024, 1, 2, 12, 0, 0, 0, ZoneOffset.UTC ),
				GitHubPullRequestState.MERGED,
				GitHubPullRequestReviewDecision.APPROVED,
				repository,
				baseRef
		);
		return List.of( pullRequest );
	}

	public static GitHubOrganization organization() {
		return new GitHubOrganization("org_123", "My Organization", true, rulesets());
	}

}
