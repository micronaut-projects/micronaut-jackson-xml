from dataclasses import dataclass
from typing import Annotated

from micronaut.core.annotation import Introspected
from tools.jackson.dataformat.xml.annotation import JacksonXmlProperty, JacksonXmlRootElement


@JacksonXmlRootElement(localName="book")
@Introspected
@dataclass
class BookSaved:
    name: str
    isbn: Annotated[str, JacksonXmlProperty(isAttribute=True)]
