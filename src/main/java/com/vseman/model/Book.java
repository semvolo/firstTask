
package com.vseman.model;

import com.vseman.enums.BookCategory;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Book {

    public static enum Field {
        ID("id"), NAME("name"), DESCRIPTION("description"), CATEGORIES("categories_txt");

        private final String fieldName;

        Field(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return this.fieldName;
        }
    }

    private String id;

    private String name;

    private String description;

    private EnumSet<BookCategory> categories;

    public Book() {

    }

    public Book(String id, String name, String description, EnumSet<BookCategory> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public EnumSet<BookCategory> getCategories() {
        return categories;
    }

    public void setCategories(EnumSet<BookCategory> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", description=" + description + ", title=" + name + ", categories" + categories + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Book))
            return false;

        Book book = (Book) o;

        if (categories != null ? !categories.equals(book.categories) : book.categories != null)
            return false;
        if (description != null ? !description.equals(book.description) : book.description != null)
            return false;
        if (id != null ? !id.equals(book.id) : book.id != null)
            return false;
        if (name != null ? !name.equals(book.name) : book.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (categories != null ? categories.hashCode() : 0);
        return result;
    }
}
