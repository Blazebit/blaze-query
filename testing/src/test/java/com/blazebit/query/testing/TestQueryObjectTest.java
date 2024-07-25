package com.blazebit.query.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class TestQueryObjectTest {

    @Test
    void should_properly_deserialize_json_string() throws JsonProcessingException {
        var actual = TestQueryObject.fromJson(TestSchemaObject.class,
            "{ \"id\": 1, \"name\": \"name\" }");
        var expected = new TestSchemaObject(1, "name");
        assertEquals(actual, expected);
    }

    @Test
    void should_properly_deserialize_json_file() throws IOException {
        var actual = TestQueryObject.fromJson(TestSchemaObject.class,
            new File("src/test/resources/test.json"));
        var expected = new TestSchemaObject(1, "name");
        assertEquals(actual, expected);
    }

    static class TestSchemaObject {

        private int id;
        private String name;

        public TestSchemaObject() {
        }

        public TestSchemaObject(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "TestSchemaObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            TestSchemaObject that = (TestSchemaObject) object;
            return id == that.id && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + Objects.hashCode(name);
            return result;
        }
    }
}