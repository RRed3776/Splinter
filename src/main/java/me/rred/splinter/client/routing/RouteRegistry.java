package me.rred.splinter.client.routing;

import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.routing.triggers.MapTrigger;
import me.rred.splinter.client.routing.triggers.Trigger;
import me.rred.splinter.client.sets.SplinterSet;

import java.util.ArrayList;
import java.util.List;

public class RouteRegistry {
    private List<Route> routes = new ArrayList<>();
    private Route defaultRoute;

    public RouteRegistry() {
        defaultRoute = new Route();
        routes.add(defaultRoute);
    }

    public Route createNewRoute(String name) {
        MapTrigger start = new MapTrigger(Trigger.TriggerSlot.START);
        MapTrigger end = new MapTrigger(Trigger.TriggerSlot.END);
        return new Route(start, end, name);
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
