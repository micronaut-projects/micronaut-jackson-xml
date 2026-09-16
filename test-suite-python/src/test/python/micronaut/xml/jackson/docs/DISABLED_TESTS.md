# Python Docs Disabled Test Inventory

This file tracks Python docs examples of Micronaut Jackson XML that are present but disabled, or that deviate from the
Java example because the direct port currently fails compilation or at runtime (Python compiler gaps). Use it as the
bug-fixing task list for the final migration wave.

## Reconciliation

- Last generated active `@Disabled` count: 1.
- Last generated command: `rg -n "@Disabled\(" test-suite-python/src/test/python`.
- Last full-suite command: `./gradlew :test-suite-python:test -Ppython-ci`.
- Last full-suite result: build successful, 2 tests executed (1 test class), 1 skipped.

## Migration Rules

- Models are `@Introspected` `@dataclass` classes (`Book`, `BookSaved`); the Jackson XML annotations are the imported
  decorators `@JacksonXmlRootElement(localName="book")` on the class and `Annotated[str, JacksonXmlProperty(isAttribute=True)]`
  on the attribute, mirroring the Java sources.
- The declarative client is a class with `...` method bodies; `@Body` parameters are `Annotated[Book, Body]`.
- The Java `BookControllerTest.testSavebook` is split into `test_save_book` (declarative client round trip, active) and
  `test_save_book_xml_document` (raw XML document shape, disabled, see below) so that the working part keeps running.

## Active `@Disabled` Tests

| Test | Reason |
| --- | --- |
| `micronaut.xml.jackson.docs.BookControllerTest.test_save_book_xml_document` | Jackson (`XmlMapper` / `JacksonXmlAnnotationIntrospector`) reads `@JacksonXmlRootElement` and `@JacksonXmlProperty(isAttribute = true)` reflectively from the Java class of the model. The Python compiler copies only JUnit / `@MicronautTest` annotations onto the generated Java class, so the `tools.jackson.dataformat.xml.annotation` decorators of a Python dataclass are invisible to Jackson: the response is `<BookSaved><name>Huckleberry Finn</name><isbn>..</isbn></BookSaved>` instead of `<book isbn=".."><name>Huckleberry Finn</name></book>`. Deserialization of `<book><name>..</name></book>` into the Python `Book` and the declarative client round trip work. `# TODO(python)` |

## Commented Unsupported Snippet Ports

None.

## Intentionally Unsupported Snippet Targets

None.
