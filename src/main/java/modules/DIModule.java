package modules;

import com.google.inject.AbstractModule;
import services.TCPResquestHandlerImpl;
import services.TCPServerImpl;
import services.interfaces.MovieService;
import services.MovieServiceImpl;
import services.interfaces.*;

public class DIModule extends AbstractModule{
    protected void configure() {
        bind(MovieService.class).to(MovieServiceImpl.class);
        bind(TCPServer.class).to(TCPServerImpl.class);
        bind(TCPResquestHandler.class).to(TCPResquestHandlerImpl.class);
    }
}