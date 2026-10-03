package com.gruppen.filmdatabase.controller;

import com.gruppen.filmdatabase.entity.Film;
import com.gruppen.filmdatabase.repository.FilmRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScraperV2 {
    static ArrayList<Map<String, String>> scrapedFilms = new ArrayList<>();

    @Autowired
    private FilmRepository filmRepository;


    public static void scrape() throws IOException, SQLException {
        // code for printing out scraped movies
        /***Document document = Jsoup.connect("https://www.imdb.com/list/ls000634294/").get();
         Elements body = document.select(".lister-list");
         for(Element e : body.select(".lister-item") ){
         System.out.println(e.select(".lister-item-header a").text());
         System.out.println(e.select(".genre").text());
         System.out.println(e.select(".runtime").text());
         System.out.println(e.select(".lister-item-year").text());
         System.out.println(e.select(".text-muted a:nth-child(1)").text());
         System.out.println(e.select(".text-muted > a:not(:first-child)").text());
         System.out.println();
         }***/

        Document document = Jsoup.connect("https://www.imdb.com/list/ls000634294/").get();
        Elements body = document.select(".lister-list");

        for (Element e : body.select(".lister-item")) {
            Map<String, String> filmTemplate = new HashMap<>();
            /**
             * String name, String category, String length, String pubDate, String director, String cast,
             *                 String scriptAuthor
             */
            String name = e.select(".lister-item-header a").text();
            String category = e.select(".genre").text();
            String length = e.select(".runtime").text();
            String pubDate = e.select(".lister-item-year").text();
            String director = e.select(".text-muted a:nth-child(1)").text();
            String cast = e.select(".text-muted > a:not(:first-child)").text();
            String scriptAuthor = null;
            // Get image url from IMDB
            String imageUrl = e.select(".lister-item-image a img").attr("loadlate");
            // Add fields to template
            filmTemplate.put("name", name);
            filmTemplate.put("category", category);
            filmTemplate.put("length", length);
            filmTemplate.put("pubDate", pubDate);
            filmTemplate.put("director", director);
            filmTemplate.put("cast", cast);
            filmTemplate.put("scriptAuthor", scriptAuthor);
            filmTemplate.put("imageUrl", imageUrl);

            scrapedFilms.add(filmTemplate);
        }
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/filmdatabase",
                "root", "");

        for (Map<String, String> scrappedFilm : scrapedFilms) {
            String name = scrappedFilm.get("name");
            String category = scrappedFilm.get("category");
            String length = scrappedFilm.get("length");
            String pubDate = scrappedFilm.get("pubDate");
            String director = scrappedFilm.get("director");
            String cast = scrappedFilm.get("cast");
            String scriptAuthor = scrappedFilm.get("scriptAuthor");
            String imageUrl = scrappedFilm.get("imageUrl");

            cast = cast.replace("'", "");
            director = director.replace("'", "");

            Film film = new Film(name, category, length, pubDate, director, cast, scriptAuthor);

            film.setBanner(getImageAsByteArray(imageUrl));

            String query = "INSERT INTO filmdatabase.tbl_films(cast, category, director, length, name, pub_date,banner) VALUES (?,?,?,?,?,?,?);";
            System.out.println(query);
            PreparedStatement toInsert = connection.prepareStatement(query);
            toInsert.setString(1, cast);
            toInsert.setString(2, category);
            toInsert.setString(3, director);
            toInsert.setString(4, length);
            toInsert.setString(5, name);
            toInsert.setString(6, pubDate);
            toInsert.setBytes(7, film.getBanner());
            toInsert.executeUpdate();

        }
        connection.close();
    }

    public static void scrape(String searchCategory, String searchPubDate) throws IOException, SQLException {
        // code for printing out scraped movies
        /***Document document = Jsoup.connect("https://www.imdb.com/list/ls000634294/").get();
         Elements body = document.select(".lister-list");
         for(Element e : body.select(".lister-item") ){
         System.out.println(e.select(".lister-item-header a").text());
         System.out.println(e.select(".genre").text());
         System.out.println(e.select(".runtime").text());
         System.out.println(e.select(".lister-item-year").text());
         System.out.println(e.select(".text-muted a:nth-child(1)").text());
         System.out.println(e.select(".text-muted > a:not(:first-child)").text());
         System.out.println();
         }***/

//
//        FilmController controller = new FilmController();
//        Document document = Jsoup.connect("https://www.imdb.com/list/ls000634294/").get();
//        Elements body = document.select(".lister-list");
//
//        Film filmTemplate;
//        for (Element e : body.select(".lister-item")) {
//            if (e.select(".genre").text().contains(category) || category == null || category.isEmpty()) {
//                if (e.select(".lister-item-year").text().contains(pubDate) || pubDate == null || pubDate.isEmpty()) {
//                    filmTemplate = new Film(e.select(".lister-item-header a").text(),
//                            e.select(".genre").text(),
//                            e.select(".runtime").text(),
//                            e.select(".lister-item-year").text(),
//                            e.select(".text-muted a:nth-child(1)").text(),
//                            e.select(".text-muted > a:not(:first-child)").text(),
//                            null
//                    );
//                    scrapedFilms.add(filmTemplate);
//                }
//            }
//            /** filmTemplate.setName(e.select(".lister-item-header a").text());
//             filmTemplate.setCategory(e.select(".genre").text());
//             filmTemplate.setLength(e.select(".runtime").text());
//             filmTemplate.setPubDate(e.select(".lister-item-year").text());
//             filmTemplate.setDirector(e.select(".text-muted a:nth-child(1)").text());
//             filmTemplate.setCast(e.select(".text-muted > a:not(:first-child)").text());
//             System.out.println(e.select(".lister-item-header a").text());
//             System.out.println(e.select(".genre").text());
//             System.out.println(e.select(".runtime").text());
//             System.out.println(e.select(".lister-item-year").text());
//             System.out.println(e.select(".text-muted a:nth-child(1)").text());
//             System.out.println(e.select(".text-muted > a:not(:first-child)").text());
//             System.out.println();*/
//
//
//        }
//        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/filmdatabase",
//                "root", "");
//
//        for (Film filmx : scrapedFilms) {
//            String cast = filmx.getCast();
//            cast = cast.replace("'", "");
//            String dir = filmx.getDirector();
//            dir = dir.replace("'", "");
//            String query = "INSERT INTO filmdatabase.tbl_films(cast, category, director, length, name, pub_date) VALUES ('" + cast + "','" + filmx.getCategory() +
//                    "','" + dir + "','" + filmx.getLength() + "','" + filmx.getName() + "','" + filmx.getPubDate() + "');";
//            System.out.println(query);
//            Statement toInsert = connection.prepareStatement(query);
//            toInsert.execute(query);
//
//            // controller.saveMovie(filmx);
//        }
//        connection.close();
        //scrapedFilms = (ArrayList<Film>) repo.saveAll(scrapedFilms);

        Document document = Jsoup.connect("https://www.imdb.com/list/ls000634294/").get();
        Elements body = document.select(".lister-list");

        for (Element e : body.select(".lister-item")) {
            Map<String, String> filmTemplate = new HashMap<>();
            String name = e.select(".lister-item-header a").text();
            String category = e.select(".genre").text();
            String length = e.select(".runtime").text();
            String pubDate = e.select(".lister-item-year").text();
            String director = e.select(".text-muted a:nth-child(1)").text();
            String cast = e.select(".text-muted > a:not(:first-child)").text();
            String scriptAuthor = null;
            if ((searchPubDate != null && !pubDate.contains(searchPubDate)) ||
                    (searchCategory != null && !category.contains(searchCategory))) {
                continue;
            }
            // Get image url from IMDB
            String imageUrl = e.select(".lister-item-image a img").attr("loadlate");
            // Add fields to template
            filmTemplate.put("name", name);
            filmTemplate.put("category", category);
            filmTemplate.put("length", length);
            filmTemplate.put("pubDate", pubDate);
            filmTemplate.put("director", director);
            filmTemplate.put("cast", cast);
            filmTemplate.put("scriptAuthor", scriptAuthor);
            filmTemplate.put("imageUrl", imageUrl);

            scrapedFilms.add(filmTemplate);
        }
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/filmdatabase",
                "root", "");

        for (Map<String, String> scrappedFilm : scrapedFilms) {
            String name = scrappedFilm.get("name");
            String category = scrappedFilm.get("category");
            String length = scrappedFilm.get("length");
            String pubDate = scrappedFilm.get("pubDate");
            String director = scrappedFilm.get("director");
            String cast = scrappedFilm.get("cast");
            String scriptAuthor = scrappedFilm.get("scriptAuthor");
            String imageUrl = scrappedFilm.get("imageUrl");

            cast = cast.replace("'", "");
            director = director.replace("'", "");

            Film film = new Film(name, category, length, pubDate, director, cast, scriptAuthor);

            film.setBanner(getImageAsByteArray(imageUrl));

            String query = "INSERT INTO filmdatabase.tbl_films(cast, category, director, length, name, pub_date,banner) VALUES (?,?,?,?,?,?,?);";
            System.out.println(query);
            PreparedStatement toInsert = connection.prepareStatement(query);
            toInsert.setString(1, cast);
            toInsert.setString(2, category);
            toInsert.setString(3, director);
            toInsert.setString(4, length);
            toInsert.setString(5, name);
            toInsert.setString(6, pubDate);
            toInsert.setBytes(7, film.getBanner());
            toInsert.executeUpdate();

        }
        connection.close();
    }

    //   https://www.libsea.com/article/how-to-convert-image-url-to-byte-array-in-java
    public static byte[] getImageAsByteArray(String imageURL) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        URL url = new URL(imageURL);
        try {
            is = url.openStream();
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
        } catch (IOException e) {
            System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
            e.printStackTrace();
            // Perform any other exception handling that's appropriate.
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return baos.toByteArray();
    }
}
