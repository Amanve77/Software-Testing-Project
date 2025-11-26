maven:
	mvn clean
	mvn compile
	mvn install

pitest:
	mvn org.pitest:pitest-maven:mutationCoverage

run:
	java -cp target/EquipmentRentalManager-1.0-SNAPSHOT.jar org.equipment.App

