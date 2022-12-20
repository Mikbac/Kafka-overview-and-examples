# Library Producer

```http request
POST http://localhost:8080/v1/books
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