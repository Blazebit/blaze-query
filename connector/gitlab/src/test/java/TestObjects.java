/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.connector.gitlab.GitlabUser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public final class TestObjects {
	private TestObjects() {
	}

	public static GitlabUser user() {
		return new GitlabUser(
				"gid://gitlab/User/31337",
				"John Doe",
				"johndoe",
				Date.from( LocalDate.of(2024, 5, 21).atStartOfDay().toInstant( ZoneOffset.UTC ) ),
				true,
				"https://secure.gravatar.com/avatar/1c337a4619dfdc8e81cc4773148a68cae8e6cad9d9f6e97fda7b3ec1592a3a1e4?s=80&d=identicon",
				"",
				false,
				null,
				Date.from(  Instant.parse("2024-05-21T13:09:23Z") ),
				"",
				false,
				0,
				true,
				"",
				"",
				"",
				"",
				null,
				null,
				"",
				"/JohnDoe",
				"https://gitlab.com/JohnDoe"
		);
	}
}
