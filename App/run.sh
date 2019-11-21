javac App.java
javac Database.java
jar cfmv App.jar Manifest.txt App.class Database.class
java -jar App.jar
