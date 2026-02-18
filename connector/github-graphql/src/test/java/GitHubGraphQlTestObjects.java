/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.connector.github.graphql.GitHubBranchProtectionRule;
import com.blazebit.query.connector.github.graphql.GitHubBranchProtectionRuleMatchingRef;
import com.blazebit.query.connector.github.graphql.GitHubRef;
import com.blazebit.query.connector.github.graphql.GitHubOrganization;
import com.blazebit.query.connector.github.graphql.GitHubPullRequest;
import com.blazebit.query.connector.github.graphql.GitHubRulePullRequestParameters;
import com.blazebit.query.connector.github.graphql.GitHubPullRequestReviewDecision;
import com.blazebit.query.connector.github.graphql.GitHubPullRequestState;
import com.blazebit.query.connector.github.graphql.GitHubRepository;
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

	public static GitHubOrganization organization() {
		return new GitHubOrganization("org_123", "My Organization", true);
	}

	public static GitHubRef branch() {
		return new GitHubRef("br_123", "main");
	}

	public static GitHubBranchProtectionRuleMatchingRef branchProtectionRuleMatchingRef() {
		return new GitHubBranchProtectionRuleMatchingRef( branch().id(), branch().name() );
	}

	public static GitHubRulesetCondition rulesetCondition() {
		return new GitHubRulesetCondition(List.of("main"), List.of("dev"), List.of(
				repository().id()), List.of( repository().name()), List.of());
	}

	public static GitHubRule pullRequestRule() {
		GitHubRulePullRequestParameters pullRequestParameters =
				new GitHubRulePullRequestParameters(
						false,
						0,
						false,
						false,
						false
				);

		return new GitHubRule("PULL_REQUEST", pullRequestParameters, null, null);
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
				false,
				true,
				GitHubRepositoryVisibility.INTERNAL,
				OffsetDateTime.of(2025, 1, 1, 9, 30, 0, 0, ZoneOffset.UTC),
				branch(),
				new GitHubRepositoryOwner( organization().id(), organization().name(),
						GitHubRepositoryOwnerType.ORGANIZATION));
	}


	public static List<GitHubRuleset> rulesets() {
		GitHubRuleset ruleset =
				new GitHubRuleset("rs_123","BRANCH", "ACTIVE", rulesetCondition(), repository().id(),null, null, List.of(pullRequestRule()));

		return List.of(ruleset);
	}

	public static List<GitHubBranchProtectionRule> branchProtectionRules() {
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
						repository().id(),
						List.of(branchProtectionRuleMatchingRef()));

		return List.of(rule);
	}

	public static List<GitHubPullRequest> pullRequests(){
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
				repository().id(),
				branch()
		);
		return List.of( pullRequest );
	}
}
