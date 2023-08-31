# Distributed Filesystem
simple distributed filesystem written Java, supports multiple client connections to primary server, with persistence to backup servers. 

A client is allowed 3 basic operations
* OPEN - download file from server to requesting client. If the file does not exit in primary server, primary server requests file from backup servers, if it exists. 
* ADD - upload file from requesting client to primary server and all backup servers 
* REMOVE - delete file from request client, primary server, and backup servers.

To compile and run: 
```
test test1
```
