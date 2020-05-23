import models.MovieOption;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import services.interfaces.MovieService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MovieServiceTest {

    private MovieService movieService;

    @Before
    public void init() throws IOException {

        List<MovieOption> list = getMovieOptions();
        this.movieService = mock(MovieService.class);


        when(movieService.findAllByTitle("The")).thenReturn(list);
    }

    private List<MovieOption> getMovieOptions() {
        List<MovieOption> list= new ArrayList<>();

        list.add(new MovieOption("The Avengers"));

        list.add(new MovieOption("The Iron Man"));
        return list;
    }

    @Test
    public void shoudReturnMovieList() throws IOException {
        Assert.assertEquals(movieService.findAllByTitle("The"), getMovieOptions());
    }


}
