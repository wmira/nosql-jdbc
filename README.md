docdb-jdbc
==========

A NoSQL Repository API for RDBMS that supports JSON types. Current implementation only supports Postgresql with [json/jsonb datatype](http://www.postgresql.org/docs/9.3/static/datatype-json.html)

## Usage
```java

//Initialize using DataSource.
DataSource ds = ...
DocumentRepository docRepo = new PgsqlDocumentRepository(ds);

```

```java

@Document
public Product {
 
  private String id;
  private String name;
  private String description;
  private String category;
  
  public String getId() { ... }
  public String getName() { ... }
  public String getDescription() { ... }
  public String getCategory() { ... }
  
  public void setId() { ... }
  public void setName() { ... }
  public void setDescription() { ... }
  public void setCategory() { ... }
}

```

```java

//persist
docRepo.save(product);

//find single
Product product = docRepo.find(Product.class,productId);

//find all products with beer category
Criteria criteria = new Criteria();
criteria.add(new FieldCriterion("category","beer");
List<Product> products = docRepo.find(Product.class,criteria);

```

## Todo
1. More Unit Tests
2. More Docs
3. Document updates
4. Drink Beer
