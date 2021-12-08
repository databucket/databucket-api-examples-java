package pl.databucket.api.java.examples;

import pl.databucket.api.java.client.Data;
import pl.databucket.api.java.client.Field;
import pl.databucket.api.java.client.Operator;
import pl.databucket.api.java.client.Rules;

public class ReserveDataExample extends DatabucketInitialization {

	public static void main(String[] args) {
		new ReserveDataExample().run();
	}

	private void run() {
		Rules rules = new Rules();
		rules.addRule(Field.ID, Operator.grater, 0);

		Data user = bucketDevUsers.reserveData(rules, true);
		System.out.println(user);
	}
}
