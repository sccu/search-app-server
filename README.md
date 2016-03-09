search-app-server
=================

search-app-server is a servlet providing tools for search process like 
text processing, query analysis, query expansion, etc.

Most configurations and search process can be specified in a search handler 
script written in scala. search-app-server embeds a scala interpreter, 
which allows you to change search logic without rebuilding a war file or 
even restarting a servlet container (in developing stage).