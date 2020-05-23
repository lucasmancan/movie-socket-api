package modules;

import com.google.inject.AbstractModule;
import services.interfaces.MovieService;
import services.MovieServiceImpl;

public class DIModule extends AbstractModule{

    protected void configure() {
        bind(MovieService.class).to(MovieServiceImpl.class);
    }
}