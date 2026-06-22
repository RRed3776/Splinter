package me.rred.splinter.client.routing;

import java.util.ArrayList;
import java.util.List;

public class RouteRegistry {
    private List<Route> routes = new ArrayList<>();
    private Route defaultRoute;

    public RouteRegistry() {
        defaultRoute = new Route();
        routes.add(defaultRoute);
    }

    public void add(Route route) {
        routes.add(route);
    }

    public void remove(Route route) {
        routes.remove(route);
    }

    public Route getDefaultRoute() {
        return defaultRoute;
    }

    public List<Route> getAllRoutes() {
        return routes;
    }

}
