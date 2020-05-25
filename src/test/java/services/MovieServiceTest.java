package services;

import com.google.inject.Guice;
import com.google.inject.Injector;
import modules.DIModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import services.interfaces.MovieService;

import java.io.IOException;
import java.util.List;

public class MovieServiceTest {

    private MovieService movieService;

    @Before
    public void init()  {
        Injector injector = Guice.createInjector(new DIModule());

        movieService = injector.getInstance(MovieService.class);
    }

    @Test
    public void shouldReturnMovieListWithoutErrors() throws IOException {
       List list = movieService.findAllByTitle("ac/dc");

        Assert.assertNotNull(list);
    }

    @Test
    public void shouldNotBreakWithSpecialChars() throws IOException {
        List list = movieService.findAllByTitle("/*as*´ ~123-/:?;");

        Assert.assertNotNull(list);
    }

}
