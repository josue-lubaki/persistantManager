package com.database;

import com.google.inject.AbstractModule;

public class ImportingDatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        // ConnectivityChecking implements Connectivity
        bind(ConnectivityChecking.class).to(Connectivity.class);
    }
}
