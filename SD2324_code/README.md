Para compilar o programa é preciso ter um terminal aberto com a diretoria antes de SD2324 e executar o seguinte comando:
javac SD2324/src/Client.java SD2324/src/MenuInicial.java SD2324/src/Server.java SD2324/src/ServerData.java SD2324/src/User.java SD2324/src/Connections/Demultiplexer.java SD2324/src/Connections/TaggedConnection.java

Para executar o Server é preciso estar dentro do src e executar:
java -cp .:../lib/sd23.jar Server

Depois para executar o clients é só executar:
java Client

Agora é só utilizar o programa
