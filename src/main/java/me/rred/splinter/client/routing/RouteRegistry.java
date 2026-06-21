package me.rred.splinter.client.routing;

import java.util.ArrayList;
import java.util.List;

public class RouteRegistry {
    private List<Route> routes = new ArrayList<>();

    public RouteRegistry() {
        Route route = new Route();
        routes.add(route); //
    }


    public void add(Route route) {
        routes.add(route);
    }

    public void remove(Route route) {
        routes.remove(route);
    }

}
