Para compilar o programa é preciso ter um terminal aberto com a diretoria antes de SD2324 e executar o seguinte comando:
javac SD2324_code/src/Client.java SD2324_code/src/ClientData.java SD2324_code/src/Server.java SD2324_code/src/ServerData.java SD2324_code/src/User.java SD2324_code/src/Connections/Demultiplexer.java SD2324_code/src/Connections/TaggedConnection.java

Para executar o Server é preciso estar dentro do src e executar:
java -cp .:../lib/sd23.jar Server

Depois para executar o clients é só executar:
java Client

Agora é só utilizar o programa
