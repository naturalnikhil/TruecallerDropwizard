package com.truecaller.hw;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.skife.jdbi.v2.DBI;

import com.truecaller.hw.core.Person;
import com.truecaller.hw.db.PersonDAO;
import com.truecaller.hw.resources.PersonResource;

public class TcApplication extends Application<TcConfiguration>
{
	public static void main(String[] args) throws Exception {
		new TcApplication().run(args);
	}

	private final HibernateBundle<TcConfiguration> hibernateBundle =
			new HibernateBundle<TcConfiguration>(Person.class) {
		public DataSourceFactory getDataSourceFactory(TcConfiguration configuration) {
			return configuration.getDataSourceFactory();
		}
	};


	@Override
	public void initialize(Bootstrap<TcConfiguration> bootstrap) 
	{
		// Enable variable substitution with environment variables
		bootstrap.setConfigurationSourceProvider(
				new SubstitutingSourceProvider(
						bootstrap.getConfigurationSourceProvider(),
						new EnvironmentVariableSubstitutor(false)
						)
				);

		bootstrap.addBundle(new MigrationsBundle<TcConfiguration>() {
			public DataSourceFactory getDataSourceFactory(TcConfiguration configuration) {
				return configuration.getDataSourceFactory();
			}
		});

		bootstrap.addBundle(hibernateBundle);
	}

	@Override
	public void run(TcConfiguration configuration, Environment environment) 
	{
		//        final DBIFactory factory = new DBIFactory();
		//        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "h2");
		//        final PersonDAO dao = jdbi.onDemand(PersonDAO.class);
		final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());

		environment.jersey().register(new PersonResource(dao));
	}

}
