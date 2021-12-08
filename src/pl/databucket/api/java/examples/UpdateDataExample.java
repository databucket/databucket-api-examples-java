package pl.databucket.api.java.examples;

import pl.databucket.api.java.client.Data;
import pl.databucket.api.java.client.Field;
import pl.databucket.api.java.client.Operator;
import pl.databucket.api.java.client.Rules;
import pl.databucket.api.java.examples.constants.TagName;
import pl.databucket.api.java.examples.constants.UserProp;

public class UpdateDataExample extends DatabucketInitialization {

	public static void main(String[] args) {
		new UpdateDataExample().run();
	}

	private void run() {
		Data user = bucketDevUsers.reserveData(new Rules(Field.ID, Operator.grater, 0), true);
		System.out.println("Before changes:");
		System.out.println(user);

		user.setTag(TagName.TRASH);
		user.setReserved(false);
		user.setProperty(UserProp.EYE_COLOR, "green");
		user = bucketDevUsers.updateData(user);

		System.out.println("After changes:");
		System.out.println(user);
	}



}
