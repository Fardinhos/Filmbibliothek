package com.gruppen.filmdatabase.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gruppen.filmdatabase.entity.FilmStatistic;
import org.springframework.stereotype.Service;


@Service
public class StatisticJsonExporter {


    public String export(FilmStatistic filmStatistic) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(filmStatistic);
    }
}
