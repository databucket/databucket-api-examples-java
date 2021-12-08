package pl.databucket.api.java.examples;

import pl.databucket.api.java.client.Data;
import pl.databucket.api.java.client.Field;
import pl.databucket.api.java.client.Operator;
import pl.databucket.api.java.client.Rules;
import pl.databucket.api.java.examples.constants.TagName;
import pl.databucket.api.java.examples.constants.UserProp;

public class DeleteDataExample extends DatabucketInitialization {

	public static void main(String[] args) {
		new DeleteDataExample().run();
	}

	private void run() {
		Rules rules = new Rules();
		rules.addRule(Field.TAG_ID, Operator.equal, databucket.getTagId(TagName.GOOD));
		rules.addRule(UserProp.FIRST_NAME, Operator.like, "John%");

		Data user = bucketDevUsers.getData(rules);

		bucketDevUsers.deleteData(user);

		System.out.println(user);
	}



}
