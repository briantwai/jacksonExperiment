package jsonParser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserRecordJsonParser {

    //TODO: this class is really messy in general and should be cleaned up
    //Mainly messy due to my incomplete understanding of JACKSON.

    private Set<String> fieldNames = new HashSet<>();

    private String fileName;
    private JsonParser jp;
    public UserRecordJsonParser(String s) throws IOException {
        fileName = s;
        JsonFactory f = new MappingJsonFactory();
        jp = f.createParser(new File(fileName));
        JsonToken current;
        current = jp.nextToken();
        if (current != JsonToken.START_ARRAY) {
            System.out.println("Error: root should be object: quiting.");
            current = jp.nextToken();
            System.out.println(current);
            return;
        }
        current = jp.nextToken();

        fieldNames.add("guid");
        fieldNames.add("isActive");
        fieldNames.add("balance");
        fieldNames.add("age");
        fieldNames.add("eyeColor");
        fieldNames.add("name");
        fieldNames.add("gender");
        fieldNames.add("email");
        fieldNames.add("phone");
        fieldNames.add("address");
        fieldNames.add("registered");
        fieldNames.add("friends");
        fieldNames.add("favoriteFruit");
        fieldNames.add("greeting");

    }

    public UserRecord getNextRecord() throws IOException {
        HashMap<String, String> fields = new HashMap<>();
        JsonToken current;
        List<String> names = new ArrayList<>();
        JsonToken x = jp.nextToken();
        while (x != JsonToken.END_ARRAY && x != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            // move from field name to field value
            current = jp.nextToken();
            if (fieldNames.contains(fieldName)) {
                if (current == JsonToken.START_ARRAY) {
                    // For each of the records in the array
                    while (jp.nextToken() != JsonToken.END_ARRAY) {
                        // read the record into a tree model,
                        // this moves the parsing position to the end of it
                        JsonNode node = jp.readValueAsTree();
                        // And now we have random access to everything in the object
                        String name = node.get("name").asText();
                        names.add(name);
                    }
                } else {
                    fields.put(fieldName, jp.getText());
                }
            } else {
                //System.out.println("Unprocessed property: " + fieldName);
                jp.skipChildren();
                return null;
            }
            x = jp.nextToken();
        }
        x = jp.nextToken();
        if(fields.size() == 0) {
            return null;
        }
        //Now we parse a userRecord out of this
        String guid = fields.get("guid");
        Boolean isActive = Boolean.valueOf(fields.get("isActive"));
        String balance = fields.get("balance");
        Double balanceValue = getBalanceValue(balance);
        int age = Integer.valueOf(fields.get("age"));
        String eyeColor = fields.get("eyeColor");
        String name = fields.get("name");
        String gender = fields.get("gender");
        Gender g = getGender(gender);
        String email = fields.get("email");
        String phone = fields.get("phone");
        String address = fields.get("address");
        String regs = fields.get("registered");
        Date registrationDate = getRegistrationDate(regs);
        //List<String> friends = fields.get("friends").findValuesAsText("name");
        String greeting = fields.get("greeting");
        int numUnreadMessages = getNumUnreadMessages(greeting);
        String favoriteFruit = fields.get("favoriteFruit");
        UserRecord userRecord = new UserRecord(guid, isActive, balanceValue, age, eyeColor, name, g, email, phone, 
                address, registrationDate, names, numUnreadMessages, favoriteFruit);
        return userRecord;
    }

    private int getNumUnreadMessages(String greeting) {
        int index = greeting.lastIndexOf(" unread mess");
        int start = greeting.lastIndexOf("You have ");
        if(start == -1 || index == -1) {
            return 0;
        }
        int m = start + "You have ".length();
        String s = greeting.substring(m, index);
        return Integer.valueOf(s);
    }

    private Date getRegistrationDate(String regs) {
        int len = "yyyy-mm-dd".length();
        String subDate = regs.substring(0, len);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
        try {
            Date d = simpleDateFormat.parse(subDate);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private Gender getGender(String gender) {
        if(gender.equals("female")) {
            return Gender.FEMALE;
        }
        return Gender.MALE;
    }

    private Double getBalanceValue(String balance) {
        NumberFormat nf = new DecimalFormat("$#,###.##");
        try {
            double dbl = nf.parse(balance).doubleValue();
            return dbl;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0.0;
        }
    }


}
