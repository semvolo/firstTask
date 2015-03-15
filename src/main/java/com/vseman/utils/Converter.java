package com.vseman.utils;

import com.vseman.enums.BookCategory;
import com.vseman.model.Book;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.util.*;

import static com.vseman.model.Book.Field.ID;
import static com.vseman.model.Book.Field.NAME;
import static com.vseman.model.Book.Field.DESCRIPTION;
import static com.vseman.model.Book.Field.CATEGORIES;

/**
 * Created by vseman on 3/13/2015.
 */
public class Converter {

    public static Book getBookFromSolrDocument(SolrDocument solrDocument) {
        Book book = new Book();
        book.setId((String) solrDocument.get(ID.getFieldName()));
        book.setName((String) solrDocument.get(NAME.getFieldName()));
        book.setDescription((String) solrDocument.get(DESCRIPTION.getFieldName()));

        List<String> list = (List<String>) solrDocument.get(CATEGORIES.getFieldName());

        EnumSet<BookCategory> categories = EnumSet.noneOf(BookCategory.class);
        for (String category : list) {
            categories.add(Enum.valueOf(BookCategory.class, category));
        }

        book.setCategories(categories);

        return book;
    }

    public static SolrInputDocument getSolrInputDocumentFromBook(Book book) {

        SolrInputDocument solrDocument = new SolrInputDocument();
        solrDocument.addField(ID.getFieldName(), book.getId());
        solrDocument.addField(NAME.getFieldName(), book.getName());
        solrDocument.addField(DESCRIPTION.getFieldName(), book.getDescription());
        solrDocument.addField(CATEGORIES.getFieldName(), book.getCategories().toArray());

        return solrDocument;
    }

    public static List<SolrInputDocument> getSolrInputDocumentsFromBooks(List<Book> books) {

        List<SolrInputDocument> solrInputDocuments = new ArrayList<>();

        for (Book book : books) {
            solrInputDocuments.add(Converter.getSolrInputDocumentFromBook(book));
        }

        return solrInputDocuments;
    }

    public static List<Book> getBooksFromSolrDocuments(SolrDocumentList solrDocuments) {

        List<Book> books = new ArrayList<Book>();

        for (SolrDocument solrDocument : solrDocuments) {
            books.add(Converter.getBookFromSolrDocument(solrDocument));
        }

        return books;
    }
}
