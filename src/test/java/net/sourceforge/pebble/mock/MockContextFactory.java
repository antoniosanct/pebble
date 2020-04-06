package net.sourceforge.pebble.mock;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class MockContextFactory implements InitialContextFactory {

	@Override
	public Context getInitialContext(Hashtable<?, ?> arg0) throws NamingException {
		return new MockContext(null, null, null);
	}

}
