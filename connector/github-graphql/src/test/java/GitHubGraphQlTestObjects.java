/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.connector.github.graphql.GitHubBranchProtectionRule;
import com.blazebit.query.connector.github.graphql.GitHubOrganization;
import com.blazebit.query.connector.github.graphql.GitHubPullRequest;
import com.blazebit.query.connector.github.graphql.GitHubRepository;
import com.blazebit.query.connector.github.graphql.GitHubRule;
import com.blazebit.query.connector.github.graphql.GitHubRuleset;

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
				GitHubRepository.Visibility.INTERNAL,
				OffsetDateTime.of(2025, 1, 1, 9, 30, 0, 0, ZoneOffset.UTC),
				new GitHubRepository.DefaultBranch("REF_someId123", "main"),
				new GitHubRepository.Owner("org_123", "My Organization",
						GitHubRepository.OwnerType.ORGANIZATION),
				pullRequests(),
				rulesets(),
				branchProtectionRules());
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
		GitHubRuleset.RulesetConditions conditions1 = new GitHubRuleset.RulesetConditions(List.of());
		GitHubRuleset ruleset =
				new GitHubRuleset("BRANCH", "ACTIVE", conditions1, List.of(pullRequestRule));
		return List.of(ruleset);
	}

	public static List<GitHubBranchProtectionRule> branchProtectionRules() {
		GitHubBranchProtectionRule.MatchingRef matchingRef =
				new GitHubBranchProtectionRule.MatchingRef("REF_someId123", "main");
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
						List.of(matchingRef));

		return List.of(rule);
	}

	public static List<GitHubPullRequest> pullRequests(){
		GitHubPullRequest.Ref baseRef = new GitHubPullRequest.Ref( "REF_someId123", "main" );

		GitHubPullRequest pullRequest = new GitHubPullRequest(
				"PR_someId456",
				true,
				OffsetDateTime.of( 2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC ),
				true,
				GitHubPullRequest.State.MERGED,
				GitHubPullRequest.ReviewDecision.APPROVED,
				baseRef
		);
		return List.of( pullRequest );
	}

	public static GitHubOrganization organization() {
		return new GitHubOrganization("org_123", "My Organization", true, rulesets());
	}

}
