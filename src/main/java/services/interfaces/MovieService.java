package services.interfaces;

import models.MovieOption;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    List<MovieOption> findAllByTitle(String query) throws IOException;
}
