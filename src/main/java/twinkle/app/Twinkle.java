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

		final Vertx vertx = Vertx.vertx(vertxOptions());
		final HttpServer httpServer = vertx.createHttpServer(httpServerOptions());
		final Router router = Router.router(vertx);

		router.mountSubRouter("/stats", new Metrics(vertx, httpServer).router());

		httpServer.requestHandler(router::accept).listen(8080, result -> {
			if (result.succeeded()) {
				logger.info("Twinkle started");
			} else {
				logger.error("Twinkle failed to start", result.cause());
				vertx.close(shutdown -> logger.info("Twinkle shut down"));
			}
		});
	}

	private VertxOptions vertxOptions() {
		return new VertxOptions()
			.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
	}

	private HttpServerOptions httpServerOptions() {
		return new HttpServerOptions().setCompressionSupported(true);
	}

	public static void main(final String[] arguments) {
		new Twinkle();
	}
}
