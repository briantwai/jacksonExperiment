package jsonParser;

import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.io.IOException;
import java.util.*;

public class UserSummary {
    private int numUsers = 0;
    private int numFemales = 0;
    private Map<Integer, Set<String>> usersRegisteredPerYear = new HashMap<>();
    private long totalNumFriends = 0;
    private double averageBalance = 0.0;
    private long femaleUnreadMessages = 0;
    private List<Integer> ageList = new ArrayList<>();

    public UserSummary() {

    }

    public void updateUserSummary(UserRecord userRecord) {
        numUsers++;
        updateUsersRegisteredPerYear(userRecord);
        updateTotalNumFriends(userRecord);
        updateAverageBalance(userRecord);
        updateAverageUnreadFemaleMessages(userRecord);
        updateMedianUserAge(userRecord);
    }

    private void updateUsersRegisteredPerYear(UserRecord userRecord) {
        Date d = userRecord.getRegistrationDate();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int year = c.get(Calendar.YEAR);
        Set<String> usersInYear = usersRegisteredPerYear.getOrDefault(year, new HashSet<>());
        usersInYear.add(userRecord.getName());
        usersRegisteredPerYear.put(year, usersInYear);
    }

    private void updateTotalNumFriends(UserRecord userRecord) {
        totalNumFriends = totalNumFriends + userRecord.getFriends().size();
    }

    private void updateAverageBalance(UserRecord userRecord) {
        double balance = userRecord.getBalance();
        averageBalance = averageBalance * ((double) (numUsers - 1) / numUsers) + balance/numUsers;
    }

    private void updateAverageUnreadFemaleMessages(UserRecord userRecord) {
        if(userRecord.getGender() == Gender.FEMALE) {
            numFemales++;
            femaleUnreadMessages = femaleUnreadMessages + userRecord.getUnreadMessages();
        }
    }

    private void updateMedianUserAge(UserRecord userRecord) {
        int userAge = userRecord.getAge();
        ageList.add(userAge);
    }

    public void printStats() {
        System.out.println("Users registered in each year: ");
        for(Integer year: usersRegisteredPerYear.keySet()) {
            System.out.println("During year " + year + " users registered are: ");
            for(String name: usersRegisteredPerYear.get(year)){
                System.out.print(name + " ");
            }
            System.out.println("");
        }
        Median median = new Median();
        double[] ages = new double[ageList.size()];
        for(int i = 0; i<ageList.size(); i++ ){
            ages[i] = ageList.get(i);
        }
        System.out.println("Average Friend Count: " + totalNumFriends/((double)numUsers));
        System.out.println("Median User age: " + median.evaluate(ages));
        System.out.println("Mean Balance Amount: " + averageBalance);
        System.out.println("Mean Female Unread Messages: " + femaleUnreadMessages/((double)numFemales));
    }

    public static void main(String[] args) {
        UserSummary userSummary = new UserSummary();
        //String fileName = "C:/Users/Brian Wai/IdeaProjects/x/src/main/java/small-user-data.json.txt";
        //String fileName = "C:/Users/Brian Wai/IdeaProjects/x/src/main/java/users-1.json";
        if(args.length == 0) {
            System.out.println("File name needed.");
            return;
        }
        String fileName = args[0];
        try {
            UserRecordJsonParser parser = new UserRecordJsonParser(fileName);
            UserRecord userRecord = parser.getNextRecord();
            int totalNumRecords = 0;
            while(userRecord != null) { //TODO: replace with optionals
                userSummary.updateUserSummary(userRecord);
                totalNumRecords++;
                if(totalNumRecords%1000 == 0) {
                    userSummary.printStats();
                    totalNumRecords = 0;
                }
                userRecord = parser.getNextRecord();
            }
            if(totalNumRecords != 0) {
                userSummary.printStats();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
