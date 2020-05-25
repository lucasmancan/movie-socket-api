package services;

import com.google.inject.Singleton;
import models.MovieOption;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import services.interfaces.MovieService;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class MovieServiceImpl implements MovieService {

    // url pattern of movie search
    private final String baseUrl = "https://www.imdb.com/find?q=%s&s=tt&ref_=fn_tt";

    public List<MovieOption> findAllByTitle(String queryString) throws IOException {

        queryString = URLEncoder.encode(queryString, "UTF-8");

        Document doc = Jsoup.connect(String.format(baseUrl, queryString)).get();

        // current path of movie information
        return doc.select(".findSection .findList .findResult .result_text a")
                .stream()
                .map(element -> new MovieOption(element.text()))
                .collect(Collectors.toList());
    }
}
