nosql-jdbc
==========

[![Build Status](https://travis-ci.org/wmira/nosql-jdbc.svg?branch=master)](https://travis-ci.org/wmira/nosql-jdbc)

A NoSQL Repository API for RDBMS that supports JSON types. Current implementation only supports Postgresql with [json/jsonb datatype](http://www.postgresql.org/docs/9.3/static/datatype-json.html)

## Usage

### Create base tables

```sql

CREATE TABLE channel (
  data jsonb NOT NULL
)

CREATE TABLE personnel (
  data jsonb NOT NULL
)

```

### Objects

```java

@Document
class Channel {

  private String id;
  private String name;

  public String getId() {
     return this.id;
  }

  public void setId() {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }
  public void setName(final String name) {
    this.name = name;
  }
}

@Document
class Personnel {

  private String id;
  private String fullname;
  private String defaultChannelId;

  @DocumentRef
  private Channel defaultChannel;

  public String getId() {
    return this.id;
  }

  public void setId() {
    this.id = id;
  }

  public String getFullname() {
    return this.fullname;
  }
  public void setFullname(final String fullname) {
    this.fullname = fullname;
  }
}
```

```java

//Initialize using DataSource.
DataSource ds = ...
JsonRepository jsonRepo = new PgsqlJsonRepository(ds);


//find
jsonRepo.find(Personnel.class,"id");
jsonRepo.find(Personnel.class,new Criteria().add(new FieldCriterion("fullName","somename")));

//save or update
jsonRepo.saveOrUpdate(new Channel());
jsonRepo.saveOrUpdate(new Personnel());

```

## Requirements
1. Jdk 1.7+
2. A Database that supports json types (current implementation only for Postgresql 9.4)

## Todo
1. More Unit Tests
2. More Docs
3. Drink Beer
