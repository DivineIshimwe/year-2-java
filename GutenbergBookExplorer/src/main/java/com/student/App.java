package com.student;

import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App {

    static Book[] allBooks = new Book[20];
    static int BookCount = 0;

    static Book[] readingList = new Book[20];
    static int listCount = 0;

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        loadBooksFromWebsite();

        String choice = "";
        while (!choice.equals("0")) {
            showMenu();
            choice = input.nextLine();

            if (choice.equals("1")) {
                showBooks();
            } else if (choice.equals("2")) {
                System.out.print("Enter a title to search: ");
                String word = input.nextLine();
                searchBook(word);
            } else if (choice.equals("3")) {
                System.out.print("Enter an author to search: ");
                String word = input.nextLine();
                searchBook(word, true);
            } else if (choice.equals("4")) {
                addBook();
            } else if (choice.equals("5")) {
                viewReadingList();
            } else if (choice.equals("6")) {
                removeBook();
            } else if (choice.equals("7")) {
                displayBookDetails();
            } else if (choice.equals("0")) {
                System.out.println("Goodbye!");
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static void showMenu() {
        System.out.println();
        System.out.println("===== GUTENBERG BOOK EXPLORER =====");
        System.out.println("1. Explore Books");
        System.out.println("2. Search by Title");
        System.out.println("3. Search by Author");
        System.out.println("4. Add Book to Reading List");
        System.out.println("5. View Reading List");
        System.out.println("6. Remove Book");
        System.out.println("7. Display Book Details");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    static void addToShelf(String title, String author, String url) {
        if (BookCount < allBooks.length) {
            allBooks[BookCount] = new Book(title, author, url);
            BookCount++;
        } else {
            System.out.println("The shelf is full.");
        }
    }

    static void loadBooksFromWebsite() {
        try {
            Document doc = Jsoup.connect("https://books.toscrape.com/").userAgent("Mozilla/5.0").get();
            Elements books = doc.select("article.product_pod");

            for (Element book : books) {
                Element link = book.selectFirst("h3 a");
                Element price = book.selectFirst("p.price_color");

                String title = link.attr("title");
                String url = link.absUrl("href");
                String priceText = price.text().replace("Â", "");

                addToShelf(title, "Price: " + priceText, url);
            }

            System.out.println("Loaded " + BookCount + " books from books.toscrape.com");
        } catch (Exception e) {
            System.out.println("Could not load books from the website: " + e.getMessage());
            System.out.println("Check your internet connection and run the program again.");
        }
    }

    static void showBooks() {
        if (BookCount == 0) {
            System.out.println("There are no books to show.");
            return;
        }

        for (int i = 0; i < BookCount; i++) {
            System.out.println((i + 1) + ". " + allBooks[i].title + " - " + allBooks[i].author);
        }
    }

    static void searchBook(String title) {
        searchBook(title, false);
    }

    static void searchBook(String keyword, boolean searchByAuthor) {
        if (keyword.trim().isEmpty()) {
            System.out.println("Please type something to search.");
            return;
        }

        String word = keyword.trim().toLowerCase();
        int found = 0;

        for (int i = 0; i < BookCount; i++) {
            String text;
            if (searchByAuthor) {
                text = allBooks[i].author.toLowerCase();
            } else {
                text = allBooks[i].title.toLowerCase();
            }

            if (text.contains(word)) {
                System.out.println((i + 1) + ". " + allBooks[i].title + " - " + allBooks[i].author);
                found++;
            }
        }

        if (found == 0) {
            System.out.println("No books found for: " + keyword);
        }
    }

    static void addBook() {
        showBooks();
        System.out.print("Which number do you want to add? ");
        String typed = input.nextLine();

        int number;
        try {
            number = Integer.parseInt(typed);
        } catch (NumberFormatException e) {
            System.out.println("Please type a number.");
            return;
        }

        if (number < 1 || number > BookCount) {
            System.out.println("That number is not on the list.");
            return;
        }

        if (listCount >= readingList.length) {
            System.out.println("Your reading list is full.");
            return;
        }

        readingList[listCount] = allBooks[number - 1];
        listCount++;
        System.out.println("Added: " + allBooks[number - 1].title);
    }

    static void viewReadingList() {
        if (listCount == 0) {
            System.out.println("Your reading list is empty.");
            return;
        }

        for (int i = 0; i < listCount; i++) {
            System.out.println((i + 1) + ". " + readingList[i].title + " - " + readingList[i].author);
        }
    }

    static void removeBook() {
        if (listCount == 0) {
            System.out.println("Your reading list is empty.");
            return;
        }

        viewReadingList();
        System.out.print("Which number do you want to remove? ");
        String typed = input.nextLine();

        int number;
        try {
            number = Integer.parseInt(typed);
        } catch (NumberFormatException e) {
            System.out.println("Please type a number.");
            return;
        }

        if (number < 1 || number > listCount) {
            System.out.println("That number is not on your list.");
            return;
        }

        int position = number - 1;
        String name = readingList[position].title;

        for (int i = position; i < listCount - 1; i++) {
            readingList[i] = readingList[i + 1];
        }

        readingList[listCount - 1] = null;
        listCount--;
        System.out.println("Removed: " + name);
    }

    static void displayBookDetails() {
        showBooks();
        System.out.print("Which number do you want details for? ");
        String typed = input.nextLine();

        int number;
        try {
            number = Integer.parseInt(typed);
        } catch (NumberFormatException e) {
            System.out.println("Please type a number.");
            return;
        }

        if (number < 1 || number > BookCount) {
            System.out.println("That number is not on the list.");
            return;
        }

        Book book = allBooks[number - 1];
        System.out.println();
        System.out.println("----- BOOK DETAILS -----");
        System.out.println("Title:  " + book.title);
        System.out.println("Author: " + book.author);
        System.out.println("URL:    " + book.url);
    }
}
