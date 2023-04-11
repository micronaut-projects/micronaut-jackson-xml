package io.micronaut.xml.jackson.graal;

import javax.xml.datatype.XMLGregorianCalendar;

import io.micronaut.core.annotation.Introspected;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

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
