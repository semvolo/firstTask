package com.vseman.service;

import com.vseman.enums.BookCategory;
import com.vseman.model.Book;
import com.vseman.utils.Converter;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.vseman.enums.UpdateModifier.*;
import static com.vseman.model.Book.Field.*;

public class SolrService {

    private static final String SOLR_SERVER_URL = "http://localhost:8983/solr/firstTask";

    private static final String COMA = ", ";

    private static final String QUERY_ALL = "*:*";

    private static final String FQ_FORMAT = "%s:%s";

    private HttpSolrServer server;

    public SolrService() {
        server = new HttpSolrServer(SOLR_SERVER_URL);
    }

    public Book getBookById(String id) throws SolrServerException {
        SolrQuery query = new SolrQuery();
        query.setQuery(QUERY_ALL);
        query.addFilterQuery(String.format(FQ_FORMAT, ID.getFieldName(), id));
        query.setStart(0);
        QueryResponse response = server.query(query);
        return response.getResults().size() != 0 ? Converter.getBookFromSolrDocument(response.getResults().get(0)) : null;
    }

    public List<Book> getBooksByCategory(BookCategory category) throws SolrServerException {
        SolrQuery query = new SolrQuery();
        query.setQuery(QUERY_ALL);
        query.addFilterQuery(String.format(FQ_FORMAT, CATEGORIES.getFieldName(), category));
        query.setStart(0);
        QueryResponse response = server.query(query);
        return response.getResults().size() != 0 ? Converter.getBooksFromSolrDocuments(response.getResults()) : new ArrayList<Book>();
    }

    public List<Book> getBooksByOrFilter(String id, String name, String description, BookCategory... categories) throws SolrServerException {
        SolrQuery query = new SolrQuery();
        query.setQuery(QUERY_ALL);
        List<String> fqs = new ArrayList<String>();

        if (id != null) {
            fqs.add(String.format(FQ_FORMAT, ID.getFieldName(), id));
        }
        if (name != null) {
            fqs.add(String.format(FQ_FORMAT, NAME.getFieldName(), name));
        }
        if (description != null) {
            fqs.add(String.format(FQ_FORMAT, DESCRIPTION.getFieldName(), description));
        }
        if (categories != null) {

            for (int i = 0; i < categories.length; i++) {
                fqs.add(String.format(FQ_FORMAT, CATEGORIES.getFieldName(), categories[i].toString()));
            }
        }
        query.addFilterQuery(StringUtils.join(fqs, COMA));
        server.query(query);
        query.setStart(0);
        QueryResponse response = server.query(query);
        return response.getResults().size() != 0 ? Converter.getBooksFromSolrDocuments(response.getResults()) : new ArrayList<Book>();
    }

    public List<Book> getBooksByAndFilter(String id, String name, String description, BookCategory... categories) throws SolrServerException {
        SolrQuery query = new SolrQuery();
        query.setQuery(QUERY_ALL);

        if (id != null) {
            query.addFilterQuery(String.format(FQ_FORMAT, ID.getFieldName(), id));
        }
        if (name != null) {
            query.addFilterQuery(String.format(FQ_FORMAT, NAME.getFieldName(), name));
        }
        if (description != null) {
            query.addFilterQuery(String.format(FQ_FORMAT, DESCRIPTION.getFieldName(), description));
        }
        if (categories != null) {

            for (int i = 0; i < categories.length; i++) {
                query.addFilterQuery(String.format(FQ_FORMAT, CATEGORIES.getFieldName(), categories[i].toString()));
            }
        }
        server.query(query);
        query.setStart(0);
        QueryResponse response = server.query(query);
        return response.getResults().size() != 0 ? Converter.getBooksFromSolrDocuments(response.getResults()) : new ArrayList<Book>();
    }

    public List<Book> getBooksByFilterQuery(String filterQuery) throws SolrServerException {
        SolrQuery query = new SolrQuery();
        query.setQuery(QUERY_ALL);

        query.addFilterQuery(filterQuery);

        server.query(query);
        query.setStart(0);
        QueryResponse response = server.query(query);
        return response.getResults().size() != 0 ? Converter.getBooksFromSolrDocuments(response.getResults()) : new ArrayList<Book>();
    }

    public void addBookToIndex(Book book) throws SolrServerException, IOException {
        server.add(Converter.getSolrInputDocumentFromBook(book));
        server.commit();
    }

    public void addBooksToIndex(List<Book> books) throws SolrServerException, IOException {

        List<SolrInputDocument> solrInputDocuments = Converter.getSolrInputDocumentsFromBooks(books);

        for (int i = 0; i < solrInputDocuments.size(); i++) {
            server.add(solrInputDocuments.get(i));
            if (i % 100 == 0) {
                server.commit();  // periodically flush
            }

        }

        server.commit();
    }

    public void deleteBookFromIndexById(String id) throws SolrServerException, IOException {
        server.deleteById(id);
        server.commit();
    }

    public void deleteAllDocumentsFromIndex() throws SolrServerException, IOException {
        server.deleteByQuery(QUERY_ALL);
        server.commit();
    }

    public void updateBookInIndex(Book book) throws SolrServerException, IOException {
        Book actual = getBookById(book.getId());
        if (actual != null && !book.equals(actual)) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField(ID.getFieldName(), book.getId());
            if (!book.getName().equals(actual.getName())) {
                HashMap<String, Object> oper = new HashMap<>();
                oper.put(SET.getModifier(), book.getName());
                doc.addField(NAME.getFieldName(), oper);
            }
            if (!book.getDescription().equals(actual.getDescription())) {
                HashMap<String, Object> oper = new HashMap<>();
                oper.put(SET.getModifier(), book.getDescription());
                doc.addField(DESCRIPTION.getFieldName(), oper);
            }
            if (!book.getCategories().equals(actual.getCategories())) {
                HashMap<String, Object> oper = new HashMap<>();
                oper.put(SET.getModifier(), book.getCategories());
                doc.addField(CATEGORIES.getFieldName(), oper);
            }
            server.add(doc);
            server.commit();
        }
    }

    public List<Book> getAllBooks() throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();
        query.setQuery(QUERY_ALL);
        query.setStart(0);
        QueryResponse response = server.query(query);
        return Converter.getBooksFromSolrDocuments(response.getResults());
    }
}