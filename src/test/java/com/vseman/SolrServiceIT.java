package com.vseman;

import com.vseman.model.Book;
import com.vseman.service.SolrService;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static com.vseman.enums.BookCategory.EDUCATION;
import static com.vseman.enums.BookCategory.HISTORY;
import static com.vseman.enums.BookCategory.HUMOR;
import static com.vseman.enums.BookCategory.ROMANCE;
import static com.vseman.enums.BookCategory.ADVENTURE;
import static com.vseman.enums.BookCategory.TECHNOLOGY;
import static junit.framework.Assert.assertEquals;

/**
 * Integration test for simple SolrService.
 */
public class SolrServiceIT {

    private SolrService solrService = new SolrService();

    @Test
    public void testAddBook() throws IOException, SolrServerException {
        Book book = new Book(UUID.randomUUID().toString(), "Treasure Island", "Best seller by R.L.S.", EnumSet.of(ADVENTURE));
        solrService.addBookToIndex(book);
        Book result = solrService.getBookById(book.getId());
        assertEquals(book, result);
    }

    @Test
    public void testAddBooks() throws IOException, SolrServerException {
        List<Book> books = prepareSimpleBookList();
        solrService.addBooksToIndex(books);
        Book result;
        for (Book book : books) {
            result = solrService.getBookById(book.getId());
            assertEquals(book, result);
        }
    }

    @Test
    public void testDeleteBookFromIndexById() throws IOException, SolrServerException {
        Book book = new Book(UUID.randomUUID().toString(), "Treasure Island", "Best seller by R.L.S.", EnumSet.of(ADVENTURE));
        solrService.addBookToIndex(book);
        solrService.deleteBookFromIndexById(book.getId());
        assertEquals(null, solrService.getBookById(book.getId()));
    }

    @Test
    public void testDeleteAllDocumentsFromIndex() throws IOException, SolrServerException {
        solrService.deleteAllDocumentsFromIndex();
        assertEquals(0, solrService.getAllBooks().size());
    }

    @Test
    public void testGetBooksByCategory() throws IOException, SolrServerException {
        solrService.deleteAllDocumentsFromIndex();

        List<Book> books = prepareSimpleBookList();
        solrService.addBooksToIndex(books);

        assertEquals(4, solrService.getBooksByCategory(ADVENTURE).size());
    }

    @Test
    public void testGetBooksByOrFilter() throws IOException, SolrServerException {
        solrService.deleteAllDocumentsFromIndex();

        List<Book> books = prepareSimpleBookList();
        solrService.addBooksToIndex(books);

        assertEquals(6, solrService.getBooksByOrFilter(null, "Island", null, HISTORY, ADVENTURE).size());
    }

    @Test
    public void testGetBooksByAndFilter() throws IOException, SolrServerException {
        solrService.deleteAllDocumentsFromIndex();

        List<Book> books = prepareSimpleBookList();
        solrService.addBooksToIndex(books);

        assertEquals(1, solrService.getBooksByAndFilter(null, null, null, HISTORY, EDUCATION).size());
    }

    @Test
    public void testGetBooksByFilterQuery() throws IOException, SolrServerException {
        solrService.deleteAllDocumentsFromIndex();

        List<Book> books = prepareSimpleBookList();
        solrService.addBooksToIndex(books);

        assertEquals(5, solrService.getBooksByFilterQuery("Island").size());
    }

    @Test
    public void testUpdateBookInIndex() throws IOException, SolrServerException {
        Book book = new Book(UUID.randomUUID().toString(), "Treasure Island", "Best seller by R.L.S.", EnumSet.of(ADVENTURE));
        solrService.addBookToIndex(book);

        book.setCategories(EnumSet.of(ADVENTURE, HUMOR));

        solrService.updateBookInIndex(book);
        Book result = solrService.getBookById(book.getId());
        assertEquals(book, result);

        book.setDescription("Bake your own cookies, on a secret island!");
        solrService.updateBookInIndex(book);
        result = solrService.getBookById(book.getId());
        assertEquals(book, result);


    }

    private List<Book> prepareSimpleBookList() {
        List<Book> bookList = new ArrayList<Book>();
        bookList.add(new Book(UUID.randomUUID().toString(), "Treasure Island", "Best seller by R.L.S.",
                EnumSet.of(ADVENTURE)));
        bookList.add(new Book(UUID.randomUUID().toString(), "Treasure Island 2.0", "Humorous remake of the famous best seller",
                EnumSet.of(ADVENTURE, HUMOR)));
        bookList.add(new Book(UUID.randomUUID().toString(), "Solr for dummies", "Get started with solr",
                EnumSet.of(EDUCATION, HUMOR, TECHNOLOGY)));
        bookList.add(new Book(UUID.randomUUID().toString(), "Moon landing", "All facts about Apollo 11, a best seller",
                EnumSet.of(HISTORY, EDUCATION)));
        bookList.add(new Book(UUID.randomUUID().toString(), "Spring Island", "The perfect island romance..",
                EnumSet.of(ROMANCE)));
        bookList.add(new Book(UUID.randomUUID().toString(), "Refactoring", "It's about improving the design of existing code.",
                EnumSet.of(TECHNOLOGY)));
        bookList.add(new Book(UUID.randomUUID().toString(), "Baking for dummies", "Bake your own cookies, on a secret island!",
                EnumSet.of(EDUCATION, HUMOR)));
        bookList.add(new Book(UUID.randomUUID().toString(), "The Pirate Island", "Oh noes, the pirates are coming!",
                EnumSet.of(ADVENTURE, HUMOR)));
        bookList.add(new Book(UUID.randomUUID().toString(), "Blackbeard", "It's the pirate Edward Teach!",
                EnumSet.of(ADVENTURE, HISTORY)));
        bookList.add(new Book(UUID.randomUUID().toString(), "Handling Cookies", "How to handle cookies in web applications",
                EnumSet.of(TECHNOLOGY)));
        return bookList;
    }
}
