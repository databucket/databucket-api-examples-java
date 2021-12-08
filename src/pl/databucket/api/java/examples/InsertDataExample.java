package pl.databucket.api.java.examples;

import pl.databucket.api.java.client.*;
import pl.databucket.api.java.examples.constants.TagName;
import pl.databucket.api.java.examples.constants.UserProp;

public class InsertDataExample extends DatabucketInitialization {

	public static void main(String[] args) {
		new InsertDataExample().run();
	}

	private void run() {
        Data user = new Data(bucketDevUsers);

		user.setTag(TagName.GOOD);
		user.setProperty(UserProp.FIRST_NAME, "James");
		user.setProperty(UserProp.LAST_NAME, "Gray");
		user.setProperty(UserProp.EYE_COLOR, "blue");
		user.setProperty(UserProp.HEIGHT, "h<1m");
		user.setProperty(UserProp.PHONE, "111222333");

		user = bucketDevUsers.insertData(user);
		System.out.println(user);
	}

}
