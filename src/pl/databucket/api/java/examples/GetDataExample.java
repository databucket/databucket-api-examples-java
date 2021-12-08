package pl.databucket.api.java.examples;

import pl.databucket.api.java.client.*;
import pl.databucket.api.java.examples.constants.TagName;
import pl.databucket.api.java.examples.constants.UserProp;

public class GetDataExample extends DatabucketInitialization {

	public static void main(String[] args) {
		new GetDataExample().run();
	}

	private void run() {
		Rules rules = new Rules();
		rules.addRule(Field.TAG_ID, Operator.equal, databucket.getTagId(TagName.GOOD));
		rules.addRule(UserProp.FIRST_NAME, Operator.like, "John%");

		Data user = bucketDevUsers.getData(new Rules(Field.ID, Operator.grater, 0));
		System.out.println(user);
	}
}
