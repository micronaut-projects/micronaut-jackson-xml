from dataclasses import dataclass

from micronaut.core.annotation import Introspected
from tools.jackson.dataformat.xml.annotation import JacksonXmlRootElement


@Introspected
@JacksonXmlRootElement(localName="book")
@dataclass
class Book:
    name: str
