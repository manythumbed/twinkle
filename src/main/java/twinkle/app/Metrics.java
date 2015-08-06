package twinkle.app;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.ext.web.Router;

final class Metrics {
	private final MetricsService metricsService;
	private final Vertx vertx;
	private final HttpServer httpServer;

	Metrics(final Vertx vertx, final HttpServer httpServer) {
		this.vertx = vertx;
		this.httpServer = httpServer;

		this.metricsService = MetricsService.create(vertx);
	}

	private JsonObject server()	{
		return metricsService.getMetricsSnapshot(vertx);
	}

	Router router()	{
		final Router router = Router.router(vertx);

		router.route(HttpMethod.GET, "/*").produces("application/json").handler(context -> {
			context.response().putHeader("content-type", "application/json");
			context.next();
		});

		router.route(HttpMethod.GET, "/server").handler(context -> context.response().end(server().encodePrettily()));

		router.route(HttpMethod.GET, "/http").handler(context -> context.response().end(http().encodePrettily()));

		return router;
	}

	private JsonObject http()	{
		return metricsService.getMetricsSnapshot(httpServer);
	}
}
