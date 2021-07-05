package pl.databucket3.api.java.examples;

import pl.databucket3.api.java.client.*;
import pl.databucket3.api.java.examples.structure.Buckets;
import pl.databucket3.api.java.examples.structure.Tags;
import pl.databucket3.api.java.examples.structure.User;

public class ExampleCRUD {

    private final String url = "http://localhost:8080";
    private final boolean logs = true;
    private final String username = "super";
    private final String password = "super";
    private final int projectId = 1;

    Databucket databucket = new Databucket(url, username, password, projectId, logs);
    
	public static void main(String[] args) {
		ExampleCRUD example = new ExampleCRUD();
		example.createData();
		example.modifyData();
		example.deleteData();
	}

	private void createData() {
        Bucket bucket = new Bucket(databucket, Buckets.DEV_USERS);

        Data user = new Data(bucket);
		user.setTag(Tags.ACTIVE);
		user.setProperty(User.FIRST_NAME, "Donald");
		user.setProperty(User.LAST_NAME, "Trump");
		user.setProperty(User.CITY, "Washington");
		user.setProperty(User.ADDRESS, "Avenue NW");
		user.setProperty(User.COMPANY, "The Trump Organization");
		user.setProperty(User.EMAIL, "donald.trump@trump.com");
		user.setProperty(User.PHONE, "0123-123-123");
		user.setProperty(User.WEB, "https://www.trump.com");
		user = bucket.insertData(user);

		System.out.println(user);
	}

	private void modifyData() {
		Bucket bucket = new Bucket(databucket, Buckets.DEV_USERS);

		Condition[] conditions = {
				new Condition(Source.FIELD, Field.TAG_ID, Operator.equal, Source.CONST, databucket.getTagId(Tags.ACTIVE)),
				new Condition(Source.PROPERTY, User.EMAIL, Operator.like, Source.CONST, "%trump.com")
		};
		// Reserve and read data
		Data user = bucket.reserveData(conditions, true);
		user.setTag(Tags.TRASH);
		user.setReserved(false);
		user.setProperty(User.COLOR, "blue");
		user = bucket.updateData(user);

		System.out.println(user);
	}

	private void deleteData() {
		Bucket bucket = new Bucket(databucket, Buckets.DEV_USERS);

		Condition[] conditions = {
				new Condition(Source.FIELD, Field.ID, Operator.graterEqual, Source.CONST, 0)
		};
		// Reserve and read data
		Data user = bucket.getData(conditions);

		// Remove data
		bucket.deleteData(user);
	}

}
