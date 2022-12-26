# Library Producer

```http request
POST http://localhost:8080/v1/async/books
Content-Type: application/json

{
  "libraryEventId": 1,
  "book": {
    "bookId": 456,
    "bookName": "Kafka Using Spring Boot",
    "bookAuthor": "Dilip"
  }
}
```

```http request
POST http://localhost:8080/v2/async/book
Content-Type: application/json

{
  "libraryEventId": 1,
  "book": {
    "bookId": 456,
    "bookName": "Kafka Using Spring Boot",
    "bookAuthor": "Dilip"
  }
}
```

```http request
POST http://localhost:8080/v3/async/book
Content-Type: application/json

{
  "libraryEventId": 1,
  "book": {
    "bookId": 456,
    "bookName": "Kafka Using Spring Boot",
    "bookAuthor": "Dilip"
  }
}
```

```http request
POST http://localhost:8080/v1/sync/books
Content-Type: application/json

{
  "libraryEventId": 1,
  "book": {
    "bookId": 456,
    "bookName": "Kafka Using Spring Boot",
    "bookAuthor": "Dilip"
  }
}
```

```http request
PUT http://localhost:8080/v1/async/books
Content-Type: application/json

{
  "libraryEventId": "1111-222-333",
  "book": {
    "bookId": 456,
    "bookName": "Kafka Book2",
    "bookAuthor": "Author Name"
  }
}
```