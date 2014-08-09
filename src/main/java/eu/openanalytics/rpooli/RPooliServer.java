
package eu.openanalytics.rpooli;

import static org.apache.commons.lang3.StringUtils.removeStart;

import java.net.URI;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.walware.ecommons.IDisposable;
import de.walware.rj.servi.pool.JMPoolServer;

/**
 * The actual server that bootstraps R nodes.
 * 
 * @author "OpenAnalytics &lt;rsb.development@openanalytics.eu&gt;"
 */
public class RPooliServer implements IDisposable
{
    private static final Log LOGGER = LogFactory.getLog(RPooliServer.class);

    private final JMPoolServer server;
    private final URI poolAddress;

    public static RPooliServer create(final ServletContext servletContext, final RPooliContext context)
    {
        final String serverId = removeStart(servletContext.getContextPath(), "/");
        return new RPooliServer(serverId, context);
    }

    private RPooliServer(final String id, final RPooliContext context)
    {
        LOGGER.info("Initializing with ID: " + id);

        try
        {
            LOGGER.info("RPooli properties file location: " + context.getPropertiesDirPath());
            LOGGER.info("Initializing: " + JMPoolServer.class.getSimpleName());
            server = new JMPoolServer(id, context);
            LOGGER.info("Starting: " + server);
            server.start();

            poolAddress = URI.create(server.getPoolAddress());
            LOGGER.info("Started with pool address: " + poolAddress);
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Failed to start server", e);
        }
    }

    public URI getPoolAddress()
    {
        return poolAddress;
    }

    @Override
    public void dispose()
    {
        try
        {
            if (server != null)
            {
                LOGGER.info("Shutting down server: " + server);
                server.shutdown();
            }
        }
        catch (final Exception e)
        {
            LOGGER.error("Failed to shutdown server", e);
        }
    }
}
