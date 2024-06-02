/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazebit.query.app;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TestEntity {
    @Id
    Long id;
    String name;
    @Embedded
    TestEmbeddable embedded;
    @ElementCollection
    Set<TestEmbeddable> elements;

    public TestEntity() {
    }

    public TestEntity(Long id, String name, TestEmbeddable embedded) {
        this.id = id;
        this.name = name;
        this.embedded = embedded;
        this.elements = new HashSet<>( Collections.singletonList( embedded ) );
    }

    @Override
    public final boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof TestEntity ) ) {
            return false;
        }

        TestEntity that = (TestEntity) o;
        return Objects.equals( id, that.id ) && Objects.equals( name, that.name );
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode( id );
        result = 31 * result + Objects.hashCode( name );
        return result;
    }
}
