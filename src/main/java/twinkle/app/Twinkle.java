package twinkle.app;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.web.Router;

public final class Twinkle {

	private Twinkle() {
		final Logger logger = LoggerFactory.getLogger(Twinkle.class);

		final Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true)));
		final HttpServer httpServer = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));
		final Router router = Router.router(vertx);

		final Metrics metrics = new Metrics(vertx, httpServer);

		router.mountSubRouter("/stats", metrics.router());

		httpServer.requestHandler(router::accept).listen(8080, result -> {
			if (result.succeeded()) {
				logger.info("Twinkle started");
			} else {
				logger.error("Twinkle failed to start", result.cause());
				vertx.close(shutdown -> logger.info("Twinkle shut down"));
			}
		});
	}

	public static void main(final String[] arguments) {
		new Twinkle();
	}
}
