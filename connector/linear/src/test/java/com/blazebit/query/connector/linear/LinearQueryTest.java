/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LinearQueryTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new LinearSchemaProvider() );
		builder.registerSchemaObjectAlias( LinearIssue.class, "LinearIssue" );
		builder.registerSchemaObjectAlias( LinearUser.class, "LinearUser" );
		builder.registerSchemaObjectAlias( LinearTeam.class, "LinearTeam" );
		builder.registerSchemaObjectAlias( LinearWorkflowState.class, "LinearWorkflowState" );
		builder.registerSchemaObjectAlias( LinearIssueLabel.class, "LinearIssueLabel" );
		builder.registerSchemaObjectAlias( LinearProject.class, "LinearProject" );
		builder.registerSchemaObjectAlias( LinearCycle.class, "LinearCycle" );
		CONTEXT = builder.build();
	}

	// ---- Issues ----

	private static LinearIssue urgentIssue() {
		return new LinearIssue(
				"issue-001", "SEC-1", "Exposed S3 bucket with public read access",
				1, "Urgent",
				OffsetDateTime.parse( "2026-01-01T09:00:00Z" ),
				OffsetDateTime.parse( "2026-01-02T09:00:00Z" ),
				"2026-01-10", null, null,
				"https://linear.app/org/issue/SEC-1",
				new LinearIssue.StateRef( "state-1", "In Progress", "started" ),
				new LinearIssue.TeamRef( "team-1", "Security", "SEC" ),
				new LinearIssue.UserRef( "user-1", "Alice", "alice@example.com" ),
				new LinearIssue.UserRef( "user-2", "Bob", "bob@example.com" ),
				List.of( new LinearIssue.LabelRef( "label-1", "security" ) ) );
	}

	private static LinearIssue lowIssue() {
		return new LinearIssue(
				"issue-002", "ENG-42", "Update dependency versions",
				4, "Low",
				OffsetDateTime.parse( "2026-02-01T09:00:00Z" ),
				OffsetDateTime.parse( "2026-02-01T10:00:00Z" ),
				null, null, null,
				"https://linear.app/org/issue/ENG-42",
				new LinearIssue.StateRef( "state-2", "Backlog", "backlog" ),
				new LinearIssue.TeamRef( "team-2", "Engineering", "ENG" ),
				null,
				new LinearIssue.UserRef( "user-2", "Bob", "bob@example.com" ),
				List.of() );
	}

	@Test
	void should_return_all_issues() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearIssue.class, List.of( urgentIssue(), lowIssue() ) );

			var result = session.createQuery(
					"SELECT i.id, i.identifier, i.priorityLabel FROM LinearIssue i",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_urgent_issues() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearIssue.class, List.of( urgentIssue(), lowIssue() ) );

			var result = session.createQuery(
					"SELECT i.id, i.title FROM LinearIssue i WHERE i.priority = 1",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "id" ) ).isEqualTo( "issue-001" );
		}
	}

	@Test
	void should_filter_issues_by_state_type() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearIssue.class, List.of( urgentIssue(), lowIssue() ) );

			var result = session.createQuery(
					"SELECT i.id FROM LinearIssue i WHERE i.state.type = 'started'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "id" ) ).isEqualTo( "issue-001" );
		}
	}

	@Test
	void should_filter_unassigned_issues() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearIssue.class, List.of( urgentIssue(), lowIssue() ) );

			var result = session.createQuery(
					"SELECT i.id, i.title FROM LinearIssue i WHERE i.assignee IS NULL",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "id" ) ).isEqualTo( "issue-002" );
		}
	}

	// ---- Users ----

	private static LinearUser activeAdminUser() {
		return new LinearUser( "user-1", "Alice", "Alice A.", "alice@example.com",
				true, true, false,
				OffsetDateTime.parse( "2025-01-01T00:00:00Z" ),
				OffsetDateTime.parse( "2026-01-01T00:00:00Z" ) );
	}

	private static LinearUser inactiveUser() {
		return new LinearUser( "user-2", "Bob", "Bob B.", "bob@example.com",
				false, false, false,
				OffsetDateTime.parse( "2025-06-01T00:00:00Z" ),
				OffsetDateTime.parse( "2026-02-01T00:00:00Z" ) );
	}

	@Test
	void should_filter_active_users() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearUser.class, List.of( activeAdminUser(), inactiveUser() ) );

			var result = session.createQuery(
					"SELECT u.id, u.email FROM LinearUser u WHERE u.active = TRUE",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "email" ) ).isEqualTo( "alice@example.com" );
		}
	}

	@Test
	void should_filter_admin_users() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearUser.class, List.of( activeAdminUser(), inactiveUser() ) );

			var result = session.createQuery(
					"SELECT u.id, u.name FROM LinearUser u WHERE u.admin = TRUE",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "Alice" );
		}
	}

	// ---- Teams ----

	private static LinearTeam publicTeam() {
		return new LinearTeam( "team-1", "Security", "SEC", "Security team", false,
				OffsetDateTime.parse( "2025-01-01T00:00:00Z" ),
				OffsetDateTime.parse( "2026-01-01T00:00:00Z" ) );
	}

	private static LinearTeam privateTeam() {
		return new LinearTeam( "team-2", "Executive", "EXEC", "Exec team", true,
				OffsetDateTime.parse( "2025-03-01T00:00:00Z" ),
				OffsetDateTime.parse( "2026-01-15T00:00:00Z" ) );
	}

	@Test
	void should_filter_private_teams() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearTeam.class, List.of( publicTeam(), privateTeam() ) );

			var result = session.createQuery(
					"SELECT t.id, t.name FROM LinearTeam t WHERE t.privateTeam = TRUE",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "Executive" );
		}
	}

	// ---- WorkflowStates ----

	private static LinearWorkflowState startedState() {
		return new LinearWorkflowState( "state-1", "In Progress", "started", "#F2C94C", 2.0,
				new LinearWorkflowState.TeamRef( "team-1", "Security", "SEC" ) );
	}

	private static LinearWorkflowState completedState() {
		return new LinearWorkflowState( "state-2", "Done", "completed", "#27AE60", 4.0,
				new LinearWorkflowState.TeamRef( "team-1", "Security", "SEC" ) );
	}

	@Test
	void should_filter_completed_workflow_states() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearWorkflowState.class, List.of( startedState(), completedState() ) );

			var result = session.createQuery(
					"SELECT s.id, s.name FROM LinearWorkflowState s WHERE s.type = 'completed'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "Done" );
		}
	}

	// ---- IssueLabels ----

	private static LinearIssueLabel securityLabel() {
		return new LinearIssueLabel( "label-1", "security", "#FF0000",
				OffsetDateTime.parse( "2025-01-01T00:00:00Z" ),
				OffsetDateTime.parse( "2026-01-01T00:00:00Z" ),
				new LinearIssueLabel.TeamRef( "team-1", "Security", "SEC" ),
				null );
	}

	private static LinearIssueLabel bugLabel() {
		return new LinearIssueLabel( "label-2", "bug", "#FF6B6B",
				OffsetDateTime.parse( "2025-01-01T00:00:00Z" ),
				OffsetDateTime.parse( "2026-01-01T00:00:00Z" ),
				null, null );
	}

	@Test
	void should_return_all_labels() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearIssueLabel.class, List.of( securityLabel(), bugLabel() ) );

			var result = session.createQuery(
					"SELECT l.id, l.name FROM LinearIssueLabel l",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_team_labels() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearIssueLabel.class, List.of( securityLabel(), bugLabel() ) );

			var result = session.createQuery(
					"SELECT l.id, l.name FROM LinearIssueLabel l WHERE l.team IS NOT NULL",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "security" );
		}
	}

	// ---- Projects ----

	private static LinearProject inProgressProject() {
		return new LinearProject( "proj-1", "SOC2 Compliance", "Achieve SOC2 Type II certification",
				"inProgress", 1, "Urgent",
				"2026-01-01", "2026-06-30",
				null, null,
				OffsetDateTime.parse( "2026-01-01T00:00:00Z" ),
				OffsetDateTime.parse( "2026-02-01T00:00:00Z" ),
				"https://linear.app/org/project/soc2",
				new LinearProject.LeadRef( "user-1", "Alice", "alice@example.com" ) );
	}

	private static LinearProject completedProject() {
		return new LinearProject( "proj-2", "Pen Test Q1", "External penetration test",
				"completed", 2, "High",
				"2025-10-01", "2025-12-31",
				OffsetDateTime.parse( "2025-12-20T00:00:00Z" ), null,
				OffsetDateTime.parse( "2025-10-01T00:00:00Z" ),
				OffsetDateTime.parse( "2025-12-20T00:00:00Z" ),
				"https://linear.app/org/project/pentest-q1",
				null );
	}

	@Test
	void should_filter_in_progress_projects() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearProject.class, List.of( inProgressProject(), completedProject() ) );

			var result = session.createQuery(
					"SELECT p.id, p.name FROM LinearProject p WHERE p.state = 'inProgress'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "SOC2 Compliance" );
		}
	}

	@Test
	void should_filter_projects_without_lead() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearProject.class, List.of( inProgressProject(), completedProject() ) );

			var result = session.createQuery(
					"SELECT p.id, p.name FROM LinearProject p WHERE p.projectLead IS NULL",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "Pen Test Q1" );
		}
	}

	// ---- Cycles ----

	private static LinearCycle activeCycle() {
		return new LinearCycle( "cycle-1", "Sprint 12", 12,
				OffsetDateTime.parse( "2026-03-17T00:00:00Z" ),
				OffsetDateTime.parse( "2026-03-31T00:00:00Z" ),
				null, null,
				OffsetDateTime.parse( "2026-03-10T00:00:00Z" ),
				OffsetDateTime.parse( "2026-03-10T00:00:00Z" ),
				new LinearCycle.TeamRef( "team-1", "Security", "SEC" ) );
	}

	private static LinearCycle completedCycle() {
		return new LinearCycle( "cycle-2", "Sprint 11", 11,
				OffsetDateTime.parse( "2026-03-03T00:00:00Z" ),
				OffsetDateTime.parse( "2026-03-16T00:00:00Z" ),
				OffsetDateTime.parse( "2026-03-16T00:00:00Z" ), null,
				OffsetDateTime.parse( "2026-02-24T00:00:00Z" ),
				OffsetDateTime.parse( "2026-03-16T00:00:00Z" ),
				new LinearCycle.TeamRef( "team-1", "Security", "SEC" ) );
	}

	@Test
	void should_filter_active_cycles() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearCycle.class, List.of( activeCycle(), completedCycle() ) );

			var result = session.createQuery(
					"SELECT c.id, c.name FROM LinearCycle c WHERE c.completedAt IS NULL",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "Sprint 12" );
		}
	}

	@Test
	void should_filter_cycles_by_team() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( LinearCycle.class, List.of( activeCycle(), completedCycle() ) );

			var result = session.createQuery(
					"SELECT c.id, c.number FROM LinearCycle c WHERE c.team.key = 'SEC'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}
}
