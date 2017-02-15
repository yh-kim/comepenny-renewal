package com.pickth.comepennyrenewal.book;

/**
 * Created by Kim on 2017-02-15.
 */

public class BookListItem {
    String title;
    String author;
    String publisher;
    String image;
    String isbn;

    public BookListItem(String title, String author, String publisher, String image, String isbn) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.image = image;
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
