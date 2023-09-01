# Distributed Filesystem
A program designed to emulate a distributed filesystem on a local machine, supporting multiple client connections to primary server, with persistence to backup servers. Because everything is designed to run on `localhost`, differnet folders must be used for each client and server instance in order to demonstrate full functionality. 

## Client
Upon connecting to a primary server, a client is allowed 3 basic operations:
* OPEN - download file from server to requesting client. If the file does not exit in primary server, primary server requests file from backup servers, if it exists. 
* ADD - upload file from requesting client to primary server and all backup servers. 
* REMOVE - delete file from requesting client, primary server, and backup servers.

A client may also connect to a backup server, but its changes will not be persisted to the primary server and other backup servers.
A client must log in with an existing username and password. For the purposes of this documentation, username `admin` and password `coms454` is used. `admin` may create new username and passwords. Usernames are stored in a plain textfile, passwords are hashed. 

There is no limit set on the number of client connections to servers.

## Server
A server can either be a backup server or a primary server. A primary server will maintain consistency across the backup servers. 
A primary server must be running in order for a backup server to run and connect to the primary, as well as for clients to connect to it. 
A new backup server may connect to the primary server at any time. 

## Running Instructions

To compile, run `javac *.java` in every subdirectory. For example:
```
cd DistributedFileSystem/Server
javac *.java
```

To Run: 

Firstly, run the primary server with a chosen port:
```
nikababtsov@LAPTOP-VGHR5LTP:/mnt/c/Users/Nika/DistributedFilesystem/Server$ java Server 5000
Primary Server started
Waiting for a client ...
```
---


Then, run the backup server from Backup1, choosing a different port and specifying the previously chosen port of the primary server:


```
nikababtsov@LAPTOP-VGHR5LTP:/mnt/c/Users/Nika/DistributedFilesystem/Backup1$ java Server 5001 5000
Just send out: JOIN 5001 to port: 5000
Got response: COMPLETE_JOIN
Backup with port 5001 is up and listening for primary
```
Primary server should say:

```
Got message: JOIN 5001
Just had backup server join on port 5001
``` 

Then, connect to the primary server as a client: 
```
nikababtsov@LAPTOP-VGHR5LTP:/mnt/c/Users/Nika/DistributedFilesystem/Client$ java Client 5000
Enter username: 
admin
Enter password: 
coms454
Welcome user
Type OPEN <filename> to retrieve file, ADD <filename> to add new file, REMOVE <filename> to remove it
>
```


