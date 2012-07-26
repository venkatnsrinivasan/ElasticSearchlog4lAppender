Disclaimer
==========
I wrote this library as part of playing around with elasticsearch and writing appenders for log4j
not out of any personal use .

This library provides a simple appender to log Log4J events (LoggingEvent)
to an elastic search cluster. It consists of 
Log4jElasticSearchAppender
================================

This class extends the AppenderSkeleton from log4j spi api and implements the append methods. 
As part of initialization, the appender can work either as Client Node of elasticsearch or
as a TransportClient without starting a node. Both these options are configurable. Further explanation
can be found in the sample log4j.properties provided.

LoggingEventJsonSourceBuilder
=============================
This provides the json string output given a LoggingEvent instance. An implementation -
ElasticSearchLoggingEventJsonSourceBuilderImpl is provided which uses the ElasticSearch
api to generate json. Additional implementations can be provided and the Appender can be changed to
use those.

Usage
=====
The pom builds two jars, one with and one without deps. If ES is installed within your webapp/application
then you probably dont need the deps. (To compile for now please skip the tests until i figure out the issue).

Start the ES instance and then use your application with the appender configured. You should 
see events getting indexed in ES. 


ISSUES
======
One HUGE issue which i havent been to get around is to the junit tests work. For some reason
in the unit testing env with maven, the connections to the clusters just hang.Iam still working on it.
But if put the jar and deps in any webapp for e.g and configure application log4j properties files 
to use this appender, it gets successfully sent and indexed in the elastic search cluster.Anyone 
who has any insights can help me with this.
 
 TODO
 ====
 Fix Tests.Add more unit tests
 Fix unit tests to work
