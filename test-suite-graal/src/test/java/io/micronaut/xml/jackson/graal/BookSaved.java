package io.micronaut.xml.jackson.graal;

import javax.xml.datatype.XMLGregorianCalendar;

import io.micronaut.core.annotation.Introspected;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "book")
@Introspected
public class BookSaved {

    private String name;

    @JacksonXmlProperty(isAttribute = true)
    private String isbn;
    private XMLGregorianCalendar date;

    public BookSaved(String name, String isbn, XMLGregorianCalendar date) {
        this.name = name;
        this.isbn = isbn;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public XMLGregorianCalendar getDate() {
        return date;
    }

    public void setDate(XMLGregorianCalendar date) {
        this.date = date;
    }
}
