># About project
>This project contains a message application that runs in a console. You can find here both the server and the client, so you can deploy the server application on a public IP or just in a local network.

># Usage
>The project offers three main intefaces for you to chose from:
>- ServerInterface - Launching this class creates a server application, that 
   > listens on the current ip, the default port is 3001
> - ChatGGUIInterface - This launches a classic GUI client interface that communicates
   with the Server application once you start to log in
> - ClientInterface - A smaller console version of the client interface, that
> allows you to communicate with people on the given server just using a console

># Security
> Extremely high security is not the main goal of this project, servers are self deployable so there is no
> server signing of the messages nor asymetric encryption. All messages between users are 
> encrypted using AES symmetric cypher. The keys are derived form the usernames of the users, which makes the keys obtainable
> for a possible attacker. This problem can be solved via later implementation
> of the Diffie-Hellman key exchange between users/server.

># Controlls
> Both console applications have commands to display info about all available commands. Server has "?"/"help" command,
> the client has "help".

># Installation
> The project needs to be compiled with all its external dependencies, which consist of GUI library JavaFX.
