package com.eventorama.mobi.lib.tests;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.DatabaseUtils;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import android.test.RenamingDelegatingContext;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;

public class ProviderTestCase3<T extends ContentProvider> extends AndroidTestCase {


	private Class<T> mProviderClass;
	private String mProviderAuthority;
	
	static final String TEST_PACKAGE = "com.eventorama.mobi.sowhatevernew";

	public ProviderTestCase3(Class<T> providerClass, String providerAuthority) {
		mProviderClass = providerClass;
		mProviderAuthority = providerAuthority;
	}



	/**
	 * The content provider that will be set up for use in each test method.
	 */
	private T mProvider;
	private MockContentResolver mResolver;
	private MyIsolatedContext mProviderContext;

	public T getProvider() {
		return mProvider;
	}
	
	
	 @Override
	    protected void setUp() throws Exception {
	        super.setUp();



	        mResolver = new MockContentResolver();
	        final String filenamePrefix = "test.";
	        RenamingDelegatingContext targetContextWrapper = new RenamingDelegatingContext(
	                new MockContext(), // The context that most methods are
	                // delegated to
	                getContext(), // The context that file methods are delegated to
	                filenamePrefix);
	        mProviderContext = new MyIsolatedContext(mResolver, targetContextWrapper);

	        mProvider = mProviderClass.newInstance();
	        mProvider.attachInfo(mProviderContext, null);
	        assertNotNull(mProvider);
	        mResolver.addProvider(mProviderAuthority, getProvider());
	    }

	  public static <T extends ContentProvider> ContentResolver newResolverWithContentProviderFromSql(
	            Context targetContext, String filenamePrefix, Class<T> providerClass, String authority,
	            String databaseName, int databaseVersion, String sql)
	            throws IllegalAccessException, InstantiationException {
	        MockContentResolver resolver = new MockContentResolver();
	        RenamingDelegatingContext targetContextWrapper = new RenamingDelegatingContext(
	                new MockContext(), // The context that most methods are delegated to
	                targetContext, // The context that file methods are delegated to
	                filenamePrefix);
	        Context context = new IsolatedContext(resolver, targetContextWrapper);
	        DatabaseUtils.createDbFromSqlStatements(context, databaseName, databaseVersion, sql);

	        T provider = providerClass.newInstance();
	        provider.attachInfo(context, null);
	        resolver.addProvider(authority, provider);

	        return resolver;
	    }

}
