package net.sourceforge.pebble.mock;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class MockContext implements Context {

	protected MockContext(Hashtable<?,?> environment, Context parent, String myName) {

	}

	@Override
	public Object addToEnvironment(String arg0, Object arg1) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bind(Name arg0, Object arg1) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void bind(String arg0, Object arg1) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Name composeName(Name arg0, Name arg1) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String composeName(String arg0, String arg1) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createSubcontext(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createSubcontext(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroySubcontext(Name arg0) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroySubcontext(String arg0) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NameParser getNameParser(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NameParser getNameParser(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookup(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookup(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookupLink(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookupLink(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rebind(Name arg0, Object arg1) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rebind(String arg0, Object arg1) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object removeFromEnvironment(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rename(Name arg0, Name arg1) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(String arg0, String arg1) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbind(Name arg0) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbind(String arg0) throws NamingException {
		// TODO Auto-generated method stub

	}

}
