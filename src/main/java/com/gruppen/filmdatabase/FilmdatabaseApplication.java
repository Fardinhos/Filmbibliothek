package com.gruppen.filmdatabase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
public class FilmdatabaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmdatabaseApplication.class, args);
    }

    /**
     *
     *
     * Quellen:
     *
     * https://www.youtube.com/watch?v=9SGDpanrc8U&t=2423s
     * https://www.tabnine.com/code/java/methods/org.springframework.web.servlet.ModelAndView/addObject
     *
     *  https://spring.io/quickstart
     *
     *    https://www.vogella.com/tutorials/SpringBoot/article.html
     *
     *    https://www.patrick-gotthard.de/mehrere-datenbanken-mit-spring-boot-und-spring-jdbc-anbinden/
     *    https://www.baeldung.com/spring-controller-vs-restcontroller
     https://www.baeldung.com/spring-jpa-like-queries
     https://stackoverflow.com/questions/17850726/sql-ignore-part-of-where-if-parameter-is-null
     https://www.baeldung.com/spring-jpa-like-queries
     https://stackoverflow.com/questions/25362540/like-query-in-spring-jparepository
     https://www.geeksforgeeks.org/how-to-iterate-hashmap-in-java/
     https://www.baeldung.com/java-optional
     https://www.geeksforgeeks.org/arraylist-removeif-method-in-java/
     https://stackoverflow.com/questions/37349455/split-string-after-every-2-words-and-store-into-list/
     https://stackoverflow.com/questions/48235379/how-to-display-byte-array-from-a-model-in-thymeleaf
     https://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp
     https://mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
     https://simplesolution.dev/spring-boot-export-download-json-file/
     https://www.baeldung.com/gson-exclude-fields-serialization
     https://www.baeldung.com/java-stream-filter-lambda
     https://stackoverflow.com/questions/59394249/cannot-convert-multipartfile-into-blob-in-spring
     https://www.javadevjournal.com/spring-security/two-factor-authentication-with-spring-security/
     https://stackoverflow.com/questions/48235379/how-to-display-byte-array-from-a-model-in-thymeleaf
     https://stackoverflow.com/questions/43079276/how-to-use-redirect-in-modelandview
     https://www.javadevjournal.com/spring-security/pass-an-additional-parameter-with-spring-security-login-page/
     https://stackoverflow.com/questions/5166898/java-lang-noclassdeffounderror-org-apache-commons-fileupload-fileitemfactory


     */

}
