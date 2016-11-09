/*
Copyright (c) 2013, TU Berlin
Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
DEALINGS IN THE SOFTWARE.
 */

package de.dailab.plistacontest.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

import org.apache.log4j.Level;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class Functions: - initializing and starting the http server -
 * configuring the http server Note: Configuration details may be provided as a
 * properties file (args[0])
 * 
 * @author andreas
 * 
 */
public class Client {

	/**
	 * the default logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(Client.class);

	/**
	 * the constructor.
	 */
	public Client() {
		super();
	}

	/**
	 * This method starts the server
	 * 
	 * @param args [hostname:port, properties_filename]
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		org.apache.log4j.Logger log4j1 = org.apache.log4j.Logger.getLogger(ContestHandler.class);
		log4j1.setLevel(Level.OFF);

		// store some configurations
		final Properties properties = new Properties();

		// load the team properties
		try {
			if (args.length > 1) {
				properties.load(new FileInputStream(args[1]));
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		// you might want to use a recommender
		Object recommender = new ContestHandler(properties, new DirtyRingBuffer(100));
		//Object recommender = new ContestHandlerOfflineCheating(null, "D:/tmp/CLEF-2015Dataset2/Json-07", 5L * 60L * 1000L);

		try {
			// initialize the recommender dynamically
			/*
			 * final Class<?> transformClass = Class.forName(args[1]);
			 * recommender = (Object) transformClass.newInstance();
			 */
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException(
					"No recommender specified or recommender not avialable.");
		}

		// configure log4j
		// if (args.length >= 3 && args[2] != null) {
		// PropertyConfigurator.configure(args[0]);
		// }
		// else {
		// PropertyConfigurator.configure("log4j.properties");
		// }
		String hostname = "0.0.0.0";
		int port = 8081;
		try {
			hostname = args[0].substring(0, args[0].indexOf(":"));
			port = Integer.parseInt(args[0].substring(args[0].indexOf(":") + 1));
		} catch (Exception e) {
			System.out.println("No hostname and port given. Using default 0.0.0.0:8081");
			logger.info(e.getMessage());
		}
		
		// set up and start server
		final Server server = new Server(new InetSocketAddress(hostname, port));
		server.setHandler(new ContestHandler(properties, recommender));
		
		logger.debug("Serverport " + server.getConnectors()[0].getPort());

		// start
		server.start();
		server.join();
	}

}
