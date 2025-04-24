/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.connector.github.graphql.GitHubBranchProtectionRule;
import com.blazebit.query.connector.github.graphql.GitHubOrganization;
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
				rulesets(),
				branchProtectionRules());
	}


	/**
	 * Example ruleset used for testing.
	 *
	 * <p>GitHub's GraphQL API exposes rulesets with a mix of parameterized and non-parameterized types.
	 * For example, rules like {@code PULL_REQUEST} expose detailed boolean parameters such as:
	 *
	 * <ul>
	 *   <li>{@code requireCodeOwnerReview}</li>
	 *   <li>{@code dismissStaleReviewsOnPush}</li>
	 *   <li>{@code requiredReviewThreadResolution}</li>
	 * </ul>
	 *
	 * <p>However, other rules like {@code REQUIRED_SIGNATURES} or {@code NON_FAST_FORWARD} appear only by type name
	 * if enabled and are omitted entirely if disabled. These rules do not include parameters.
	 */
	public static List<GitHubRuleset> rulesets() {
		GitHubRule.RuleParameters prParams = new GitHubRule.RuleParameters(
				true,
				0,
				false,
				true,
				true,
				true,
				true
		);
		GitHubRule pullRequestRule = new GitHubRule("PULL_REQUEST", prParams);

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

	public static GitHubOrganization organization() {
		return new GitHubOrganization("org_123", "My Organization", true);
	}

}
