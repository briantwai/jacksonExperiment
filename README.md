# jacksonExperiment

To build the code, take the attached pom.xml file and the files and insert them into INTELLIJ IDEA. Either that, or download the things in the pom.xml file via Maven, and build the files using java8.
You’ll want to run the main function in the class UserSummary.java (via running the class). However, you’ll need to input the name of your file as an argument into the code (e.g. /run userSummary input.json). 
After completing the above steps, running the main program should work as intended.
To add more filters, simply change the userSummary class to extract the summary data needed from each User Record, as we already grab all the relevant information from each user by default (there’s no need to change the parser).
