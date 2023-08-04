package io.micronaut.xml.jackson.graal;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.core.annotation.Introspected;

import javax.xml.datatype.XMLGregorianCalendar;

@Introspected
@JacksonXmlRootElement(localName = "book")
public class Book {

    private String name;
    private XMLGregorianCalendar date;

    public Book(String name, XMLGregorianCalendar date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public XMLGregorianCalendar getDate() {
        return date;
    }

    public void setDate(XMLGregorianCalendar date) {
        this.date = date;
    }
}
