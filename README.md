[![Build Status](https://travis-ci.com/Blazebit/blaze-query.svg?branch=master)](https://travis-ci.org/Blazebit/blaze-query)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.blazebit/blaze-query-core-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.blazebit/blaze-query-core-api)
[![Slack Status](https://blazebit.herokuapp.com/badge.svg)](https://blazebit.herokuapp.com)

[![Javadoc - Query](https://www.javadoc.io/badge/com.blazebit/blaze-query-core-api.svg?label=javadoc%20-%20query-core-api)](http://www.javadoc.io/doc/com.blazebit/blaze-query-core-api)

Blaze-Query
==========
Blaze-Query is a toolkit that can be used to build a custom expression and predicate DSL based on blaze-domain with a strong focus on string and query serialization of expressions.

What is it?
===========

Blaze-Expression provides a common expression AST that is enriched with blaze-domain types which enables string and query serialization. 

The expression AST in the core API module is supposed to be general purpose and supposed to cover mostly structural aspects.
An expression compiler that implements a syntax similar to the JPQL.Next expression language is provided out of the box, but a custom syntax can be used by providing a custom compiler.
The implementation provides support for an interpreter for expressions, given that the domain types have proper interpreters registered for the operations registered. 
Blaze-Expression comes with a persistence module that provides an interpreter and JPQL.Next rendering support to a string or Blaze-Persistence query builders for persistence related models.
The _declarative_ submodule allows to define expression related metadata via annotations on the domain model elements.

In short, Blaze-Expression allows you to have a custom DSL based on your own domain model that translates into queries for efficient database execution but also supports interpretation and serialization for storage.

Features
==============

Blaze-Expression has support for

* Make use of custom domain model in DSL via Blaze-Domain
* Serialize expressions to string form for storage
* Serialize expressions to expressions/predicates in Blaze-Persistence queries
* Interpret expressions on custom objects
* Full custom function support

How to use it?
==============

Blaze-Expression is split up into different modules. We recommend that you define a version property in your parent pom that you can use for all artifacts. Modules are all released in one batch so you can safely increment just that property. 

```xml
<properties>
    <blaze-expression.version>1.0.0-SNAPSHOT</blaze-expression.version>
</properties>
```

Alternatively you can also use our BOM in the `dependencyManagement` section.

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.blazebit</groupId>
            <artifactId>blaze-expression-bom</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>    
    </dependencies>
</dependencyManagement>
```

## Manual setup

For compiling you will only need API artifacts and for the runtime you need impl and integration artifacts.

Blaze-Expression Core module dependencies

```xml
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-expression-core-api</artifactId>
    <version>${blaze-expression.version}</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-expression-core-impl</artifactId>
    <version>${blaze-expression.version}</version>
    <scope>runtime</scope>
</dependency>
```

Blaze-Expression Declarative module dependencies

```xml
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-expression-declarative-api</artifactId>
    <version>${blaze-expression.version}</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-expression-declarative-impl</artifactId>
    <version>${blaze-expression.version}</version>
    <scope>runtime</scope>
</dependency>
```

Blaze-Expression Persistence module dependencies

```xml
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-expression-persistence</artifactId>
    <version>${blaze-expression.version}</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-expression-declarative-persistence</artifactId>
    <version>${blaze-expression.version}</version>
    <scope>compile</scope>
</dependency>
```

Documentation
=========

Currently there is no documentation other than the Javadoc.
 
Core quick-start
=================

To work with Blaze-Expression, a `ExpressionServiceFactory` is needed which requires that a domain model is built first.  

```java
DomainBuilder domainBuilder = Domain.getDefaultProvider().createDefaultBuilder();
domainBuilder.createEntityType("Cat")
    .addAttribute("name", "String")
    .addAttribute("age", "Integer")
  .build();
DomainModel domain = domainBuilder.build();
```

With that `DomainModel` a `ExpressionServiceFactory` can be created and an expression compiled.

```java
ExpressionServiceFactory expressionService = Expressions.forModel(domain);
ExpressionCompiler compiler = expressionService.createCompiler();
ExpressionCompiler.Context context = compiler.createContext(Collections.singletonMap("c", domain.getType("Cat")));
Expression expression = compiler.createExpression("c.age", context);
```

The expression string is parsed, type checked and enriched with the result domain types.
The metadata defined for domain types is then used internally to implement interpretation or serialization.

Such a simple expression isn't very interesting, but to go further, the definition of some basic types and their operators is necessary which is provided by the persistence module.

Declarative Persistence usage
=================

The persistence and declarative persistence modules allow to make use of some commonly used basic types and provide a wide set of builtin functions:

```java
@DomainType
@EntityView(Cat.class)
interface CatModel {
  @EntityAttribute
  String getName();
  @EntityAttribute("AGE(birthday)")
  Integer getAge();
}
```

Assuming the domain model was already built, we could formulate a predicate:

```java
ExpressionService expressionService = Expressions.forModel(domain);
ExpressionCompiler compiler = expressionService.createCompiler();
ExpressionCompiler.Context context = compiler.createContext(
    Collections.singletonMap("c", domain.getType("CatModel"))
);
Predicate predicate = compiler.createPredicate("c.age > 18", context);
```

The predicate could be evaluated against an object i.e. interpreted

```java
ExpressionInterpreter interpreter = expressionService.createInterpreter();
ExpressionInterpreter.Context context = interpreter.createContext(
    Collections.singletonMap("c", new CatModelImpl("Cat 1", 19))
);
Boolean result = interpreter.evaluate(predicate, context);
```

This would yield `true` as the age of the cat in the example is 19. This could also be serialized to a query

```java
CriteriaBuilder<Cat> criteriaBuilder = criteriaBuilderFactory.create(entityManager, Cat.class, "cat");
ExpressionSerializer<WhereBuilder> serializer = expressionService.createSerializer(WhereBuilder.class);
ExpressionSerializer.Context context = new PersistenceExpressionSerializerContext(
    Collections.singletonMap("c", "cat")
);
serializer.serializeTo(context, predicate, criteriaBuilder);
```

This will result in a query like the following

```sql
SELECT cat
FROM Cat cat
WHERE AGE(cat.birthday) > 18
```

Licensing
=========

This distribution, as a whole, is licensed under the terms of the Apache
License, Version 2.0 (see LICENSE.txt).

References
==========

Project Site:              https://expression.blazebit.com (coming at some point)
