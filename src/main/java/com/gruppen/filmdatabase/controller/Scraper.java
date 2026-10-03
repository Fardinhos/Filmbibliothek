package com.gruppen.filmdatabase.controller;

import com.gruppen.filmdatabase.entity.Film;
import com.gruppen.filmdatabase.repository.FilmRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.thymeleaf.context.IContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Scraper {
    static ArrayList<Film> scrapedFilms = new ArrayList<>();





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




        FilmController controller = new FilmController();
        Document document = Jsoup.connect("https://www.imdb.com/list/ls000634294/").get();
        Elements body = document.select(".lister-list");

        Film filmTemplate;
        for(Element e : body.select(".lister-item") ){
            filmTemplate= new Film(e.select(".lister-item-header a").text(),
                    e.select(".genre").text(),
                    e.select(".runtime").text(),
                    e.select(".lister-item-year").text(),
                    e.select(".text-muted a:nth-child(1)").text(),
                    e.select(".text-muted > a:not(:first-child)").text(),
                    null
            );
            /** filmTemplate.setName(e.select(".lister-item-header a").text());
             filmTemplate.setCategory(e.select(".genre").text());
             filmTemplate.setLength(e.select(".runtime").text());
             filmTemplate.setPubDate(e.select(".lister-item-year").text());
             filmTemplate.setDirector(e.select(".text-muted a:nth-child(1)").text());
             filmTemplate.setCast(e.select(".text-muted > a:not(:first-child)").text());
             System.out.println(e.select(".lister-item-header a").text());
             System.out.println(e.select(".genre").text());
             System.out.println(e.select(".runtime").text());
             System.out.println(e.select(".lister-item-year").text());
             System.out.println(e.select(".text-muted a:nth-child(1)").text());
             System.out.println(e.select(".text-muted > a:not(:first-child)").text());
             System.out.println();*/
            scrapedFilms.add(filmTemplate);


        }
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/filmdatabase",
                "root", "");

        for(Film filmx : scrapedFilms) {
            String cast = filmx.getCast();
            cast = cast.replace("'", "");
            String dir = filmx.getDirector();
            dir = dir.replace("'", "");
            String query = "INSERT INTO filmdatabase.tbl_films(cast, category, director, length, name, pub_date) VALUES ('"+ cast+"','"+filmx.getCategory() +
                    "','"+dir +"','"+ filmx.getLength()+"','"+filmx.getName() +"','"+filmx.getPubDate() +"');";
            System.out.println(query);
            Statement toInsert = connection.prepareStatement(query);
            toInsert.execute(query);

            // controller.saveMovie(filmx);
        }
        connection.close();
        //scrapedFilms = (ArrayList<Film>) repo.saveAll(scrapedFilms);
    }
    public static void scrape(String category, String pubDate) throws IOException, SQLException {
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




        FilmController controller = new FilmController();
        Document document = Jsoup.connect("https://www.imdb.com/list/ls000634294/").get();
        Elements body = document.select(".lister-list");

        Film filmTemplate;
        for(Element e : body.select(".lister-item") ){
            if(e.select(".genre").text().contains(category) || category == null || category.isEmpty()) {
                if(e.select(".lister-item-year").text().contains(pubDate) || pubDate == null || pubDate.isEmpty() ) {
                    filmTemplate = new Film(e.select(".lister-item-header a").text(),
                            e.select(".genre").text(),
                            e.select(".runtime").text(),
                            e.select(".lister-item-year").text(),
                            e.select(".text-muted a:nth-child(1)").text(),
                            e.select(".text-muted > a:not(:first-child)").text(),
                            null
                    );
                    scrapedFilms.add(filmTemplate);
                }
            }
            /** filmTemplate.setName(e.select(".lister-item-header a").text());
             filmTemplate.setCategory(e.select(".genre").text());
             filmTemplate.setLength(e.select(".runtime").text());
             filmTemplate.setPubDate(e.select(".lister-item-year").text());
             filmTemplate.setDirector(e.select(".text-muted a:nth-child(1)").text());
             filmTemplate.setCast(e.select(".text-muted > a:not(:first-child)").text());
             System.out.println(e.select(".lister-item-header a").text());
             System.out.println(e.select(".genre").text());
             System.out.println(e.select(".runtime").text());
             System.out.println(e.select(".lister-item-year").text());
             System.out.println(e.select(".text-muted a:nth-child(1)").text());
             System.out.println(e.select(".text-muted > a:not(:first-child)").text());
             System.out.println();*/



        }
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/filmdatabase",
                "root", "");

        for(Film filmx : scrapedFilms) {
            String cast = filmx.getCast();
            cast = cast.replace("'", "");
            String dir = filmx.getDirector();
            dir = dir.replace("'", "");
            String query = "INSERT INTO filmdatabase.tbl_films(cast, category, director, length, name, pub_date) VALUES ('"+ cast+"','"+filmx.getCategory() +
                    "','"+dir +"','"+ filmx.getLength()+"','"+filmx.getName() +"','"+filmx.getPubDate() +"');";
            System.out.println(query);
            Statement toInsert = connection.prepareStatement(query);
            toInsert.execute(query);

            // controller.saveMovie(filmx);
        }
        connection.close();
        //scrapedFilms = (ArrayList<Film>) repo.saveAll(scrapedFilms);
    }
}
