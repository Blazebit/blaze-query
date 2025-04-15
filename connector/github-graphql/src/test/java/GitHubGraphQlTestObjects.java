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
				rulesets(),
				branchProtectionRules());
	}

	public static List<GitHubRuleset> rulesets() {
		GitHubRule.RuleParameters prParams = new GitHubRule.RuleParameters(true, 0);
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
				new GitHubBranchProtectionRule("BPR_anotherId321", false, true, List.of(matchingRef));

		return List.of(rule);
	}

	public static GitHubOrganization organization() {
		return new GitHubOrganization("org_123", "My Organization", true);
	}

}
