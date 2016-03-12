search-app-server
=================

[![Build Status](https://travis-ci.org/sccu/search-app-server.svg?branch=master)](https://travis-ci.org/sccu/search-app-server)

search-app-server is a servlet guiding search processes and providing tools 
for search processes like text processing, query analysis, query expansion, 
etc.

search-app-server embeds a Scala interpreter, which allow you can control 
most configurations and search process with Scala script. So, you can change 
search logic in a Scala script file without re-building the war file and 
even restart a servlet container, which is very convenient during development.