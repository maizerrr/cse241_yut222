javac user.java
javac staff.java
javac admin.java
javac Database.java
jar cfmv user.jar user_manifest.txt user.class Database.class
jar cfmv staff.jar staff_manifest.txt staff.class Database.class
jar cfmv admin.jar admin_manifest.txt admin.class Database.class
